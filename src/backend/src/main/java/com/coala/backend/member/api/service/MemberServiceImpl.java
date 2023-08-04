package com.coala.backend.member.api.service;

import com.coala.backend.member.common.jwt.JwtAuthFilter;
import com.coala.backend.member.common.jwt.JwtTokenProvider;
import com.coala.backend.member.common.jwt.TokenDto;
import com.coala.backend.member.db.dto.request.LoginRequestDto;
import com.coala.backend.member.db.dto.response.BaseResponseDto;
import com.coala.backend.member.db.dto.response.MemberInfoResponseDto;
import com.coala.backend.member.db.entity.Certification;
import com.coala.backend.member.db.entity.Member;
import com.coala.backend.member.db.entity.RefreshToken;
import com.coala.backend.member.db.repository.CertificationRepository;
import com.coala.backend.member.db.repository.MemberRepository;
import com.coala.backend.member.db.repository.RefreshTokenRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.Random;


@Service
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService{

    private final JwtTokenProvider jwtTokenProvider;
    private final PasswordEncoder passwordEncoder;
    private final MemberRepository memberRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final CertificationRepository certificationRepository;

    private static String accessToken = "";
    private static String refreshToken = "";

    private final EmailService emailService;


    private Logger logger = LoggerFactory.getLogger(MemberServiceImpl.class);

    @Override
    @Transactional
    public BaseResponseDto signUp(Map<String, String> info) {

        Member member = new Member();
        member.setEmail(info.get("email"));
        member.setName(info.get("name"));
        member.setNickname(info.get("nickname"));
        member.setStudentId(info.get("studentId"));
        member.setDepart(info.get("depart"));
        member.setOrdinal(info.get("ordinal"));
        member.setPhoneNo(info.get("phoneNo"));
        member.setPassword(info.get("password"));

        // 기본적으로 User
        member.setRoles(Collections.singletonList("ROLE_USER"));

        // email 중복검사
        if(memberRepository.findByEmail(member.getEmail()).isPresent()){
            return new BaseResponseDto("이미 가입된 회원입니다.", 400);
        }

        // Password Encode
        member.setEncodePassword(passwordEncoder.encode(member.getPassword()));

        logger.info("member : {}", member);
        // Success
        // 회원가입
        memberRepository.save(member);

        // 중복 가입 방지 용
        certificationRepository.save(new Certification(member.getEmail(), null));
        return new BaseResponseDto("회원가입이 성공적으로 완료되었습니다.", 201);
    }

    @Override
    @Transactional
    public BaseResponseDto login(LoginRequestDto loginRequestDto, HttpServletResponse response) {
        Optional<Member> temp = memberRepository.findByEmail(loginRequestDto.getEmail());

        // Email 검사
        if(temp.isEmpty()){
            return new BaseResponseDto("존재하지 않는 회원입니다.",404);
        }

        Member member = temp.get();

        // Password Check
        if(!passwordEncoder.matches(loginRequestDto.getPassword(), member.getPassword())){
            return new BaseResponseDto("비밀번호가 일치하지 않습니다.", 400);
        }

        // Create Token
        TokenDto tokenDto = jwtTokenProvider.createAllToken(loginRequestDto.getEmail());

        // Check Refresh Token
        Optional<RefreshToken> refreshToken = refreshTokenRepository.findByEmail(loginRequestDto.getEmail());

        // refresh Token
        if(refreshToken.isPresent()){
            refreshTokenRepository.save(refreshToken.get().updateToken(tokenDto.getRefreshToken()));
        }else{
            RefreshToken newToken = new RefreshToken(tokenDto.getRefreshToken(), loginRequestDto.getEmail());
            refreshTokenRepository.save(newToken);
        }

        // response header AccessToken / RefreshToken => redis 적용해야함
        setHeader(response, tokenDto);

        logger.info("AccessToken : {}", response.getHeader(JwtTokenProvider.ACCESS_TOKEN));
        logger.info("RefreshToken : {}", response.getHeader(JwtTokenProvider.REFRESH_TOKEN));

        return new BaseResponseDto("성공적으로 로그인이 완료되었습니다.",200);
    }

    @Override
    public MemberInfoResponseDto loadInfo(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {
        accessToken = jwtTokenProvider.getHeaderToken(httpServletRequest, "Access");
        refreshToken = jwtTokenProvider.getHeaderToken(httpServletRequest, "Refresh");

        BaseResponseDto baseResponseDto = reissue(httpServletRequest, httpServletResponse);

        if(baseResponseDto.getStatusCode() != 200){
            return new MemberInfoResponseDto("인증정보가 만료되었습니다. 재 로그인 해주세요.", 401);
        }

        String email = jwtTokenProvider.getEmailFromToken(accessToken);


        Member member = memberRepository.findByEmail(email).get();

        MemberInfoResponseDto memberInfoResponseDto = new MemberInfoResponseDto();
        memberInfoResponseDto.setEmail(member.getEmail());
        memberInfoResponseDto.setName(member.getName());
        memberInfoResponseDto.setNickname(member.getNickname());
        memberInfoResponseDto.setStudentId(member.getStudentId());
        memberInfoResponseDto.setDepart(member.getDepart());
        memberInfoResponseDto.setOrdinal(member.getOrdinal());
        memberInfoResponseDto.setPhoneNo(member.getPhoneNo());
        memberInfoResponseDto.setMsg(member.getName() + " 유저의 정보를 성공적으로 불러왔습니다.");
        memberInfoResponseDto.setStatusCode(HttpStatus.OK.value());

        logger.info("user Info : {}", member);
        return memberInfoResponseDto;
    }

    @Override
    public BaseResponseDto updateInfo(Map<String, String> info, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {
        accessToken = jwtTokenProvider.getHeaderToken(httpServletRequest, "Access");
        refreshToken = jwtTokenProvider.getHeaderToken(httpServletRequest, "Refresh");

        BaseResponseDto baseResponseDto = reissue(httpServletRequest, httpServletResponse);

        if(baseResponseDto.getStatusCode() != 200){
            return new BaseResponseDto("인증정보가 만료되었습니다. 재 로그인 해주세요.", 401);
        }

        String email = jwtTokenProvider.getEmailFromToken(accessToken);

        Member member = memberRepository.findByEmail(email).get();

        member.setNickname(info.get("nickname"));
        member.setPassword(passwordEncoder.encode(info.get("password")));

        logger.info("user Info : {}", member);

        memberRepository.save(member);

        return new BaseResponseDto("회원정보가 성공적으로 재설정 되었습니다.", 200);
    }

    @Override
    public BaseResponseDto logout() {
        // redis에 logout token 추가 => "logout" : "accesstoken"
        // reids의 refreshtoken 제거
        // refreshToken 제거

        return null;
    }

    // header 저장
    public void setHeader(HttpServletResponse response, TokenDto tokenDto) {
        response.addHeader(JwtTokenProvider.ACCESS_TOKEN, tokenDto.getAccessToken());
        response.addHeader(JwtTokenProvider.REFRESH_TOKEN, tokenDto.getRefreshToken());
    }

    // Access Token 만료 시 재발급
    public BaseResponseDto reissue(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse){
        accessToken = jwtTokenProvider.getHeaderToken(httpServletRequest, "Access");
        refreshToken = jwtTokenProvider.getHeaderToken(httpServletRequest, "Refresh");

        // 토큰 검정
        if(!jwtTokenProvider.tokenValidation(accessToken)){
            if(jwtTokenProvider.refreshTokenValidation(refreshToken)){
                logger.info("refreshToken : {}", refreshToken);
                // access 토큰 재발급
                // ID 정보
                String loginEmail = jwtTokenProvider.getEmailFromToken(refreshToken);

                // new AccessToken
                String newAccessToken = jwtTokenProvider.createToken(loginEmail, "Access");

                // AccessToken add to header
                jwtTokenProvider.setHeaderAccessToken(httpServletResponse, newAccessToken);

                JwtAuthFilter jwtAuthFilter = new JwtAuthFilter(jwtTokenProvider);

                // Security context add certification info
                jwtAuthFilter.setAuthentication(loginEmail);

                logger.info("accessToken : {}", newAccessToken);

                accessToken = newAccessToken;

                // 새로 생성된 토큰 적용
                return new BaseResponseDto("Access Token의 재밝급이 완료되었습니다.", 200);
            }else{
                // token 둘다 무효
                return new BaseResponseDto("인증정보가 만료되었습니다.", 401);
            }
        }else{
            logger.info("email : {}", jwtTokenProvider.getEmailFromToken(accessToken));
            logger.info("accessToken : {}", accessToken);
            // 이미 유효한 토큰 적용
            return new BaseResponseDto("아직 유효한 토큰입니다.", 200);
        }
    }

    // 인증번호 인증
    @Override
    @Transactional
    public BaseResponseDto certification(Map<String, String> info){
        String email = info.get("email");

        // redis에 저장한 인증번호와일치 확인 "certification" : "random"
        Optional<Certification> temp = certificationRepository.findByEmail(email);

        // 검색 되지 않음
        if(temp.isEmpty()){
            return new BaseResponseDto("인증번호가 존재하지 않습니다.", 500);
        }

        // otp가 일치하지 않음
        if(!temp.get().getOtp().equals(info.get("otp"))){
            return new BaseResponseDto("인증번호가 일치하지 않습니다.", 404);
        }

        // 회원가입 인증 완료시 삭제
        if(info.get("type").equals("certification")){
            certificationRepository.delete(temp.get());
        }

        // 비밀번호 찾기 인증 완료시 otp만 삭제
        if(info.get("type").equals("find")){
            Certification certification = new Certification();
            certification.setEmail(email);
            certificationRepository.save(certification);
        }

        return new BaseResponseDto("인증이 완료되었습니다. 비밀번호를 변경해주세요.", 200);
    }

    // 비밀번호 찾기 후 수행.
    @Override
    @Transactional
    public BaseResponseDto updatePassword(Map<String, String> info) {
        // 이메일 기준으로 데이터 접근 => 비 로그인 상태
        Member member = memberRepository.findByEmail(info.get("email")).get();
        member.setPassword(passwordEncoder.encode(info.get("password")));

        return new BaseResponseDto("비밀번호가 성공적으로 재설정 되었습니다.", 200);
    }

    // 인증 메일 전송
    @Override
    public BaseResponseDto sendOTP(Map<String, String> info) {
        String email = info.get("email");

        // member에 객체 존재 확인
        Optional<Member> temp = memberRepository.findByEmail(email);

        // 비밀번호 찾기
        if(info.get("type").equals("find")){
            // 존재 여부 판별

            if(temp.isEmpty()){
                return new BaseResponseDto("존재하지 않는 회원입니다.", 404);
            }

            Member member = temp.get();

            // name & studentId Check
            if(!info.get("name").equals(member.getName()) || !info.get("studentId").equals(member.getStudentId())){
                return new BaseResponseDto("이름 또는 학번이 일치하지 않습니다.", 404);
            }

            // 임시번호 저장 객체 생성
            Certification certification = new Certification();

            certification.setEmail(email);

            // 임시 번호 생성
            String authNum = createCode();

            // 인증 번호 추가
            certification.setOtp(authNum);

            // 저장
            certificationRepository.save(certification);

            // 비밀번호 찾기 이메일 전송
            emailService.sendMail(email, "find", authNum);

            // 로직 종료(이후 인증하면 무조건 비밀번호 변경 가능하도록)
            return new BaseResponseDto(email + "로 정상적으로 인증번호가 전송되었습니다.", 200);
        }

        // 이메일 인증
        if(info.get("type").equals("certification")){
            if(!temp.isEmpty()){
                return new BaseResponseDto("이미 존재하는 회원입니다.", 400);
            }

            // 임시번호 저장 객체 생성
            Certification certification = new Certification();

            certification.setEmail(info.get("email"));

            // 임시 번호 생성
            String authNum = createCode();

            // 인증 번호 추가
            certification.setOtp(authNum);

            // 저장
            certificationRepository.save(certification);

            // 인증 이메일 전송
            emailService.sendMail(email, "certification", authNum);

            // 로직 종료(이후 인증하면 무조건 비밀번호 변경 가능하도록)
            return new BaseResponseDto(email + "로 정상적으로 인증번호가 전송되었습니다.", 200);
        }

        return new BaseResponseDto("실패.", 500);
    }

    public String createCode() {
        Random random = new Random();
        StringBuffer key = new StringBuffer();

        for (int i = 0; i < 8; i++) {
            int index = random.nextInt(4);

            switch (index) {
                case 0: key.append((char) ((int) random.nextInt(26) + 97)); break;
                case 1: key.append((char) ((int) random.nextInt(26) + 65)); break;
                default: key.append(random.nextInt(9));
            }
        }
        return key.toString();
    }



}
