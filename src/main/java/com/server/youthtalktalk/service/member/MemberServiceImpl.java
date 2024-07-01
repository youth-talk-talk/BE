package com.server.youthtalktalk.service.member;

import com.server.youthtalktalk.domain.member.Member;
import com.server.youthtalktalk.domain.member.SocialType;
import com.server.youthtalktalk.dto.member.CheckIfJoinedDto;
import com.server.youthtalktalk.dto.member.apple.AppleDto;
import com.server.youthtalktalk.dto.member.apple.AppleTokenResponseDto;
import com.server.youthtalktalk.global.response.BaseResponseCode;
import com.server.youthtalktalk.global.response.exception.BusinessException;
import com.server.youthtalktalk.global.response.exception.member.AppleNeedAddSignUpException;
import com.server.youthtalktalk.global.util.AppleAuthUtil;
import com.server.youthtalktalk.repository.MemberRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import static com.server.youthtalktalk.dto.member.apple.AppleDto.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService {

    private final MemberRepository memberRepository;
    private final AppleAuthUtil appleAuthUtil;

    @Override
    @Transactional
    public String checkIfJoined(CheckIfJoinedDto checkIfJoinedDto) {
        String socialType = checkIfJoinedDto.getSocialType().toUpperCase();
        String socialId = checkIfJoinedDto.getSocialId();
        return memberRepository.findBySocialTypeAndSocialId(SocialType.valueOf(socialType), socialId)
                .map(member -> member.getRole().name()).orElseGet(() -> "회원정보 없음");
    }

    @Override
    @Transactional
    public void appleLogin(String userIdentifier, String identityToken, String authorizationCode) {
        log.info("[AUTH] apple login : userIdentifier = {} identityToken = {} authorizationCode = {}", userIdentifier, identityToken, authorizationCode);
        // 회원가입하지 않은 유저
        if(userIdentifier == null || identityToken == null || authorizationCode == null) {
            throw new BusinessException(BaseResponseCode.APPLE_NEED_SIGN_UP);
        }

        // identityToken 서명 검증
        Claims claims = appleAuthUtil.verifyIdentityToken(identityToken);
        log.info("[AUTH] apple login : identityToken 검증 claims sub = {}",claims.getSubject());
        // apple ID Server에 애플 토큰 요청
        AppleTokenResponseDto appleTokenResponseDto = appleAuthUtil.getAppleToken(authorizationCode);
        // 유효한 idToken이 없을 경우
        String idToken = appleTokenResponseDto.idToken();
        log.info("[AUTH] apple login : idToken = {}",idToken);
        if(idToken.isEmpty()){
            throw new BusinessException(BaseResponseCode.APPLE_NEED_SIGN_UP);
        }
        // 유효한 idToken이 있을 경우 -> 애플 회원가입을 완료한 유저
        Claims idTokenClaims = Jwts.parserBuilder()
                .build()
                .parseClaimsJws(idToken)
                .getBody();

        // sub(고유 id) 클레임 추출
        String sub = claims.get("sub", String.class);
        String email = claims.get("email", String.class);
        String emailVerified = claims.get("email_verified", String.class);

        log.info("[AUTH] apple login : idToken sub(고유 id) = {} email = {}",sub,email);

        //Member member = memberRepository.findBySocialTypeAndSocialId(SocialType.APPLE, sub).orElseThrow(AppleNeedAddSignUpException::new);
        // 회원 존재 시 jwt 토큰 발행 로직
        //
        //
    }
}
