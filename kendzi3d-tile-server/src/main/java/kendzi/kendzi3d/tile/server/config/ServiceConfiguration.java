package kendzi.kendzi3d.tile.server.config;

import java.util.Properties;

import kendzi.kendzi3d.render.conf.RenderDataSourceConf;
import kendzi.kendzi3d.render.conf.RenderDataSourceConfLoader;
import kendzi.kendzi3d.render.conf.RenderEngineConf;
import kendzi.kendzi3d.render.conf.RenderEngineConfLoader;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;

@Configuration
//@PropertySource("/WEB-INF/services.properties")
//@FileSystemResourceLoader()
//@PropertySource("file:WEB-INF/application.properties")
@PropertySource("classpath:application.properties")
public class ServiceConfiguration {

    @Autowired Environment environment;

    //    @Bean    //(destroyMethod="close")
    //    public javax.sql.DataSource dataSource() {
    //        String username = this.environment.getProperty("jdbc.username");
    //        String password = this.environment.getProperty("jdbc.password");
    //        String url = this.environment.getProperty("jdbc.url");
    //
    //        //        DataSource ds = new BasicDataSource();
    //        //        ds.setDriverClassName(driverClassName);
    //
    //        Kendzi3dPostgisDataSource ds = new Kendzi3dPostgisDataSource();
    //
    //        ds.setUrl(url);
    //        ds.setUsername(username);
    //        ds.setPassword(password);
    //
    //        return ds;
    //    }

    @Bean RenderEngineConf renderEngineConf() {

        Properties p = new Properties() {
            /**
             * 
             */
            private static final long serialVersionUID = 1L;

            @Override
            public String getProperty(String key, String defaultValue) {
                return ServiceConfiguration.this.environment.getProperty(key, defaultValue);
            }
        };

        return RenderEngineConfLoader.load(p);
    }

    @Bean RenderDataSourceConf renderDataSourceConf() {

        Properties p = new Properties() {
            /**
             * 
             */
            private static final long serialVersionUID = 1L;

            @Override
            public String getProperty(String key, String defaultValue) {
                return ServiceConfiguration.this.environment.getProperty(key, defaultValue);
            }
        };

        return RenderDataSourceConfLoader.load(p);
    }
}
