package com.bootcamp19.calculatewordservice.util;

import com.bootcamp19.calculatewordservice.exception.BadRequestException;
import io.minio.*;
import io.minio.http.Method;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.concurrent.TimeUnit;

/**
 * MinIO: bucket = "folder" utama, object = file di dalam bucket.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class MinioUtil {

    private final MinioClient minioClient;

    @Value("${minio.bucket.name}")
    private String bucketName;

    @Value("${minio.presigned-expiry-minutes}")
    private int presignedExpiryMinutes;

    private void ensureBucketExists() throws Exception {
        boolean exists = minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build());
        if (!exists) {
            minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
            log.info("Bucket {} dibuat", bucketName);
        }
    }

    public void upload(String objectName, byte[] content, String contentType) {
        try {
            ensureBucketExists();
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucketName)
                            .object(objectName)
                            .stream(new ByteArrayInputStream(content), content.length, -1)
                            .contentType(contentType)
                            .build()
            );
            log.info("Upload {} ke bucket {} berhasil", objectName, bucketName);
        } catch (Exception e) {
            log.error("Gagal upload ke MinIO", e);
            throw new BadRequestException("Gagal upload ke MinIO: " + e.getMessage());
        }
    }

    /** Link download sementara, tanpa membagikan access key ke user. */
    public String getPresignedUrl(String objectName) {
        try {
            return minioClient.getPresignedObjectUrl(
                    GetPresignedObjectUrlArgs.builder()
                            .method(Method.GET)
                            .bucket(bucketName)
                            .object(objectName)
                            .expiry(presignedExpiryMinutes, TimeUnit.MINUTES)
                            .build()
            );
        } catch (Exception e) {
            throw new BadRequestException("Gagal membuat presigned URL: " + e.getMessage());
        }
    }

    /** Download lewat aplikasi (stream dari MinIO ke response). */
    public void download(String objectName, HttpServletResponse response) {
        try {
            StatObjectResponse meta = minioClient.statObject(
                    StatObjectArgs.builder().bucket(bucketName).object(objectName).build()
            );
            response.setContentType(meta.contentType());
            response.setContentLengthLong(meta.size());
            response.setHeader("Content-Disposition", "attachment; filename=\"" + objectName + "\"");

            try (InputStream input = minioClient.getObject(
                    GetObjectArgs.builder().bucket(bucketName).object(objectName).build())) {
                input.transferTo(response.getOutputStream());
                response.getOutputStream().flush();
            }
        } catch (Exception e) {
            throw new BadRequestException("Gagal download file: " + e.getMessage());
        }
    }
}
