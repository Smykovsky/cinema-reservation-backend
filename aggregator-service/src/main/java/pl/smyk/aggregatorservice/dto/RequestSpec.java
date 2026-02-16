package pl.smyk.aggregatorservice.dto;

import lombok.Builder;
import org.springframework.http.HttpMethod;

import java.util.Map;

import lombok.Getter;

@Getter
@Builder
public class RequestSpec<R> {

    private String url;
    private HttpMethod method;
    private Map<String, ?> queryParams;
    private Map<String, ?> pathVariables;
    private Map<String, String> headers;
    private Class<R> responseType;
}