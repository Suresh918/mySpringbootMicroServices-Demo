package com.example.mirai.libraries.jackson;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.core.json.JsonWriteFeature;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.SerializationFeature;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;

@Configuration
@EnableAutoConfiguration
public class HttpMessageConverterConfiguration {
	@Bean
	@Order(0)
	public MappingJackson2HttpMessageConverter actuatorJacksonHttpMessageConverter(Jackson2ObjectMapperBuilder builder) {
		MappingJackson2HttpMessageConverter jsonConverter = new MappingJackson2HttpMessageConverter(builder.build()) {
			@Override
			protected boolean canWrite(MediaType mediaType) {
				//only exact matches
				if (mediaType == null) {
					return false;
				}
				return super.canWrite(mediaType);
			}
		};
		jsonConverter.setSupportedMediaTypes(MediaType.parseMediaTypes(List.of(
				"application/vnd.spring-boot.actuator.v2+json",
				"application/vnd.spring-boot.actuator.v3+json"
		)));
		return jsonConverter;
	}

	@Bean
	@Order()
	public MappingJackson2HttpMessageConverter defaultJackson2HttpMessageConverter(ObjectMapper objectMapper) {
		MappingJackson2HttpMessageConverter jsonConverter = new MappingJackson2HttpMessageConverter();
		objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, true);
		objectMapper.configure(JsonWriteFeature.ESCAPE_NON_ASCII.mappedFeature(), true);
		objectMapper.setVisibility(objectMapper.getSerializationConfig().getDefaultVisibilityChecker()
				.withFieldVisibility(JsonAutoDetect.Visibility.ANY)
				.withGetterVisibility(JsonAutoDetect.Visibility.NONE)
				.withSetterVisibility(JsonAutoDetect.Visibility.NONE)
				.withCreatorVisibility(JsonAutoDetect.Visibility.NONE));
		objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
		objectMapper.setPropertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE);
		jsonConverter.setObjectMapper(objectMapper);
		return jsonConverter;
	}
}
