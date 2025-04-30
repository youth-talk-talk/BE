package com.server.youthtalktalk.domain.post.repostiory;

import com.server.youthtalktalk.domain.post.entity.Post;
import lombok.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
    boolean existsById(@NonNull Long id);
}
