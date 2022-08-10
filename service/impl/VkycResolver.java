package aubank.retail.liabilities.service.impl;

import aubank.retail.liabilities.domains.Vkyc;
import aubank.retail.liabilities.dtos.AofRequestDto;
import aubank.retail.liabilities.dtos.VkycObject;
import aubank.retail.liabilities.exceptions.AuRlMicroServiceException;
import aubank.retail.liabilities.repositories.VkycRepo;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;

import static org.springframework.beans.BeanUtils.copyProperties;
@Service
public class VkycResolver{
    @Autowired
    VkycRepo vkycRepo;

    @Autowired
    ObjectMapper objectMapper = new ObjectMapper();

    public AofRequestDto getOrInsert(AofRequestDto requestDto) throws AuRlMicroServiceException, IOException {
        return null;
    }

    public Object add(AofRequestDto requestDto) throws AuRlMicroServiceException, IOException {
        Vkyc vkyc = new Vkyc();
        VkycObject vkycObject = new VkycObject();
        objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        vkycObject = objectMapper.convertValue(requestDto.getObjectData(),
                VkycObject.class);
        vkyc.setVkycObject(vkycObject);
        return vkycRepo.save(vkyc);
    }

    public Object update(AofRequestDto requestDto) throws AuRlMicroServiceException, IOException {
        Vkyc vkyc = new Vkyc();
        VkycObject vkycObject = new VkycObject();
        objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        vkycObject = objectMapper.convertValue(requestDto.getObjectData(),
                VkycObject.class);
        vkyc = vkycRepo.findByRecordIdentifier1(requestDto.getAofId());
        vkyc.setVkycObject(vkycObject);
        return vkycRepo.save(vkyc);
    }

    public Object delete(AofRequestDto requestDto) throws AuRlMicroServiceException, IOException {
        return null;
    }

    public Object get(AofRequestDto requestDto) throws AuRlMicroServiceException, IOException {
        return vkycRepo.findByRecordIdentifier1(requestDto.getAofId());
    }
}
