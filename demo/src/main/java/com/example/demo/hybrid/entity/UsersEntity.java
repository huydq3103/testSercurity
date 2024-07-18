package com.example.demo.hybrid.entity;

import com.example.demo.hybrid.until.constant.CommonConstant;
import jakarta.persistence.*;
import lombok.*;

@Entity(name = "users")
@Table
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UsersEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.NONE)
    private Long accountId;

    @Column(columnDefinition = CommonConstant.VARCHAR_50)
    private String userName;

    @Column(columnDefinition = CommonConstant.LONG_TEXT)
    private String password;

    @Column(columnDefinition = CommonConstant.LONG_TEXT)
    private String email;

    @Column(columnDefinition =CommonConstant.LONG_TEXT)
    private String encryptedAESKey;

    @Column(columnDefinition = CommonConstant.LONG_TEXT)
    private String publicKeyPath;

    @Column(columnDefinition = CommonConstant.LONG_TEXT)
    private String privateKeyPath;

}
