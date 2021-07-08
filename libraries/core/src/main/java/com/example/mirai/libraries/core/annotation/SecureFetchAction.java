package com.example.mirai.libraries.core.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Documented;

/**
 * Describes SecureFetchAction attribute at the Method Level
 *
 * <p>Used for methods that need to check on FETCH case action
 *
 * <p>Should be used together with {@link SecureFetchCriteria} or {@link SecureFetchViewCriteria} to apply fetch restrictions
 *
 * <p>First, Checks whether the user is authorized for "FETCH" case action or not.
 * <p>If user is authorized to fetch, evaluates the fetch_rule and concatenate(using AND) it with the value of {@link SecureFetchCriteria} annotated argument
 * <p>Also evaluates the fetch_view_rule and concatenate(using AND) it with the value of {@link SecureFetchViewCriteria} annotated argument
 *
 * @throws {@link com.example.mirai.libraries.core.exception.UnauthorizedException}, If user is not authorized to FETCH
 *
 * @author ptummala
 * @since 1.0.0
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Inherited
@Documented
public @interface SecureFetchAction {
}
