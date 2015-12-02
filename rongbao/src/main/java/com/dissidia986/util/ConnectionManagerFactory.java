package com.dissidia986.util;

import org.apache.http.HttpHost;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.config.SocketConfig;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;

public class ConnectionManagerFactory {
	//最大连接数
    private static int MAX_CON = 5;
    //最大路由数
    private static int MAX_ROUTE = 2;
    //超时时间:单位-毫秒
    private static int TIMEOUT = 5000; 
    private volatile static PoolingHttpClientConnectionManager poolingHttpClientConnectionManager = null;
    //私有化默认构造函数，保证不可实例化
    private ConnectionManagerFactory() {}
    /**
	 * 单例的HTTP连接管理器
	 * @return
	 */
    public static PoolingHttpClientConnectionManager getInstance() {
        if (poolingHttpClientConnectionManager == null) {
            synchronized (ConnectionManagerFactory.class) {
                if (poolingHttpClientConnectionManager == null) {
                    Registry<ConnectionSocketFactory> registry = RegistryBuilder.<ConnectionSocketFactory>create()
                            .register("http", new PlainConnectionSocketFactory())
                            //SSL socket通信
                            //.register("https", SSLFactory.getInstance())
                            .build();
                    poolingHttpClientConnectionManager = new PoolingHttpClientConnectionManager(registry);
                    poolingHttpClientConnectionManager.setMaxTotal(MAX_CON);
                    poolingHttpClientConnectionManager.setDefaultMaxPerRoute(MAX_ROUTE);
                    //融宝超时时间设置为5秒
                    HttpRoute route = new HttpRoute(new HttpHost("www.reapal.com", 80));
                    poolingHttpClientConnectionManager.setSocketConfig(route.getTargetHost(),SocketConfig.custom().
                    	    setSoTimeout(TIMEOUT).build());
                }
            }
        }
        return poolingHttpClientConnectionManager;
    }

}
