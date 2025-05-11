package com.team7.Idam.domain.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;

import java.io.IOException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FileUploadService {

    private final S3Client s3Client;
    private final String region;

    @Value("${AWS_S3_BUCKET}")
    private String bucket;

    // S3에 파일 업로드
    public String upload(MultipartFile file) {
        String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();

        try {
            s3Client.putObject(
                    PutObjectRequest.builder()
                            .bucket(bucket)
                            .key(fileName)
                            .contentType(file.getContentType())
                            .contentDisposition("inline") // 브라우저에서 바로 보이게
                            .build(),
                    RequestBody.fromInputStream(file.getInputStream(), file.getSize())
            );
        } catch (IOException e) {
            throw new RuntimeException("파일 업로드 실패", e);
        }

        return "https://" + bucket + ".s3." + region + ".amazonaws.com/" + fileName;
    }

    // S3에서 파일 삭제
    public void delete(String fileUrl) {
        String fileName = fileUrl.substring(fileUrl.lastIndexOf("/") + 1);

        s3Client.deleteObject(DeleteObjectRequest.builder()
                .bucket(bucket)
                .key(fileName)
                .build());
    }
}
