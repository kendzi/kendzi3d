package kendzi.kendzi3d.tile.server.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.config.annotation.AsyncSupportConfigurer;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

@Configuration
@ComponentScan(basePackages="kendzi.kendzi3d.tile.server")
@EnableWebMvc
public class MvcConfiguration extends WebMvcConfigurerAdapter{

    //public class MvcConfiguration extends WebMvcConfigurationSupport {

    @Bean
    public ViewResolver getViewResolver(){
        InternalResourceViewResolver resolver = new InternalResourceViewResolver();
        resolver.setPrefix("/WEB-INF/views/");
        resolver.setSuffix(".jsp");
        return resolver;
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/resources/**").addResourceLocations("/resources/");
    }

    /**
     * {@inheritDoc}
     *
     * @see org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter#configureAsyncSupport(org.springframework.web.servlet.config.annotation.AsyncSupportConfigurer)
     */
    @Override
    public void configureAsyncSupport(AsyncSupportConfigurer configurer) {
        // TODO Auto-generated method stub


        configurer.setTaskExecutor(new  SimpleAsyncTaskExecutor());
        super.configureAsyncSupport(configurer);
    }

    //
    //    @Override
    //    @Bean
    //    public RequestMappingHandlerAdapter
    //                  requestMappingHandlerAdapter() {
    //
    //        RequestMappingHandlerAdapter requestMappingHandlerAdapter = super.requestMappingHandlerAdapter();
    //
    //        requestMappingHandlerAdapter.setAsyncRequestTimeout(timeout)
    //
    //        requestMappingHandlerAdapter.get
    //
    //    }

}
