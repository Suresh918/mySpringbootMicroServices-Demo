package com.example.mirai.libraries.core.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.annotation.Documented;

/**
 * Describes ViewMapper attribute at the constructor level
 * <p>Generally used for constructors in View classes.
 * <p>This is to map the columns in the view table with the fields in the view class, while fetching the data from views.
 *
 * @author ptummala
 * @since 1.0.0
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.CONSTRUCTOR)
@Inherited
@Documented
public @interface ViewMapper {

}
