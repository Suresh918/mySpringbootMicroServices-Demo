package com.example.mirai.libraries.core.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.annotation.Documented;

/**
 * Describes AclImpactedByEntities attribute at the class level
 * <p>Generally used for Entity classes.
 * <p>If the acl of current entity is impacted by any changes in the other entity, The Other entity has to be added in the list of values
 *
 * <p>By adding this, it assures that any changes in the list of mentioned entities, refreshes the abac roles related to the current entity
 *
 * <p>Should be used with a value. No default value.
 *
 * @author ptummala
 * @since 1.0.0
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Inherited
@Documented
public @interface AclImpactedByEntities {
    Class[] value();
}
