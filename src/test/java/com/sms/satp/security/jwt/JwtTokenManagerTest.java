package com.sms.satp.security.jwt;

import static com.sms.satp.utils.JwtUtils.TOKEN_TYPE;
import static com.sms.satp.utils.JwtUtils.TOKEN_USER_ID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.sms.satp.entity.system.UserEntity;
import com.sms.satp.repository.SystemRoleRepository;
import com.sms.satp.repository.UserRepository;
import com.sms.satp.security.TokenType;
import com.sms.satp.security.pojo.CustomUser;
import com.sms.satp.security.strategy.SecurityStrategyFactory;
import com.sms.satp.security.strategy.impl.UserSecurityStrategy;
import com.sms.satp.service.UserGroupService;
import com.sms.satp.utils.JwtUtils;
import com.sms.satp.utils.SecurityUtil;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwsHeader;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SigningKeyResolver;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.SignatureException;
import java.time.Duration;
import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

public class JwtTokenManagerTest {

    private static String token = "token";
    private static String id = "id";
    private static String userTokenType = "USER";
    private static JwsHeader<?> jwsHeader;
    private static MockedStatic<JwtUtils> jwtUtilsMockedStatic;
    private static MockedStatic<SecurityUtil> securityUtilMockedStatic;

    static {
        jwsHeader = mock(JwsHeader.class);
        when(jwsHeader.get(TOKEN_USER_ID)).thenReturn(id);
        when(jwsHeader.get(TOKEN_TYPE)).thenReturn(userTokenType);
        jwtUtilsMockedStatic = Mockito.mockStatic(JwtUtils.class);
        securityUtilMockedStatic = Mockito.mockStatic(SecurityUtil.class);
    }

    @AfterAll
    public static void close() {
        jwtUtilsMockedStatic.close();
        securityUtilMockedStatic.close();
    }

    SecurityStrategyFactory securityStrategyFactory = mock(SecurityStrategyFactory.class);
    SigningKeyResolver signingKeyResolver = mock(SigningKeyResolver.class);
    UserSecurityStrategy userSecurityStrategy = mock(UserSecurityStrategy.class);
    UserRepository userRepository = mock(UserRepository.class);
    UserGroupService userGroupService = mock(UserGroupService.class);
    SystemRoleRepository roleRepository = mock(SystemRoleRepository.class);
    JwtTokenManager jwtTokenManager = new JwtTokenManager(userRepository, userGroupService, roleRepository,
        securityStrategyFactory,
        signingKeyResolver);

    @Test
    @DisplayName("Generate token for user successfully")
    public void generateAccessTokenTestSuccess() {
        when(securityStrategyFactory.fetchSecurityStrategy(TokenType.USER)).thenReturn(userSecurityStrategy);
        Duration duration = Duration.ofDays(7);
        when(userSecurityStrategy.obtainTokenExpirationTime()).thenReturn(duration);
        CustomUser customUser = new CustomUser("username", "password",
            Arrays.asList(new SimpleGrantedAuthority("role1"), new SimpleGrantedAuthority("role2")),
            "id", "name", TokenType.USER);
        String token = "123";
        jwtUtilsMockedStatic.when(() -> JwtUtils.encodeJwt(any(CustomUser.class),
            any(SigningKeyResolver.class), any(Duration.class))).thenReturn(Optional.of(token));
        String accessToken = jwtTokenManager.generateAccessToken(customUser);
        assertThat(accessToken).isEqualTo(token);
    }

    @Test
    @DisplayName("Failed to generate token for user")
    public void generateAccessTokenTest() {
        when(securityStrategyFactory.fetchSecurityStrategy(TokenType.USER)).thenReturn(userSecurityStrategy);
        Duration duration = Duration.ofDays(7);
        when(userSecurityStrategy.obtainTokenExpirationTime()).thenReturn(duration);
        CustomUser customUser = new CustomUser("username", "password",
            Arrays.asList(new SimpleGrantedAuthority("role1"), new SimpleGrantedAuthority("role2")),
            "id", "name", TokenType.USER);
        jwtUtilsMockedStatic.when(() -> JwtUtils.encodeJwt(any(CustomUser.class),
            any(SigningKeyResolver.class), any(Duration.class))).thenReturn(Optional.empty());
        assertThatThrownBy(() -> jwtTokenManager.generateAccessToken(customUser))
            .isInstanceOf(RuntimeException.class);
    }

    @Test
    @DisplayName("Parsing out identity information based on token")
    public void createAuthenticationTest() {
        String username = "username";
        String email = "email";
        String groupId = "groupId";
        jwtUtilsMockedStatic.when(() -> JwtUtils.decodeJwt(any(String.class), any(SigningKeyResolver.class)))
            .thenReturn(jwsHeader);
        Optional<UserEntity> userEntityOptional =
            Optional.ofNullable(UserEntity.builder().id(id).username(username).email(email).groupId(groupId).build());
        when(userRepository.findById(id)).thenReturn(userEntityOptional);
        when(userGroupService.getAuthoritiesByUserGroup(groupId)).thenReturn(Collections.emptyList());
        Authentication mockAuthentication = mock(Authentication.class);
        securityUtilMockedStatic.when(() -> SecurityUtil.newAuthentication(id, email, username,
            Collections.emptyList(), TokenType.USER)).thenReturn(mockAuthentication);
        Authentication authentication = jwtTokenManager.createAuthentication(token);
        assertThat(authentication).isEqualTo(mockAuthentication);
    }

    @Test
    @DisplayName("Failed to Parse out identity information based on token because of exception")
    public void createAuthenticationWhileThrowExceptionTest() {
        jwtUtilsMockedStatic.when(() -> JwtUtils.decodeJwt(any(String.class), any(SigningKeyResolver.class)))
            .thenReturn(jwsHeader);
        when(userRepository.findById(id)).thenThrow(RuntimeException.class);
        Authentication authentication = jwtTokenManager.createAuthentication(token);
        assertThat(authentication).isEqualTo(null);
    }

    @Test
    @DisplayName("Failed to Parse out identity information based on token because of user id is not exist")
    public void createAuthenticationFailedTest() {
        jwtUtilsMockedStatic.when(() -> JwtUtils.decodeJwt(any(String.class), any(SigningKeyResolver.class)))
            .thenReturn(jwsHeader);
        when(userRepository.findById(id)).thenReturn(Optional.empty());
        Authentication authentication = jwtTokenManager.createAuthentication(token);
        assertThat(authentication).isEqualTo(null);
    }

    @Test
    @DisplayName("Parsing out user id based on token")
    public void getUserIdTest() {
        jwtUtilsMockedStatic.when(() -> JwtUtils.decodeJwt(any(String.class), any(SigningKeyResolver.class)))
            .thenReturn(jwsHeader);
        assertThat(jwtTokenManager.getUserId(token)).isEqualTo(id);
    }

    @Test
    @DisplayName("Verify token")
    public void validateTest() {
        jwtUtilsMockedStatic.when(() -> JwtUtils.decodeJwt(any(String.class), any(SigningKeyResolver.class)))
            .thenReturn(jwsHeader);
        assertThat(jwtTokenManager.validate(token)).isEqualTo(true);
    }

    @Test
    @DisplayName("Throw SignatureException when verify token")
    public void validateSignatureExceptionTest() {
        jwtUtilsMockedStatic.when(() -> JwtUtils.decodeJwt(any(String.class), any(SigningKeyResolver.class)))
            .thenThrow(SignatureException.class);
        assertThat(jwtTokenManager.validate(token)).isEqualTo(false);
    }

    @Test
    @DisplayName("Throw MalformedJwtException when verify token")
    public void validateMalformedJwtExceptionTest() {
        jwtUtilsMockedStatic.when(() -> JwtUtils.decodeJwt(any(String.class), any(SigningKeyResolver.class)))
            .thenThrow(MalformedJwtException.class);
        assertThat(jwtTokenManager.validate(token)).isEqualTo(false);
    }

    @Test
    @DisplayName("Throw ExpiredJwtException when verify token")
    public void validateExpiredJwtExceptionTest() {
        jwtUtilsMockedStatic.when(() -> JwtUtils.decodeJwt(any(String.class), any(SigningKeyResolver.class)))
            .thenThrow(ExpiredJwtException.class);
        assertThat(jwtTokenManager.validate(token)).isEqualTo(false);
    }

    @Test
    @DisplayName("Throw UnsupportedJwtException when verify token")
    public void validateUnsupportedJwtExceptionTest() {
        jwtUtilsMockedStatic.when(() -> JwtUtils.decodeJwt(any(String.class), any(SigningKeyResolver.class)))
            .thenThrow(UnsupportedJwtException.class);
        assertThat(jwtTokenManager.validate(token)).isEqualTo(false);
    }

    @Test
    @DisplayName("Throw IllegalArgumentException when verify token")
    public void validateIllegalArgumentExceptionTest() {
        jwtUtilsMockedStatic.when(() -> JwtUtils.decodeJwt(any(String.class), any(SigningKeyResolver.class)))
            .thenThrow(IllegalArgumentException.class);
        assertThat(jwtTokenManager.validate(token)).isEqualTo(false);
    }

}