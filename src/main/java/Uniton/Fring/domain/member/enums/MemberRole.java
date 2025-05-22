package Uniton.Fring.domain.member.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum MemberRole {
    ADMIN("ROLE_ADMIN"),
    CONSUMER("ROLE_CONSUMER"),
    FARMER("ROLE_FARMER"),;

    private final String authority;
}
