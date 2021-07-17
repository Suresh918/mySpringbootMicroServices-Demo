package com.example.mirai.projectname.releasepackageservice.releasepackage.model.aggregate;

import java.util.List;

import com.example.mirai.libraries.core.model.AggregateInterface;
import com.example.mirai.projectname.releasepackageservice.releasepackage.model.dto.AutomaticClosureError;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Immutable;

@Immutable
@Getter
@Setter
public class AutomaticClosureAggregate implements AggregateInterface {

    private List<AutomaticClosureError> automaticClosureErrors;
}
