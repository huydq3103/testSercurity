package com.example.demo.hybrid.entity;

import com.example.demo.hybrid.until.constant.CommonConstant;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity(name = "transaction_history")
@Table
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TransactionHistoryEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.NONE)
    private Long id;

    @Column(columnDefinition = CommonConstant.LONG_TEXT)
    private String transactionId;


    @Column(columnDefinition = CommonConstant.LONG_TEXT)
    private String account;

    @Column(columnDefinition = CommonConstant.LONG_TEXT)
    private Long inDebt;

    @Column(columnDefinition = CommonConstant.LONG_TEXT)
    private Long have;

    @JsonFormat(pattern = CommonConstant.DATE_FORMAT)
    private LocalDate time;

    @Column(columnDefinition = CommonConstant.LONG_TEXT)
    private String encryptedAESKey;
}
