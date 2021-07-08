package com.example.mirai.libraries.backgroundable.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Inherited
public @interface Backgroundable {
	String name();                          //name of the background task

	String idGenerator();                    //spel to generate part of job id

	String parentIdExtractor();              //spel to extract parent id

	String parentName();                    //name of parent entity

	String context() default "[response]";   //spel to extract context information

	String title() default "";   			//spel to extract title

	long timeout() default Long.MAX_VALUE;  //waiting time for thread to return, after which thread will be run in background and consumer will get an exception

	Class[] exceptions() default {};        //list of exceptions to ignore
}
