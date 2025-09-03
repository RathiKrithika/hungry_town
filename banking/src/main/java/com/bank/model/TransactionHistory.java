package com.bank.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Date;
import java.util.UUID;


@Document(collection = "transaction_history")
@Getter
@Setter
@NoArgsConstructor
public class TransactionHistory {

    // The @Id field in MongoDB is typically a String. It's automatically managed by the database.
    @Id
    private String id;

    // We use @Field to specify the document field name if it's different from the property name.
    // In this case, it matches, but it's good practice.
    @Field("transaction_id")
    private String transactionId;

    @Field("occurred_for")
    private Long occurredFor;

    private Long participant;

    @Field("occurred_on")
    private Date txnOccurredAt;

    @Field("closing_balance")
    private Double closingBalance;

    @Field("txn_type")
    private String txnType;

    private Double amount;

    // AllArgsConstructor is excluded to manage the UUID and date generation
    // in a custom constructor.

    // A custom constructor to set the transactionId and txnOccurredAt automatically.
    // We removed the @PrePersist annotation which is for JPA.
    public TransactionHistory(Long occurredFor, Long participant, Double closingBalance, String txnType,
                              Double amount) {

        this.transactionId = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        this.txnOccurredAt = new Date();
        this.occurredFor = occurredFor;
        this.participant = participant;
        this.closingBalance = closingBalance;
        this.txnType = txnType;
        this.amount = amount;
    }

    public TransactionHistory(long l, String s, long l1, long l2, Date date, double v, String string, double v1) {
        this.id = String.valueOf(l);
        this.transactionId = s;
        this.occurredFor = l1;
        this.participant = l2;
        this.txnOccurredAt = date;
        this.amount = v;
        this.txnType = string;
        this.closingBalance = v1;


    }
}
