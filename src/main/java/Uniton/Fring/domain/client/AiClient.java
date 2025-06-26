package Uniton.Fring.domain.client;

import Uniton.Fring.domain.client.dto.req.RelatedProductsRequestDto;
import Uniton.Fring.domain.client.dto.req.TitleSuggestionRequestDto;
import Uniton.Fring.domain.client.dto.res.RecommendedProductsResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class AiClient {

    private final RestTemplate restTemplate;

    @Value("${ai.base-url}")
    private String baseUrl;

    private URI uri(String path) {
        return UriComponentsBuilder.fromUriString(baseUrl).path(path).build().toUri();
    }

    public List<Long> relatedProducts(RelatedProductsRequestDto relatedProductsRequestDto) {

        try {
            ResponseEntity<RecommendedProductsResponseDto> res =
                    restTemplate.exchange(
                            uri("/index_product_data"),
                            HttpMethod.POST,
                            new HttpEntity<>(relatedProductsRequestDto),
                            new ParameterizedTypeReference<RecommendedProductsResponseDto>() {});

            return res.getBody() == null
                    ? List.of()
                    : res.getBody().getRecommendedProductIds();

        } catch (RestClientException e) {
            log.warn("AI product-related 실패", e);
            return List.of();
        }
    }

    public String suggestTitle(TitleSuggestionRequestDto titleSuggestionRequestDto) {

        try {
            return restTemplate.postForObject(uri("/title-suggest"), titleSuggestionRequestDto, String.class);
        } catch (RestClientException e) {
            log.warn("AI title-suggest 실패", e);
            return null;
        }
    }
}
