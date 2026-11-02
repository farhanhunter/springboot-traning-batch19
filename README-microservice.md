# Microservice Batch 19: Count Character

Studi kasus Day 1 (menghitung frekuensi huruf) dibawa sampai ke arsitektur microservice.
Format project mengikuti `springboot-traning-batch18`.

```
Postman ──► gatewayservice :8080 ──► (Eureka: lb://) ──► userservice          :8081  ──► PostgreSQL
              │  cek APIKey + JWT                        calculatewordservice :8082  ──► PostgreSQL, Redis, Kafka, MinIO
              │                                                    ▲
              └──────────── registryservice (Eureka) :8761 ────────┘   userservice ──Feign──► calculatewordservice
```

| Service | Port | Context path | Isi materi |
|---|---|---|---|
| registryservice | 8761 | - | Eureka Server |
| gatewayservice | 8080 | `/gateway/**` | Routing `lb://`, WebFlux Security, verifikasi JWT, header APIKey |
| userservice | 8081 | `/user` | Register (BCrypt), Login (JWT), JPA (derived/JPQL/native), Validation, Exception Handler, Feign ke calculatewordservice, AOP logging, Scheduler |
| calculatewordservice | 8082 | `/calculateword` | Hitung huruf, JPA, Redis `@Cacheable`/`@CacheEvict`, Kafka producer/consumer, Feign pihak ketiga, MinIO + presigned URL, Scheduler laporan harian, AOP, SSE |

## 1. Jalankan infrastruktur (Docker)

```bash
docker compose -f docker-compose-db.yaml up -d      # skip kalau belajar-postgres Day 1 masih jalan
docker compose -f docker-compose-redis.yaml up -d
docker compose -f docker-compose-kafka.yaml up -d
docker compose -f docker-compose-minio.yaml up -d
```

| UI | URL |
|---|---|
| Eureka | http://localhost:8761 |
| Redis Insight | http://localhost:5540 (host `redis-batch19`, port 6379) |
| Redpanda Console (Kafka) | http://localhost:8088 |
| MinIO Console | http://localhost:9001 (minioadmin / minioadmin) |

## 2. Jalankan service (urutan penting)

1. `registryservice`, tunggu sampai dashboard Eureka terbuka
2. `userservice` dan `calculatewordservice`
3. `gatewayservice` paling akhir

Dari terminal: `./mvnw spring-boot:run` di folder masing-masing service.
Untuk build tanpa infrastruktur menyala: `./mvnw package -DskipTests`.

## 3. Coba lewat Postman

Import `batch19-countchar.postman_collection.json`. Semua request lewat gateway `http://localhost:8080/gateway` dan wajib header `APIKey: 123456`.
Request **Login** otomatis menyimpan token ke variabel collection.

| Method | Endpoint (setelah `/gateway`) | Token |
|---|---|---|
| POST | `/user/rest/v1/users/register` | tidak |
| POST | `/user/rest/v1/auth/login` | tidak |
| GET | `/user/rest/v1/users/me` | ya |
| GET | `/user/rest/v1/analyses/{id}` (Feign ke calculatewordservice) | ya |
| POST | `/calculateword/rest/v1/analyses` body `{"kalimat":"..."}` | ya |
| POST | `/calculateword/rest/v1/analyses/random` (quote dari dummyjson.com) | ya |
| GET | `/calculateword/rest/v1/analyses?huruf=a` | ya |
| GET | `/calculateword/rest/v1/analyses/{id}` (cache Redis) | ya |
| DELETE | `/calculateword/rest/v1/analyses/{id}` (evict cache) | ya |
| GET | `/calculateword/rest/v1/analyses/trending?limit=5` | ya |
| POST | `/calculateword/rest/v1/reports/generate` | ya |
| GET | `/calculateword/rest/v1/reports/{file}/url` | ya |
| GET | `/calculateword/sse/subscribe/huruf-event` | ya |

Contoh response `POST /analyses` dengan kalimat `gajah mati`:

```json
{
  "reqId": "…",
  "status": "T",
  "message": "Analisis berhasil",
  "data": {
    "id": 1,
    "kalimat": "gajah mati",
    "totalHuruf": 9,
    "hurufTerbanyak": "a",
    "frekuensi": { "a": 3, "g": 1, "h": 1, "i": 1, "j": 1, "m": 1, "t": 1 },
    "createdBy": 1,
    "createdAt": "2026-11-02 09:30:00"
  }
}
```

## 4. Alur data saat membuat analisis

1. Gateway cek `APIKey`, verifikasi JWT, lalu menambahkan header `X-User-Id`.
2. `calculatewordservice` menghitung huruf (`CharCounterUtil`), simpan ke tabel `trx_analysis`.
3. `AnalysisTrackingAspect` (`@AfterReturning`) mengirim kalimat ke topic Kafka `WORD_ANALYZED`.
4. `WordAnalyzedConsumer` menambah skor huruf di Redis Sorted Set `batch19:huruf:trending` dan mengirim event SSE.
5. `GET /analyses/{id}` pertama kali query ke DB, berikutnya diambil dari Redis (`batch19:analysis::{id}`, TTL 2 menit).

## Perbedaan dengan batch18

- Studi kasus pokemon diganti count character; `pokemonservice` menjadi `calculatewordservice`.
- Password disimpan sebagai hash BCrypt (batch18 masih plain text).
- Path register di gateway disamakan dengan controller (`/users/register`).
- `@Value("${security.headers.apiKey}")` yang kurang kurung kurawal sudah diperbaiki.
- Filter gateway dibuat di `SecurityConfig` (bukan `@Component`) supaya tidak jalan dua kali.
- Kafka memakai mode KRaft (`apache/kafka:3.8.0`) tanpa Zookeeper, sesuai materi Day 6.
- Versi Spring Cloud diatur lewat BOM `spring-cloud-dependencies` di semua service.
- Port disamakan dengan deck Day 7 (gateway 8080, user 8081, calculateword 8082).
