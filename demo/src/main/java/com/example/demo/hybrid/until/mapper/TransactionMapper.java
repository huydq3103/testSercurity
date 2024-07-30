package com.example.demo.hybrid.until.mapper;

import com.example.demo.hybrid.dto.DecryptedTransactionDTO;
import com.example.demo.hybrid.dto.TransactionDTO;
import com.example.demo.hybrid.entity.TransactionHistoryEntity;
import com.example.demo.hybrid.until.validate.ValidationUtils;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class TransactionMapper {

    public static TransactionDTO convertToDTO(TransactionHistoryEntity entity) {

        ValidationUtils.validateObject(entity);

        // Sử dụng builder để tạo đối tượng DecryptedTransactionDTO
        return TransactionDTO.builder()
                .transactionId(entity.getTransactionId())
                .account(entity.getAccount())
                .inDebt(entity.getInDebt())
                .have(entity.getHave())
                .time(entity.getTime())
                .build();
    }

    public static List<TransactionDTO> convertToDTOList(List<TransactionHistoryEntity> entities) {
        if (Objects.isNull(entities)) {
            return Collections.emptyList();
        }

        return entities.stream()
                .map(TransactionMapper::convertToDTO)
                .collect(Collectors.toList());
    }
}
