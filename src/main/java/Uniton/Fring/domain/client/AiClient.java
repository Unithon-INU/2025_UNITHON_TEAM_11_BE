package Uniton.Fring.domain.client;

import Uniton.Fring.domain.client.dto.req.RelatedProductsRequestDto;
import Uniton.Fring.domain.client.dto.req.TitleSuggestionRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Component
@RequiredArgsConstructor
public class AiClient {

    private final RestTemplate restTemplate;

    @Value("${ai.base-url}")
    private String baseUrl;

    private String url(String path) {
        return baseUrl + path;
    }

    public List<Long> getRecommendedProductIds(RelatedProductsRequestDto dto) {
        ResponseEntity<List<Long>> response = restTemplate.exchange(
                url("product"),
                HttpMethod.POST,
                new HttpEntity<>(dto),
                new ParameterizedTypeReference<List<Long>>() {}
        );
        return response.getBody();
    }

    public String getRecommendedTitle(TitleSuggestionRequestDto dto) {
        return restTemplate.postForObject(url("title"), dto, String.class);
    }
}
