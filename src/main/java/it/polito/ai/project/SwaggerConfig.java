package it.polito.ai.project;

import com.google.common.base.Predicates;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.OAuthBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.*;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger.web.ApiKeyVehicle;
import springfox.documentation.swagger.web.SecurityConfiguration;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Configuration
@EnableSwagger2
public class SwaggerConfig {
    @Value("${appconfig.clientSecret}")
    private String swaggerAppClientId;

    @Value("${appconfig.clientId}")
    private String swaggerClientSecret;
    private static final String swaggerTokenURL= "/oauth/token";
    private static final String SECURITY_SCHEMA_OAUTH2 = "oauth2";


    @Bean
    public Docket api() {
        return new Docket(DocumentationType.SWAGGER_2)
          .select()
          .apis(RequestHandlerSelectors.basePackage("it.polito.ai.project.rest"))
          .paths(PathSelectors.any())
          .build()
                .securityContexts(Collections.singletonList(securityContext()))
                .securitySchemes(new ArrayList<SecurityScheme>(Arrays.asList(oauth()))).apiInfo(apiInfo());
    }
    @Bean
    public SecurityScheme oauth() {
        return new OAuthBuilder()
                .name(SECURITY_SCHEMA_OAUTH2)
                .grantTypes(grantTypes())
                .scopes(scopes())
                .build();
    }
    private SecurityContext securityContext() {
        return SecurityContext.builder().securityReferences(defaultAuth())
                .build();
    }
    private List<SecurityReference> defaultAuth() {
        final AuthorizationScope[] authorizationScopes = new AuthorizationScope[1];
        authorizationScopes[0] = new AuthorizationScope("default", "write and read");
        return Collections.singletonList(new SecurityReference(SECURITY_SCHEMA_OAUTH2,authorizationScopes));
    }
    @Bean
    public SecurityConfiguration securityInfo() {
        return new SecurityConfiguration(swaggerAppClientId, swaggerClientSecret,  "", "", "Bearer access token", ApiKeyVehicle.HEADER, HttpHeaders.AUTHORIZATION,": Bearer");
    }


    private List<AuthorizationScope> scopes() {
        ArrayList<AuthorizationScope> ret=new ArrayList<AuthorizationScope>();
        ret.add(new AuthorizationScope("default", "write and read"));
        return ret;
    }

    private List<GrantType> grantTypes() {
        GrantType grantType = new ResourceOwnerPasswordCredentialsGrant(swaggerTokenURL);
        return new ArrayList<GrantType>(Arrays.asList(grantType));
    }
    private ApiInfo apiInfo() {
        return new ApiInfoBuilder().title("Backend API").description("")
                .license("Open Source")
                .licenseUrl("https://www.apache.org/licenses/LICENSE-2.0")
                .version("1.0.0")
                .build();

    }
}