package com.portifolio.api.service.impl;

import com.portifolio.api.dto.request.MessageRequest;
import com.portifolio.api.dto.response.MessageResponse;
import com.portifolio.api.entity.ContactMessage;
import com.portifolio.api.entity.enums.MessageSource;
import com.portifolio.api.exception.ResourceNotFoundException;
import com.portifolio.api.exception.TooManyRequestsException;
import com.portifolio.api.mapper.MessageMapper;
import com.portifolio.api.ratelimit.ContactRateLimiter;
import com.portifolio.api.repository.ContactMessageRepository;
import com.portifolio.api.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MessageServiceImpl implements MessageService {

    private static final Logger log = LoggerFactory.getLogger(MessageServiceImpl.class);
    private static final String RESOURCE = "Mensagem";

    private final ContactMessageRepository repository;
    private final MessageMapper mapper;
    private final ContactRateLimiter rateLimiter;

    @Override
    @Transactional
    public void receive(MessageRequest request, String clientKey) {
        if (request.website() != null && !request.website().isBlank()) {
            log.info("Recado descartado pelo honeypot (origem {})", clientKey);
            return;
        }
        if (!rateLimiter.tryAcquire(clientKey)) {
            throw new TooManyRequestsException("Muitas mensagens enviadas. Tente novamente mais tarde.");
        }
        ContactMessage entity = mapper.toEntity(request);
        if (entity.getSource() == null) {
            entity.setSource(MessageSource.FORM);
        }
        repository.save(entity);
    }

    @Override
    public List<MessageResponse> findAll() {
        return mapper.toResponseList(repository.findAllByOrderByCreatedAtDesc());
    }

    @Override
    public long countUnread() {
        return repository.countByReadFalse();
    }

    @Override
    @Transactional
    public MessageResponse markAsRead(Long id) {
        ContactMessage message = getOrThrow(id);
        if (!message.isRead()) {
            message.setRead(true);
            message = repository.saveAndFlush(message);
        }
        return mapper.toResponse(message);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        repository.delete(getOrThrow(id));
    }

    private ContactMessage getOrThrow(Long id) {
        return repository.findById(id).orElseThrow(() -> new ResourceNotFoundException(RESOURCE, id));
    }
}