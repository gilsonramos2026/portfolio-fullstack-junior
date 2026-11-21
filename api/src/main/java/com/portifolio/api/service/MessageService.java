package com.portifolio.api.service;

import com.portifolio.api.dto.request.MessageRequest;
import com.portifolio.api.dto.response.MessageResponse;

import java.util.List;

public interface MessageService {

    /**
     * Registra um recado enviado por um visitante.
     *
     * @param request   dados do recado enviado
     * @param clientKey identificador do remetente (IP) usado no controle da taxa
     */
    void receive(MessageRequest request, String clientKey);

    List<MessageResponse> findAll();

    long countUnread();

    MessageResponse markAsRead(Long id);

    void delete(Long id);
}