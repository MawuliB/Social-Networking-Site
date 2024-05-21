package com.mawuli.sns.utility.cloudinary;

import com.cloudinary.*;
import com.cloudinary.utils.ObjectUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Service
public class CloudinaryService {
    private static final Logger log = LoggerFactory.getLogger(CloudinaryService.class);

    @Value("${cloud-name}")
    private String cloudName;

    @Value("${api-key}")
    private String apiKey;

    @Value("${api-secret}")
    private String apiSecret;

    @Value("${cloud-url}")
    private String url;

    Cloudinary cloudinary;

    public CloudinaryService() {
        try {
            cloudinary = new Cloudinary("cloudinary://932645885612349:yY0N7jInhrmuAmKwH9IUul8lM8U@dlzewc2k7");
        } catch (Exception e) {
            CloudinaryService.log.error("Error creating CloudinaryService: {}", e.getMessage());
        }
    }


    public Map upload(MultipartFile multipartFile) throws IOException {
        if(cloudinary == null) {
            throw new IOException("Cloudinary is not initialized");
        }
        Map params = ObjectUtils.asMap("resource_type", "auto");
        Map result = cloudinary.uploader().upload(multipartFile.getBytes(), params);
        return result;
    }

    public Map videoUpload(MultipartFile multipartFile) throws IOException {
        if(cloudinary == null) {
            throw new IOException("Cloudinary is not initialized");
        }
        Map params = ObjectUtils.asMap("resource_type", "video");
        Map result = cloudinary.uploader().upload(multipartFile.getBytes(), params);
        return result;
    }

    public Map delete(String id) throws IOException {
        return cloudinary.uploader().destroy(id, ObjectUtils.emptyMap());
    }

    private File convert(MultipartFile multipartFile) throws IOException {
        File file = new File(Objects.requireNonNull(multipartFile.getOriginalFilename()));
        FileOutputStream fo = new FileOutputStream(file);
        fo.write(multipartFile.getBytes());
        fo.close();
        return file;
    }

}