package com.andreamarino.pharmazon.security.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import static com.andreamarino.pharmazon.security.user.Role.*;
import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@EnableMethodSecurity
public class SecurityConfiguration {

        private static final String[] WHITE_LIST_URL = {"/auth/**", "/chat-socket/**", "/user/sendEmail"};
        private final JwtAuthenticationFilter jwtAuthFilter;
        private final AuthenticationProvider authenticationProvider;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {        
        http
                .csrf(AbstractHttpConfigurer::disable)
                .csrf((csrf) -> csrf.disable())
                .authorizeHttpRequests(req ->
                        req.requestMatchers(WHITE_LIST_URL)
                                .permitAll()
                                .requestMatchers("/user/updatePassword").hasAnyRole(ADMIN.name(), CLIENT.name())
                                .requestMatchers("/address/insert").hasAnyRole(CLIENT.name())
                                .requestMatchers("/address/getUser").hasAnyRole(CLIENT.name())
                                .requestMatchers("/address/update").hasAnyRole(CLIENT.name())
                                .requestMatchers("/address/deactivate").hasAnyRole(CLIENT.name())
                                .requestMatchers("/user/getUser").hasAnyRole(ADMIN.name(), CLIENT.name())
                                .requestMatchers("/user/update").hasAnyRole(ADMIN.name(), CLIENT.name())
                                .requestMatchers("/user/delete").hasAnyRole(ADMIN.name(), CLIENT.name())
                                .requestMatchers("/user/list").hasAnyRole(ADMIN.name())
                                .requestMatchers("/client/addAddress").hasAnyRole(CLIENT.name())
                                .requestMatchers("/client/list").hasAnyRole(CLIENT.name())
                                .requestMatchers("/category/insert").hasAnyRole(ADMIN.name())
                                .requestMatchers("/category/update").hasAnyRole(ADMIN.name())
                                .requestMatchers("/category/list").hasAnyRole(ADMIN.name(), CLIENT.name())
                                .requestMatchers("/product/list").hasAnyRole(ADMIN.name(), CLIENT.name())
                                .requestMatchers("/product/listByCategory").hasAnyRole(ADMIN.name(), CLIENT.name())
                                .requestMatchers("/product/listByName").hasAnyRole(ADMIN.name(), CLIENT.name())
                                .requestMatchers("/product/userCart").hasAnyRole(ADMIN.name(), CLIENT.name())
                                .requestMatchers("/product/insert").hasAnyRole(ADMIN.name())
                                .requestMatchers("/product/update").hasAnyRole(ADMIN.name())
                                .requestMatchers("/product/getProduct").hasAnyRole(ADMIN.name(), CLIENT.name())
                                .requestMatchers("/product/hide").hasAnyRole(ADMIN.name())
                                .requestMatchers("/cart/getCartItem/delivered").hasAnyRole(CLIENT.name())
                                .requestMatchers("/product/activate").hasAnyRole(ADMIN.name())
                                .requestMatchers("/service/list").hasAnyRole(ADMIN.name(), CLIENT.name())
                                .requestMatchers("/service/getService").hasAnyRole(ADMIN.name(), CLIENT.name())
                                .requestMatchers("/service/getService/name").hasAnyRole(ADMIN.name(), CLIENT.name())
                                .requestMatchers("/service/insert").hasAnyRole(ADMIN.name())
                                .requestMatchers("/service/update").hasAnyRole(ADMIN.name())
                                .requestMatchers("/service/hide").hasAnyRole(ADMIN.name())
                                .requestMatchers("/booking/insert").hasAnyRole(ADMIN.name(), CLIENT.name())
                                .requestMatchers("/booking/list").hasAnyRole(ADMIN.name())
                                .requestMatchers("/booking/list/notAccepted").hasAnyRole(ADMIN.name())
                                .requestMatchers("/booking/list/accepted").hasAnyRole(ADMIN.name())
                                .requestMatchers("/booking/update").hasAnyRole(ADMIN.name())
                                .requestMatchers("/booking/delete").hasAnyRole(ADMIN.name(), CLIENT.name())
                                .requestMatchers("/order/insert").hasAnyRole(CLIENT.name())
                                .requestMatchers("/order/checkOrder").hasAnyRole(ADMIN.name())
                                .requestMatchers("/order/listOrder/NoSomeStates").hasAnyRole(ADMIN.name())
                                .requestMatchers("/order/listOrder/waiting").hasAnyRole(ADMIN.name())
                                .requestMatchers("/order/listOrder/history").hasAnyRole(ADMIN.name())
                                .requestMatchers("/order/listOrder/user").hasAnyRole(CLIENT.name())
                                .requestMatchers("/order/updateState").hasAnyRole(ADMIN.name())
                                .requestMatchers("/cart/insertProduct").hasAnyRole(CLIENT.name())
                                .requestMatchers("/cart/removeProduct").hasAnyRole(CLIENT.name())
                                .requestMatchers("/cart/list").hasAnyRole(ADMIN.name(), CLIENT.name())
                                .requestMatchers("/cart/preparationOrder").hasAnyRole(CLIENT.name())
                                .requestMatchers("/creditCard/insert").hasAnyRole(CLIENT.name())
                                .requestMatchers("/creditCard/updateBalance").hasAnyRole(CLIENT.name())
                                .requestMatchers("/creditCard/deactivate").hasAnyRole(CLIENT.name())
                                .requestMatchers("/creditCard/list").hasAnyRole(CLIENT.name())
                                .requestMatchers("/email/send").hasAnyRole(CLIENT.name())
                                .requestMatchers("/chat/insert").hasAnyRole(CLIENT.name())
                                .requestMatchers("/chat/getChatAccepted").hasAnyRole(ADMIN.name(), CLIENT.name())
                                .requestMatchers("/chat/getChatWaiting").hasAnyRole(ADMIN.name())
                                .requestMatchers("/chat/getMessages").hasAnyRole(ADMIN.name())
                                .requestMatchers("/chat/getClientOnline").hasAnyRole(ADMIN.name())
                                .requestMatchers("/chat/updateChats").hasAnyRole(ADMIN.name())
                                .requestMatchers("/chat/closeChat").hasAnyRole(ADMIN.name(), CLIENT.name())
                                .requestMatchers("/feedback/insert").hasAnyRole(CLIENT.name())
                                .requestMatchers("/feedback/update").hasAnyRole(CLIENT.name())
                                .requestMatchers("/feedback/delete").hasAnyRole(CLIENT.name(), ADMIN.name())
                                .requestMatchers("/feedback/listFeedback/user").hasAnyRole(CLIENT.name())
                                .requestMatchers("/feedback/listFeedback").hasAnyRole(ADMIN.name())
                                .requestMatchers("/state/list").hasAnyRole(ADMIN.name())
                                .anyRequest()
                                .authenticated()
                )
                .sessionManagement(session -> session.sessionCreationPolicy(STATELESS))
                .authenticationProvider(authenticationProvider)
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);
        http.cors();
        return http.build();
    }
}
