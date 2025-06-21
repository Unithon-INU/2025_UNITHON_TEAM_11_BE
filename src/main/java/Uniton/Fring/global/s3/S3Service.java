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

    public Pair<String, List<String>> uploadMainAndStepImages(List<MultipartFile> images, String mainDir, String stepDir) {
        String mainImageUrl = null;
        List<String> stepImageUrls = new ArrayList<>();

        if (images != null && !images.isEmpty()) {
            try {
                mainImageUrl = upload(images.get(0), mainDir);

                for (int i = 1; i < images.size(); i++) {
                    String stepUrl = upload(images.get(i), stepDir);
                    stepImageUrls.add(stepUrl);
                }

            } catch (IOException e) {
                throw new CustomException(ErrorCode.FILE_CONVERT_FAIL);
            }
        }

        return Pair.of(mainImageUrl, stepImageUrls);
    }
}
