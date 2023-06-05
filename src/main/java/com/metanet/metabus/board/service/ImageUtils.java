package com.metanet.metabus.board.service;

import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class ImageUtils {
    public static File mergeImagesVertically(MultipartFile[] imageFiles) throws IOException {
        int maxWidth = 0;
        int totalHeight = 0;

        // 이미지 파일들의 최대 너비와 총 높이를 계산합니다.
        for (MultipartFile imageFile : imageFiles) {
            BufferedImage image = ImageIO.read(imageFile.getInputStream());
            maxWidth = Math.max(maxWidth, image.getWidth());
            totalHeight += image.getHeight();
        }

        // 새로운 이미지를 생성합니다.
        BufferedImage mergedImage = new BufferedImage(maxWidth, totalHeight, BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics = mergedImage.createGraphics();

        // 배경을 흰색으로 채웁니다.
        graphics.setColor(Color.WHITE);
        graphics.fillRect(0, 0, maxWidth, totalHeight);

        int currentHeight = 0;
        for (MultipartFile imageFile : imageFiles) {
            BufferedImage image = ImageIO.read(imageFile.getInputStream());
            graphics.drawImage(image, 0, currentHeight, null);
            currentHeight += image.getHeight();
        }

        // 합쳐진 이미지를 파일로 저장합니다.
        File mergedFile = File.createTempFile("merged", ".jpg");
        ImageIO.write(mergedImage, "jpg", mergedFile);

        return mergedFile;
    }

    public static boolean isImageFile(MultipartFile file) {
        String contentType = file.getContentType();
        if (contentType != null && contentType.startsWith("image")) {
            return true;
        }
        return false;
    }
}
