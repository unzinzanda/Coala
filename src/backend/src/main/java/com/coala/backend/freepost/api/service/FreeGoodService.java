package com.coala.backend.freepost.api.service;

import com.coala.backend.freepost.db.dto.request.FreeGoodRequestDto;
import com.coala.backend.freepost.db.dto.response.BaseResponseDto;
import com.coala.backend.member.db.entity.Member;
import jakarta.servlet.http.HttpServletRequest;

import java.net.http.HttpClient;

public interface FreeGoodService {
    public BaseResponseDto good(FreeGoodRequestDto freeGoodRequestDto, Member member);
    public BaseResponseDto unGood(FreeGoodRequestDto freeGoodRequestDto, Member member);
}