package com.portifolio.api.mapper;

import com.portifolio.api.dto.request.MessageRequest;
import com.portifolio.api.dto.response.MessageResponse;
import com.portifolio.api.entity.ContactMessage;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface MessageMapper {

    ContactMessage toEntity(MessageRequest request);

    MessageResponse toResponse(ContactMessage message);

    List<MessageResponse>toResponseList(List<ContactMessage> messages);
}
