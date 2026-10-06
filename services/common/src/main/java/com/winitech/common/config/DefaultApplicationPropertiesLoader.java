package com.winitech.common.config;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.PropertiesPropertySource;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * <pre>
 * com.winitech.common.config
 * └ DefaultApplicationPropertiesLoader.java
 * </pre>
 * @author : coding (클라우드팀)
 * @since : 2025-03-07 15:22
 **/
public class DefaultApplicationPropertiesLoader implements EnvironmentPostProcessor {
	@Override
	public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
		Properties properties = new Properties();
		try (InputStream is = getClass().getClassLoader().getResourceAsStream("default-application.properties")) {
			properties.load(is);
		} catch (IOException e) {
			throw new RuntimeException("default-application.properties 로딩에 실패하였습니다.", e);
		}
		environment.getPropertySources().addLast(new PropertiesPropertySource("defaultProperties", properties));
	}
}
