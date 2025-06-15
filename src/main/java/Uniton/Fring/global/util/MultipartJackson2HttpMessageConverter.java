package Uniton.Fring.global.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.AbstractJackson2HttpMessageConverter;
import org.springframework.stereotype.Component;

import java.lang.reflect.Type;

@Component
public class MultipartJackson2HttpMessageConverter extends AbstractJackson2HttpMessageConverter {

     // "Content-Type: multipart/form-data" 헤더를 지원하는 HTTP 요청 변환기
    public MultipartJackson2HttpMessageConverter(ObjectMapper objectMapper) {
        super(objectMapper, MediaType.APPLICATION_OCTET_STREAM);
    }

    // 읽기 가능한지 판단 (클래스 + MediaType)
    @Override
    public boolean canRead(Class<?> clazz, MediaType mediaType) {
        if (mediaType == null) return false;
        return MediaType.MULTIPART_FORM_DATA.includes(mediaType); // charset 포함해도 OK
    }

    // 읽기 가능한지 판단 (MediaType만)
    @Override
    protected boolean canRead(MediaType mediaType) {
        return MediaType.MULTIPART_FORM_DATA.includes(mediaType);
    }

    // 쓰기는 전부 비활성화 (이 컨버터는 읽기 전용)
    @Override
    public boolean canWrite(Class<?> clazz, MediaType mediaType) {
        return false;
    }

    @Override
    public boolean canWrite(Type type, Class<?> clazz, MediaType mediaType) {
        return false;
    }

    @Override
    protected boolean canWrite(MediaType mediaType) {
        return false;
    }
}