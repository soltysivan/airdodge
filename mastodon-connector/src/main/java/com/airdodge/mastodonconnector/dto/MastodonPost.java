package com.airdodge.mastodonconnector.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MastodonPost {
    @JsonProperty("id")
    private String id;
    @JsonProperty("content")
    private String content;
    @JsonProperty("created_at")
    private Instant createdAt;

    @JsonIgnore
    public boolean isValid() {
        return id != null && content != null;
    }
}
