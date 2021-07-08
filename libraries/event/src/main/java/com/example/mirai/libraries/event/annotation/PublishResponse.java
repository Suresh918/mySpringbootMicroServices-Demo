package com.example.mirai.libraries.event.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.example.mirai.libraries.event.DefaultEventBuilder;

@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE, ElementType.METHOD })
@Inherited
public @interface PublishResponse {
	boolean isAsync() default false;

	String destination();

	String eventType();

	String eventEntity() default "[]";

	Class eventBuilder() default DefaultEventBuilder.class;

	Class responseClass() default Class.class;
}
