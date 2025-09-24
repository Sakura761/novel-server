package com.sakura.novel.service;

import org.springframework.web.multipart.MultipartFile;

/**
 * 文件上传服务接口
 */
public interface FileUploadService {

    /**
     * 上传文件到jsdelivr CDN
     * @param file 要上传的文件
     * @param fileName 文件名
     * @return CDN链接
     */
    String uploadToJsdelivr(MultipartFile file, String fileName);

    /**
     * 上传文件到 MinIO
     * @param file         上传的文件
     * @param objectName   文件在存储桶中的完整名称 (包含路径, 例如: "avatars/my-image.jpg")
     * @return 文件的访问URL
     */
    String uploadToMinio(MultipartFile file, String objectName);

    /**
     * 生成唯一文件名
     * @param originalFilename 原始文件名
     * @return 唯一文件名
     */
    String generateUniqueFileName(String originalFilename);
}
