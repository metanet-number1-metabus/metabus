package com.metanet.metabus.board;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.PutObjectRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;

@Service
@RequiredArgsConstructor
public class AwsS3 {

    private final AmazonS3 amazonS3;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    public com.metanet.metabus.board.dto.AwsS3 upload(File multipartFile, String dirName, String fileName) throws IOException {

//        File file = convertMultipartFileToFile(multipartFile)
//                .orElseThrow(() -> new IllegalArgumentException("MultipartFile -> File convert fail"));

        return upload(multipartFile, fileName);
    }

    private com.metanet.metabus.board.dto.AwsS3 upload(File file, String fileName) {
        String key = fileName;
        String path = putS3(file, key);
        removeFile(file);

        return com.metanet.metabus.board.dto.AwsS3
                .builder()
                .key(key)
                .path(path)
                .build();
    }


    private String putS3(File uploadFile, String fileName) {
        amazonS3.putObject(new PutObjectRequest(bucket, fileName, uploadFile)
                .withCannedAcl(CannedAccessControlList.PublicRead));
        return getS3(bucket, fileName);
    }

    private String getS3(String bucket, String fileName) {
        return amazonS3.getUrl(bucket, fileName).toString();
    }

    private void removeFile(File file) {
        file.delete();
    }

//    public Optional<File> convertMultipartFileToFile(MultipartFile multipartFile) throws IOException {
//        File file = new File(System.getProperty("user.dir") + "/" + multipartFile.getOriginalFilename());
//
//        if (file.createNewFile()) {
//            try (FileOutputStream fos = new FileOutputStream(file)) {
//                fos.write(multipartFile.getBytes());
//            }
//            return Optional.of(file);
//        }
//        return Optional.empty();
//    }


}
