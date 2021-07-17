package com.example.mirai.projectname.reviewservice.reviewentry.model;

import com.example.mirai.projectname.reviewservice.shared.converter.ContextTypeConverter;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Convert;
import javax.persistence.Embeddable;
import java.io.Serializable;

@Embeddable
@Getter
@Setter
@EqualsAndHashCode
public class ReviewEntryContext implements Serializable {

    @Convert(converter = ContextTypeConverter.class)
    private String type;
    private String contextId;
    private String name;
}
