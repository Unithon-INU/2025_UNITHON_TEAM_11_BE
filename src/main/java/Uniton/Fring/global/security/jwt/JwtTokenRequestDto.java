package Uniton.Fring.global.security.jwt;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class JwtTokenRequestDto {

    private String refreshToken;
}
