package com.coala.backend.community.common.dto;


import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor
public class BasePostResponseDto {

    private String msg;
    private int statusCode;
    private int detail;
    private List<?> list = new ArrayList<>();

    @Builder
    public BasePostResponseDto(String msg, int statusCode, int detail, List<?> list) {
        this.msg = msg;
        this.statusCode = statusCode;
        this.detail = detail;
        this.list = list;
    }
}
