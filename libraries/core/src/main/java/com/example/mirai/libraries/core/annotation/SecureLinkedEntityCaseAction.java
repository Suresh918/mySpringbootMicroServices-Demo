package com.example.mirai.libraries.core.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.annotation.Documented;

/**
 * Describes SecureLinkedEntityCaseAction attribute on an individual method
 * <p>This annotation checks whether the logged in user is authorized to perform the given case action
 * <p>In the entity-acl, if the case action is not available in the section of current entity and is listed under the section of other entity classes,
 * to be specific, if the case action depends on the state of the other entities, the other entities need to be added in <b>links</b>.
 *
 * <p>From entity-acl file, It fetches the list of authorized case actions for the classes that are mentioned in links.
 * <p>If the given case action is in the list of authorized case actions, it will proceed with the execution of the current method
 * <p>Applying this annotation without any caseAction will not have any affect
 * @throws {@link com.example.mirai.libraries.core.exception.UnauthorizedException} if the case action is not found in authorized case actions
 * @author ptummala
 * @since 1.0.0
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Inherited
@Documented
public @interface SecureLinkedEntityCaseAction {
    String caseAction() default "[unassigned]";

    Class[] links() default {};
}
