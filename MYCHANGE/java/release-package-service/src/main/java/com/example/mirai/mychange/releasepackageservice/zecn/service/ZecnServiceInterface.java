package com.example.mirai.projectname.releasepackageservice.zecn.service;

public interface ZecnServiceInterface {
    void processAndSendMessage(String ecnId,String title,String state);
    void sendMessage(String message);
}
