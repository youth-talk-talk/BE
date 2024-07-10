package com.server.youthtalktalk.service.post;

import com.server.youthtalktalk.domain.ItemType;
import com.server.youthtalktalk.domain.member.Member;
import com.server.youthtalktalk.domain.post.Post;
import com.server.youthtalktalk.domain.post.Review;
import com.server.youthtalktalk.dto.post.PostListRepDto;
import com.server.youthtalktalk.dto.post.PostRepDto;
import com.server.youthtalktalk.global.response.exception.post.PostNotFoundException;
import com.server.youthtalktalk.repository.PostRepository;
import com.server.youthtalktalk.repository.ScrapRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import static com.server.youthtalktalk.dto.post.PostListRepDto.*;

@Service
@RequiredArgsConstructor
public class PostReadServiceImpl implements PostReadService {
    private final PostRepository postRepository;
    private final ScrapRepository scrapRepository;

    /** 게시글, 리뷰 상세 조회 */
    @Override
    @Transactional
    public PostRepDto getPostById(Long postId) {
        Post post = postRepository.findById(postId).orElseThrow(PostNotFoundException::new);
        return post.toPostRepDto();
    }

    /** 게시글 전체 조회 */
    @Override
    @Transactional
    public PostListRepDto getAllPost(Pageable pageable) {
        return null;
    }

    /** 리뷰 카테고리별 전체 조회 */
    @Override
    @Transactional
    public PostListRepDto getAllReviewByCategory(Pageable pageable, List<String> category) {
        return null;
    }

    /** 나의 게시글, 리뷰 전체 조회 */
    @Override
    @Transactional
    public PostListRepDto getAllMyPost(Pageable pageable, String type, Member member) {
        return null;
    }

    /** 게시글, 리뷰 키워드 검색 */
    @Override
    @Transactional
    public PostListRepDto getAllPostByKeyword(Pageable pageable, String keyword, String type) {
        return null;
    }

    public PostListRepDto toPostListRepDto(List<Post> topList,List<Post> postList) {
//        List<PostDto> top5_posts = new ArrayList<>();
//        for (Post post : topList) {
//            top5_posts.add(PostDto.builder()
//                    .postId(post.getId())
//                    .title(post.getTitle())
//                    .content(post.getContent())
//                    .writerId(post.getWriter().getId())
//                    .policyId(post instanceof Review ? ((Review) post).getPolicy().getPolicyId() : null)
//                    .policyTitle(post instanceof Review ? ((Review)post).getPolicy().getTitle() : null )
//                    .comments(post.getPostComments().size())
//                    .scrap()
//                    .scraps(scrapRepository.findAllByItemIdAndItemType(post.getId(), ItemType.POST).size())
//                    .build());
//        }
//        return null;
        return null;
    }
}
