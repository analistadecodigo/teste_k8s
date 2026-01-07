package com.valdirsantos714.backend.adapters.out.repository;

import com.valdirsantos714.backend.adapters.out.repository.entity.ExpenseEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface ExpenseJpaRepository extends JpaRepository<ExpenseEntity, Long> {
    @Query("SELECT e FROM ExpenseEntity e WHERE e.user.email = :email")
    List<ExpenseEntity> findByUserEmail(@Param("email") String email);

    @Query("SELECT e FROM ExpenseEntity e WHERE e.user.email = :email AND e.id = :id")
    Optional<ExpenseEntity> findByUserEmailAndId(@Param("email") String email, @Param("id") Long id);

    @Modifying
    @Transactional
    @Query("DELETE FROM ExpenseEntity e WHERE e.user.email = :email AND e.id = :id")
    void deleteByUserEmailAndId(@Param("email") String email, @Param("id") Long id);

    @Query("SELECT COALESCE(SUM(e.amount), 0.0) FROM ExpenseEntity e WHERE e.user.email = :email")
    Double sumExpensesByUserEmail(@Param("email") String email);
}
