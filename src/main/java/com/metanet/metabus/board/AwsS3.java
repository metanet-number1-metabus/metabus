package com.metanet.metabus.board;


import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.metanet.metabus.board.dto.AwsS3Dto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;


import java.io.File;
import java.io.IOException;
@RequiredArgsConstructor
@Component
public class AwsS3 {

    private final AmazonS3 amazonS3;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    public AwsS3Dto upload(File multipartFile, String dirName, String fileName) throws IOException {

        return upload(multipartFile, fileName);
    }

    private AwsS3Dto upload(File file, String fileName) {
        String key = fileName;
        String path = putS3(file, key);
        removeFile(file);

        return AwsS3Dto
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



}
