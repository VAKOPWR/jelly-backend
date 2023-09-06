package com.vako.application.location.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

import java.math.BigDecimal;

@Entity
@Data
@Table(name = "location")
public class Location {
    @Id
    @GeneratedValue
    private Long id;
    private String userId;
    private BigDecimal latitude;
    private BigDecimal longitude;
}
