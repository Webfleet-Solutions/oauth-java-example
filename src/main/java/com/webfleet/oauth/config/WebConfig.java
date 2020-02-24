package com.webfleet.oauth.config;

import com.webfleet.oauth.common.RandomKeyInterceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.web.servlet.error.ErrorViewResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.config.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Locale;
import java.util.Map;


@Configuration
@EnableWebMvc
public class WebConfig implements WebMvcConfigurer {

    @Bean
    public ErrorViewResolver errorViewResolver(ViewResolver mvcViewResolver) {
        return new InternalErrorViewResolver(mvcViewResolver);
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Map static resources in public folder to /static/ path
        registry.addResourceHandler("/static/**")
                .setCachePeriod(365 * 24 * 3600)
                .addResourceLocations("classpath:/public/");
    }

    @Override
    public void addInterceptors(final InterceptorRegistry registry) {
        // Add a session attribute with a RandomKey instance
        registry.addInterceptor(new RandomKeyInterceptor());
    }

    @Override
    public void configureViewResolvers(ViewResolverRegistry registry) {
        registry.jsp();
    }

    private static class InternalErrorViewResolver implements ErrorViewResolver {
        private static final Logger LOG = LoggerFactory.getLogger(InternalErrorViewResolver.class);
        private ViewResolver mvcViewResolver;

        public InternalErrorViewResolver(final ViewResolver mvcViewResolver) {
            this.mvcViewResolver = mvcViewResolver;
        }

        @Override
        public ModelAndView resolveErrorView(final HttpServletRequest request, final HttpStatus status, final Map<String, Object> model) {
            try {
                return new ModelAndView(mvcViewResolver.resolveViewName("error", Locale.ENGLISH), model);
            } catch (Exception e) {
                LOG.error("Couldn't render error view", e);
            }
            return new ModelAndView((model1, request1, response) ->
                    response.getOutputStream().println("<html><body><h1>Error page</h1></body></html>"), model);
        }
    }
}
