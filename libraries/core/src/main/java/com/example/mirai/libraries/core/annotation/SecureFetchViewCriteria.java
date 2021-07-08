package com.example.mirai.libraries.core.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Documented;

/**
 * Describes SecureFetchViewCriteria attribute at the Parameter Level
 * <p>Used for methods that need to check on FETCH case action
 * <p>should be used together with {@link SecureFetchAction}
 * <p>Used to apply the fetch restrictions on view
 * <p>If user is authorized to fetch, evaluates the fetch_view_rule and concatenate(using AND) it with the value of {@link SecureFetchViewCriteria} annotated argument
 *
 * @author ptummala
 * @since 1.0.0
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
@Inherited
@Documented
public @interface SecureFetchViewCriteria {
}
