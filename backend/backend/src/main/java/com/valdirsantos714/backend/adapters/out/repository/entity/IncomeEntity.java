package com.valdirsantos714.backend.adapters.out.repository.entity;

import com.valdirsantos714.backend.adapters.out.repository.mapper.UserMapper;
import com.valdirsantos714.backend.application.core.domain.Income;
import com.valdirsantos714.backend.application.core.domain.enums.IncomeCategory;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "TB_INCOME")
@Getter
@Setter
@EqualsAndHashCode(of = "id")
@NoArgsConstructor
@AllArgsConstructor
public class IncomeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    private String description;

    @Column(nullable = false)
    private Double amount;

    private LocalDate date;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserEntity user;

    @Enumerated(EnumType.STRING)
    private IncomeCategory category;

    public IncomeEntity(Income income) {
        this.id = income.getId();
        this.name = income.getName();
        this.description = income.getDescription();
        this.amount = income.getAmount();
        this.date = income.getDate();
        this.user = UserMapper.toUserEntity(income.getUser());
        this.category = income.getCategory();
    }
}
