package com.server.youthtalktalk.domain.post;

import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Getter;

@Embeddable
@Getter
public class Content {
    private String content;

    @Enumerated(EnumType.STRING)
    private ContentType type;
}
