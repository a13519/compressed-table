package com.example.clearmatch.controller;

import com.example.clearmatch.model.PaymentEvent;
import com.example.clearmatch.model.ReconciliationResult;
import com.example.clearmatch.service.Iso20022ExtractionService;
import com.example.clearmatch.service.MatchingService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/reconcile")
public class ReconciliationController {

    private final Iso20022ExtractionService extractionService;
    private final MatchingService matchingService;

    public ReconciliationController(
            Iso20022ExtractionService extractionService,
            MatchingService matchingService) {

        this.extractionService = extractionService;
        this.matchingService = matchingService;
    }

    @PostMapping(
            value = "/camt054",
            consumes = MediaType.APPLICATION_XML_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ReconciliationResult reconcile(@RequestBody String xml) {

        PaymentEvent payment = extractionService.parseCamt054(xml);

        return matchingService.reconcile(payment);
    }
}
