package aubank.retail.liabilities.service.impl;

import aubank.retail.liabilities.domains.Account;
import aubank.retail.liabilities.dtos.AccountObject;
import aubank.retail.liabilities.dtos.AofRequestDto;
import aubank.retail.liabilities.exceptions.AuRlMicroServiceException;
import aubank.retail.liabilities.repositories.AccountRepo;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class AccountResolver {

    @Autowired
    AccountRepo accountRepo;
    @Autowired
    ObjectMapper objectMapper;

    public AofRequestDto getOrInsert(AofRequestDto requestDto) throws AuRlMicroServiceException, IOException {
        return null;
    }

    public Object add(AofRequestDto requestDto) throws AuRlMicroServiceException, IOException {
        Account account = new Account();
        AccountObject accountObject = new AccountObject();
        objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        accountObject = objectMapper.convertValue(requestDto.getObjectData(),
                AccountObject.class);
        account.setAccountObject(accountObject);
        return accountRepo.save(account);
    }

    public Object update(AofRequestDto requestDto) throws AuRlMicroServiceException, IOException {
        Account account = new Account();
        AccountObject accountObject = new AccountObject();
        objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        accountObject = objectMapper.convertValue(requestDto.getObjectData(),
                AccountObject.class);
        account = accountRepo.findByRecordIdentifier1(requestDto.getAofId());
        account.setAccountObject(accountObject);
        return accountRepo.save(account);
    }

    public Object delete(AofRequestDto requestDto) throws AuRlMicroServiceException, IOException {
        return null;
    }

    public Object get(AofRequestDto requestDto) throws AuRlMicroServiceException, IOException {
        return accountRepo.findByRecordIdentifier1(requestDto.getAofId());
    }
}
