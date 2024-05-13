package com.mawuli.sns.domain.entities;

import com.mawuli.sns.security.domain.entities.User;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "contacts", indexes = @Index(name = "index_user_id", columnList = "user_id"))
public class Contact {
    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
    @ManyToOne
    @JoinColumn(name = "contact_id")
    private User contact;
    private Boolean isAccepted;

    private Boolean isBlacklisted;

}
