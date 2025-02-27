package com.four.brothers.runtou.dto.model;

import com.four.brothers.runtou.domain.Orderer;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class OrdererDto implements ModelDto<OrdererDto, Orderer> {
  private Long id;
  private String accountId;
  private String password;
  private String realName;
  private String nickname;
  private String accountNumber;
  private Boolean isDoingJobNow;
  private String selfIntroduction;
  private Integer point;

  @Override
  public OrdererDto toDtoFromEntity(Orderer entity) {
    this.id = entity.getId();
    this.accountId = entity.getAccountId();
    this.password = entity.getPassword();
    this.realName = entity.getRealName();
    this.nickname = entity.getNickname();
    this.accountNumber = entity.getAccountNumber();
    this.isDoingJobNow = entity.getIsDoingJobNow();
    this.selfIntroduction = entity.getSelfIntroduction();
    this.point = entity.getPoint();

    return this;
  }

  @Override
  public String getFieldValueByName(String fieldName) {
    switch (fieldName) {
      case "id":
        return String.valueOf(this.id);
      case "accountId":
        return this.accountId;
      case "password":
        return this.password;
      case "realName":
        return this.realName;
      case "nickname":
        return this.nickname;
      case "accountNumber":
        return this.accountNumber;
      case "isDoingJobNow":
        return String.valueOf(this.isDoingJobNow);
      case "selfIntroduction":
        return this.selfIntroduction;
      case "point":
        return String.valueOf(this.point);
    }
    return "";
  }
}
