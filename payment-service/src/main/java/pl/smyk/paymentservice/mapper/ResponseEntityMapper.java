package pl.smyk.paymentservice.mapper;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.HashMap;

@RequiredArgsConstructor
@Service
public class ResponseEntityMapper {
    private final ObjectMapper objectMapper;

    public HashMap<String, Object> convertResponseToMap(ResponseEntity<?> response) {
        Object body = response.getBody();
        HashMap<String, Object> responseMap =objectMapper.convertValue(body, HashMap.class);
        return responseMap;
    }
}
