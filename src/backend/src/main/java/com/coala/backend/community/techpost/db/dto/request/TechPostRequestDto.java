package com.coala.backend.community.techpost.db.dto.request;

import com.coala.backend.community.techpost.db.entity.TechImage;
import com.coala.backend.member.db.entity.Member;
import com.coala.backend.community.techpost.db.entity.TechPost;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

/*
    태크 게시판 요청 Dto 입니다.
- 추천 수 기능 미구현
* */
@Getter
@NoArgsConstructor
public class TechPostRequestDto {
    private Long id;
    private String title;
    private String detail;
    @JsonIgnore
    private Member nickname;
    private List<String> imagePath;

    @Builder
    public TechPostRequestDto(Long id, String title, String detail,
                              Member nickname, List<String> imagePath) {
        this.id = id;
        this.title = title;
        this.detail = detail;
        this.nickname = nickname;
        this.imagePath = imagePath;
    }

    public TechPost toEntity(Member member) {
        return TechPost.builder()
                .memberId(member)
                .title(this.title)
                .detail(this.detail)
                .nickname(this.nickname)
                .build();
    }
}
