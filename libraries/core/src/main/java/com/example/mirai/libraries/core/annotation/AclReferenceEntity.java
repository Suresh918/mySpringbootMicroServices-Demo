package com.example.mirai.libraries.core.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.annotation.Documented;

/**
 * Describes AclReferenceEntity attribute at the class level
 * <p>Generally used for Entity classes.
 * <p>Holds the entity class that that need to be referred in entity-acl or property-acl.
 * <p>For example, If the current entity, does not have any case actions or restrictions on its own, and
 * if it depends on the state of another entity to define the restrictions/permissions, the other entity has to be
 * added as an AclReferenceEntity
 * <p>Should be used with a value. No default value.
 *
 * @author ptummala
 * @since 1.0.0
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE })
@Inherited
@Documented
public @interface AclReferenceEntity {
	Class value();
}
