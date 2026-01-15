package com.kote.banking.service;

import com.kote.banking.dto.TransactionSearchDto;
import com.kote.banking.entity.Transaction;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class TransactionSpecification {
    public Specification<Transaction> getTransactions(TransactionSearchDto dto){
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            Predicate sourceAccountId = criteriaBuilder.equal(root.get("sourceAccountId"), dto.getAccountNumber());
            Predicate targetAccountId = criteriaBuilder.equal(root.get("targetAccountId"), dto.getAccountNumber());

            Predicate or = criteriaBuilder.or(sourceAccountId, targetAccountId);
            predicates.add(or);


            if (dto.getEndDate() != null){
                Predicate startDate = criteriaBuilder.lessThanOrEqualTo(root.get("createdAt"), dto.getEndDate());
                predicates.add(startDate);
            }

            if (dto.getCreatedAt() != null){
                Predicate startDate = criteriaBuilder.greaterThanOrEqualTo(root.get("createdAt"), dto.getCreatedAt());
                predicates.add(startDate);
            }

            if (dto.getTransactionType() != null){
                Predicate type = criteriaBuilder.equal(root.get("transactionType"), dto.getTransactionType());
                predicates.add(type);
            }
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
