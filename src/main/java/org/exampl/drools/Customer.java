package org.exampl.drools;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class Customer {
    private CustomerType type;

    private int years;

    private int discount;

    // Standard getters and setters
    public enum CustomerType {
        INDIVIDUAL,
        BUSINESS;
    }
}
