package com.example.mirai.libraries.core.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.annotation.Documented;

/**
 * Describes SecurePropertyRead attribute on an individual method
 * <p>When applied on a method, it looks for private fields related to the current Entity,
 * <p>If the private fields are not maintained for entity class and if it has to look for private fields of another entity,
 * the related entity class has to be specified in {@link AclReferenceEntity} annotation
 * <p>Supported return types - BaseEntityInterface, BaseEntityList, Collection of BaseEntityInterface, AggregateInterface
 * <p>If the return type is {@link com.example.mirai.libraries.core.model.BaseEntityInterface}, it nullifies those values of fields which are marked as private in acl file
 * <p>If the return type is {@link com.example.mirai.libraries.core.model.BaseEntityList}, it gets the results of BaseEntityList, which is list of entities,
 * and nullifies the values of fields which are marked as private in acl file
 * <p>If the return type is Collection of entities, it loops through entities list and nullify the private fields
 * <p>If the return type is {@link com.example.mirai.libraries.core.model.AggregateInterface}, it iterates through the aggregate structure, and nullify the private fields
 *
 * @author ptummala
 * @since 1.0.0
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Inherited
@Documented
public @interface SecurePropertyRead {
}
