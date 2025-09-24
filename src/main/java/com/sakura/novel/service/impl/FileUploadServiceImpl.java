package com.sakura.novel.service.impl;

import com.sakura.novel.service.FileUploadService;
import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.InputStream;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * 文件上传服务实现类
 * 使用GitHub作为存储，通过jsdelivr CDN提供访问
 * 使用MinIO作为对象存储解决方案
 */
@Service
public class FileUploadServiceImpl implements FileUploadService {

    @Value("${github.token:}")
    private String githubToken;

    @Value("${github.repo:}")
    private String githubRepo;

    @Value("${github.owner:}")
    private String githubOwner;

    // MinIO 相关配置
    @Value("${minio.endpoint}")
    private String minioEndpoint;
    @Value("${minio.access-key}")
    private String minioAccessKey;
    @Value("${minio.secret-key}")
    private String minioSecretKey;
    @Value("${minio.bucket-name}")
    private String minioBucketName;

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    // MinIO 客户端实例
    private MinioClient minioClient;

    /**
     * 使用 @PostConstruct 注解，在服务初始化时创建 MinIO 客户端
     */
    @PostConstruct
    public void init() {
        this.minioClient = MinioClient.builder()
                .endpoint(minioEndpoint)
                .credentials(minioAccessKey, minioSecretKey)
                .build();
    }

    /**
     * 实现上传文件到 MinIO 的方法
     */
    @Override // CHANGE HERE: 修改方法签名
    public String uploadToMinio(MultipartFile file, String objectName) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("文件不能为空");
        }
        if (objectName == null || objectName.isEmpty()) {
            throw new IllegalArgumentException("对象名称不能为空");
        }

        try {
            // ... 检查和创建 bucket 的代码保持不变 ...
            boolean found = minioClient.bucketExists(BucketExistsArgs.builder().bucket(minioBucketName).build());
            if (!found) {
                minioClient.makeBucket(MakeBucketArgs.builder().bucket(minioBucketName).build());
            }

            // 使用 putObject 上传文件
            try (InputStream inputStream = file.getInputStream()) {
                minioClient.putObject(
                        PutObjectArgs.builder()
                                .bucket(minioBucketName)
                                .object(objectName) // CHANGE HERE: 直接使用传入的完整对象名
                                .stream(inputStream, file.getSize(), -1)
                                .contentType(file.getContentType())
                                .build()
                );
            }

            // 返回文件的访问URL
            // CHANGE HERE: 同样使用完整的对象名
            return String.format("%s/%s/%s", minioEndpoint, minioBucketName, objectName);

        } catch (Exception e) {
            throw new RuntimeException("上传文件到MinIO失败: " + e.getMessage(), e);
        }
    }
    @Override
    public String uploadToJsdelivr(MultipartFile file, String fileName) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("文件不能为空");
        }

        if (githubToken.isEmpty() || "your_github_token_here".equals(githubToken)) {
            throw new RuntimeException("GitHub token未配置，请在application.yml中配置github.token");
        }

        if (githubOwner.isEmpty() || "your_github_username".equals(githubOwner)) {
            throw new RuntimeException("GitHub owner未配置，请在application.yml中配置github.owner");
        }

        if (githubRepo.isEmpty() || "your_repo_name".equals(githubRepo)) {
            throw new RuntimeException("GitHub repo未配置，请在application.yml中配置github.repo");
        }

        try {
            // 将文件编码为base64
            byte[] fileContent = file.getBytes();
            String encodedContent = Base64.getEncoder().encodeToString(fileContent);

            // 构建GitHub API URL
            String apiUrl = String.format("https://api.github.com/repos/%s/%s/contents/avatars/%s",
                    githubOwner, githubRepo, fileName);

            // 构建请求体
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("message", "Upload avatar: " + fileName);
            requestBody.put("content", encodedContent);

            // 设置请求头
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", "Bearer " + githubToken);

            HttpEntity<String> entity = new HttpEntity<>(
                    objectMapper.writeValueAsString(requestBody), headers);

            // 发送请求到GitHub API
            ResponseEntity<String> response = restTemplate.exchange(
                    apiUrl, HttpMethod.PUT, entity, String.class);

            if (response.getStatusCode() == HttpStatus.CREATED ||
                    response.getStatusCode() == HttpStatus.OK) {
                // 返回jsdelivr CDN链接
                return String.format("https://cdn.jsdelivr.net/gh/%s/%s@main/avatars/%s",
                        githubOwner, githubRepo, fileName);
            } else {
                throw new RuntimeException("上传文件失败，HTTP状态码: " + response.getStatusCode());
            }

        } catch (IOException e) {
            throw new RuntimeException("读取文件失败: " + e.getMessage(), e);
        } catch (Exception e) {
            throw new RuntimeException("上传文件到GitHub失败: " + e.getMessage(), e);
        }
    }

    @Override
    public String generateUniqueFileName(String originalFilename) {
        if (originalFilename == null || originalFilename.isEmpty()) {
            return UUID.randomUUID().toString() + ".jpg";
        }

        String extension = "";
        int dotIndex = originalFilename.lastIndexOf(".");
        if (dotIndex > 0) {
            extension = originalFilename.substring(dotIndex);
        }

        return UUID.randomUUID().toString() + extension;
    }
}
