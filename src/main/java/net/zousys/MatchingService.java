package com.example.clearmatch.service;

import com.example.clearmatch.model.Invoice;
import com.example.clearmatch.model.PaymentEvent;
import com.example.clearmatch.model.ReconciliationResult;
import com.example.clearmatch.repo.InvoiceRepository;
import org.springframework.stereotype.Service;

@Service
public class MatchingService {

    private final InvoiceRepository invoiceRepository;

    public MatchingService(InvoiceRepository invoiceRepository) {
        this.invoiceRepository = invoiceRepository;
    }

    public ReconciliationResult reconcile(PaymentEvent payment) {

        return invoiceRepository.findByInvoiceNo(payment.endToEndId())
                .map(invoice -> evaluate(payment, invoice))
                .orElse(new ReconciliationResult(
                        "UNMATCHED",
                        null,
                        0,
                        "Invoice not found"
                ));
    }

    private ReconciliationResult evaluate(PaymentEvent payment, Invoice invoice) {

        int score = 0;

        // exact invoice reference
        if (payment.endToEndId().equals(invoice.invoiceNo())) {
            score += 100;
        }

        // amount validation
        if (payment.amount().compareTo(invoice.outstanding()) == 0) {
            score += 50;
        }

        // debtor validation
        if (payment.debtorName().equalsIgnoreCase(invoice.customerName())) {
            score += 20;
        }

        if (score >= 150) {
            return new ReconciliationResult(
                    "MATCHED",
                    invoice.invoiceNo(),
                    score,
                    "Auto-matched successfully"
            );
        }

        return new ReconciliationResult(
                "REVIEW",
                invoice.invoiceNo(),
                score,
                "Requires manual review"
        );
    }
}
