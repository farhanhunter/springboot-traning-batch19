package com.bootcamp19.calculatewordservice.util;

import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Logika count character dari Day 1, sekarang dipakai di dalam microservice.
 * "Logikanya sama, cuma pintu masuknya berubah dari main() ke HTTP request."
 */
public class CharCounterUtil {
    private CharCounterUtil() {}

    /**
     * Hitung frekuensi huruf a-z. Hasil otomatis urut a-z karena memakai array int[26].
     * Contoh: "gajah mati" -> {a=3, g=1, h=1, i=1, j=1, m=1, t=1}
     */
    public static Map<String, Integer> hitungFrekuensi(String kalimat) {
        int[] freq = new int[26]; // index 0 = 'a', index 25 = 'z'

        if (kalimat != null) {
            // Locale.ROOT: aman dari bug locale Turki ("I".toLowerCase() jadi "ı")
            for (char c : kalimat.toLowerCase(Locale.ROOT).toCharArray()) {
                // cek range manual, BUKAN Character.isLetter('é') yang bisa bikin index di luar 0..25
                if (c >= 'a' && c <= 'z') {
                    freq[c - 'a']++;
                }
            }
        }

        Map<String, Integer> hasil = new LinkedHashMap<>();
        for (int i = 0; i < freq.length; i++) {
            if (freq[i] > 0) {
                hasil.put(String.valueOf((char) ('a' + i)), freq[i]);
            }
        }
        return hasil;
    }

    public static int totalHuruf(Map<String, Integer> frekuensi) {
        int total = 0;
        for (int jumlah : frekuensi.values()) {
            total += jumlah;
        }
        return total;
    }

    /** Huruf dengan frekuensi terbesar. Kalau seri, ambil yang paling awal di alfabet. */
    public static String hurufTerbanyak(Map<String, Integer> frekuensi) {
        String terbanyak = null;
        int max = 0;
        for (Map.Entry<String, Integer> entry : frekuensi.entrySet()) {
            if (entry.getValue() > max) {
                max = entry.getValue();
                terbanyak = entry.getKey();
            }
        }
        return terbanyak;
    }
}
