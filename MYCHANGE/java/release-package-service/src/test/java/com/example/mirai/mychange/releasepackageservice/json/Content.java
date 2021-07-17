package com.example.mirai.projectname.releasepackageservice.json;

import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Content {
    protected DocumentContext documentContext;

    public Content(String content) {
        this.documentContext = JsonPath.parse(content);
    }

    static Date convertStringToDate(String dateString) {
        if (dateString == null)
            return null;
        try {
            return new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSX").parse(dateString);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }
}
