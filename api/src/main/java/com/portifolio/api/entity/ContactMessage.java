package com.portifolio.api.entity;

import com.portifolio.api.entity.enums.MessageSource;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "contact_messages")
public class ContactMessage extends BaseEntity{

    @Column(nullable = false, length = 80)
    private String name;

    @Column(nullable = false, length = 160)
    private String email;

    @Column(nullable = false, length = 2000)
    private String message;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private MessageSource source = MessageSource.FORM;

    @Column(name = "is_read", nullable = false)
    private boolean read;
}
