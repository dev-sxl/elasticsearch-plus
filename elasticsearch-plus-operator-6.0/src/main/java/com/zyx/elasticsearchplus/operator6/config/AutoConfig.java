package com.zyx.elasticsearchplus.operator6.config;

import com.xyz.elasticsearchplus.core.operator.OperatorFactory;
import com.zyx.elasticsearchplus.operator6.impl.DocOperator;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author sxl
 */
@Slf4j
@Configuration
public class AutoConfig {

    @Bean(destroyMethod = "close")
    public RestHighLevelClient client(@Value("${spring.elasticsearch.rest.username}") String userName,
                                      @Value("${spring.elasticsearch.rest.password}") String password,
                                      @Value("${spring.elasticsearch.rest.uris}") String url,
                                      @Value("${spring.elasticsearch.rest.port}") Integer port) {
        CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
        credentialsProvider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(userName, password));
        RestClientBuilder builder = RestClient.builder(new HttpHost(url, port))
                                              .setHttpClientConfigCallback(httpClientBuilder -> httpClientBuilder.setDefaultCredentialsProvider(credentialsProvider));
        RestHighLevelClient client = new RestHighLevelClient(builder);
        // init DocOperator
        OperatorFactory.init(new DocOperator(client));
        return client;
    }

}
