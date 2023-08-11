package com.coala.backend.community.freepost.api.service;

import com.coala.backend.community.freepost.db.dto.request.FreePostRequestDto;
import com.coala.backend.community.freepost.db.entity.FreeImage;
import com.coala.backend.community.freepost.db.entity.FreePost;
import com.coala.backend.community.freepost.db.repository.FreeGoodRepository;
import com.coala.backend.community.freepost.db.repository.FreeImageRepository;
import com.coala.backend.community.freepost.db.repository.FreePostRepository;
import com.coala.backend.community.common.dto.CommunityBaseResponseDto;
import com.coala.backend.community.freepost.db.dto.response.FreePostResponseDto;
import com.coala.backend.member.db.entity.Member;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/*
 자유게시판 기능 구현 Impl 입니다.

* */
@Slf4j
@Service
@RequiredArgsConstructor
public class FreePostServiceImpl implements FreePostService{
    private final FreePostRepository freePostRepository;
    private final FreeGoodRepository freeGoodRepository;
    private final FreeImageRepository freeImageRepository;

//    private final S3UploadService s3UploadService;

    // S3 주소
    static String str = "https://coala.s3.ap-northeast-2.amazonaws.com/Free/";
    @Transactional
    @Override
    public CommunityBaseResponseDto savePost(FreePostRequestDto postDto, Member member) {

          FreePost freePost = FreePost.builder()
                .memberId(member)
                .title(postDto.getTitle())
                .detail(postDto.getDetail())
                .isAnonymous(postDto.isAnonymous())
                .build();

        freePostRepository.saveAndFlush(freePost);

        if (!postDto.getImagePath().isEmpty()) {
            for (int i = 0; i < postDto.getImagePath().size(); i++) {
                freeImageRepository.save(FreeImage.builder()
                        .fpId(freePost)
                        .imagePath(postDto.getImagePath().get(i))
                        .build());
            }
        }

        return CommunityBaseResponseDto.builder()
                .statusCode(200)
                .msg("성공, 게시글 Id 반환")
                .id(freePost.getId())
                .build();
    }

    @Transactional
    @Override
    public CommunityBaseResponseDto getPostList(int page) {
        Pageable pageable = PageRequest.of(page,7, Sort.by("createAt").descending().and(Sort.by("updateAt")));

        List<FreePostResponseDto> allList = freePostRepository.findAll(pageable).stream()
                .map(freePost -> FreePostResponseDto.builder()
                        .id(freePost.getId())
                        .memberId(freePost.getMemberId())
                        .title(freePost.getTitle())
                        .detail(freePost.getDetail())
                        .createAt(freePost.getCreateAt())
                        .updateAt(freePost.getUpdateAt())
                        .isAnonymous(freePost.isAnonymous())
                        .views(freePost.getViews())
                        .commentCount(freePost.getComments().size())
                        .goodCount(freePost.getGoods().size())
                        .build())
                .collect(Collectors.toList());

        return CommunityBaseResponseDto.builder()
                .statusCode(200)
                .msg("성공, 페이지 수 & 해당 페이지 글 목록")
                .detail(1 + allList.size() / 8)
                .list(allList)
                .build();
    }

    @Transactional
    @Override
    public FreePostResponseDto getPost(Long id) {
        FreePost freePost = freePostRepository.findById(id).orElseThrow(() -> {
            return new IllegalArgumentException("없는 게시글 입니다.");
        });

        freePost.views();

        List<String> uri = new ArrayList<>();
        List<FreeImage> imageList = freeImageRepository.findByFpId(freePost);
        for (int i = 0; i < imageList.size(); i++) {
            FreeImage freeImage = imageList.get(i);
            uri.add(str + freeImage.getImagePath());
        }

        return FreePostResponseDto.builder()
                .id(freePost.getId())
                .memberId(freePost.getMemberId())
                .title(freePost.getTitle())
                .detail(freePost.getDetail())
                .createAt(freePost.getCreateAt())
                .updateAt(freePost.getUpdateAt())
                .isAnonymous(freePost.isAnonymous())
                .imagePath(uri)
                .views(freePost.getViews())
                .commentCount(freePost.getComments().size())
                .goodCount(freePost.getGoods().size())
                .build();
    }

    @Transactional
    @Override
    public void deletePost(Long id, Member member) {
        FreePost freePost = freePostRepository.findById(id).orElseThrow(() -> {
            return new IllegalArgumentException("게시판이 존재하지 않습니다.");
        });

        if (!freePost.getMemberId().getEmail().equals(member.getEmail())) {
            throw new IllegalArgumentException("작성자가 아닙니다.");
        }

        freeImageRepository.deleteByFpId(freePost);
        freeGoodRepository.deleteByFpId(freePost);
        freePostRepository.deleteById(id);
    }

    @Transactional
    @Override
    public CommunityBaseResponseDto searchPosts(String keyword, int page) {
        Pageable pageable = PageRequest.of(page,7, Sort.by("createAt").descending().and(Sort.by("updateAt")));
        List<FreePostResponseDto> searchList = freePostRepository.findByTitleContaining(keyword, pageable).stream()
                .map(freePost -> FreePostResponseDto.builder()
                        .id(freePost.getId())
                        .memberId(freePost.getMemberId())
                        .title(freePost.getTitle())
                        .detail(freePost.getDetail())
                        .createAt(freePost.getCreateAt())
                        .updateAt(freePost.getUpdateAt())
                        .isAnonymous(freePost.isAnonymous())
                        .views(freePost.getViews())
                        .commentCount(freePost.getComments().size())
                        .goodCount(freePost.getGoods().size())
                        .build())
                .collect(Collectors.toList());

        return CommunityBaseResponseDto.builder()
                .statusCode(200)
                .msg("성공, 페이지 수 & 해당 페이지 글 목록")
                .detail(1 + searchList.size() / 8)
                .list(searchList)
                .build();
    }

    @Transactional
    @Override
    public CommunityBaseResponseDto updateFreePost(Long id, FreePostRequestDto dto, Member member) {
        FreePost freePost = freePostRepository.findById(id).orElseThrow(() -> {
            return new IllegalArgumentException("게시글이 존재하지 않습니다.");
        });

        if (!freePost.getMemberId().getEmail().equals(member.getEmail())) {
            throw new IllegalArgumentException("작성자가 아닙니다.");
        }

        freePost.updateFreePost(
                dto.getTitle(),
                dto.getDetail(),
                dto.isAnonymous());

        freePostRepository.save(freePost);
        freeImageRepository.deleteByFpId(freePost);

        for (int i = 0; i < dto.getImagePath().size(); i++) {
            freeImageRepository.save(FreeImage.builder()
                            .fpId(freePost)
                            .imagePath(dto.getImagePath().get(i))
                    .build());
        }

        return CommunityBaseResponseDto.builder()
                .statusCode(200)
                .msg("성공, 게시글 Id 반환")
                .id(id)
                .build();
    }
}
