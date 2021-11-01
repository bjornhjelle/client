package com.example.client.config;

import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ClientHttpConnector;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.util.ResourceUtils;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.TrustManagerFactory;
import java.io.FileInputStream;
import java.security.KeyStore;

@Slf4j
@Configuration
public class WebClientConfiguration {

    @Value("${server.ssl.key-store}")
    String keystorePath;

    @Value("${server.ssl.key-store-password}")
    String keystorePass;

    @Value("${server.ssl.trust-store}")
    String truststorePath;

    @Value("${server.ssl.trust-store-password}")
    String truststorePass;

    public SslContext getTwoWaySslContext() {

        log.info("WebClient Key-Store path/pass: {}/{}", keystorePath, keystorePass);
        log.info("WebClient Trust-Store path/pass: {}/{}", truststorePath, truststorePass);

        try (
                FileInputStream keyStoreFileInputStream = new FileInputStream(ResourceUtils.getFile(keystorePath));
                FileInputStream trustStoreFileInputStream = new FileInputStream(ResourceUtils.getFile(truststorePath));
        ) {
            KeyStore keyStore = KeyStore.getInstance("PKCS12");
            keyStore.load(keyStoreFileInputStream, keystorePass.toCharArray());
            KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance("SunX509");
            keyManagerFactory.init(keyStore, keystorePass.toCharArray());

            KeyStore trustStore = KeyStore.getInstance("JKS");
            trustStore.load(trustStoreFileInputStream, truststorePass.toCharArray());
            TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance("SunX509");
            TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            trustManagerFactory.init(trustStore);

            return SslContextBuilder.forClient()
                    .keyManager(keyManagerFactory)
                    .trustManager(trustManagerFactory)
                    .build();

        } catch (Exception e) {
            log.error("An error has occurred: ", e);
        }

        return null;
    }

    @Bean
    WebClient getWebClient() {

        HttpClient httpClient = HttpClient.create().secure(sslSpec -> sslSpec.sslContext(getTwoWaySslContext()));
        ClientHttpConnector clientHttpConnector = new ReactorClientHttpConnector(httpClient);

        return WebClient.builder()
                .clientConnector(clientHttpConnector)
                .build();
    }

}
