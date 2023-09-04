package cn.gmlee.tools.gray.filter;

import cn.gmlee.tools.gray.assist.ExchangeAssist;
import cn.gmlee.tools.gray.assist.PropAssist;
import cn.gmlee.tools.gray.balancer.GrayReactorServiceInstanceLoadBalancer;
import cn.gmlee.tools.gray.server.GrayServer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.DefaultRequest;
import org.springframework.cloud.client.loadbalancer.LoadBalancerUriTools;
import org.springframework.cloud.client.loadbalancer.Response;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.cloud.gateway.filter.RouteToRequestUrlFilter;
import org.springframework.cloud.gateway.support.DelegatingServiceInstance;
import org.springframework.cloud.gateway.support.NotFoundException;
import org.springframework.cloud.gateway.support.ServerWebExchangeUtils;
import org.springframework.cloud.loadbalancer.core.ServiceInstanceListSupplier;
import org.springframework.cloud.loadbalancer.support.LoadBalancerClientFactory;
import org.springframework.core.Ordered;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.net.URI;

/**
 * 灰度发布过滤器.
 */
@Slf4j
public class GrayBalancerFilter implements GlobalFilter, Ordered {

    protected static final int ORDER = GrayClientIpFilter.ORDER + 1;

    private final LoadBalancerClientFactory clientFactory;
    private final GrayServer grayServer;

    /**
     * Instantiates a new Gray load balancer client filter.
     *
     * @param clientFactory the client factory
     * @param grayServer    the gray server
     */
    public GrayBalancerFilter(LoadBalancerClientFactory clientFactory, GrayServer grayServer) {
        this.clientFactory = clientFactory;
        this.grayServer = grayServer;
    }

    @Override
    public int getOrder() {
        return ORDER;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        // 判断拦截器执行顺序是否符合要求
        if(ExchangeAssist.filter(exchange)){
            log.warn("灰度负载拦截器不符合顺序要求");
            return chain.filter(exchange);
        }
        // 此开关控制灰度负载均衡是否生效
        if (!PropAssist.enable(exchange, grayServer.properties)) {
            log.info("灰度负载拦截器尚未开启总开关: {}", grayServer.properties.getEnable());
            return chain.filter(exchange);
        }
        return doFilter(exchange, chain);
    }

    private Mono<Void> doFilter(ServerWebExchange exchange, GatewayFilterChain chain) {
        return this.choose(exchange).doOnNext((response) -> {
            if (!response.hasServer()) {
                String msg = String.format("实例丢失: %s", exchange.getAttribute(ServerWebExchangeUtils.GATEWAY_ROUTE_ATTR));
                throw NotFoundException.create(true, msg);
            }
            URI uri = exchange.getRequest().getURI();
            DelegatingServiceInstance serviceInstance = new DelegatingServiceInstance(response.getServer(), uri.getScheme());
            URI requestUrl = this.reconstructURI(serviceInstance, uri);
            exchange.getAttributes().put(ServerWebExchangeUtils.GATEWAY_REQUEST_URL_ATTR, requestUrl);
        }).then(chain.filter(exchange));
    }

    /**
     * Reconstruct uri uri.
     *
     * @param serviceInstance the service instance
     * @param original        the original
     * @return the uri
     */
    protected URI reconstructURI(ServiceInstance serviceInstance, URI original) {
        return LoadBalancerUriTools.reconstructURI(serviceInstance, original);
    }

    private Mono<Response<ServiceInstance>> choose(ServerWebExchange exchange) {
        URI uri = exchange.getAttribute(ServerWebExchangeUtils.GATEWAY_REQUEST_URL_ATTR);
        GrayReactorServiceInstanceLoadBalancer loadBalancer = new GrayReactorServiceInstanceLoadBalancer(
                clientFactory.getLazyProvider(uri.getHost(), ServiceInstanceListSupplier.class), grayServer
        );
        return loadBalancer.choose(new DefaultRequest(exchange));
    }
}