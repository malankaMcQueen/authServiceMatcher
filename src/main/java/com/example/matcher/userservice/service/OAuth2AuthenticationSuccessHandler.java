package com.example.matcher.userservice.service;

import com.example.matcher.userservice.aspect.AspectAnnotation;
import com.example.matcher.userservice.configuration.SecurityConfiguration;
import com.example.matcher.userservice.dto.UserDTO;
import com.example.matcher.userservice.model.User;
import com.example.matcher.userservice.repository.UserRepository;
//import io.jsonwebtoken.io.IOException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.io.IOException;
import java.util.Arrays;

@Component
@RequiredArgsConstructor

public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    private static final Logger logger = LoggerFactory.getLogger(SecurityConfiguration.class);
    private final UserRepository userRepository;
    private final UserService userService;
    private final OAuth2AuthorizedClientService authorizedClientService;

    @AspectAnnotation
    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication auth
    ) throws IOException {
        if (auth instanceof OAuth2AuthenticationToken auth2AuthenticationToken) {
            var principal = auth2AuthenticationToken.getPrincipal();
            String userEmail = principal.getAttribute("email"); // Email пользователя
            User user = userService.getByEmail(userEmail);
            if (user == null) {
                UserDTO userDTO = new UserDTO();
                userDTO.setEmail(userEmail);
                user = userService.registerUser(userDTO);
                logger.info("Create new user: " + user);
            }
            logger.info("User auth: " + user);
        }
        else {
            logger.error("SOMETHING ERROR IN SUCCESS HANDLER");
        }
        super.clearAuthenticationAttributes(request);
        super.getRedirectStrategy().sendRedirect(request, response, "/UserService/auth/google");
    }
}
