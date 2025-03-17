package com.swd392.preOrderBlindBox.service.serviceimpl;

import com.swd392.preOrderBlindBox.dto.OtpMailDTO;
import com.swd392.preOrderBlindBox.service.service.MailService;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MailServiceImpl implements MailService {

  private final JavaMailSender javaMailSender;

  @Override
  public void sendMail(OtpMailDTO otpMailDTO) {
    String subject = "OTP For Reset Password: ";
    SimpleMailMessage mailMessage = new SimpleMailMessage();
    mailMessage.setTo(otpMailDTO.getReceiverMail());
    mailMessage.setSubject(subject);
    mailMessage.setText(String.format("Hello! This is your OTP code: %s", otpMailDTO.getOtpCode()));
    javaMailSender.send(mailMessage);
  }
}
