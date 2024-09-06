package com.server.youthtalktalk.dto.announcement;

import java.util.Optional;

public record AnnouncementUpdateDto(
        Optional<String> title,
        Optional<String> content
) {
}
