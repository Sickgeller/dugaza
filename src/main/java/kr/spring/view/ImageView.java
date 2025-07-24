package kr.spring.view;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.view.AbstractView;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class ImageView extends AbstractView{

	@Override
	protected void renderMergedOutputModel(Map<String, Object> model, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		
		byte[] file = (byte[])model.get("imageFile");
		String filename = (String)model.get("filename");
		
		String ext = filename.substring(
				         filename.lastIndexOf("."));
		if(ext.equalsIgnoreCase(".gif")) {
			ext = "image/gif";
		}else if(ext.equalsIgnoreCase(".png")) {
			ext = "image/png";
		}else {
			ext = "image/jpeg";
		}
		
		//컨텐트 타입 지정
		response.setContentType(ext);
		//컨텐트의 용량 지정
		response.setContentLength(file.length);
		//파일명 구하기
		filename = new String(
				  filename.getBytes("utf-8"),"iso-8859-1");
		//HTTP 응답 메시지 헤더 셋팅
		response.setHeader("Content-Disposition", 
				"attachment; filename=\"" + filename +"\";");
		response.setHeader(
				      "Content-Transfer-Encoding", "binary");
		
		OutputStream out = response.getOutputStream();
		
		InputStream input = 
				       new ByteArrayInputStream(file);
		IOUtils.copy(input, out);
		out.flush();
		out.close();
		input.close();		
	}

}





