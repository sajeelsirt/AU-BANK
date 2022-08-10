package aubank.retail.liabilities.service.impl;

import aubank.retail.liabilities.domains.Payment;
import aubank.retail.liabilities.dtos.PaymentObject;
import aubank.retail.liabilities.dtos.AofRequestDto;
import aubank.retail.liabilities.exceptions.AuRlMicroServiceException;
import aubank.retail.liabilities.repositories.PaymentRepo;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.io.IOException;

@Service
public class PaymentResolver{
    @Autowired
    PaymentRepo paymentRepo;
    @Autowired
    ObjectMapper objectMapper = new ObjectMapper();

    public AofRequestDto getOrInsert(AofRequestDto requestDto) throws AuRlMicroServiceException, IOException {
        return null;
    }

    public Object add(AofRequestDto requestDto) throws AuRlMicroServiceException, IOException {
        Payment payment = new Payment();
        PaymentObject paymentObject = new PaymentObject();
        objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        paymentObject = objectMapper.convertValue(requestDto.getObjectData(),
                PaymentObject.class);
        payment.setPaymentObject(paymentObject);
        return paymentRepo.save(payment);
    }

    public Object update(AofRequestDto requestDto) throws AuRlMicroServiceException, IOException {
        Payment payment = new Payment();
        PaymentObject paymentObject = new PaymentObject();
        objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        paymentObject = objectMapper.convertValue(requestDto.getObjectData(),
                PaymentObject.class);
        payment = paymentRepo.findByRecordIdentifier1(requestDto.getAofId());
        payment.setPaymentObject(paymentObject);
        return paymentRepo.save(payment);
    }

    public Object delete(AofRequestDto requestDto) throws AuRlMicroServiceException, IOException {
        return null;
    }

    public Object get(AofRequestDto requestDto) throws AuRlMicroServiceException, IOException {
        return paymentRepo.findByRecordIdentifier1(requestDto.getAofId());
    }
}
