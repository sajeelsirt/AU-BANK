package aubank.retail.liabilities.service.impl;

import aubank.retail.liabilities.domains.Appointment;
import aubank.retail.liabilities.dtos.AppointmentObject;
import aubank.retail.liabilities.dtos.AofRequestDto;
import aubank.retail.liabilities.exceptions.AuRlMicroServiceException;
import aubank.retail.liabilities.repositories.AppointmentRepo;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class AppointmentResolver {

    @Autowired
    AppointmentRepo appointmentRepo;
    @Autowired
    ObjectMapper objectMapper;

    public AofRequestDto getOrInsert(AofRequestDto requestDto) throws AuRlMicroServiceException, IOException {
        return null;
    }

    public Object add(AofRequestDto requestDto) throws AuRlMicroServiceException, IOException {
        Appointment appointment = new Appointment();
        AppointmentObject appointmentObject = new AppointmentObject();
        objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        appointmentObject = objectMapper.convertValue(requestDto.getObjectData(),
                AppointmentObject.class);
        appointment.setAppointmentObject(appointmentObject);
        return appointmentRepo.save(appointment);
    }

    public Object update(AofRequestDto requestDto) throws AuRlMicroServiceException, IOException {
        Appointment appointment = new Appointment();
        AppointmentObject appointmentObject = new AppointmentObject();
        objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        appointmentObject = objectMapper.convertValue(requestDto.getObjectData(),
                AppointmentObject.class);
        appointment = appointmentRepo.findByRecordIdentifier1(requestDto.getAofId());
        appointment.setAppointmentObject(appointmentObject);
        return appointmentRepo.save(appointment);
    }

    public Object delete(AofRequestDto requestDto) throws AuRlMicroServiceException, IOException {
        return null;
    }

    public Object get(AofRequestDto requestDto) throws AuRlMicroServiceException, IOException {
        return appointmentRepo.findByRecordIdentifier1(requestDto.getAofId());
    }
}
