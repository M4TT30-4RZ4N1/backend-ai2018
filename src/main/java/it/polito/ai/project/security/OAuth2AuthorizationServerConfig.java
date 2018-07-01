package it.polito.ai.project.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;


@Configuration
@EnableAuthorizationServer
public class OAuth2AuthorizationServerConfig extends AuthorizationServerConfigurerAdapter{
        /**
         *  Gestisce l'autenticazione con username e password ed è definito in WebSecurityConfig
         */
    private final AuthenticationManager authenticationManager;
    private final RepositoryUserDetailsService userDetailsService;

    @Autowired
    public OAuth2AuthorizationServerConfig(@Qualifier("authenticationManagerBean") AuthenticationManager authenticationManager, RepositoryUserDetailsService userDetailsService) {
        this.authenticationManager = authenticationManager;
        this.userDetailsService = userDetailsService;
    }

    /**
     * Definisce i permessi relativi all'accesso al token endpoint /oauth/token
     * @param oauthServer
     */
    @Override
    public void configure(final AuthorizationServerSecurityConfigurer oauthServer){
        oauthServer.tokenKeyAccess("permitAll()").checkTokenAccess("isAuthenticated()");
    }

    /**
     * Configura lo storage dei token come inmemory e imposta i parametri da usare per la base authentication dei client
     * La password (secret) è bcrypt round 4
     * @param clients
     * @throws Exception
     */
    @Override
    public void configure(final ClientDetailsServiceConfigurer clients) throws Exception {
        clients
                .inMemory().withClient("fooClientIdPassword").secret("$2a$04$01S/P3ty1diiGWso4RO4WOvjqj6P.vcunzmbbogsN.9Igp6Uu2qWa")
                .authorizedGrantTypes("password", "authorization_code", "refresh_token").scopes("default")
                .accessTokenValiditySeconds(3600) // 1 hour
                .refreshTokenValiditySeconds(2592000)
        ;
    }

    /**
     * Imposta lo storage, il converter dei token e il gestore dell'autenticazione (username e pass di oauth2)
     * @param endpoints
     * @throws Exception
     */
    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints){
        endpoints.tokenStore(tokenStore())
                .accessTokenConverter(accessTokenConverter())
                .authenticationManager(authenticationManager)
                .userDetailsService(userDetailsService);
    }

    @Bean
    public TokenStore tokenStore() {
    return new JwtTokenStore(accessTokenConverter());
    }

    /**
     * Firma e Verifica i token con una chiave simmetrica.
     */
    @Bean
    public JwtAccessTokenConverter accessTokenConverter() {
    final JwtAccessTokenConverter converter = new JwtAccessTokenConverter();
    converter.setSigningKey("123");
    return converter;
    }


    @Bean
    @Primary
    public DefaultTokenServices tokenServices() {
        final DefaultTokenServices defaultTokenServices = new DefaultTokenServices();
        defaultTokenServices.setTokenStore(tokenStore());
        defaultTokenServices.setSupportRefreshToken(true);
        return defaultTokenServices;
    }




}