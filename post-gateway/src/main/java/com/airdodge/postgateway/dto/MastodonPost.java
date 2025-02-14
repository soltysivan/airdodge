package com.airdodge.postgateway.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
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
