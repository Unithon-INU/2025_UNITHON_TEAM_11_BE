package Uniton.Fring.global.security.jwt;

import Uniton.Fring.domain.member.entity.Member;
import Uniton.Fring.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final MemberRepository memberRepository;

    // 이메일 기반 사용자 조 ( 식별자 : 이메일 )
    @Override
    public UserDetailsImpl loadUserByUsername(String username) throws UsernameNotFoundException {
        Member member =  memberRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException(username));

        // UserDetails 객체 생성
        return new UserDetailsImpl(member);
    }
}
