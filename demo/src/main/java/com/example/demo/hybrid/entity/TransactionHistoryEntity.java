package com.example.demo.hybrid.entity;

import com.example.demo.hybrid.until.constant.CommonConstant;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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
    private Long id;

    @Column(columnDefinition = CommonConstant.LONG_TEXT)
    private String transactionId;

    @Column(columnDefinition = CommonConstant.VARCHAR_10)
    private String account;

    @Column(columnDefinition = CommonConstant.VARCHAR_10)
    private BigDecimal inDebt;

    @Column(columnDefinition = CommonConstant.VARCHAR_10)
    private BigDecimal have;

    private LocalDate time;

    @Column(columnDefinition =CommonConstant.LONG_TEXT)
    private String encryptedAESKey;

    @Column(columnDefinition = CommonConstant.LONG_TEXT)
    private String publicKeyPath;

    @Column(columnDefinition = CommonConstant.LONG_TEXT)
    private String privateKeyPath;

}
