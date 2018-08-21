package com.shuman.transfers.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.math.BigDecimal;

/**
 * Using this class for both DAO and DTO layers for the sake of simplicity
 */
@Entity
@Table(name = "accounts")
@Data
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
public class Account {
    @Id
    @GeneratedValue
    private Long id;

    private BigDecimal balance;

    public Account(BigDecimal balance) {
        this.balance = balance;
    }
}
