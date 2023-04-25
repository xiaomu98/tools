package cn.gmlee.tools.spring.config;

import cn.gmlee.tools.spring.util.IocUtil;
import cn.gmlee.tools.spring.SpringInstanceProvider;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * The type Spring config.
 *
 * @author Jas°
 */
@Configuration
public class SpringConfig {
    /**
     * 初始化GM InstanceFactory.
     *
     * @param context the context
     * @return the spring instance provider
     */
    @Bean
    public SpringInstanceProvider instanceFactory(ApplicationContext context) {
        SpringInstanceProvider provider = new SpringInstanceProvider(context);
        IocUtil.setInstanceProvider(provider);
        return provider;
    }
}
