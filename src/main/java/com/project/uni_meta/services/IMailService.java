package com.project.uni_meta.services;

import com.project.uni_meta.dtos.DataMailDTO;
import jakarta.mail.MessagingException;

public interface IMailService {
    void sendHtmlMail(DataMailDTO dataMail, String templateName) throws MessagingException;
}
