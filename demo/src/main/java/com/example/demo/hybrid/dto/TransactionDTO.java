package com.example.demo.hybrid.dto;

import com.example.demo.hybrid.until.constant.CommonConstant;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TransactionDTO {

    private String transactionId;

    private String account;

    private Long inDebt;

    private Long have;

    @JsonFormat(pattern = CommonConstant.DATE_FORMAT)
    private LocalDate time;


}
