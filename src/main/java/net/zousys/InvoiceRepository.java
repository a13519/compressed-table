package com.example.clearmatch.repo;

import com.example.clearmatch.model.Invoice;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public class InvoiceRepository {

    private final List<Invoice> invoices = List.of(
            new Invoice("INV-2026-001", new BigDecimal("100000.00"), "ABC Manufacturing Ltd"),
            new Invoice("INV-2026-002", new BigDecimal("50000.00"), "ABC Manufacturing Ltd")
    );

    public Optional<Invoice> findByInvoiceNo(String invoiceNo) {
        return invoices.stream()
                .filter(i -> i.invoiceNo().equals(invoiceNo))
                .findFirst();
    }
}
