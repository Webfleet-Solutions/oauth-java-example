package com.webfleet.oauth.config;

import feign.Logger;
import feign.codec.Encoder;
import feign.form.FormEncoder;
import feign.slf4j.Slf4jLogger;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.cloud.openfeign.support.SpringEncoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Scope;

import static org.springframework.beans.factory.config.BeanDefinition.SCOPE_PROTOTYPE;


@Configuration
@EnableFeignClients("com.webfleet.oauth")
public class FeignConfig
{
    private final ObjectFactory<HttpMessageConverters> messageConverters;

    public FeignConfig(final ObjectFactory<HttpMessageConverters> messageConverters)
    {
        this.messageConverters = messageConverters;
    }

    @Bean
    Logger.Level levelLogger()
    {
        // Feign's own logger level
        return Logger.Level.FULL;
    }

    @Bean
    Logger logger()
    {
        // Required to log with slf4j
        return new Slf4jLogger();
    }

    /**
     * Use feign to parse HTML forms
     *
     * @return a {@link FormEncoder} able to use feign for form parsing
     */
    @Bean
    @Primary
    @Scope(SCOPE_PROTOTYPE)
    Encoder feignFormEncoder()
    {
        return new FormEncoder(new SpringEncoder(this.messageConverters));
    }
}
