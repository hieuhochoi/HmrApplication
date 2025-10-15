package com.example.hrmapplication.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.view.RedirectView;

@Controller
public class LogoutController {

    @Value("${spring.security.oauth2.client.provider.keycloak.issuer-uri}")
    private String issuerUri;

    @GetMapping("/logout")
    public RedirectView logout(HttpServletRequest request, Authentication authentication) {
        // Logout từ Spring Security
        new SecurityContextLogoutHandler().logout(request, null, null);

        // Tạo URL logout của Keycloak
        String logoutUrl = issuerUri + "/protocol/openid-connect/logout";

        // Redirect về trang chủ sau khi logout
        String redirectUri = request.getScheme() + "://" + request.getServerName() +
                ":" + request.getServerPort();

        // Nếu có thông tin user từ OIDC
        if (authentication != null && authentication.getPrincipal() instanceof OidcUser) {
            OidcUser oidcUser = (OidcUser) authentication.getPrincipal();
            String idToken = oidcUser.getIdToken().getTokenValue();

            logoutUrl += "?id_token_hint=" + idToken +
                    "&post_logout_redirect_uri=" + redirectUri;
        } else {
            logoutUrl += "?post_logout_redirect_uri=" + redirectUri;
        }

        return new RedirectView(logoutUrl);
    }
}