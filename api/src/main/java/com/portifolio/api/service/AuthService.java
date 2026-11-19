package com.portifolio.api.service;

import com.portifolio.api.dto.request.ChangePasswordRequest;
import com.portifolio.api.dto.request.LoginRequest;
import com.portifolio.api.dto.response.LoginResponse;

public interface AuthService {

    /**
     * @param clientKey identificador do cliente (IP) usado no bloqueio por tentativas excessivas
     */
    LoginResponse login(LoginRequest request, String clientKey);

    void changePassword(String username, ChangePasswordRequest request);
}