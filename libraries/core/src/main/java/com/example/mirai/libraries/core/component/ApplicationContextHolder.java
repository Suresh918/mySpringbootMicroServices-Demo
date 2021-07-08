package com.example.mirai.libraries.core.component;

import com.example.mirai.libraries.core.service.ServiceInterface;
import org.springframework.beans.BeansException;
import org.springframework.context.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.support.Repositories;
import org.springframework.stereotype.Component;

/**
 * Implements {@link ApplicationContextAware} interface
 * <p>Holds the {@link ApplicationContext} that the object runs in.
 * <p>Provides access to set of collaborating beans
 *
 * @author ptummala
 * @since 1.0.0
 */
@Component
public class ApplicationContextHolder implements ApplicationContextAware {
    private static ApplicationContext applicationContext;

    private static Repositories repositories;

    public static ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    /**
     * Set the ApplicationContext that this object runs in.
     * Normally this call will be used to initialize the object.
     * <p>Invoked after population of normal bean properties but before an init callback such
     * as {@link org.springframework.beans.factory.InitializingBean#afterPropertiesSet()}
     * or a custom init-method. Invoked after {@link ResourceLoaderAware#setResourceLoader},
     * {@link ApplicationEventPublisherAware#setApplicationEventPublisher} and
     * {@link MessageSourceAware}, if applicable.
     *
     * @param applicationContext the ApplicationContext object to be used by this object
     * @throws ApplicationContextException in case of context initialization errors
     * @throws BeansException              if thrown by application context methods
     * @see org.springframework.beans.factory.BeanInitializationException
     */
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) {
        ApplicationContextHolder.applicationContext = applicationContext;
        repositories = new Repositories(applicationContext);
    }

    public static Repositories getRepositories() {
        return repositories;
    }

    public static JpaRepository getJpaRepository(Class c) {
        return (JpaRepository) repositories.getRepositoryFor(c).get();
    }

    public static ServiceInterface getService(Class serviceClass) {
        return (ServiceInterface) applicationContext.getBean(serviceClass);
    }

    public static Object getBean(Class beanClass) {
        return applicationContext.getBean(beanClass);
    }
}
