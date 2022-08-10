package aubank.retail.liabilities.service.impl;

import aubank.retail.liabilities.domains.Aadhaar;
import aubank.retail.liabilities.dtos.AadhaarObject;
import aubank.retail.liabilities.dtos.AofRequestDto;
import aubank.retail.liabilities.exceptions.AuRlMicroServiceException;
import aubank.retail.liabilities.repositories.AadhaarRepo;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.io.IOException;

@Service
public class AadhaarResolver {

    @Autowired
    AadhaarRepo aadhaarRepo;
    @Autowired
    ObjectMapper objectMapper;

    public AofRequestDto getOrInsert(AofRequestDto requestDto) throws AuRlMicroServiceException, IOException {
        return null;
    }

    public Object add(AofRequestDto requestDto) throws AuRlMicroServiceException, IOException {
        Aadhaar aadhaar = new Aadhaar();
        AadhaarObject aadhaarObject = new AadhaarObject();
        objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        aadhaarObject = objectMapper.convertValue(requestDto.getObjectData(),
                AadhaarObject.class);
        aadhaar.setAadhaarObject(aadhaarObject);
        return aadhaarRepo.save(aadhaar);
    }

    public Object update(AofRequestDto requestDto) throws AuRlMicroServiceException, IOException {
        Aadhaar aadhaar = new Aadhaar();
        AadhaarObject aadhaarObject = new AadhaarObject();
        objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        aadhaarObject = objectMapper.convertValue(requestDto.getObjectData(),
                AadhaarObject.class);
        aadhaar = aadhaarRepo.findByRecordIdentifier1(requestDto.getAofId());
        aadhaar.setAadhaarObject(aadhaarObject);
        return aadhaarRepo.save(aadhaar);
    }

    public Object delete(AofRequestDto requestDto) throws AuRlMicroServiceException, IOException {
        return null;
    }

    public Object get(AofRequestDto requestDto) throws AuRlMicroServiceException, IOException {
        return aadhaarRepo.findByRecordIdentifier1(requestDto.getAofId());
    }
}
