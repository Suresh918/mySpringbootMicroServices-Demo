package com.example.mirai.libraries.core.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.annotation.Documented;

/**
 * Describes SecurePropertyMerge attribute on an individual method
 * <p>When applied on a method, it fetches the protected fields of the entity from property-acl file and checks if the field that is being updated is in the list of protected fields
 *
 * <p>Supported only if the first argument of the applied method is Long (entity Id) or BaseEntityInterface (entity) or AggregateInterface (aggregate)
 * and if the number of arguments are greater than two and the third argument is list of changed attributes
 * <p>If the arguments size is less than 2 or the third argument type is not list, applying this annotation will not have any affect.
 *
 * @author Harshit Kapoor
 * @throws {@link com.example.mirai.libraries.core.exception.UnauthorizedException} If the field is listed in protected fields
 * @since 1.0.0
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Inherited
@Documented
public @interface SecurePropertyMerge {
}
