package com.example.mirai.libraries.event;

import com.example.mirai.libraries.core.annotation.ServiceClass;
import com.example.mirai.libraries.core.component.ApplicationContextHolder;
import com.example.mirai.libraries.core.exception.InternalAssertionException;
import com.example.mirai.libraries.core.model.AggregateInterface;
import com.example.mirai.libraries.core.model.BaseEntityInterface;
import com.example.mirai.libraries.core.service.ServiceInterface;
import lombok.extern.slf4j.Slf4j;

import org.springframework.core.annotation.AnnotationUtils;

@Slf4j
public class AggregateEventBuilder implements EventBuilderInterface {
	private Class responseClass;

	@Override
	public void setResponseClass(Class responseClass) {
		this.responseClass = responseClass;
	}

	@Override
	public Object translateResponse(Object obj) {
		if (!(obj instanceof BaseEntityInterface)) {
			throw new InternalAssertionException("Not Able To Get Aggregate, Object Not Instance Of Base Entity Interface");
		}
		if (!(AggregateInterface.class.isAssignableFrom(responseClass))) {
			throw new InternalAssertionException("Not Able To Get Aggregate, Entity Class Not Aggregate Interface");
		}
		BaseEntityInterface entity = (BaseEntityInterface) obj;
		AggregateInterface aggregate = getServiceInstance(entity).getAggregate(entity.getId(), this.responseClass);
		return aggregate;
	}


	private ServiceInterface getServiceInstance(Object object) {
		ServiceClass serviceClassAnnotation = AnnotationUtils.findAnnotation(object.getClass(), ServiceClass.class);
		Class serviceClass = serviceClassAnnotation.value();
		ServiceInterface serviceClassInstance = (ServiceInterface) ApplicationContextHolder.getApplicationContext().getBean(serviceClass);
		return serviceClassInstance;
	}
}
