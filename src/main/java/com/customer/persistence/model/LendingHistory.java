package com.customer.persistence.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "lendingHistory")
public class LendingHistory {
    @Id
    @GeneratedValue(generator = "UUID")
    @Column(name = "id")
    private UUID id;

    @Column(name = "customer_id")
    @Setter
    @Getter
    private Integer customerId;

    @Column(name = "book_id")
    @Setter
    @Getter
    private UUID bookId;

    @Column(name = "lend_date")
    @Setter
    @Getter
    private Date lendDate;

    @Column(name = "lent_till")
    @Setter
    private Date lendTill;

    public void setBookId(UUID bookId) {
        this.bookId = bookId;
    }

    public void setCustomerId(Integer customerId) {
        this.customerId = customerId;
    }

    public void setLendDate(Date lendDate) {
        this.lendDate = lendDate;
    }

    public void setLendTill(Date lendTill) {
        this.lendTill = lendTill;
    }

    public Integer getCustomerId() {
        return customerId;
    }

    public UUID getBookId() {
        return bookId;
    }
}
