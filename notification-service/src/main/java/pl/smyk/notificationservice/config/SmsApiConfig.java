package pl.smyk.notificationservice.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pl.smsapi.OAuthClient;
import pl.smsapi.api.SmsFactory;
import pl.smsapi.proxy.ProxyNative;

@Configuration
public class SmsApiConfig {
    @Value("${smsapi.oauthtoken}")
    private String oauthtoken;
    @Value("${smsapi.baseurl}")
    private String baseUrl;

    @Bean
    public OAuthClient oAuthClient() {
        return new OAuthClient(oauthtoken);
    }

    @Bean
    public ProxyNative proxyNative() {
        return new ProxyNative(baseUrl);
    }

    @Bean
    public SmsFactory smsFactory(OAuthClient client, ProxyNative proxy) {
        return new SmsFactory(client, proxy);
    }
}
