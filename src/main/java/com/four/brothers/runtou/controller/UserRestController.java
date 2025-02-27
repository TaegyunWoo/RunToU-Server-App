package com.four.brothers.runtou.controller;

import com.four.brothers.runtou.dto.UserRole;
import com.four.brothers.runtou.exception.BadRequestException;
import com.four.brothers.runtou.exception.code.LoginExceptionCode;
import com.four.brothers.runtou.exception.code.PointExceptionCode;
import com.four.brothers.runtou.exception.code.RequestExceptionCode;
import com.four.brothers.runtou.exception.code.SignupExceptionCode;
import com.four.brothers.runtou.service.PointService;
import com.four.brothers.runtou.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import static com.four.brothers.runtou.dto.AdminDto.*;
import static com.four.brothers.runtou.dto.LoginDto.*;
import static com.four.brothers.runtou.dto.OrdererDto.*;
import static com.four.brothers.runtou.dto.PerformerDto.*;
import static com.four.brothers.runtou.dto.UserDto.*;

@Tag(name = "UserController",description = "유저 관련 API")
@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/user")
public class UserRestController {
  private final UserService userService;
  private final PointService pointService;

  @Operation(summary = "심부름 요청자 회원가입")
  @PostMapping("/signup/orderer")
  public SignUpAsOrdererResponse signUpAsOrderer(
    @Parameter(name = "회원 정보")
    @Validated @RequestBody SignUpAsOrdererRequest request,
    BindingResult bindingResult, HttpServletRequest requestMsg
  ) {
    boolean result = false;

    if (bindingResult.hasFieldErrors()) {
      throw new BadRequestException(RequestExceptionCode.WRONG_FORMAT, bindingResult.getFieldError().getDefaultMessage());
    }

    try {
      result = userService.signUpAsOrderer(request);
    } catch (DataIntegrityViolationException e) {
      throw new BadRequestException(SignupExceptionCode.ALREADY_EXIST_INFO, "회원가입 정보가 중복됩니다.");
    }

    return new SignUpAsOrdererResponse(result);
  }

  @Operation(summary = "계정 아이디 중복 확인")
  @PostMapping("/signup/accountid")
  public DuplicatedAccountIdResponse checkDuplicatedAccountId(
    @Parameter(name = "확인할 아이디")
    @Validated @RequestBody DuplicatedAccountIdRequest request,
    BindingResult bindingResult
  ) {
    if (bindingResult.hasFieldErrors()) {
      throw new BadRequestException(RequestExceptionCode.WRONG_FORMAT, bindingResult.getFieldError().getDefaultMessage());
    }

    return userService.isDuplicatedAccountId(request);
  }

  @Operation(summary = "닉네임 중복 확인")
  @PostMapping("/signup/nickname")
  public DuplicatedNicknameResponse checkDuplicatedNickname(
    @Parameter(name = "확인할 닉네임")
    @Validated @RequestBody DuplicatedNicknameRequest request, BindingResult bindingResult
  ) {

    if (bindingResult.hasFieldErrors()) {
      throw new BadRequestException(RequestExceptionCode.WRONG_FORMAT, bindingResult.getFieldError().getDefaultMessage());
    }

    return userService.isDuplicatedNickname(request);
  }

  @Operation(summary = "심부름 요청자 or 심부름 수행자 로그인")
  @PostMapping("/signin")
  public LoginResponse loginAsOrderer(@Validated @RequestBody LoginRequest request,
                                      BindingResult bindingResult,
                                      HttpServletRequest httpServletRequest) {

    if (bindingResult.hasFieldErrors()) {
      throw new BadRequestException(RequestExceptionCode.WRONG_FORMAT, bindingResult.getFieldError().getDefaultMessage());
    }

    LoginUser loginUser = null;
    if (request.getRole() == UserRole.ORDERER) {
      loginUser = userService.loginAsOrderer(request);
    } else if (request.getRole() == UserRole.PERFORMER) {
      loginUser = userService.loginAsPerformer(request);
    }

    if (loginUser == null) {
      throw new BadRequestException(LoginExceptionCode.WRONG_VALUE, "로그인 정보가 잘못되었습니다.");
    }

    HttpSession session = httpServletRequest.getSession();
    session.setAttribute("loginUser", loginUser);

    return new LoginResponse(true,
      loginUser.getUserPk(),
      loginUser.getAccountId(),
      loginUser.getRealName(),
      loginUser.getNickname(),
      loginUser.getPhoneNumber(),
      loginUser.getAccountNumber(),
      loginUser.getRole());
  }

  @Operation(summary = "심부름 수행자 회원가입")
  @PostMapping("/signup/performer")
  public SignUpAsPerformerResponse signUpAsPerformer(
    @Parameter(name = "회원 정보")
    @Validated @RequestBody SignUpAsPerformerRequest request,
    BindingResult bindingResult, HttpServletRequest requestMsg
  ) {
    boolean result = false;

    if (bindingResult.hasFieldErrors()) {
      throw new BadRequestException(RequestExceptionCode.WRONG_FORMAT, bindingResult.getFieldError().getDefaultMessage());
    }

    try {
      result = userService.signUpAsPerformer(request);
    } catch (DataIntegrityViolationException e) {
      throw new BadRequestException(SignupExceptionCode.ALREADY_EXIST_INFO, "회원가입 정보가 중복됩니다.");
    }

    return new SignUpAsPerformerResponse(result);
  }


  @Operation(summary = "로그아웃")
  @GetMapping("/logout")
  public boolean logout(HttpServletRequest request) {
    request.getSession().removeAttribute("loginUser");

    return true;
  }

  @Operation(summary = "현재 로그인한 사용자 정보")
  @GetMapping("/test")
  public LoginUser test(
    @Parameter(hidden = true) @SessionAttribute LoginUser loginUser
  ) {
    return loginUser;
  }

  @Operation(summary = "관리자 등록")
  @PostMapping("/admin")
  public SignUpAsOrdererResponse addAdmin(
    @Parameter(name = "회원 정보")
    @Validated @RequestBody SignUpAsAdminRequest request,
    BindingResult bindingResult, HttpServletRequest requestMsg
  ) {
    boolean result = false;

    if (bindingResult.hasFieldErrors()) {
      throw new BadRequestException(RequestExceptionCode.WRONG_FORMAT, bindingResult.getFieldError().getDefaultMessage());
    }

    try {
      result = userService.addNewAdmin(request);
    } catch (DataIntegrityViolationException e) {
      throw new BadRequestException(SignupExceptionCode.ALREADY_EXIST_INFO, "관리자 정보가 중복됩니다.");
    }

    return new SignUpAsOrdererResponse(result);
  }

  @Operation(summary = "포인트 충전")
  @PostMapping("/point")
  PointChargeResponse chargePoint(@RequestBody @Validated PointChargeRequest request,
                                  BindingResult bindingResult,
                                  @Parameter(hidden = true)
                                  @SessionAttribute LoginUser loginUser) {
    if (bindingResult.hasErrors())  {
      throw new BadRequestException(PointExceptionCode.WRONG_CHARGE_VALUE, "충전 금액은 1000 이상, 100000 이하입니다.");
    }

    PointChargeResponse response = pointService.chargeUserPoint(request.getEarnPoint(), loginUser);
    return response;
  }

  @Operation(summary = "내 포인트 조회")
  @GetMapping("/point")
  PointInfo chargePoint(@Parameter(hidden = true)
                        @SessionAttribute LoginUser loginUser) {
    PointInfo response = pointService.showPointInfo(loginUser);
    return response;
  }

  @Operation(summary = "자기소개글 변경")
  @PostMapping("/profile/introduction")
  boolean updateUserSelfIntroduction(@RequestBody @Validated SelfIntroductionUpdateRequest request,
                                     BindingResult bindingResult,
                                     @Parameter(hidden = true) @SessionAttribute LoginUser loginUser) {
    if (bindingResult.hasErrors()) {
      throw new BadRequestException(RequestExceptionCode.WRONG_FORMAT, bindingResult.getFieldError().getDefaultMessage());
    }

    userService.updateUserSelfIntroduction(request, loginUser);

    return true;
  }

  @Operation(summary = "프로필 조회")
  @GetMapping("/profile/{accountId}")
  ProfileInfo getUserProfile(@PathVariable String accountId,
                             @Parameter(hidden = true) @SessionAttribute LoginUser loginUser) {
    ProfileInfo result = userService.showUserProfileInfo(accountId);
    return result;
  }

}
