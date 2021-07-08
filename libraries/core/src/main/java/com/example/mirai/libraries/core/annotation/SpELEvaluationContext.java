package com.example.mirai.libraries.core.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.annotation.Documented;

/**
 * Describes SpELEvaluationContext attribute at the class level
 * <p>Generally used for Entity classes. The value is the class against which the methods in acl will be evaluated.
 * <p>Should be used with a value. No default value.
 *
 * @author ptummala
 * @since 1.0.0
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Inherited
@Documented
public @interface SpELEvaluationContext {
    Class value();
}
