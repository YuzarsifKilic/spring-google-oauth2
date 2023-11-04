package com.yuzarsif.googleoauth2.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BaseUser {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    private String id;
    private String email;
    private String username;
    @Enumerated(EnumType.STRING)
    private LoginProvider loginProvider;
    @ManyToMany(fetch = FetchType.EAGER)
    private List<Authority> authorities;
}
