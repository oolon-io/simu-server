package io.oolon.simu.server.wepay.conf;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.apache.http.HttpHost;
import org.apache.http.client.config.RequestConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.oolon.http.config.HttpProxySelector;
import io.oolon.http.config.PoolConfig;
import io.oolon.http.config.RequestConfigDelegater;
import io.oolon.http.utils.HttpClientUtil;

@Component
public class HttpClientIniter {
	
	
	@PostConstruct
	public void init() {
        HttpClientUtil.initPool(poolConfig);
        HttpClientUtil.setRequestConfigDelegater(requestConfigDelegater);
        HttpClientUtil.initProxy(httpProxySelector);
	}
	
	@Resource
	private PoolConfig poolConfig;
	
	@Resource
	private HttpProxySelector httpProxySelector;
	
	@Resource
	private RequestConfigDelegater requestConfigDelegater;

}

@Component
class PoolConfigSpring implements PoolConfig{
	
	@Value("${http.pool.totalMax}")
	private Integer totalMax;

	@Value("${http.pool.DefaultMaxPerRoute}")
	private Integer defaultMaxPerRoute;

	@Override
	public int getMaxTotal() {
		return totalMax;
	}

	@Override
	public int getDefaultMaxPerRoute() {
		return defaultMaxPerRoute;
	}

	@Override
	public Map<String, Integer> getSpecHostsMax() {
		return new HashMap<String, Integer>();
	}

}
@Component
class ProxyConfigSpring extends HttpProxySelector {
	
	@Value("${http.useProxy:N}")
	private String useProxy;

	@Value("${http.proxy.host:}")
	private	String proxyHost;

	@Value("${http.proxy.port:}")
	private Integer proxyPort;

	@Value("${ignore.ips:}")
	private String ignoreIps;
	
	
	

	@Override
	public HttpHost getProxy(HttpHost target) {
		if(ignoreIps == null)
			ignoreIps = "";
		if("Y".equalsIgnoreCase(useProxy) && !ignoreIps.contains(target.getHostName()))
		    return getDefaultProxy();
		else
			return null;
	}

	@Override
	protected HttpHost getDefaultProxy() {
		if(proxy != null) {
		    return proxy;
		}else {
			proxy = new HttpHost(proxyHost, proxyPort);
			return proxy;
		}
	}

	@Override
	protected Map<HttpHost, HttpHost> getSpecialProxyMap() {
		return new HashMap<HttpHost, HttpHost>();
	}

	@Override
	protected Set<HttpHost> getIgnoreHosts() {
		return null;/*改写getProxy实现，此处空方法了*/
	}
	
	private static HttpHost proxy = null;

}

@Component
class RequestConfigSpring extends RequestConfigDelegater {
	
	@Value("${http.socketTimeout}")
	private Integer socketTimeout;

	@Value("${http.connectionTimeout}")
	private Integer connectionTimeout;

	@Value("${http.connectionRequestTimeout}")
	private Integer connectionRequestTimeout;
	
	private static RequestConfig defaultRequestConfig = null;

	@Override
	public RequestConfig getDefaultRequestConfig() {
        if(defaultRequestConfig != null)
        	return defaultRequestConfig;
        else {
        	RequestConfig.Builder builder = RequestConfig.custom();
	        if (connectionRequestTimeout != null)
	            builder.setConnectionRequestTimeout(connectionRequestTimeout);
	        if (connectionTimeout != null)
	            builder.setConnectTimeout(connectionTimeout);
	        if (socketTimeout != null)
	            builder.setSocketTimeout(socketTimeout);
	        defaultRequestConfig = builder.build();
        }
		 return defaultRequestConfig;
	}

	@Override
	public Map<String, RequestConfig> getSpecialRequestConfig() {
		return new HashMap<String,RequestConfig>();
	}

}