package com.example.mirai.projectname.releasepackageservice.releasepackage.model;


import java.io.Serializable;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Embeddable;

import com.example.mirai.libraries.core.converter.UpperCaseConverter;
import com.example.mirai.libraries.core.model.ContextInterface;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReleasePackageContext implements Serializable, ContextInterface {


    @Convert(converter = UpperCaseConverter.class)
    private String type;
    private String contextId;

    @Column(length = 256)
    private String name;
    private String status;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ReleasePackageContext)) return false;
        ReleasePackageContext releasePackageContext = (ReleasePackageContext) o;
        return Objects.equals(getContextId(), releasePackageContext.getContextId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getContextId());
    }

}
