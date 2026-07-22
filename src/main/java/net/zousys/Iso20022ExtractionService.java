package com.example.clearmatch.service;

import com.example.clearmatch.model.PaymentEvent;
import com.prowidesoftware.swift.model.mx.MxCamt05400108;
import com.prowidesoftware.swift.model.mx.dic.EntryTransaction9;
import com.prowidesoftware.swift.model.mx.dic.ReportEntry9;
import org.springframework.stereotype.Service;

@Service
public class Iso20022ExtractionService {

    public PaymentEvent parseCamt054(String xml) {

        MxCamt05400108 mx = MxCamt05400108.parse(xml);

        ReportEntry9 entry = mx.getBkToCstmrDbtCdtNtfctn()
                .getNtfctn().get(0)
                .getNtry().get(0);

        EntryTransaction9 tx = entry.getNtryDtls()
                .get(0)
                .getTxDtls().get(0);

        return new PaymentEvent(
                tx.getRefs().getEndToEndId(),
                tx.getRefs().getTxId(),
                entry.getAmt().getValue(),
                entry.getAmt().getCcy(),
                tx.getRltdPties().getDbtr().getNm()
        );
    }
}
