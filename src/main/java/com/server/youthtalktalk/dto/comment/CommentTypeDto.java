package com.server.youthtalktalk.dto.comment;

import java.util.Optional;

public record CommentTypeDto(Optional<String> policyId, Optional<Long> postId) {
}
