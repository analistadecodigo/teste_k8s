package com.valdirsantos714.backend.adapters.out.repository;

import com.valdirsantos714.backend.adapters.out.repository.entity.IncomeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface IncomeJpaRepository extends JpaRepository<IncomeEntity, Long> {
    @Query("SELECT i FROM IncomeEntity i WHERE i.user.email = :email")
    List<IncomeEntity> findByUserEmail(@Param("email") String email);

    @Query("SELECT i FROM IncomeEntity i WHERE i.user.email = :email AND i.id = :id")
    Optional<IncomeEntity> findByUserEmailAndId(@Param("email") String email, @Param("id") Long id);

    @Modifying
    @Transactional
    @Query("DELETE FROM IncomeEntity i WHERE i.user.email = :email AND i.id = :id")
    void deleteByUserEmailAndId(@Param("email") String email, @Param("id") Long id);

    @Query("SELECT COALESCE(SUM(i.amount), 0.0) FROM IncomeEntity i WHERE i.user.email = :email")
    Double sumIncomesByUserEmail(@Param("email") String email);
}
