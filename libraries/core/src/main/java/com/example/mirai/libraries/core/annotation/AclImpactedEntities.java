package com.example.mirai.libraries.core.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.annotation.Documented;

/**
 * Describes AclImpactedEntities attribute at the class level
 * <p>Can Only be applied to Entity classes.
 * <p>If the changes in current entity impacts the permissions/authorizations of another related entity.
 * All The Related entities need to specified in the value of the annotation
 *
 * <p>By adding this, it assures that any changes in the current entity, refreshes the abac roles related to the entity classes mentioned in the value
 *
 * <p>Should be used with a value. No default value.
 *
 * For Example: If Permissions of Entity A are affected by the changes in entity B, then entity A should be added in the value of AclImpactedEntities annotation on entity B
 * <pre>
 *{@code @}AclImpactedEntities({EntityB.class})
 * Class Entity A {
 *
 *}
 * </pre>
 * @author ptummala
 * @since 1.0.0
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Inherited
@Documented
public @interface AclImpactedEntities {
    Class[] value();
}
