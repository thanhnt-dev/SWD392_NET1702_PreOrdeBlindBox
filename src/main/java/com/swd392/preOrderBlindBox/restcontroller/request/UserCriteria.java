package com.swd392.preOrderBlindBox.restcontroller.request;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserCriteria extends BaseCriteria {
  private Integer currentPage;
  private Integer pageSize;
  private String search;
  private String email;
  private String phone;
}
