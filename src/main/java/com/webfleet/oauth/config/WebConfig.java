package com.webfleet.oauth.config;

import com.webfleet.oauth.common.RandomKeyInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewResolverRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;


@Configuration
@EnableWebMvc
public class WebConfig implements WebMvcConfigurer
{

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry)
    {
        // Map static resources in public folder to /static/ path
        registry.addResourceHandler("/static/**")
                .addResourceLocations("classpath:/public/");
    }

    @Override
    public void addInterceptors(final InterceptorRegistry registry)
    {
        // Add a session attribute with a RandomKey instance
        registry.addInterceptor(new RandomKeyInterceptor());
    }

    @Override
    public void configureViewResolvers(ViewResolverRegistry registry)
    {
        registry.jsp();
    }

}
