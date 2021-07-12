package com.example.mirai.projectname.changerequestservice.changerequest.model;

import com.example.mirai.libraries.core.converter.UpperCaseConverter;
import com.example.mirai.libraries.core.model.ContextInterface;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ChangeRequestContext implements ContextInterface, Serializable {
    @Convert(converter = UpperCaseConverter.class)
    private String type;
    private String contextId;

    @Column(length = 256)
    private String name;
    private String status;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ChangeRequestContext)) return false;
        ChangeRequestContext changeRequestContext = (ChangeRequestContext) o;
        return Objects.equals(getContextId(), changeRequestContext.getContextId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getContextId());
    }

}
