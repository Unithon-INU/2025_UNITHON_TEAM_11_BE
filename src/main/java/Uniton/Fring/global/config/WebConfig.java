package Uniton.Fring.global.config;

import io.micrometer.common.lang.Nullable;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/uploads/**") // 브라우저에서 접근할 URL 경로
                .addResourceLocations("file:./uploads/"); // 실제 파일이 저장된 로컬 경로
    }

    // multipart/form-data의 charset 문제 해결을 위한 커스텀 설정 (추가된 코드)
    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        // 기존 FormHttpMessageConverter 제거
        converters.removeIf(converter -> converter instanceof FormHttpMessageConverter);

        // 커스텀 FormHttpMessageConverter 추가 (charset 무시)
        FormHttpMessageConverter converter = new FormHttpMessageConverter() {
            @Override
            public boolean canRead(Class<?> clazz, @Nullable MediaType mediaType) {
                if (mediaType != null && mediaType.getType().equals("multipart")) {
                    // charset 파라미터를 제거한 MediaType 생성
                    MediaType mediaTypeWithoutCharset = MediaType.valueOf(
                            mediaType.getType() + "/" + mediaType.getSubtype()
                    );
                    return super.canRead(clazz, mediaTypeWithoutCharset);
                }
                return super.canRead(clazz, mediaType);
            }
        };
        converters.add(converter);
    }
}
