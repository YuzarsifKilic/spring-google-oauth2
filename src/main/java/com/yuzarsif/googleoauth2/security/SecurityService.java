package com.yuzarsif.googleoauth2.security;

import com.yuzarsif.googleoauth2.models.BaseUser;
import com.yuzarsif.googleoauth2.models.LoginProvider;
import com.yuzarsif.googleoauth2.repositories.BaseUserRepository;
import lombok.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

@Service
@Value
public class SecurityService implements UserDetailsManager {

    PasswordEncoder passwordEncoder;
    Executor executor;

    DefaultOAuth2UserService oauth2Delegate = new DefaultOAuth2UserService();
    OidcUserService oidcDelegate = new OidcUserService();
    BaseUserRepository baseUserRepository;


    @Bean
    OAuth2UserService<OidcUserRequest, OidcUser> oidcLoginHandler() {
        return userRequest -> {
            LoginProvider loginProvider = LoginProvider.valueOf(userRequest.getClientRegistration().getRegistrationId().toUpperCase());

            OidcUser oidcUser = oidcDelegate.loadUser(userRequest);
            SecurityUser securityUser = SecurityUser
                    .builder()
                    .loginProvider(loginProvider)
                    .username(oidcUser.getFullName())
                    .email(oidcUser.getEmail())
                    .attributes(oidcUser.getAttributes())
                    .authorities(oidcUser.getAuthorities())
                    .build();

            saveOauth2User(securityUser);

            return securityUser;
        };
    }

    private void saveOauth2User(SecurityUser securityUser) {
        CompletableFuture.runAsync(() -> createUser(securityUser), executor);
    }

    private void createUser(SecurityUser securityUser) {
        BaseUser baseUser = saveUserIfNotExists(securityUser);
    }

    private BaseUser saveUserIfNotExists(SecurityUser securityUser) {
        BaseUser baseUser = baseUserRepository.findByEmail(securityUser.getEmail())
                .orElseGet(() -> baseUserRepository
                        .save(BaseUser
                                .builder()
                                .username(securityUser.getUsername())
                                .email(securityUser.getEmail())
                                .loginProvider(securityUser.getLoginProvider())
                                .build()));

        return baseUser;
    }

    @Bean
    OAuth2UserService<OAuth2UserRequest, OAuth2User> oauth2LoginHandler() {
        return userRequest -> {
            LoginProvider loginProvider = LoginProvider.valueOf(userRequest.getClientRegistration().getRegistrationId().toUpperCase());

            OAuth2User oAuth2User = oauth2Delegate.loadUser(userRequest);

            SecurityUser securityUser = SecurityUser
                    .builder()
                    .loginProvider(loginProvider)
                    .attributes(oAuth2User.getAttributes())
                    .authorities(oAuth2User.getAuthorities())
                    .build();

            saveOauth2User(securityUser);

            return securityUser;
        };
    }

    @Override
    public void createUser(UserDetails user) {
        if (userExists(user.getUsername())) {
            throw new IllegalArgumentException(String.format("UserEntity %s already exists!", user.getUsername()));
        }

        createUser(SecurityUser
                .builder()
                .loginProvider(LoginProvider.APP)
                .username(user.getUsername())
                .authorities(user.getAuthorities())
                .build());
    }

    @Override
    public void updateUser(UserDetails user) {

    }

    @Override
    public void deleteUser(String username) {

    }

    @Override
    public void changePassword(String oldPassword, String newPassword) {

    }

    @Override
    public boolean userExists(String username) {
        return baseUserRepository.existsById(username);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return baseUserRepository
                .findByEmail(username)
                .map(user -> SecurityUser
                        .builder()
                        .username(user.getUsername())
                        .email(user.getEmail())
                        .loginProvider(user.getLoginProvider())
                        .authorities(user
                                .getAuthorities()
                                .stream()
                                .map(userAuthority -> new SimpleGrantedAuthority(userAuthority.getName()))
                                .toList())
                        .build())
                .orElseThrow(() ->new UsernameNotFoundException(String.format("%s not found", username)));
    }
}
