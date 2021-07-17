package com.example.mirai.projectname.releasepackageservice.releasepackage.model.dto;

import java.io.Serializable;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AutomaticClosureError implements Serializable {
    private String releasePackageNumber;
    private String ecnId;
    private String errorMessage;
    private List<String> recipientMailIds;
}
