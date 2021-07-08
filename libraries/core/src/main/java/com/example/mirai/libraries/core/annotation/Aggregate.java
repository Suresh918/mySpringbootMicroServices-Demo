package com.example.mirai.libraries.core.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Documented;

/**
 * Describes Aggregate attribute at the Field Level
 * <p>This denotes that the entity/field with this annotation is not an entity by itself, but rather contains entity fields or collection of entities in its structure
 * <p>Multiple fields in the class can be annotated with this annotation
 *
 * @author ptummala
 * @since 1.0.0
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.FIELD })
@Inherited
@Documented
public @interface Aggregate {
}
