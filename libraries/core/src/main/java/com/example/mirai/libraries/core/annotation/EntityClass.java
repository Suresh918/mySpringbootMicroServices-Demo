package com.example.mirai.libraries.core.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.annotation.Documented;

/**
 * Describes EntityClass attribute at the Field Level or at the class level
 * <p>At the class level, Generally used for Service classes. It describes the entity that this service operates on.
 * <p>At the Field level, Generally used in Case status/Audit Aggregate structures, to describe which entity the current audit/case status object related to.
 * <p>Should be used with a value. No default value.
 *
 * @author ptummala
 * @since 1.0.0
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE, ElementType.FIELD })
@Inherited
@Documented
public @interface EntityClass {
	Class value();
}
