package ar.utn.ba.ddsi.services.impl;

import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.util.UUID;

@Service
public class MinioService{

  private final MinioClient minioClient;
  private final String bucketName;
  private final String publicBaseUrl;

  public MinioService(
      @Value("${minio.endpoint}") String endpoint,
      @Value("${minio.access-key}") String accessKey,
      @Value("${minio.secret-key}") String secretKey,
      @Value("${minio.bucket}") String bucketName,
      @Value("${minio.public-base-url}") String publicBaseUrl) {

    this.minioClient = MinioClient.builder()
        .endpoint(endpoint)
        .credentials(accessKey, secretKey)
        .build();

    this.bucketName = bucketName;
    this.publicBaseUrl = publicBaseUrl;
  }

  public String upload(MultipartFile file) {
    try {
      String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();

      minioClient.putObject(
          PutObjectArgs.builder()
              .bucket(bucketName)
              .object(fileName)
              .stream(file.getInputStream(), file.getSize(), -1)
              .contentType(file.getContentType())
              .build()
      );

      // URL COMPLETA → igual que antes, pero ahora configurable
      return publicBaseUrl + "/" + bucketName + "/" + fileName;

    } catch (Exception e) {
      throw new RuntimeException("Error subiendo archivo a MinIO", e);
    }
  }
}


