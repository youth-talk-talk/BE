package com.server.youthtalktalk.service.post;

import com.server.youthtalktalk.domain.ItemType;
import com.server.youthtalktalk.domain.image.entity.PostImage;
import com.server.youthtalktalk.domain.image.repository.ImageRepository;
import com.server.youthtalktalk.domain.member.entity.Member;
import com.server.youthtalktalk.domain.member.entity.Role;
import com.server.youthtalktalk.domain.policy.entity.Category;
import com.server.youthtalktalk.domain.policy.entity.Policy;
import com.server.youthtalktalk.domain.post.dto.PostCreateReqDto;
import com.server.youthtalktalk.domain.post.dto.PostUpdateReqDto;
import com.server.youthtalktalk.domain.post.entity.Content;
import com.server.youthtalktalk.domain.post.entity.ContentType;
import com.server.youthtalktalk.domain.post.entity.Post;
import com.server.youthtalktalk.domain.post.entity.Review;
import com.server.youthtalktalk.domain.post.dto.PostRepDto;
import com.server.youthtalktalk.domain.member.repository.MemberRepository;
import com.server.youthtalktalk.domain.policy.repository.PolicyRepository;
import com.server.youthtalktalk.domain.post.service.PostService;
import com.server.youthtalktalk.domain.post.repostiory.PostRepository;
import com.server.youthtalktalk.domain.scrap.repository.ScrapRepository;
import com.server.youthtalktalk.global.response.BaseResponseCode;
import com.server.youthtalktalk.global.response.exception.BusinessException;
import com.server.youthtalktalk.global.response.exception.InvalidValueException;
import com.server.youthtalktalk.global.response.exception.policy.PolicyNotFoundException;
import com.server.youthtalktalk.global.response.exception.post.PostNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
class PostServiceTest {

    @Autowired
    private PostService postService;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private PolicyRepository policyRepository;
    @Autowired
    private PostRepository postRepository;
    @Autowired
    private ScrapRepository scrapRepository;
    @Autowired
    private ImageRepository imageRepository;

    private Member member;
    private Policy policy;
    private Post post;
    private static final String CONTENT = "content";
    private static final String IMAGE_URL = "https://example-bucket-name.s3.amazonaws.com/long-file-name-1234";
    
    @BeforeEach
    void init(){
        this.member = memberRepository.save(Member.builder()
                .username("member1")
                .role(Role.USER)
                .build());

        this.policy = policyRepository.save(Policy.builder()
                        .policyId("policyId")
                        .title("policy1")
                        .category(Category.JOB)
                        .build());

        this.post = postRepository.save(Post.builder()
                .title("post1")
                .writer(member)
                .contents(getContents(CONTENT, IMAGE_URL))
                .build());

        imageRepository.save(PostImage.builder().post(null).imgUrl(IMAGE_URL).build());
    }

    @Test
    @DisplayName("자유글 생성 성공")
    void successCreateFreePost() throws IOException {
        // Given
        PostCreateReqDto postCreateReqDto = new PostCreateReqDto("post", getContents(CONTENT, IMAGE_URL), "post", null);
        // When
        PostRepDto postRepDto = postService.createPost(postCreateReqDto,this.member);

        // Then
        assertThat(postRepDto.getPostType()).isEqualTo("post");
        assertThat(postRepDto.getTitle()).isEqualTo("post");
        assertThat(postRepDto.getContentList().get(0).getType()).isEqualTo(ContentType.TEXT);
        assertThat(postRepDto.getContentList().get(1).getType()).isEqualTo(ContentType.IMAGE);
        assertThat(postRepDto.getImages().size()).isEqualTo(1);
    }

    @Test
    @DisplayName("리뷰 생성 성공")
    void successCreateReview() throws IOException {
        // Given
        PostCreateReqDto postCreateReqDto = new PostCreateReqDto("review", getContents(CONTENT, IMAGE_URL), "review", this.policy.getPolicyId());
        // When
        PostRepDto postRepDto = postService.createPost(postCreateReqDto,this.member);
        // Then
        assertThat(postRepDto.getPostType()).isEqualTo("review");
        assertThat(postRepDto.getTitle()).isEqualTo("review");
        assertThat(postRepDto.getContentList().get(0).getType()).isEqualTo(ContentType.TEXT);
        assertThat(postRepDto.getContentList().get(1).getType()).isEqualTo(ContentType.IMAGE);
        assertThat(postRepDto.getImages().size()).isEqualTo(1);
    }

    @Test
    @DisplayName("존재하지 않는 정책일 경우 리뷰 생성 실패")
    void failCreateReviewIfNotExistPolicy(){
        // Given
        PostCreateReqDto postCreateReqDto = new PostCreateReqDto("review", getContents(CONTENT, IMAGE_URL), "review", "notExistId");
        // When
        // Then
        assertThatThrownBy(() -> postService.createPost(postCreateReqDto,this.member))
                .isInstanceOf(PolicyNotFoundException.class);
    }

    @Test
    @DisplayName("타입이 자유글 혹은 리뷰가 아닌 경우 리뷰 생성 실패")
    void failCreatePostIfNotExistType(){
        // Given
        PostCreateReqDto postCreateReqDto = new PostCreateReqDto("post", getContents(CONTENT, IMAGE_URL), "none", null);
        // When
        // Then
        assertThatThrownBy(() -> postService.createPost(postCreateReqDto,this.member))
                .isInstanceOf(InvalidValueException.class);
    }

    @Test
    @DisplayName("자유글 수정 성공")
    void successUpdateFreePost() throws IOException {
        // Given
        String updatedImageUrl = "https://s3.region-name.amazonaws.com/bucket-name/updatedImage";
        imageRepository.save(PostImage.builder().post(null).imgUrl(updatedImageUrl).build());

        List<String> addImgUrlList = new ArrayList<>();
        addImgUrlList.add(updatedImageUrl);
        List<String> deleteImgUrlList = new ArrayList<>();
        deleteImgUrlList.add(IMAGE_URL);

        PostUpdateReqDto postUpdateReqDto = PostUpdateReqDto.builder()
                .title("updatedTitle")
                .contentList(getContents("updatedContent", updatedImageUrl))
                .addImgUrlList(addImgUrlList)
                .deletedImgUrlList(deleteImgUrlList)
                .build();
        // When
        PostRepDto postRepDto = postService.updatePost(post.getId(), postUpdateReqDto, this.member);
        // Then
        assertThat(postRepDto.getTitle()).isEqualTo("updatedTitle");
        assertThat(postRepDto.getContentList().get(0).getContent()).isEqualTo("updatedContent");
        assertThat(postRepDto.getImages().size()).isEqualTo(1);
        assertThat(postRepDto.getImages().get(0)).isEqualTo(updatedImageUrl);
    }

    @Test
    @DisplayName("리뷰 수정 성공")
    void successUpdateReview() throws IOException {
        // Given
        String updatedImageUrl = "https://s3.region-name.amazonaws.com/bucket-name/updatedImage";
        imageRepository.save(PostImage.builder().post(null).imgUrl(updatedImageUrl).build());

        Review review = postRepository.save(Review.builder()
                .title("review1")
                .writer(member)
                .policy(policy)
                .contents(getContents(CONTENT, IMAGE_URL))
                .build());

        List<String> addImgUrlList = new ArrayList<>();
        addImgUrlList.add(updatedImageUrl);
        List<String> deleteImgUrlList = new ArrayList<>();
        deleteImgUrlList.add(IMAGE_URL);

        PostUpdateReqDto postUpdateReqDto = PostUpdateReqDto.builder()
                .title("updatedTitle")
                .contentList(getContents("updatedContent", updatedImageUrl))
                .policyId(this.policy.getPolicyId())
                .addImgUrlList(addImgUrlList)
                .deletedImgUrlList(deleteImgUrlList)
                .build();
        // When
        PostRepDto postRepDto = postService.updatePost(review.getId(), postUpdateReqDto,this.member);
        // Then
        assertThat(postRepDto.getTitle()).isEqualTo("updatedTitle");
        assertThat(postRepDto.getContentList().get(0).getContent()).isEqualTo("updatedContent");
        assertThat(postRepDto.getPolicyId()).isEqualTo(review.getPolicy().getPolicyId());
        assertThat(postRepDto.getImages().size()).isEqualTo(1);
        assertThat(postRepDto.getImages().get(0)).isEqualTo(updatedImageUrl);
    }

    @Test
    @DisplayName("존재하지 않는 게시글 수정 실패")
    void failUpdatePostIfNotExistPost() {
        // Given
        PostUpdateReqDto postUpdateReqDto = PostUpdateReqDto.builder()
                .title("updatedTitle")
                .build();
        // When, Then
        assertThatThrownBy(()-> postService.updatePost(Long.MAX_VALUE, postUpdateReqDto, this.member))
                .isInstanceOf(PostNotFoundException.class);
    }

    @Test
    @DisplayName("작성자가 아닐 경우 게시글 수정 실패")
    void failUpdatePostIfNotWriter() {
        // Given
        Member other = Member.builder()
                .username("other")
                .role(Role.USER)
                .build();
        PostUpdateReqDto postUpdateReqDto = PostUpdateReqDto.builder()
                .title("updatedTitle")
                .build();
        // When, Then
        assertThatThrownBy(()-> postService.updatePost(this.post.getId(), postUpdateReqDto, other))
                .isInstanceOf(BusinessException.class)
                .hasMessage(BaseResponseCode.POST_ACCESS_DENIED.getMessage());
    }

    @Test
    @DisplayName("존재하지 않는 정책일 경우 리뷰 수정 실패")
    void failUpdatePostIfNotExistPolicy() {
        // Given
        Review review = postRepository.save(Review.builder()
                .title("review1")
                .writer(member)
                .contents(getContents(CONTENT, IMAGE_URL))
                .build());
        PostUpdateReqDto postUpdateReqDto = PostUpdateReqDto.builder()
                .title("updatedTitle")
                .policyId("notExistId")
                .build();
        // When, Then
        assertThatThrownBy(()-> postService.updatePost(review.getId(), postUpdateReqDto, this.member))
                .isInstanceOf(PolicyNotFoundException.class);
    }

    @Test
    @DisplayName("게시글 삭제 성공")
    void successDeletePost(){
        // When
        postService.deletePost(this.post.getId(), this.member);
        // Then
        assertThat(postRepository.findById(this.post.getId())).isEmpty();
    }

    @Test
    @DisplayName("존재하지 않는 게시글 삭제 실패")
    void failDeletePostIfNotExistPost(){
        assertThatThrownBy(() -> postService.deletePost(Long.MAX_VALUE, this.member))
                .isInstanceOf(PostNotFoundException.class);
    }

    @Test
    @DisplayName("작성자가 아닐 경우 게시글 삭제 실패")
    void failDeletePostIfNotWriter(){
        // Given
        Member other = Member.builder()
                .username("other")
                .role(Role.USER)
                .build();
        // When, Then
        assertThatThrownBy(() -> postService.deletePost(this.post.getId(), other))
                .isInstanceOf(BusinessException.class)
                .hasMessage(BaseResponseCode.POST_ACCESS_DENIED.getMessage());
    }

    @Test
    @DisplayName("게시글 스크랩 등록 / 취소 성공")
    void successScrapPostAndCancel(){
        postService.scrapPost(post.getId(), member); // 스크랩
        assertThat(scrapRepository.existsByMemberIdAndItemIdAndItemType(member.getId(), post.getId().toString(), ItemType.POST)).isTrue();

        postService.scrapPost(post.getId(), member); // 스크랩 취소
        assertThat(scrapRepository.existsByMemberIdAndItemIdAndItemType(member.getId(), post.getId().toString(), ItemType.POST)).isFalse();
    }

    @Test
    @DisplayName("존재하지 않는 게시글 스크랩 실패")
    void failScrapPostIfNotExistPost(){
        assertThatThrownBy(() -> postService.scrapPost(Long.MAX_VALUE, this.member))
                .isInstanceOf(PostNotFoundException.class);
    }

    @Test
    @DisplayName("게시글 Content 리스트에서 이미지 추출 성공")
    void successExtractImageUrl(){
        // Given
        List<Content> contentList = new ArrayList<>();
        contentList.add(Content.builder()
                .content(CONTENT)
                .type(ContentType.TEXT)
                .build());
        for(int i = 0; i < 5; i++){
            contentList.add(Content.builder()
                    .content(IMAGE_URL)
                    .type(ContentType.IMAGE)
                    .build());
        }
        Post post = postRepository.save(Post.builder()
                .title("post")
                .writer(member)
                .contents(contentList)
                .build());

        // When
        List<String> imageUrlList = postService.extractImageUrl(post);
        // Then
        assertThat(imageUrlList).hasSize(5);
    }

    List<Content> getContents(String content, String imageUrl){
        List<Content> contentList = new ArrayList<>(List.of(
                Content.builder().content(content).type(ContentType.TEXT).build(),
                Content.builder().content(imageUrl).type(ContentType.IMAGE).build()
        ));
        return contentList;
    }

}