package com.server.youthtalktalk.domain.member.controller;

import com.server.youthtalktalk.domain.image.service.ImageService;
import com.server.youthtalktalk.domain.member.entity.Member;
import com.server.youthtalktalk.domain.member.dto.MemberInfoDto;
import com.server.youthtalktalk.domain.member.dto.MemberUpdateDto;
import com.server.youthtalktalk.domain.member.dto.SignUpRequestDto;
import com.server.youthtalktalk.domain.member.dto.apple.AppleDto;
import com.server.youthtalktalk.global.response.BaseResponse;
import com.server.youthtalktalk.domain.member.service.MemberService;
import jakarta.validation.Valid;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import static com.server.youthtalktalk.global.response.BaseResponseCode.*;

@RestController
@RequiredArgsConstructor
public class MemberController {

    private static final int MAX_PROFILE_SIZE = 5 * 1024 * 1024; // 5MB
    public static final String NICKNAME_REGEX = "^[a-zA-Z가-힣0-9]+$"; // 닉네임 조건

    private final MemberService memberService;
    private final ImageService imageService;

    /**
     * 회원가입 api
     */
    @PostMapping("/signUp")
    public BaseResponse<Long> signUp(@Valid @RequestBody SignUpRequestDto signUpRequestDto) {
        Long memberId = memberService.signUp(signUpRequestDto);
        return new BaseResponse<>(memberId, SUCCESS);
    }

    /**
     * 회원정보 조회 api
     */
    @GetMapping("/members/me")
    public BaseResponse<MemberInfoDto> getMemberInfo() {
        Member member = memberService.getCurrentMember();
        MemberInfoDto memberInfo = memberService.getMemberInfo(member);
        return new BaseResponse<>(memberInfo, SUCCESS);
    }

    /**
     * 회원정보 수정 api
     */
    @PatchMapping("/members/me")
    public BaseResponse<MemberInfoDto> updateMemberInfo(@Valid @RequestBody MemberUpdateDto memberUpdateDto) {
        Member member = memberService.getCurrentMember();
        memberService.updateMemberInfo(memberUpdateDto, member);
        MemberInfoDto memberInfo = memberService.getMemberInfo(member);
        return new BaseResponse<>(memberInfo, SUCCESS_MEMBER_UPDATE);
    }

    /**
     * 회원탈퇴 api
     */
    @PostMapping("/members/me")
    public BaseResponse<String> deleteMember(@RequestBody(required = false) AppleDto.AppleCodeRequestDto appleCodeRequestDto) {
        Member member = memberService.getCurrentMember();
        memberService.deleteMember(member,appleCodeRequestDto);
        return new BaseResponse<>(SUCCESS_MEMBER_DELETE);
    }

    /**
     * 차단 등록 api
     */
    @PostMapping("/members/block/{blockId}")
    public BaseResponse<String> blockMember(@PathVariable Long blockId) {
        Member member = memberService.getCurrentMember();
        memberService.blockMember(member,blockId);
        return new BaseResponse<>(SUCCESS_MEMBER_BLOCK);
    }

    /**
     * 차단 해제 api
     */
    @PostMapping("/members/unblock/{unblockId}")
    public BaseResponse<String> unblockMember(@PathVariable Long unblockId) {
        Member member = memberService.getCurrentMember();
        memberService.unblockMember(member,unblockId);
        return new BaseResponse<>(SUCCESS_MEMBER_UNBLOCK);
    }

    /**
     * 프로필 이미지 등록 api
     */
    @PostMapping("/members/profile")
    public BaseResponse<String> uploadProfileImage(@RequestParam("image") MultipartFile image) throws IOException {
        if (image.getSize() > MAX_PROFILE_SIZE) {
            return new BaseResponse<>(EXCEED_PROFILE_SIZE);
        }
        String imgUrl = imageService.uploadMultiFile(image);
        imageService.saveProfileImage(imgUrl, memberService.getCurrentMember());
        return new BaseResponse<>(imgUrl, SUCCESS);
    }

    /**
     * 프로필 이미지 삭제 api
     */
    @DeleteMapping("/members/profile")
    public BaseResponse<String> deleteProfileImage() {
        Member member = memberService.getCurrentMember();
        if (member.getProfileImage() == null) {
            return new BaseResponse<>(SUCCESS_EMPTY_PROFILE);
        }
        imageService.deleteProfileImage(member);
        return new BaseResponse<>(SUCCESS);
    }

}
