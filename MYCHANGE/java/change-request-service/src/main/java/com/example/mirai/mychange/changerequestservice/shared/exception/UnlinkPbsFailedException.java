package com.example.mirai.projectname.changerequestservice.shared.exception;

import com.example.mirai.libraries.cerberus.shared.exception.CerberusException;
import lombok.Getter;

@Getter
public class UnlinkPbsFailedException extends RuntimeException {
    private static final long serialVersionUID = 1L;
    private String message;
    private Integer status;
    public UnlinkPbsFailedException(CerberusException e) {
        this.message = e.getMessage();
        this.status = e.getStatus();
    }
}
