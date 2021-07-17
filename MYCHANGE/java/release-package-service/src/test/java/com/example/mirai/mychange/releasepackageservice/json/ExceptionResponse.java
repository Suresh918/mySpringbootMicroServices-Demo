package com.example.mirai.projectname.releasepackageservice.json;

import java.util.Date;

public class ExceptionResponse extends Content {

    public ExceptionResponse(String content) {
        super(content);
    }

    public String getApplicationStatusCode() {
        return documentContext.read("application_status_code");
    }

    public String getError() {
        return documentContext.read("error");
    }

    public Integer getSeverity() {
        return documentContext.read("severity");
    }

    public String getMessage() {
        return documentContext.read("message");
    }

    public String getPath() {
        return documentContext.read("path");
    }

    public Date getTimestamp() {
        return convertStringToDate(documentContext.read("timestamp"));
    }

}
