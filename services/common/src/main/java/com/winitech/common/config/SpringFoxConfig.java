package com.winitech.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.*;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * com.winitech.common.configurer
 * └ SpringFoxConfig.java
 * @author : coding (클라우드팀)
 * @see : None
 * @since : 2024/08/07
 **/
@Configuration
public class SpringFoxConfig {
	@Bean
	public Docket api() {
		return new Docket(DocumentationType.SWAGGER_2)
				.select()
				.apis(RequestHandlerSelectors.any())
				.paths(PathSelectors.any())
				.build()
				.apiInfo(apiInfo())
				.securitySchemes(Arrays.asList(securityScheme(), orgIdSecurityScheme()))
				.securityContexts(Arrays.asList(securityContext()));
	}


	private ApiInfo apiInfo() {
//		String exampleBearerToken = "Bearer eyJhbGciOiJIUzUxMiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJ3aW5pdGVjaCIsImlhdCI6MTczMDg3NjkzOCwibmJmIjoxNzMwODc2OTI4LCJleHAiOjE3MzM0Njg5MzgsImp0aSI6IjU0ZGVlMTk5LTNlZjQtNGE1NS1hNmQ4LTBmMGNkODIwMmU4ZiIsInVpZCI6ImFhMTIzNDk4LWQyNjEtNDc4Yy04M2MzLWFkY2RmNzFmYjhhZSIsInVmbiI6ImEiLCJ1bG4iOiJkbWluIiwib2lkIjpbIjBiNWI5MGY1LWJkOTItNDZhZC1hNmFlLWVkMWY5YTY1NTk1OSIsIjJiMjg0YjMxLThhMTEtNDQxNy1iODkwLWE5YTc4NDA2OTRiOCJdLCJnY2QiOlsiQURNSU4iLCJBRE1JTiJdfQ.kdnPdQajoWWvAUUNBQHShmwY04wx9faMefsSb3GG0qXa4gEPVEuTdkKLYDhV6kzFi9DA-oO6te9ZGVC1pqoRsA";
//		String exampleOrgId = "0b5b90f5-bd92-46ad-a6ae-ed1f9a655959";
//		String html = "테스트를 위해서 Authorize 버튼을 클릭한 다음 'Bearer JWT토큰'과 '조직 ID'에 아래의 값을 입력하고 각각 <strong>Login</strong> 합니다.";
//		html += "<ul>";
//		html += "<li>Bearer JWT토큰 : <textarea>" + exampleBearerToken + "</textarea></li>";
//		html += "<li>조직 ID : <textarea>" + exampleOrgId + "</textarea></li>";
//		html += "</ul>";
		String html = "";

		return new ApiInfo(
				"Winitech Developer Portal",
				html,
				"1.0",
				null,
				new Contact("Winitech Co.,Ltd", "", ""),
				null,
				null,
				new ArrayList<>());
	}

	private SecurityScheme securityScheme() {
		return new ApiKey("Bearer JWT토큰", "Authorization", "header");
	}

	private SecurityScheme orgIdSecurityScheme() {
		return new ApiKey("조직 ID", "X-Org-Id", "header");
	}

	private SecurityContext securityContext() {
		List<SecurityReference> securityReferenceList = Arrays.asList(
				new SecurityReference("Bearer JWT토큰", new AuthorizationScope[0]),
				new SecurityReference("조직 ID", new AuthorizationScope[0])
		);

		return SecurityContext.builder()
				.securityReferences(securityReferenceList)
				.build();
	}
}
