package com.kote.banking.repository;

import com.kote.banking.entity.Tenant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface TenantRepository extends JpaRepository<Tenant, Long> {
    @Query("SELECT t.identifier FROM Tenant t")
    List<String> findAllIds();
}
