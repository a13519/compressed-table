package com.example.clearmatch.model;

import java.math.BigDecimal;

public record Invoice(
        String invoiceNo,
        BigDecimal outstanding,
        String customerName
) {}
