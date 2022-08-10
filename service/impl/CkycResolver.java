package aubank.retail.liabilities.service.impl;

import aubank.retail.liabilities.domains.Ckyc;
import aubank.retail.liabilities.dtos.CkycObject;
import aubank.retail.liabilities.dtos.AofRequestDto;
import aubank.retail.liabilities.exceptions.AuRlMicroServiceException;
import aubank.retail.liabilities.repositories.CkycRepo;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class CkycResolver {

    @Autowired
    CkycRepo ckycRepo;
    @Autowired
    ObjectMapper objectMapper;

    public AofRequestDto getOrInsert(AofRequestDto requestDto) throws AuRlMicroServiceException, IOException {
        return null;
    }

    public Object add(AofRequestDto requestDto) throws AuRlMicroServiceException, IOException {
        Ckyc ckyc = new Ckyc();
        CkycObject ckycObject = new CkycObject();
        objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        ckycObject = objectMapper.convertValue(requestDto.getObjectData(),
                CkycObject.class);
        ckyc.setCkycObject(ckycObject);
        return ckycRepo.save(ckyc);
    }

    public Object update(AofRequestDto requestDto) throws AuRlMicroServiceException, IOException {
        Ckyc ckyc = new Ckyc();
        CkycObject ckycObject = new CkycObject();
        objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        ckycObject = objectMapper.convertValue(requestDto.getObjectData(),
                CkycObject.class);
        ckyc = ckycRepo.findByRecordIdentifier1(requestDto.getAofId());
        ckyc.setCkycObject(ckycObject);
        return ckycRepo.save(ckyc);
    }

    public Object delete(AofRequestDto requestDto) throws AuRlMicroServiceException, IOException {
        return null;
    }

    public Object get(AofRequestDto requestDto) throws AuRlMicroServiceException, IOException {
        return ckycRepo.findByRecordIdentifier1(requestDto.getAofId());
    }
}
