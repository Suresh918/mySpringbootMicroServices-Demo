package com.example.mirai.libraries.security.abac.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@Inherited
public @interface AbacSubject {
	/**
	 * Field of entity/element class that will hold roles value. Sample values that can be held in the roles are CB, CCB, CS1, MDO, D&EPL.
	 */
	String role() default "[unassigned]";

	String principal() default "[unassigned]";
}
