package com.server.youthtalktalk.service.comment;

import com.server.youthtalktalk.repository.CommentRepository;
import com.server.youthtalktalk.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class CommentServiceImpl {

    private final CommentRepository commentRepository;
    private final MemberRepository memberRepository;

}
