package Uniton.Fring.global.s3;

import Uniton.Fring.global.exception.CustomException;
import Uniton.Fring.global.exception.ErrorCode;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.PutObjectRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class S3Service {

    private final AmazonS3Client amazonS3Client;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    // MultipartFile을 전달받아 File로 전환한 후 S3에 업로드
    public String upload(MultipartFile multipartFile, String dirName) throws IOException {

        log.info("[s3] 파일 업로드 요청");
        System.out.println("Tmp dir: " + System.getProperty("java.io.tmpdir"));
        System.out.println("Original filename: " + multipartFile.getOriginalFilename());

        File uploadFile = convert(multipartFile)
                .orElseThrow(() -> new CustomException(ErrorCode.FILE_CONVERT_FAIL));

        return upload(uploadFile, dirName);
    }

    private String upload(File uploadFile, String dirName) {

        String fileName = dirName + "/" + uploadFile.getName();
        String uploadImageUrl = putS3(uploadFile, fileName);

        // convert() 과정에서 로컬에 생성된 파일 삭제
        removeNewFile(uploadFile);

        return uploadImageUrl;
    }

    private String putS3(File uploadFile, String fileName) {

        // PublicRead 권한으로 upload
        amazonS3Client.putObject(new PutObjectRequest(bucket, fileName, uploadFile));

        // File의 URL return
        return amazonS3Client.getUrl(bucket, fileName).toString();
    }

    private void removeNewFile(File targetFile) {

        String name = targetFile.getName();

        // convert() 과정에서 로컬에 생성된 파일을 삭제
        if (targetFile.delete()){
            log.info(name + "파일 삭제 완료");
        } else {
            log.info(name + "파일 삭제 실패");
        }
    }

    public Optional<File> convert(MultipartFile multipartFile) throws IOException {

        // 원본 파일 확장자 가져오기
        String originalFilename = multipartFile.getOriginalFilename();
        String extension = "";

        if (originalFilename != null && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }

        // UUID + 확장자 조합으로 새로운 파일명 생성
        String uuidFileName = UUID.randomUUID().toString() + extension;

        File convertFile = new File(System.getProperty("java.io.tmpdir") + "/" + uuidFileName);

        if (convertFile.createNewFile()) {
            try (FileOutputStream fos = new FileOutputStream(convertFile)) {
                fos.write(multipartFile.getBytes());
            }
            return Optional.of(convertFile);
        }

        return Optional.empty();
    }

    public Pair<String, List<String>> uploadMainAndDescriptionImages(MultipartFile mainImage, List<MultipartFile> descriptionImages,
                                                                     String mainDir, String descDir) {
        String mainImageUrl = null;
        List<String> descImageUrls = new ArrayList<>();

        try {
            // 메인 이미지 업로드
            if (mainImage != null && !mainImage.isEmpty()) {
                mainImageUrl = upload(mainImage, mainDir);
            }

            // 설명 이미지들 업로드
            if (descriptionImages != null && !descriptionImages.isEmpty()) {
                for (MultipartFile image : descriptionImages) {
                    if (image != null && !image.isEmpty()) {
                        descImageUrls.add(upload(image, descDir));
                    }
                }
            }

            log.info("메인 이미지 업로드 여부: {}, 설명 이미지 {}개 업로드 완료", mainImageUrl != null, descImageUrls.size());

            return Pair.of(mainImageUrl, descImageUrls);

        } catch (IOException e) {
            throw new CustomException(ErrorCode.FILE_CONVERT_FAIL);
        }
    }

    public void delete(String imageUrl) {
        try {
            String key = extractKeyFromUrl(imageUrl); // 버킷 내 객체 키 추출
            amazonS3Client.deleteObject(bucket, key);
            log.info("[s3] 이미지 삭제 성공: {} ({})", key, imageUrl);
        } catch (Exception e) {
            log.warn("[s3] 이미지 삭제 실패: {}", imageUrl, e);
        }
    }

    // S3 URL에서 key 추출
    private String extractKeyFromUrl(String imageUrl) {
        URI uri = URI.create(imageUrl);
        // S3 도메인이 아니면 예외 발생
        if (!uri.getHost().contains("amazonaws.com")) {
            throw new CustomException(ErrorCode.FILE_DELETE_FAIL);
        }
        return uri.getPath().substring(1);
    }

    public String getDefaultProfileImageUrl() {
        return amazonS3Client.getUrl(bucket, "profileImages/default.png").toString();
    }
}
