package org.lahlou.net.ebankservice.entities;

import jakarta.persistence.*;
import lombok.*;
import org.lahlou.net.ebankservice.model.Customer;

import java.util.Date;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BankAccount {

    @Id
    private String id;

    private double balance;

    private Date createdAt;

    private String type;

    private Long customerId;

    @Transient
    private Customer customer;
}
