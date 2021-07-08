package com.example.mirai.libraries.core.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Documented;

/**
 * Describes AggregateRoot attribute at the Field Level
 * <p>The Root element/starting point in the aggregate class has to be marked with this annotation
 * <p>This denotes that the entity/field with this annotation is the lead to create or read the aggregate
 * <p><b>Only one</b> field in the class should be annotated with this annotation.
 *
 * @author ptummala
 * @since 1.0.0
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.FIELD })
@Inherited
@Documented
public @interface AggregateRoot {
}
