package com.example.mirai.projectname.reviewservice.review.model;

import com.example.mirai.projectname.reviewservice.shared.converter.ContextTypeConverter;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Convert;
import javax.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
@Getter
@Setter
public class ReviewContext implements Serializable {
    @Convert(converter = ContextTypeConverter.class)
    private String type;
    private String contextId;
    private String name;
    private String status;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ReviewContext)) return false;
        ReviewContext reviewContext = (ReviewContext) o;
        return Objects.equals(getContextId(), reviewContext.getContextId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getContextId());
    }

}
