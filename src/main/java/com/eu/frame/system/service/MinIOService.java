package com.eu.frame.system.service;

import com.eu.frame.system.pojo.vo.FileVo;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.UUID;

/**
 * 文件服务
 */
@Service
public class MinIOService {

    @Value("${minio.endpoint}")
    private String domainUrl;

    @Value("${minio.bucket-name}")
    private String bucketName;

    private MinioClient minioClient;


    public MinIOService(@Value("${minio.endpoint}") String endpoint,
                        @Value("${minio.username}") String username,
                        @Value("${minio.password}") String password) {

        this.minioClient = MinioClient.builder().endpoint(endpoint).credentials(username, password).build();
    }

    /**
     * 文件上传
     *
     * @param file
     * @return
     */
    public FileVo upload(MultipartFile file) {

        //文件服务器地址
        String domainUrl = String.format("%s/%s", this.domainUrl, this.bucketName);
        //生成一个新的 uuid
        String uuid = UUID.randomUUID().toString();
        //文件的原始名称
        String originalFilename = file.getOriginalFilename();
        //文件扩展名
        int index = originalFilename.lastIndexOf(".");
        String extensionName = index > 0 ? originalFilename.substring(originalFilename.lastIndexOf(".")) : "";
        //文件上传到文件服务器的名称
        String fileName = String.format("%s%s", uuid, extensionName);
        //可以访问的 url
        String url = String.format("%s/%s", domainUrl, fileName);

        FileVo vo = new FileVo();
        vo.setDomain(String.format("%s%s", domainUrl, "/"));
        vo.setOriName(originalFilename);
        vo.setAbsolutePath(url);
        vo.setExtName(extensionName);
        vo.setPath(fileName);

        try {
            minioClient.putObject(PutObjectArgs.builder().bucket(this.bucketName)
                    .object(fileName)
                    .stream(file.getInputStream(), file.getInputStream().available(), -1)
                    .build());
            return vo;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * 下载
     *
     * @param url
     */
    public byte[] download(String url) {
        InputStream in;
        String objectName;
        byte[] bytes = new byte[0];
        try {
            String[] minioInfo = url.split("/", -1);
            objectName = minioInfo[4];
            in = minioClient.getObject(minioInfo[3], objectName);
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            int i;
            while ((i = in.read()) != -1) {
                out.write(i);
            }
            bytes = out.toByteArray();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return bytes;
    }

}
