package com.example.mirai.projectname.releasepackageservice.releasepackage.model.aggregate;

import com.example.mirai.libraries.core.annotation.Aggregate;
import com.example.mirai.libraries.core.annotation.AggregateRoot;
import com.example.mirai.libraries.core.model.AggregateInterface;
import com.example.mirai.projectname.releasepackageservice.releasepackage.model.ReleasePackage;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Immutable;

@Immutable
@Getter
@Setter
public class ReleasePackageAggregate implements AggregateInterface {

    @AggregateRoot
    private ReleasePackage releasePackage;

    @Aggregate
    private ReleasePackageMyTeamDetailsAggregate myTeamDetails;

}
