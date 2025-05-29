package com.server.youthtalktalk.domain.image.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.server.youthtalktalk.domain.announcement.entity.Announcement;
import com.server.youthtalktalk.domain.image.entity.AnnouncementImage;
import com.server.youthtalktalk.domain.image.entity.Image;
import com.server.youthtalktalk.domain.image.entity.PostImage;
import com.server.youthtalktalk.domain.image.entity.ProfileImage;
import com.server.youthtalktalk.domain.member.entity.Member;
import com.server.youthtalktalk.domain.member.service.MemberService;
import com.server.youthtalktalk.domain.post.entity.Post;
import com.server.youthtalktalk.domain.image.repository.ImageRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class ImageServiceImpl implements ImageService{

    // @Value는 lombok 어노테이션이 아님에 주의! 버켓 이름 동적 할당(properties에서 가져옴)
    @Value("${cloud.aws.s3.bucket}")
    private String bucket;
    @Value(("${cloud.aws.region.static}"))
    private String region;

    private final AmazonS3 amazonS3;
    private final ImageRepository imageRepository;

    @Override
    public String uploadMultiFile(MultipartFile multipartFile) throws IOException {
        try {
            // 파일 이름의 중복을 막기 위해 "UUID(랜덤 값) + 원본파일이름"로 연결함
            String s3FileName = UUID.randomUUID() + "-" + multipartFile.getOriginalFilename();

            // 파일 사이즈를 ContentLength를 이용하여 S3에 알려줌
            ObjectMetadata objMeta = new ObjectMetadata();
            // url을 클릭 시 사진이 웹에서 보이는 것이 아닌 바로 다운되는 현상을 해결하기 위해 메타데이터 타입 설정
            objMeta.setContentType(multipartFile.getContentType());
            InputStream inputStream = multipartFile.getInputStream();
            objMeta.setContentLength(inputStream.available());

            // 파일 stream을 열어서 S3에 파일을 업로드
            amazonS3.putObject(bucket, s3FileName, inputStream, objMeta);
            inputStream.close();

            // Url 가져와서 반환
            log.info("S3 upload file name = {}", s3FileName);
            return amazonS3.getUrl(bucket, s3FileName).toString();
        }
        catch (AmazonS3Exception e) {
            throw new AmazonS3Exception("Failed to upload multiple files", e);
        }
    }

    @Override
    public List<String> uploadMultiFiles(List<MultipartFile> multipartFileList) throws IOException {
        List<String> fileList = new ArrayList<>();
        try {
            for (MultipartFile file : multipartFileList) {
                // 파일 이름의 중복을 막기 위해 "UUID(랜덤 값) + 원본파일이름"로 연결함
                String s3FileName = UUID.randomUUID() + "-" + file.getOriginalFilename();

                // 파일 사이즈를 ContentLength를 이용하여 S3에 알려줌
                ObjectMetadata objMeta = new ObjectMetadata();
                // url을 클릭 시 사진이 웹에서 보이는 것이 아닌 바로 다운되는 현상을 해결하기 위해 메타데이터 타입 설정
                objMeta.setContentType(file.getContentType());
                InputStream inputStream = file.getInputStream();
                objMeta.setContentLength(inputStream.available());

                // 파일 stream을 열어서 S3에 파일을 업로드
                amazonS3.putObject(bucket, s3FileName, inputStream, objMeta);
                inputStream.close();

                // Url 가져와서 반환
                log.info("S3 upload file name = {}", s3FileName);
                fileList.add(amazonS3.getUrl(bucket, s3FileName).toString());
            }
        }
        catch (AmazonS3Exception e) {
            throw new AmazonS3Exception("Failed to upload multiple files", e);
        }
        return fileList;
    }

    @Override
    public void mappingPostImage(List<String> imgUrls,Post post) {
        List<Image> images = imageRepository.findAllByImgUrlIn(imgUrls);
        for (Image image : images) {
            ((PostImage)image).setPost(post);
        }
    }

    @Override
    public void createPostImage(String imgUrl) {
        imageRepository.save(PostImage.builder()
                .imgUrl(imgUrl)
                .post(null)
                .build());
    }

    @Override
    public void saveProfileImage(String imgUrl, Member member) {
        ProfileImage profileImage = ProfileImage.builder().imgUrl(imgUrl).build();
        profileImage.setMember(member);
        imageRepository.save(profileImage);
    }

    @Override
    public void deleteProfileImage(Member member) {
        try {
            String bucketUrl = "https://s3." + region + ".amazonaws.com/" + bucket;
            ProfileImage profileImage = member.getProfileImage();
            String fileName = profileImage.getImgUrl().substring(bucketUrl.length() + 1);
            DeleteObjectRequest request = new DeleteObjectRequest(bucket, fileName);
            amazonS3.deleteObject(request);
            member.updateProfileImage(null);
            log.info("delete departmentImgUrl = {}", profileImage.getImgUrl());
        } catch (AmazonS3Exception e) {
            throw new AmazonS3Exception("Failed to delete multiple files", e);
        }
    }

    @Override
    public void deleteMultiFile(List<String> imgUrlList) {
        try {
            String bucketUrl = "https://s3."+region+".amazonaws.com/"+bucket;
            imageRepository.deleteAllByImgUrlIn(imgUrlList);
            for (String fileUrl : imgUrlList) {
                log.info("delete departmentImgUrl = {}",fileUrl);
                String fileName = fileUrl.substring(bucketUrl.length() + 1);
                DeleteObjectRequest request = new DeleteObjectRequest(bucket, fileName);
                amazonS3.deleteObject(request);
            }
        }
        catch (AmazonS3Exception e) {
            throw new AmazonS3Exception("Failed to delete multiple files", e);
        }
    }

    @Override
    public void savePostImageList(List<String> imageUrlList, Post post) {
        List<Image> imageList = new ArrayList<>();
        for (String imageUrl : imageUrlList) {
            PostImage image = PostImage.builder()
                    .imgUrl(imageUrl)
                    .build();
            image.setPost(post);
            imageList.add(image);
        }
        imageRepository.saveAll(imageList);
    }

    @Override
    public void saveAnnouncementImageList(List<String> imageUrlList, Announcement announcement) {
        List<Image> imageList = new ArrayList<>();
        for (String imageUrl : imageUrlList) {
            AnnouncementImage image = AnnouncementImage.builder()
                    .imgUrl(imageUrl)
                    .build();
            image.setAnnouncement(announcement);
            imageList.add(image);
        }
        imageRepository.saveAll(imageList);
    }
}
