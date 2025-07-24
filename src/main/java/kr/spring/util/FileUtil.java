package kr.spring.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.UUID;

import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class FileUtil {
    // 업로드 상대경로 (정적 리소스 기준)
    private static final String STATIC_UPLOAD_PATH = "src/main/resources/static/assets/upload";
    private static final String URL_UPLOAD_PATH = "/assets/upload";
    
    // 톰캣 배포 환경용 절대 경로
    private static final String TOMCAT_UPLOAD_PATH = "C:/DUGAZA/upload";
    /**
     * 환경에 따른 업로드 경로 결정
     * @return 업로드 경로
     */
    private static String getUploadPath() {
        String uploadPath = System.getProperty("user.dir") + File.separator + "src" + File.separator + "main" + File.separator + "resources" + File.separator + "static" + File.separator + "assets" + File.separator + "upload";
        File uploadDir = new File(uploadPath);
        if (!uploadDir.exists()) {
            boolean created = uploadDir.mkdirs();
            log.info("[getUploadPath] 업로드 폴더 생성: {} => {}", uploadPath, created);
        }
        log.info("[getUploadPath] 업로드 경로: {}", uploadPath);
        return uploadPath;
    }

    /**
     * 파일 업로드 (HttpServletRequest 사용)
     * @param request HttpServletRequest
     * @param file MultipartFile
     * @return 저장된 파일명 (UUID+확장자)
     */
    public static String createFile(HttpServletRequest request, MultipartFile file)
            throws IllegalStateException, IOException {
        String uploadPath = getUploadPath();
        File uploadDir = new File(uploadPath);
        if (!uploadDir.exists()) {
            boolean created = uploadDir.mkdirs();
            log.info("[createFile] 업로드 폴더 생성: {} => {}", uploadPath, created);
        }
        String filename = null;
        if (file != null && !file.isEmpty()) {
            String originalFilename = file.getOriginalFilename();
            String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            filename = UUID.randomUUID().toString() + extension;
            File destFile = new File(uploadPath + File.separator + filename);
            file.transferTo(destFile);
            log.info("[createFile] 파일 업로드 완료: {} ({} bytes)", destFile.getAbsolutePath(), destFile.length());
        } else {
            log.warn("[createFile] 업로드할 파일이 없거나 비어있음");
        }
        return filename;
    }

    /**
     * 파일 업로드 (서브디렉토리 지정)
     * @param file MultipartFile
     * @param subDirectory 하위폴더명 (예: car, profile 등)
     * @return 저장된 파일명 (UUID+확장자)
     */
    public static String uploadFile(MultipartFile file, String subDirectory)
            throws IllegalStateException, IOException {
        if (file == null || file.isEmpty()) {
            log.warn("[uploadFile] 업로드할 파일이 없거나 비어있음");
            return null;
        }
        String uploadPath = getUploadPath() + File.separator + subDirectory;
        File uploadDir = new File(uploadPath);
        if (!uploadDir.exists()) {
            boolean created = uploadDir.mkdirs();
            log.info("[uploadFile] 업로드 폴더 생성: {} => {}", uploadPath, created);
        }
        String originalFilename = file.getOriginalFilename();
        String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        String filename = UUID.randomUUID().toString() + extension;
        File destFile = new File(uploadPath + File.separator + filename);
        file.transferTo(destFile);
        log.info("[uploadFile] 파일 업로드 완료: {} ({} bytes)", destFile.getAbsolutePath(), destFile.length());
        return filename;
    }

    /**
     * 파일 삭제 (HttpServletRequest 사용)
     * @param request HttpServletRequest
     * @param filename 파일명
     */
    public static void removeFile(HttpServletRequest request, String filename) {
        if (filename != null) {
            String path = getUploadPath();
            File file = new File(path + File.separator + filename);
            if (file.exists()) {
                boolean deleted = file.delete();
                log.info("[removeFile] 파일 삭제: {} => {}", file.getAbsolutePath(), deleted);
            } else {
                log.warn("[removeFile] 파일이 존재하지 않음: {}", file.getAbsolutePath());
            }
        }
    }

    /**
     * 파일 삭제 (서브디렉토리 지정)
     * @param subDirectory 하위폴더명
     * @param filename 파일명
     */
    public static void removeFile(String subDirectory, String filename) {
        if (filename != null) {
            String path = getUploadPath() + File.separator + subDirectory;
            File file = new File(path + File.separator + filename);
            if (file.exists()) {
                boolean deleted = file.delete();
                log.info("[removeFile] 파일 삭제: {} => {}", file.getAbsolutePath(), deleted);
            } else {
                log.warn("[removeFile] 파일이 존재하지 않음: {}", file.getAbsolutePath());
            }
        }
    }

    /**
     * 파일을 byte 배열로 반환
     * @param path 파일 전체 경로
     * @return byte[]
     */
    public static byte[] getBytes(String path) {
        FileInputStream fis = null;
        byte[] readbyte = null;
        try {
            fis = new FileInputStream(path);
            readbyte = new byte[fis.available()];
            fis.read(readbyte);
            log.info("[getBytes] 파일 읽기 성공: {} ({} bytes)", path, readbyte.length);
        } catch (Exception e) {
            log.error("[getBytes] 파일 변환 오류: {}", e.toString());
        } finally {
            if (fis != null) try { fis.close(); } catch (IOException e) {}
        }
        return readbyte;
    }
}











