package com.airdodge.mastodonconnector.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "mastodon_offset")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PostOffset {

    @Id
    private String id;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    public PostOffset(String id) {
        this.id = id;
        this.createdAt = LocalDateTime.now();
    }
}
