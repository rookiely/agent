package com.yang.server.service;

public interface MailService {

    void sendSimpleMail(String to, String subject, String content);
}
