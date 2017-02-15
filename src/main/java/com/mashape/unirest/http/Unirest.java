/*
The MIT License

Copyright (c) 2013 Mashape (http://mashape.com)

Permission is hereby granted, free of charge, to any person obtaining
a copy of this software and associated documentation files (the
"Software"), to deal in the Software without restriction, including
without limitation the rights to use, copy, modify, merge, publish,
distribute, sublicense, and/or sell copies of the Software, and to
permit persons to whom the Software is furnished to do so, subject to
the following conditions:

The above copyright notice and this permission notice shall be
included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package com.mashape.unirest.http;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.HttpHost;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.conn.routing.HttpRoutePlanner;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.impl.nio.conn.PoolingNHttpClientConnectionManager;

import com.mashape.unirest.http.async.utils.AsyncIdleConnectionMonitorThread;
import com.mashape.unirest.http.options.Option;
import com.mashape.unirest.http.options.Options;
import com.mashape.unirest.http.utils.SyncIdleConnectionMonitorThread;
import com.mashape.unirest.request.GetRequest;
import com.mashape.unirest.request.HttpRequestWithBody;

public class Unirest {

	/**
	 * Set insecure ssl
	 */
	public static void setIgnoreSSLCerts(boolean ignore) {
		Options.setOption(Option.USE_INSECURE_SSL, ignore);
		// Reload the client implementations
		Options.refresh();
	}

	/**
	 * Set the default route planner
	 */
	public static void setRoutePlanner(HttpRoutePlanner httpRoutePlanner) {
		Options.setOption(Option.ROUTE_PLANNER, httpRoutePlanner);
		// Reload the client implementations
		Options.refresh();
	}

	/**
	 * get the route planner
	 */
	public static HttpRoutePlanner getRoutePlanner() {
		return (HttpRoutePlanner) Options.getOption(Option.ROUTE_PLANNER);
	}

	/**
	 * Set the default async connection manager
	 */
	public static void setAsyncConnectionManager(
			PoolingNHttpClientConnectionManager connectionManager) {
		Options.setOption(Option.ASYNC_CONNECTION_MANAGER, connectionManager);
		// Reload the client implementations
		Options.refresh();
	}

	/**
	 * Set the connection manager
	 */
	public static PoolingNHttpClientConnectionManager getAsyncConnectionManager() {
		return (PoolingNHttpClientConnectionManager) Options
				.getOption(Option.ASYNC_CONNECTION_MANAGER);
	}

	/**
	 * Set the default connection manager
	 */
	public static void setConnectionManager(
			PoolingHttpClientConnectionManager connectionManager) {
		Options.setOption(Option.CONNECTION_MANAGER, connectionManager);
		// Reload the client implementations
		Options.refresh();
	}

	/**
	 * Set the connection manager
	 */
	public static PoolingHttpClientConnectionManager getConnectionManager() {
		return (PoolingHttpClientConnectionManager) Options
				.getOption(Option.CONNECTION_MANAGER);
	}

	/**
	 * Set the default credential provider
	 */
	public static void setCredentialsProvider(CredentialsProvider provider) {
		Options.setOption(Option.CREDENTIALS_PROVIDER, provider);
		// Reload the client implementations
		Options.refresh();
	}

	/**
	 * Get the credential provider
	 */
	public static CredentialsProvider getCredentialsProvider() {
		return (CredentialsProvider) Options
				.getOption(Option.CREDENTIALS_PROVIDER);
	}

	/**
	 * Set the HttpClient implementation to use for every synchronous request
	 */
	public static void setHttpClient(HttpClient httpClient) {
		Options.setOption(Option.HTTPCLIENT, httpClient);
	}

	/**
	 * Set the asynchronous AbstractHttpAsyncClient implementation to use for
	 * every asynchronous request
	 */
	public static void setAsyncHttpClient(
			CloseableHttpAsyncClient asyncHttpClient) {
		Options.setOption(Option.ASYNCHTTPCLIENT, asyncHttpClient);
	}

	/**
	 * Gets the HttpClient implementation to use for every synchronous request
	 */
	public static HttpClient getHttpClient() {
		return (HttpClient) Options.getOption(Option.HTTPCLIENT);
	}

	/**
	 * Gets the asynchronous AbstractHttpAsyncClient implementation to use for
	 * every asynchronous request
	 */
	public static CloseableHttpAsyncClient getAsyncHttpClient() {
		return (CloseableHttpAsyncClient) Options
				.getOption(Option.ASYNCHTTPCLIENT);
	}

	/**
	 * Set a proxy
	 */
	public static void setProxy(HttpHost proxy) {
		Options.setOption(Option.PROXY, proxy);

		// Reload the client implementations
		Options.refresh();
	}

	/**
	 * Set the connection timeout and socket timeout
	 * 
	 * @param connectionTimeout
	 *            The timeout until a connection with the server is established
	 *            (in milliseconds). Default is 10000. Set to zero to disable
	 *            the timeout.
	 * @param socketTimeout
	 *            The timeout to receive data (in milliseconds). Default is
	 *            60000. Set to zero to disable the timeout.
	 */
	public static void setTimeouts(long connectionTimeout, long socketTimeout) {
		Options.setOption(Option.CONNECTION_TIMEOUT, connectionTimeout);
		Options.setOption(Option.SOCKET_TIMEOUT, socketTimeout);

		// Reload the client implementations
		Options.refresh();
	}

	/**
	 * Set the concurrency levels
	 * 
	 * @param maxTotal
	 *            Defines the overall connection limit for a connection pool.
	 *            Default is 200.
	 * @param maxPerRoute
	 *            Defines a connection limit per one HTTP route (this can be
	 *            considered a per target host limit). Default is 20.
	 */
	public static void setConcurrency(int maxTotal, int maxPerRoute) {
		Options.setOption(Option.MAX_TOTAL, maxTotal);
		Options.setOption(Option.MAX_PER_ROUTE, maxPerRoute);

		// Reload the client implementations
		Options.refresh();
	}

	/**
	 * Clear default headers
	 */
	public static void clearDefaultHeaders() {
		Options.setOption(Option.DEFAULT_HEADERS, null);
	}

	/**
	 * Set default header
	 */
	@SuppressWarnings("unchecked")
	public static void setDefaultHeader(String name, String value) {
		Object headers = Options.getOption(Option.DEFAULT_HEADERS);
		if (headers == null) {
			headers = new HashMap<String, String>();
		}
		((Map<String, String>) headers).put(name, value);
		Options.setOption(Option.DEFAULT_HEADERS, headers);
	}

	/**
	 * Close the asynchronous client and its event loop. Use this method to
	 * close all the threads and allow an application to exit.
	 */
	public static void shutdown() throws IOException {
		// Closing the Sync HTTP client
		CloseableHttpClient syncClient = (CloseableHttpClient) Options
				.getOption(Option.HTTPCLIENT);
		if (syncClient != null) {
			syncClient.close();
		}

		SyncIdleConnectionMonitorThread syncIdleConnectionMonitorThread = (SyncIdleConnectionMonitorThread) Options
				.getOption(Option.SYNC_MONITOR);
		if (syncIdleConnectionMonitorThread != null) {
			syncIdleConnectionMonitorThread.interrupt();
		}

		// Closing the Async HTTP client (if running)
		CloseableHttpAsyncClient asyncClient = (CloseableHttpAsyncClient) Options
				.getOption(Option.ASYNCHTTPCLIENT);
		if (asyncClient != null && asyncClient.isRunning()) {
			asyncClient.close();
		}

		AsyncIdleConnectionMonitorThread asyncMonitorThread = (AsyncIdleConnectionMonitorThread) Options
				.getOption(Option.ASYNC_MONITOR);
		if (asyncMonitorThread != null) {
			asyncMonitorThread.interrupt();
		}
	}

	public static GetRequest get(String url) {
		return new GetRequest(HttpMethod.GET, url, null, null);
	}

	public static GetRequest get(String url,
			HttpClientContext httpClientContext, RequestConfig requestConfig) {
		return new GetRequest(HttpMethod.GET, url, httpClientContext,
				requestConfig);
	}

	public static GetRequest head(String url) {
		return new GetRequest(HttpMethod.HEAD, url, null, null);
	}

	public static GetRequest head(String url,
			HttpClientContext httpClientContext, RequestConfig requestConfig) {
		return new GetRequest(HttpMethod.HEAD, url, httpClientContext,
				requestConfig);
	}

	public static HttpRequestWithBody options(String url) {
		return new HttpRequestWithBody(HttpMethod.OPTIONS, url, null, null);
	}

	public static HttpRequestWithBody options(String url,
			HttpClientContext httpClientContext, RequestConfig requestConfig) {
		return new HttpRequestWithBody(HttpMethod.OPTIONS, url,
				httpClientContext, requestConfig);
	}

	public static HttpRequestWithBody post(String url) {
		return new HttpRequestWithBody(HttpMethod.POST, url, null, null);
	}

	public static HttpRequestWithBody post(String url,
			HttpClientContext httpClientContext, RequestConfig requestConfig) {
		return new HttpRequestWithBody(HttpMethod.POST, url, httpClientContext,
				requestConfig);
	}

	public static HttpRequestWithBody delete(String url) {
		return new HttpRequestWithBody(HttpMethod.DELETE, url, null, null);
	}

	public static HttpRequestWithBody delete(String url,
			HttpClientContext httpClientContext, RequestConfig requestConfig) {
		return new HttpRequestWithBody(HttpMethod.DELETE, url,
				httpClientContext, requestConfig);
	}

	public static HttpRequestWithBody patch(String url) {
		return new HttpRequestWithBody(HttpMethod.PATCH, url, null, null);
	}

	public static HttpRequestWithBody patch(String url,
			HttpClientContext httpClientContext, RequestConfig requestConfig) {
		return new HttpRequestWithBody(HttpMethod.PATCH, url,
				httpClientContext, requestConfig);
	}

	public static HttpRequestWithBody put(String url) {
		return new HttpRequestWithBody(HttpMethod.PUT, url, null, null);
	}

	public static HttpRequestWithBody put(String url,
			HttpClientContext httpClientContext, RequestConfig requestConfig) {
		return new HttpRequestWithBody(HttpMethod.PUT, url, httpClientContext,
				requestConfig);
	}
}
