package com.ipl;

import java.net.Proxy;

import org.apache.http.HttpHeaders;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.NTCredentials;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.BasicCredentialsProvider;

import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

public class UnirestIPLTest {
	public static void main(String[] args) throws UnirestException {
		String url = "http://ipinfo.io";
		String proxyHost = "";
		int proxyPort = 8888;
		String proxyUser = "";
		String proxyPwd = "";
		BasicCredentialsProvider credsProvider = new BasicCredentialsProvider();
		credsProvider.setCredentials(new AuthScope(proxyHost, proxyPort),
				new NTCredentials(proxyUser, proxyPwd, proxyHost, ""));
		Unirest.setCredentialsProvider(credsProvider);
		HttpHost proxy = new HttpHost(proxyHost, proxyPort,
				Proxy.Type.HTTP.name());
		RequestConfig config = RequestConfig.custom().setProxy(proxy).build();
		String body = Unirest
				.get(url, null, config)
				.header(HttpHeaders.USER_AGENT,
						"curl/7.9.8 (i686-pc-linux-gnu) libcurl 7.9.8 (OpenSSL 0.9.6b) (ipv6 enabled)")
				.header(HttpHeaders.ACCEPT, "application/json").asString()
				.getBody().toString();
		System.out.println(body);
	}
}
