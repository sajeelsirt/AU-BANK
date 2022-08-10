package aubank.retail.liabilities.service.impl;

import aubank.retail.liabilities.domains.Contact;
import aubank.retail.liabilities.dtos.ContactObject;
import aubank.retail.liabilities.dtos.AofRequestDto;
import aubank.retail.liabilities.exceptions.AuRlMicroServiceException;
import aubank.retail.liabilities.repositories.ContactRepo;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class ContactResolver{
    @Autowired
    ContactRepo contactRepo;
    @Autowired
    ObjectMapper objectMapper;

    public AofRequestDto getOrInsert(AofRequestDto requestDto) throws AuRlMicroServiceException, IOException {
        return null;
    }

    public Object add(AofRequestDto requestDto) throws AuRlMicroServiceException, IOException {
        Contact contact = new Contact();
        ContactObject contactObject = new ContactObject();
        objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        contactObject = objectMapper.convertValue(requestDto.getObjectData(),
                ContactObject.class);
        contact.setContactObject(contactObject);
        return contactRepo.save(contact);
    }

    public Object update(AofRequestDto requestDto) throws AuRlMicroServiceException, IOException {
        Contact contact = new Contact();
        ContactObject contactObject = new ContactObject();
        objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        contactObject = objectMapper.convertValue(requestDto.getObjectData(),
                ContactObject.class);
        contact = contactRepo.findByRecordIdentifier1(requestDto.getAofId());
        contact.setContactObject(contactObject);
        return contactRepo.save(contact);
    }

    public Object delete(AofRequestDto requestDto) throws AuRlMicroServiceException, IOException {
        return null;
    }

    public Object get(AofRequestDto requestDto) throws AuRlMicroServiceException, IOException {
        return contactRepo.findByRecordIdentifier1(requestDto.getAofId());
    }
}
