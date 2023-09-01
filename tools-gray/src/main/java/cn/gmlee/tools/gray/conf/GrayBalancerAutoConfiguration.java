package cn.gmlee.tools.gray.conf;

import cn.gmlee.tools.gray.filter.GrayBalancerFilter;
import cn.gmlee.tools.gray.filter.GrayClientIpFilter;
import cn.gmlee.tools.gray.server.GrayServer;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.loadbalancer.support.LoadBalancerClientFactory;
import org.springframework.context.annotation.Bean;

/**
 * 灰度负载自动装配
 */
@EnableConfigurationProperties(GrayProperties.class)
@ConditionalOnClass(name = {"org.springframework.cloud.gateway.filter.GlobalFilter"})
public class GrayBalancerAutoConfiguration {

    /**
     * Gray server gray server.
     *
     * @param properties the properties
     * @return the gray server
     */
    @Bean
    @ConditionalOnMissingBean({GrayServer.class})
    public GrayServer grayServer(GrayProperties properties) {
        return new GrayServer(properties);
    }

    /**
     * Gray balancer filter gray client ip filter.
     *
     * @return the gray client ip filter
     */
    @Bean
    @ConditionalOnMissingBean({GrayClientIpFilter.class})
    public GrayClientIpFilter grayBalancerFilter() {
        return new GrayClientIpFilter();
    }

    /**
     * Gray balancer filter gray balancer filter.
     *
     * @param clientFactory the client factory
     * @param grayServer    the gray server
     * @return the gray load balancer client filter
     */
    @Bean
    @ConditionalOnMissingBean({GrayBalancerFilter.class})
    public GrayBalancerFilter grayBalancerFilter(LoadBalancerClientFactory clientFactory, GrayServer grayServer) {
        return new GrayBalancerFilter(clientFactory, grayServer);
    }
}