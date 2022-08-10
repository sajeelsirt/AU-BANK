package aubank.retail.liabilities.service.impl;

import aubank.retail.liabilities.domains.Pan;
import aubank.retail.liabilities.dtos.AofRequestDto;
import aubank.retail.liabilities.dtos.PanDataDto;
import aubank.retail.liabilities.dtos.PanObject;
import aubank.retail.liabilities.dtos.pan.PanRequestDto;
import aubank.retail.liabilities.exceptions.AuRlMicroServiceException;
import aubank.retail.liabilities.repositories.PanRepo;
import aubank.retail.liabilities.util.NextScreenScheduling;
import aubank.retail.liabilities.util.Utility;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class PanResolver {

    @Autowired
    PanRepo panRepo;
    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    Utility utility;

    @Autowired
    ServiceExecuteImpl serviceExecute;

    @Autowired
    AofResolver aofResolver;

    @Autowired
    NextScreenScheduling nextScreenScheduling;

    @Autowired
    IgwExecuteImpl igwExecute;

    public AofRequestDto getOrInsert(AofRequestDto requestDto) throws AuRlMicroServiceException, IOException {
        return null;
    }

    public Object add(AofRequestDto requestDto) throws AuRlMicroServiceException, IOException {
        Pan pan = new Pan();
        PanObject panObject = new PanObject();
        objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        panObject = objectMapper.convertValue(requestDto.getObjectData(),
                PanObject.class);
        pan.setPanObject(panObject);
        return panRepo.save(pan);
    }

    public Object update(AofRequestDto requestDto) throws AuRlMicroServiceException, IOException {
        Pan pan = new Pan();
        PanObject panObject = new PanObject();
        objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        panObject = objectMapper.convertValue(requestDto.getObjectData(),
                PanObject.class);
        pan = panRepo.findByRecordIdentifier1(requestDto.getAofId());
        pan.setPanObject(panObject);
        return panRepo.save(pan);
    }

    public Object delete(AofRequestDto requestDto) throws AuRlMicroServiceException, IOException {
        return null;
    }

    public Object get(AofRequestDto requestDto) throws AuRlMicroServiceException, IOException {
        return panRepo.findByRecordIdentifier1(requestDto.getAofId());
    }

    @SneakyThrows
    public Object panValidation(PanRequestDto panRequestDto, HttpHeaders httpHeaders) {

        List<String> requestid = httpHeaders.get("requestid");
        String reqid = requestid.get(0);
        Pan panDetails = panRepo.findByRecordIdentifier1(panRequestDto.getAofId());
        log.info("panDetails : {}", panDetails);

        //vahana pan validation api
        HttpHeaders panHeaders = utility.getApiHttpHeaders("PAN_VERIFICATION", panRequestDto.getAofId());
        panHeaders.set("requestid", reqid);
        Map<String, Object> panMap = new HashMap<>();
        panMap.put("PAN_NUMBER", panRequestDto.getPanId());
        panMap.put("AOF_ID", panRequestDto.getAofId());
        String panValidationResponse = igwExecute.executeIgwApi(panHeaders, panMap);
        log.info("parseESBApiMethod: {}", panValidationResponse);
        JsonNode panJsonNode = objectMapper.readTree(panValidationResponse);
        log.info("panJsonNode: {}", panJsonNode);
        if (panJsonNode.get("process_status").textValue().equals("Failure")){
            log.info("Wrong pan number");
            throw new Exception("Wrong pan number");
        }
        JsonNode apiResponse = panJsonNode.get("response").get("result");
        log.info("apiResponse : {}", apiResponse);
        PanDataDto panDataDto = objectMapper.convertValue(apiResponse, PanDataDto.class);
        log.info("panDataDto : {}", panDataDto);

        //update pan details if exists else create new
        if (panDetails != null){
            PanObject panObject = panDetails.getPanObject();
            panObject.setPanData(panDataDto);
            panDetails.setPanObject(panObject);
            panRepo.save(panDetails);
        }else {
            log.info("panDataDto: {}", panDataDto);
            PanObject panObject = new PanObject();
            panObject.setAofId(panRequestDto.getAofId());
            panObject.setStatus("success");
            panObject.setPanData(panDataDto);
            log.info("panObject: {}", panObject);
            Pan pan = new Pan();
            pan.setPanObject(panObject);
            pan.setRecordIdentifier1(panRequestDto.getAofId());
            panRepo.save(pan);
        }
        //update new screen
        aofResolver.updateNextScreen(panRequestDto.getAofId(),nextScreenScheduling.getNextScreen("Pan"));
        return panDataDto;
    }
}
