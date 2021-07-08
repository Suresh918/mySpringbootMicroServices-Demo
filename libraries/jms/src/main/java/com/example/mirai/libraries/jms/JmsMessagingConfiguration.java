package com.example.mirai.libraries.jms;

import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
/*
import com.tibco.tibjms.TibjmsConnectionFactory;*/

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.autoconfigure.jms.DefaultJmsListenerContainerFactoryConfigurer;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.config.JmsListenerContainerFactory;
import org.springframework.jms.connection.CachingConnectionFactory;
import org.springframework.jms.core.JmsTemplate;

@EnableJms
@Configuration
@ConfigurationPropertiesScan
@ConditionalOnExpression("!T(org.springframework.util.StringUtils).isEmpty('${mirai.libraries.jms.url:}')")
public class JmsMessagingConfiguration {
	JmsConfigurationProperties jmsConfigurationProperties;

	@Autowired
	public JmsMessagingConfiguration(JmsConfigurationProperties jmsConfigurationProperties) {
		this.jmsConfigurationProperties = jmsConfigurationProperties;
	}

	@Bean
	@Primary
	public ConnectionFactory connectionFactory() throws JMSException {
		/*TibjmsConnectionFactory connectionFactory = new TibjmsConnectionFactory();
		connectionFactory.setServerUrl(jmsConfigurationProperties.getUrl());
		connectionFactory.setUserName(jmsConfigurationProperties.getUsername());
		connectionFactory.setUserPassword(jmsConfigurationProperties.getPassword());
		connectionFactory.setConnAttemptCount(jmsConfigurationProperties.getConnectAttemptCount());
		connectionFactory.setConnAttemptDelay(jmsConfigurationProperties.getConnectAttemptDelay());
		connectionFactory.setConnAttemptTimeout(jmsConfigurationProperties.getConnectAttemptTimeout());
		connectionFactory.setReconnAttemptCount(jmsConfigurationProperties.getReconnectAttemptCount());
		connectionFactory.setReconnAttemptDelay(jmsConfigurationProperties.getReconnectAttemptDelay());
		connectionFactory.setReconnAttemptTimeout(jmsConfigurationProperties.getReconnectAttemptTimeout());
		connectionFactory.createConnection();

		CachingConnectionFactory cachingConnectionFactory = new CachingConnectionFactory();
		cachingConnectionFactory.setSessionCacheSize(jmsConfigurationProperties.getSessionCacheSize());
		cachingConnectionFactory.setTargetConnectionFactory(connectionFactory);

		return cachingConnectionFactory;*/
		return null;
	}

	@Bean
	public JmsTemplate jmsTemplate(ConnectionFactory connectionFactory) {
		JmsTemplate jmsTemplate = new JmsTemplate(connectionFactory);
		jmsTemplate.setSessionTransacted(jmsConfigurationProperties.getTransactedSession());
		jmsTemplate.setPubSubDomain(true);
		jmsTemplate.setExplicitQosEnabled(jmsConfigurationProperties.getExplicitQosEnabled());
		return jmsTemplate;
	}

	@Bean(name = "pointToPointJmsTemplate")
	public JmsTemplate pointToPointJmsTemplate(ConnectionFactory connectionFactory) {
		JmsTemplate jmsTemplate = new JmsTemplate(connectionFactory);
		jmsTemplate.setSessionTransacted(jmsConfigurationProperties.getTransactedSession());
		jmsTemplate.setPubSubDomain(false);
		jmsTemplate.setExplicitQosEnabled(jmsConfigurationProperties.getExplicitQosEnabled());
		return jmsTemplate;
	}

	@Bean(name = "jmsListenerContainerFactory")
	public JmsListenerContainerFactory<?> jmsListenerContainerFactory(ConnectionFactory connectionFactory,
			DefaultJmsListenerContainerFactoryConfigurer configurer) {
		DefaultJmsListenerContainerFactory jmsListenerContainerFactory = new DefaultJmsListenerContainerFactory();
		configurer.configure(jmsListenerContainerFactory, connectionFactory);
		jmsListenerContainerFactory.setSessionAcknowledgeMode(jmsConfigurationProperties.getAcknowledgeMode());
		return jmsListenerContainerFactory;
	}
}
