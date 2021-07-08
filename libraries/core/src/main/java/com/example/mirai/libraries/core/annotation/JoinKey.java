package com.example.mirai.libraries.core.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.annotation.Documented;

/**
 * Describes JoinKey attribute at the Field Level
 * <p>This is generally used in view classes
 * <p>The Field, on which this annotation is applied, is the one that can be used to join the respective rows in entity and view.
 *
 * @author ptummala
 * @since 1.0.0
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.FIELD })
@Inherited
@Documented
public @interface JoinKey {
}
