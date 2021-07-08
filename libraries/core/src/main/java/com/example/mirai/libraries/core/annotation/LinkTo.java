package com.example.mirai.libraries.core.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Documented;

/**
 * Describes LinkTo attribute at the Field Level
 * <p>This is used in aggregate classes- means for the classes that implement AggregateInterface
 * <p>Describes the field or list of fields it has relationship with(OneToOne, OneToMany, ManyToOne, ManyToMany)
 * <p>The aggregate creation and reading aggregate logic relies on the value of this annotation to read the specific entity while building teh aggregate
 * <p>Should provide value when using this annotation, Accepts list of classes, No default value.
 *
 * @author ptummala
 * @since 1.0.0
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
@Inherited
@Documented
public @interface LinkTo {
    Class[] value();
}
