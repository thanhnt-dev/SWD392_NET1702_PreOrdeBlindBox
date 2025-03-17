package com.swd392.preOrderBlindBox.service.service;

import com.swd392.preOrderBlindBox.dto.OtpMailDTO;

public interface MailService {
  void sendMail(OtpMailDTO otpMailDTO);
}
