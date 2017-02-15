package com.mashape.unirest.http.options;

import java.util.HashMap;
import java.util.Map;

import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.conn.routing.HttpRoutePlanner;
import org.apache.http.conn.ssl.AllowAllHostnameVerifier;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.ProxyAuthenticationStrategy;
import org.apache.http.impl.conn.DefaultRoutePlanner;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.impl.nio.client.HttpAsyncClientBuilder;
import org.apache.http.impl.nio.conn.PoolingNHttpClientConnectionManager;
import org.apache.http.impl.nio.reactor.DefaultConnectingIOReactor;
import org.apache.http.nio.reactor.IOReactorException;
import org.apache.http.protocol.HttpContext;

import com.mashape.unirest.http.async.utils.AsyncIdleConnectionMonitorThread;
import com.mashape.unirest.http.utils.SyncIdleConnectionMonitorThread;

public class Options {

	public static final long CONNECTION_TIMEOUT = 10000;
	private static final long SOCKET_TIMEOUT = 60000;
	public static final int MAX_TOTAL = 200;
	public static final int MAX_PER_ROUTE = 20;

	private static Map<Option, Object> options = new HashMap<Option, Object>();

	public static void setOption(Option option, Object value) {
		options.put(option, value);
	}

	public static Object getOption(Option option) {
		return options.get(option);
	}

	static {
		refresh();
	}

	public static void refresh() {
		// Load timeouts
		Object connectionTimeout = Options.getOption(Option.CONNECTION_TIMEOUT);
		if (connectionTimeout == null)
			connectionTimeout = CONNECTION_TIMEOUT;
		Object socketTimeout = Options.getOption(Option.SOCKET_TIMEOUT);
		if (socketTimeout == null)
			socketTimeout = SOCKET_TIMEOUT;

		// Load limits
		Object maxTotal = Options.getOption(Option.MAX_TOTAL);
		if (maxTotal == null)
			maxTotal = MAX_TOTAL;
		Object maxPerRoute = Options.getOption(Option.MAX_PER_ROUTE);
		if (maxPerRoute == null)
			maxPerRoute = MAX_PER_ROUTE;

		// Load proxy if set
		HttpHost proxy = (HttpHost) Options.getOption(Option.PROXY);

		// ignore ssl certs
		Boolean ignoreSSLCerts = (Boolean) Options
				.getOption(Option.USE_INSECURE_SSL);
		ignoreSSLCerts = ignoreSSLCerts != null ? ignoreSSLCerts : false;

		// Create common default configuration
		RequestConfig clientConfig = RequestConfig.custom()
				.setConnectTimeout(((Long) connectionTimeout).intValue())
				.setSocketTimeout(((Long) socketTimeout).intValue())
				.setConnectionRequestTimeout(((Long) socketTimeout).intValue())
				.setProxy(proxy).build();

		PoolingHttpClientConnectionManager syncConnectionManager = (PoolingHttpClientConnectionManager) Options
				.getOption(Option.CONNECTION_MANAGER);
		if (syncConnectionManager == null) {
			syncConnectionManager = new PoolingHttpClientConnectionManager();
			setOption(Option.CONNECTION_MANAGER, syncConnectionManager);
		}
		syncConnectionManager.setMaxTotal((Integer) maxTotal);
		syncConnectionManager.setDefaultMaxPerRoute((Integer) maxPerRoute);

		CredentialsProvider credentialsProvider = (CredentialsProvider) Options
				.getOption(Option.CREDENTIALS_PROVIDER);
		if (credentialsProvider == null) {
			credentialsProvider = new BasicCredentialsProvider();
			setOption(Option.CREDENTIALS_PROVIDER, credentialsProvider);
		}

		HttpRoutePlanner routePlanner = (HttpRoutePlanner) Options
				.getOption(Option.ROUTE_PLANNER);
		if (routePlanner == null) {
			routePlanner = new DefaultRoutePlanner(null);
			setOption(Option.ROUTE_PLANNER, routePlanner);
		}

		ProxyAuthenticationStrategy proxyStrategy = new ProxyAuthenticationStrategy() ;

		// Create clients
		HttpClientBuilder builder = HttpClientBuilder.create()
				.setDefaultRequestConfig(clientConfig)
				.setDefaultCredentialsProvider(credentialsProvider)
				.setRoutePlanner(routePlanner)
				.setTargetAuthenticationStrategy(proxyStrategy)
				.setConnectionManager(syncConnectionManager);
		if (ignoreSSLCerts) {
			builder.setHostnameVerifier(new AllowAllHostnameVerifier());
		}
		setOption(Option.HTTPCLIENT, builder.build());
		SyncIdleConnectionMonitorThread syncIdleConnectionMonitorThread = new SyncIdleConnectionMonitorThread(
				syncConnectionManager);
		setOption(Option.SYNC_MONITOR, syncIdleConnectionMonitorThread);
		syncIdleConnectionMonitorThread.start();

		PoolingNHttpClientConnectionManager asyncConnectionManager = (PoolingNHttpClientConnectionManager) Options
				.getOption(Option.ASYNC_CONNECTION_MANAGER);
		if (asyncConnectionManager == null) {
			DefaultConnectingIOReactor ioreactor;
			try {
				ioreactor = new DefaultConnectingIOReactor();
				asyncConnectionManager = new PoolingNHttpClientConnectionManager(
						ioreactor);
				setOption(Option.ASYNC_CONNECTION_MANAGER,
						asyncConnectionManager);
			} catch (IOReactorException e) {
				throw new RuntimeException(e);
			}
		}
		asyncConnectionManager.setMaxTotal((Integer) maxTotal);
		asyncConnectionManager.setDefaultMaxPerRoute((Integer) maxPerRoute);
		HttpAsyncClientBuilder asyncBuilder = HttpAsyncClientBuilder.create()
				.setDefaultRequestConfig(clientConfig)
				.setRoutePlanner(routePlanner)
				.setTargetAuthenticationStrategy(proxyStrategy)
				.setDefaultCredentialsProvider(credentialsProvider)
				.setConnectionManager(asyncConnectionManager);
		if (ignoreSSLCerts) {
			asyncBuilder.setHostnameVerifier(new AllowAllHostnameVerifier());
		}
		CloseableHttpAsyncClient asyncClient = asyncBuilder.build();
		setOption(Option.ASYNCHTTPCLIENT, asyncClient);
		setOption(Option.ASYNC_MONITOR, new AsyncIdleConnectionMonitorThread(
				asyncConnectionManager));
	}

}
