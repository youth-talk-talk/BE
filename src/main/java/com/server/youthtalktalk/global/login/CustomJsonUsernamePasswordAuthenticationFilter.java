package com.server.youthtalktalk.global.login;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.server.youthtalktalk.dto.member.apple.AppleTokenResponseDto;
import com.server.youthtalktalk.global.response.BaseResponse;
import com.server.youthtalktalk.global.response.BaseResponseCode;
import com.server.youthtalktalk.global.response.exception.BusinessException;
import com.server.youthtalktalk.global.response.exception.InvalidValueException;
import com.server.youthtalktalk.global.util.AppleAuthUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.util.StreamUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import static com.server.youthtalktalk.global.response.BaseResponseCode.APPLE_USER_IDENTIFIER_ERROR;
import static com.server.youthtalktalk.global.response.BaseResponseCode.MEMBER_ACCESS_DENIED;

/**
 * "/login" 요청 왔을 때 JSON 값을 매핑 처리하는 필터
 * 스프링 시큐리티의 폼 기반의 UsernamePasswordAuthenticationFilter를 커스텀하여
 * 폼 로그인 대신 Json Login만 처리하도록 설정함
 */
@Slf4j
public class CustomJsonUsernamePasswordAuthenticationFilter extends AbstractAuthenticationProcessingFilter {

    private static final String DEFAULT_LOGIN_REQUEST_URL = "/login";
    private static final String HTTP_METHOD = "POST";
    private static final String CONTENT_TYPE = "application/json";
    private static final String USERNAME_KEY = "username";
    private static final String AUTHORIZATION_CODE = "authorizationCode"; // 애플 로그인
    private static final String IDENTITY_TOKEN = "identityToken";
    private static final AntPathRequestMatcher DEFAULT_LOGIN_PATH_REQUEST_MATCHER =
            new AntPathRequestMatcher(DEFAULT_LOGIN_REQUEST_URL, HTTP_METHOD); // POST "/login" 으로 온 요청

    private final ObjectMapper objectMapper;
    private final AppleAuthUtil appleAuthUtil;

    public CustomJsonUsernamePasswordAuthenticationFilter(ObjectMapper objectMapper, AppleAuthUtil appleAuthUtil) {
        super(DEFAULT_LOGIN_PATH_REQUEST_MATCHER);
        this.objectMapper = objectMapper;
        this.appleAuthUtil = appleAuthUtil;
    }

    /**
     * 인증 처리 메소드
     *
     * UsernamePasswordAuthenticationFilter와 동일하게 UsernamePasswordAuthenticationToken 사용
     * StreamUtils를 통해 request에서 messageBody(JSON) 반환
     *
     * 요청 JSON Example
     * {
     *    "username" : "kakao12345678"
     * }
     * messageBody를 objectMapper.readValue()로 Map으로 변환 (Key : JSON의 키 -> username)
     * Map의 Key로 해당 username 추출 후
     * CustomAuthenticationToken의 파라미터 principal, credentials에 대입
     *
     * AbstractAuthenticationProcessingFilter(부모)의 getAuthenticationManager()로 AuthenticationManager 객체를 반환 받은 후
     * authenticate()의 파라미터로 CustomAuthenticationToken 객체를 넣고 인증 처리
     * (여기서 AuthenticationManager 객체는 ProviderManager -> SecurityConfig에서 설정)
     */
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException, IOException, ServletException {
        if(request.getContentType() == null || !request.getContentType().equals(CONTENT_TYPE)) {
            throw new AuthenticationServiceException("Authentication Content-Type not supported: " + request.getContentType());
        }

        String messageBody = StreamUtils.copyToString(request.getInputStream(), StandardCharsets.UTF_8);
        Map<String, String> loginDataMap = objectMapper.readValue(messageBody, Map.class);
        String username = loginDataMap.get(USERNAME_KEY);

        // apple 토큰 검증 과정
        if(username.startsWith("apple")){ // 애플 로그인 요청일 경우
            String identityToken = loginDataMap.get(IDENTITY_TOKEN);
            String authorizationCode = loginDataMap.get(AUTHORIZATION_CODE);
            if(identityToken==null || authorizationCode==null){
                throw new InvalidValueException(BaseResponseCode.INVALID_INPUT_VALUE);
            }

            String sub = appleVerification(identityToken,authorizationCode); // 검증 완료된 애플 고유 아이디
            log.info("[AUTH] apple login sub = {}",username);
            if(!username.equals("apple"+sub))
                throw new BusinessException(APPLE_USER_IDENTIFIER_ERROR);
        }

        UsernamePasswordAuthenticationToken authRequest =
                new UsernamePasswordAuthenticationToken(username, "");

        return this.getAuthenticationManager().authenticate(authRequest);
    }

    private String appleVerification(String identityToken, String authorizationCode){
        log.info("[AUTH] apple login request : identityToken = {} authorizationCode = {}", identityToken, authorizationCode);

        // identityToken 서명 검증
        Claims claims = appleAuthUtil.verifyIdentityToken(identityToken);
        log.info("[AUTH] apple login verification : identityToken 검증 성공");
        // apple ID Server에 애플 토큰 요청
        AppleTokenResponseDto appleTokenResponseDto = appleAuthUtil.getAppleToken(authorizationCode);

        String idToken = appleTokenResponseDto.idToken();
        log.info("[AUTH] apple login token request : idToken = {}",idToken);
        // 유효한 idToken이 없을 경우
        if(idToken==null || idToken.isEmpty()){
            throw new BusinessException(BaseResponseCode.APPLE_NEED_SIGN_UP);
        }
        // 유효한 idToken이 있을 경우 -> 애플 회원가입을 완료한 유저

        // sub(고유 id) 클레임 추출
        String sub = claims.get("sub", String.class);
        String email = claims.get("email", String.class);
        log.info("[AUTH] apple login token request : idToken sub(고유 id) = {} email = {}",sub,email);
        return sub;
    }
}
