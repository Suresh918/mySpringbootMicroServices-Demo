package com.example.mirai.libraries.jms;

import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.jms.annotation.JmsListenerConfigurer;
import org.springframework.jms.config.JmsListenerEndpointRegistrar;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnExpression("!T(org.springframework.util.StringUtils).isEmpty('${mirai.libraries.jms.url:}')")
public class JmsListenerConfigurerImpl implements JmsListenerConfigurer {
	private JmsListenerEndpointRegistrar jmsListenerEndpointRegistrar;

	@EventListener(ApplicationStartedEvent.class)
	public void start() {
		jmsListenerEndpointRegistrar.getEndpointRegistry().start();
	}


	@Override
	public void configureJmsListeners(JmsListenerEndpointRegistrar jmsListenerEndpointRegistrar) {
		this.jmsListenerEndpointRegistrar = jmsListenerEndpointRegistrar;
	}
}
