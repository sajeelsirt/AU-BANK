package aubank.retail.liabilities.service.impl;

import aubank.retail.liabilities.domains.Aof;
import aubank.retail.liabilities.dtos.AofObject;
import aubank.retail.liabilities.dtos.AofRequestDto;
import aubank.retail.liabilities.exceptions.AuRlMicroServiceException;
import aubank.retail.liabilities.repositories.AofRepo;
import aubank.retail.liabilities.util.NextScreenScheduling;
import aubank.retail.liabilities.util.Utility;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.io.IOException;
import java.sql.Timestamp;

@Service
@Slf4j
public class AofResolver{

    @Autowired
    AofRepo aofRepo;
    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    Utility utility;
    @Autowired
    NextScreenScheduling nextScreenScheduling;

    public Aof getOrInsert(AofRequestDto requestDto) throws AuRlMicroServiceException, IOException {
        Aof aof1 = new Aof();
        if (!requestDto.getAofId().isEmpty() &&
                !(requestDto.getAofId() == null)) {
            aof1 = get(requestDto);
            } else {
                Aof aof = aofRepo.findByText1AndText2(requestDto.getRecordIdentifier2(),
                        requestDto.getRecordIdentifier3());
                if (aof == null) {
                    aof1 = add(requestDto);
                    System.out.println(aof1.getRecordIdentifier2());
                    //set next scrren as pANb
                }else{
                    requestDto.setAofId(aof.getRecordIdentifier1());
                         aof1 = get(requestDto);
                }
    }
        System.out.println(aof1);
            return aof1;
    }

    public Aof add(AofRequestDto requestDto) throws AuRlMicroServiceException, IOException {
        Aof aof = new Aof();
        AofObject aofObject = new AofObject();
 //       objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        aofObject = objectMapper.convertValue(requestDto.getObjectData(),
                AofObject.class);
        aofObject.setNextScreen(nextScreenScheduling.getNextScreen(requestDto.getObjectType()));
        aof.setRecordIdentifier2(requestDto.getRecordIdentifier2());
        aof.setRecordIdentifier3(requestDto.getRecordIdentifier3());
        aof.setCreatedBy(requestDto.getLoginId());
        aof.setCreatedTimestamp(new Timestamp(System.currentTimeMillis()));
        String aofId = utility.createAofId(requestDto);
        aof.setRecordIdentifier1(aofId);
        aofObject.setAofId(aofId);
        aof.setAofObject(aofObject);
        aofRepo.save(aof);
        return aof;
    }

    public Aof update(AofRequestDto requestDto) throws AuRlMicroServiceException, IOException {
        Aof aof = new Aof();
        AofObject aofObject = new AofObject();
        objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        aofObject = objectMapper.convertValue(requestDto.getObjectData(),
                AofObject.class);
        aof.setModifiedBy(requestDto.getLoginId());
        aof.setModifiedTimestamp(new Timestamp(System.currentTimeMillis()));
        aof.setAofObject(aofObject);
        return aofRepo.save(aof);
    }

    public Aof delete(AofRequestDto requestDto) throws AuRlMicroServiceException, IOException {
        return null;
    }

    public Aof get(AofRequestDto requestDto) throws AuRlMicroServiceException, IOException {
        return aofRepo.findByRecordIdentifier1(requestDto.getAofId());
    }

    public Aof updateNextScreen(String aofId, String nextScreen) throws Exception {
        Aof aof = aofRepo.findByRecordIdentifier1(aofId);
        if (aof == null){
            log.info("aof ID does not exists");
            throw new Exception("aof ID does not exists");
        }
        AofObject aofObject = aof.getAofObject();
        aofObject.setNextScreen(nextScreen);
        aof.setAofObject(aofObject);
        return aofRepo.save(aof);
    }
}
