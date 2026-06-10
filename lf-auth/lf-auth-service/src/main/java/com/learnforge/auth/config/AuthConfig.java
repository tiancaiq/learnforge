package com.learnforge.auth.config;

import org.apache.tomcat.util.http.LegacyCookieProcessor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.embedded.tomcat.TomcatContextCustomizer;
import org.springframework.cloud.bootstrap.encrypt.KeyProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.rsa.crypto.KeyStoreKeyFactory;

import java.security.KeyPair;

@Configuration
@EnableConfigurationProperties(KeyProperties.class)
public class AuthConfig {

    @Bean
    @ConfigurationProperties(prefix = "encrypt")
    public KeyProperties keyProperties(){
        return new KeyProperties();
    }

    @Bean
    public KeyPair keyPair(KeyProperties keyProperties){
        // Get key factory
        KeyStoreKeyFactory keyStoreKeyFactory =
                new KeyStoreKeyFactory(
                        keyProperties.getKeyStore().getLocation(),
                        keyProperties.getKeyStore().getPassword().toCharArray());
        //Read key pair
        return keyStoreKeyFactory.getKeyPair(
                keyProperties.getKeyStore().getAlias(),
                keyProperties.getKeyStore().getSecret().toCharArray());
    }

    @Bean
    public TomcatContextCustomizer cookieTomcatContextCustomizer(){
        return context -> context.setCookieProcessor(new LegacyCookieProcessor());
    }
}
