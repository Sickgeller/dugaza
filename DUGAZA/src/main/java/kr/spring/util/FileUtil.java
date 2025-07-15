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
	//업로드 상대경로
	private static final String UPLOAD_PATH = "/assets/upload";
	
	//파일 업로드 처리
	public static String createFile(
			HttpServletRequest request,
			MultipartFile file)
			throws IllegalStateException, IOException {
		// 컨텍스트 경로상의 절대경로 구하기
		String path = new File("target/classes/static/assets/upload").getAbsolutePath();
		File uploadDir = new File(path);
		if (!uploadDir.exists()) {
			uploadDir.mkdirs(); // 폴더가 없으면 생성
		}
		String filename = null;
		if(file != null && !file.isEmpty()) {
			// 파일명이 중복되지 않도록 파일명 변경
			filename =
					UUID.randomUUID()
							+ file.getOriginalFilename()
							.substring(file.getOriginalFilename()
									.lastIndexOf("."));
			file.transferTo(new File(path + "/" + filename));
		}
		return filename;
	}
	
	//파일 업로드 처리 (HttpServletRequest 없이)
	public static String uploadFile(MultipartFile file, String subDirectory) 
			throws IllegalStateException, IOException {
		// 업로드 경로 설정
		String basePath = new File("target/classes/static/assets/upload").getAbsolutePath();
		String uploadPath = basePath + "/" + subDirectory;
		
		File uploadDir = new File(uploadPath);
		if (!uploadDir.exists()) {
			uploadDir.mkdirs(); // 폴더가 없으면 생성
		}
		
		String filename = null;
		if(file != null && !file.isEmpty()) {
			// 파일명이 중복되지 않도록 파일명 변경
			filename = UUID.randomUUID().toString() + 
					file.getOriginalFilename().substring(
							file.getOriginalFilename().lastIndexOf("."));
			file.transferTo(new File(uploadPath + "/" + filename));
		}
		return filename;
	}
	//파일 삭제
	public static void removeFile(
			                HttpServletRequest request,
			                        String filename) {
		if(filename!=null) {
			//컨텍스트 경로상의 절대경로 구하기
			String path = request.getSession()
					             .getServletContext()
					             .getRealPath(UPLOAD_PATH);
			File file = new File(path + "/" + filename);
			if(file.exists()) file.delete();
		}
	}
	
	//지정한 경로의 파일을 읽어들여 byte 배열로 반환
	public static byte[] getBytes(String path) {
		FileInputStream fis = null;
		byte[] readbyte = null;
		try {
			fis = new FileInputStream(path);
			readbyte = new byte[fis.available()];
			fis.read(readbyte);
		}catch(Exception e) {
			log.error("<<파일 변환 오류>> : {}",e.toString());
		}finally {
			if(fis!=null)try {fis.close();}catch(IOException e) {}
		}
		return readbyte;
	}
	
	
}











