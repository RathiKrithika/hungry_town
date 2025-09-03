package com.bank.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Date;
import java.util.concurrent.ThreadLocalRandom;

@Document(collection = "account_owner")
@Getter
@Setter
@NoArgsConstructor
public class AccountOwner {

    // MongoDB uses a String for the document ID, which is automatically handled.
    @Id
    private String id;

    private String name;

    // Use @Field to specify the name of the field in the MongoDB document.
    @Field("account_num")
    private Long accountNumber;

    @Field("created_at")
    private Date createdAt;

    @Field("phone_number")
    private String phoneNumber;

    // Custom constructor to handle automatic field population, replacing @PrePersist.
    public AccountOwner(String name, String phoneNumber) {
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.createdAt = new Date();
        // Generate a 10-digit account number.
        this.accountNumber = ThreadLocalRandom.current().nextLong(1_000_000_000L, 10_000_000_000L);
    }
}
