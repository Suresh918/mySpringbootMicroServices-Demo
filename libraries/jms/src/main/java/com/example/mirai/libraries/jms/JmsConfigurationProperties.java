package com.example.mirai.libraries.jms;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

import lombok.Data;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

@Configuration
@ConfigurationProperties(prefix = "mirai.libraries.jms")
@Data
@Validated
public class JmsConfigurationProperties {

	/**
	 * JMS server URL, can accept comma separated FT URL using following format "tcp://HOST-1:PORT, tcp://HOST-2:PORT"
	 */
	private String url;

	/**
	 * Username used to authenticate connections to the JMS server.
	 */
	private String username;

	/**
	 * Password used to authenticate connections to the JMS server.
	 */
	private String password;

	/**
	 * A client program attempts to connect to its server (or in fault-tolerant configurations, it iterates through its URL list) until it establishes its first connection to an JMS server. This property determines the maximum number of iterations.
	 */
	private Integer connectAttemptCount = 20;

	/**
	 * When attempting a first connection, the client sleeps for this interval (in milliseconds) between attempts to connect to its server (or in fault-tolerant configurations, iterations through its URL list).
	 */
	private Integer connectAttemptDelay = 1000;

	/**
	 * When attempting to connect to the EMS server, you can set this connection timeout period to abort the connection attempt after a specified period of time (in milliseconds).
	 */
	private Integer connectAttemptTimeout = 1000;

	/**
	 * After losing its server connection, a client program configured with more than one server URL attempts to reconnect, iterating through its URL list until it re-establishes a connection with an EMS server. This property determines the maximum number of iterations.
	 */
	private Integer reconnectAttemptCount = 150;

	/**
	 * When attempting to reconnect, the client sleeps for this interval (in milliseconds) between iterations through its URL list.
	 */
	private Integer reconnectAttemptDelay = 6000;

	/**
	 * When attempting to reconnect to the EMS server, you can set this connection timeout period to abort the connection attempt after a specified period of time (in milliseconds).
	 */
	private Integer reconnectAttemptTimeout = 1000;

	/**
	 * Specify the desired size for the JMS Session cache (per JMS Session type).
	 */
	private Integer sessionCacheSize = 1;

	/**
	 * 1(AUTO): Messages sent or received from the session are automatically acknowledged.
	 * 2(CLIENT): Messages are acknowledged once the message listener implementation has called Message.acknowledge().
	 * 3(DUPS_OK): Similar to auto acknowledgment except that said acknowledgment is lazy.
	 */
	@Min(1)
	@Max(3)
	private Integer acknowledgeMode = 1;

	/**
	 * TODO: Doesn't seem to be used, check with Harshit before removing.
	 */
	private Boolean transactedSession = false;

	/**
	 * TODO: Doesn't seem to be used, check with Harshit before removing.
	 */
	private Boolean explicitQosEnabled = true;
}
