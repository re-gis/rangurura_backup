//package com.backend.proj.utils;
//
//import java.io.IOException;
//import java.util.Map;
//
//import org.springframework.stereotype.Service;
//import org.springframework.web.multipart.MultipartFile;
//
//import com.backend.proj.config.CloudinaryConfig;
//import com.backend.proj.exceptions.BadRequestException;
//import com.cloudinary.utils.ObjectUtils;
//
//import lombok.RequiredArgsConstructor;
//
//@Service
//@RequiredArgsConstructor
//public class UploadDoc {
//    private final CloudinaryConfig cloudinaryConfig;
//
//    public String uploadDoc(MultipartFile doc) throws IOException, BadRequestException {
//        if (doc == null) {
//            throw new BadRequestException("File cannot be null");
//        }
//
//        Map<?, ?> uploadResult = cloudinaryConfig.cloudinary().uploader().upload(doc.getBytes(), ObjectUtils.asMap(
//                "resource_type", "raw"));
//        return (String) uploadResult.get("url");
//    }
//
//    public String uploadRecord(MultipartFile record) throws IOException, BadRequestException {
//        if (record == null) {
//            throw new BadRequestException("Record can't be null");
//        }
//
//        Map<?, ?> uploadResult = cloudinaryConfig.cloudinary().uploader().upload(record.getBytes(), ObjectUtils.emptyMap());
//        return (String) uploadResult.get("url");
//    }
//}

package com.backend.proj.utils;

import java.io.IOException;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.backend.proj.config.CloudinaryConfig;
import com.backend.proj.exceptions.BadRequestException;
import com.cloudinary.utils.ObjectUtils;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UploadDoc {
    private final CloudinaryConfig cloudinaryConfig;

    public String uploadDoc(MultipartFile doc) throws IOException, BadRequestException {
        if (doc == null || doc.isEmpty()) {
            throw new BadRequestException("Proof file cannot be null or empty");
        }

        Map<?, ?> uploadResult = cloudinaryConfig.cloudinary().uploader().upload(doc.getBytes(), ObjectUtils.asMap(
                "resource_type", "auto")); // Set resource type to auto to let Cloudinary determine the type
        return (String) uploadResult.get("url");
    }

    public String uploadRecord(MultipartFile record) throws IOException, BadRequestException {
        if (record == null || record.isEmpty()) {
            throw new BadRequestException("Record file cannot be null or empty");
        }

        Map<?, ?> uploadResult = cloudinaryConfig.cloudinary().uploader().upload(record.getBytes(), ObjectUtils.asMap(
                "resource_type", "auto")); // Set resource type to auto to let Cloudinary determine the type
        return (String) uploadResult.get("url");
    }
}

