package kr.spring.member.email;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.messaging.MessagingException;
import org.springframework.stereotype.Component;

import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class EmailSender {
	@Autowired
	private JavaMailSender mailSender;
	
	public void sendEmail(Email email) throws Exception{
		MimeMessage msg = mailSender.createMimeMessage();
		
		try {
			msg.setFrom();
			msg.setSubject(email.getSubject());
			msg.setText(email.getContent());
			msg.setRecipients(MimeMessage.RecipientType.TO, InternetAddress.parse(email.getReceiver()));
		} catch (MessagingException e) {
			log.error(e.toString());
		}
		mailSender.send(msg);
	}
}
