package com.portifolio.api.service.impl;

import com.portifolio.api.dto.request.MessageRequest;
import com.portifolio.api.entity.ContactMessage;
import com.portifolio.api.entity.enums.MessageSource;
import com.portifolio.api.exception.ResourceNotFoundException;
import com.portifolio.api.exception.TooManyRequestsException;
import com.portifolio.api.mapper.MessageMapper;
import com.portifolio.api.ratelimit.ContactRateLimiter;
import com.portifolio.api.repository.ContactMessageRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MessageServiceImplTest {

    @Mock
    private ContactMessageRepository repository;
    @Mock
    private ContactRateLimiter rateLimiter;

    private MessageServiceImpl service;

    @BeforeEach
    void setUp() {
        service = new MessageServiceImpl(
                repository,
                Mappers.getMapper(MessageMapper.class),
                rateLimiter
        );
    }

    private MessageRequest request(String website) {
        return new MessageRequest(
                "Ana",
                "ana@email.com",
                "Olá, gostaria de conversar sobre uma vaga",
                null,
                website
        );
    }

    @Test
    void savesMessageWithDefaultSourceAndUnreadFlag() {
        when(rateLimiter.tryAcquire("ip")).thenReturn(true);

        service.receive(request(null), "ip");

        ArgumentCaptor<ContactMessage> captor = ArgumentCaptor.forClass(ContactMessage.class);
        verify(repository).save(captor.capture());

        ContactMessage saved = captor.getValue();
        assertThat(saved.getSource()).isEqualTo(MessageSource.FORM);
        assertThat(saved.isRead()).isFalse();
        assertThat(saved.getEmail()).isEqualTo("ana@email.com");
    }

    @Test
    void discardsMessageWhenHoneypotIsFilled() {
        service.receive(request("http://spam.example"), "ip");

        verify(repository, never()).save(any());
        verify(rateLimiter, never()).tryAcquire(any());
    }

    @Test
    void rejectsWhenRateLimitIsExceeded() {
        when(rateLimiter.tryAcquire("ip")).thenReturn(false);

        assertThatThrownBy(() -> service.receive(request(null), "ip"))
                .isInstanceOf(TooManyRequestsException.class);

        verify(repository, never()).save(any());
    }

    @Test
    void markAsReadUpdatesFlag() {
        ContactMessage entity = new ContactMessage();
        when(repository.findById(1L)).thenReturn(Optional.of(entity));
        when(repository.saveAndFlush(entity)).thenReturn(entity);

        assertThat(service.markAsRead(1L).read()).isTrue();
    }

    @Test
    void deleteUnknownMessageThrowsNotFound() {
        when(repository.findById(9L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.delete(9L))
                .isInstanceOf(ResourceNotFoundException.class);

        verify(repository, never()).delete(any());
    }
}