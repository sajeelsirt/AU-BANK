package aubank.retail.liabilities.controller;


import aubank.retail.liabilities.dtos.ResponseDto;
import aubank.retail.liabilities.exceptions.AuRlMicroServiceException;
import aubank.retail.liabilities.util.ApplicationConstant;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Log
public class ExceptionalController {


    @Autowired
    private ObjectMapper objectMapper;

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ResponseDto> vahanaRunTimException(RuntimeException ex) throws JsonProcessingException {

        String message = ex.getMessage();
        ResponseDto responseDto = new ResponseDto(ApplicationConstant.FAILURE, ApplicationConstant.FAILURE_CODE, message, null);
        ex.printStackTrace();
        return new ResponseEntity<>(responseDto, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(AuRlMicroServiceException.class)
    public ResponseEntity<ResponseDto> vahanaException(AuRlMicroServiceException ex) throws JsonProcessingException {
        ResponseDto responseDto = new ResponseDto(ApplicationConstant.FAILURE, ApplicationConstant.FAILURE_CODE, ex.getMessage(), ex.getMessage());
        ex.printStackTrace();
        return new ResponseEntity<>(responseDto, HttpStatus.INTERNAL_SERVER_ERROR);
    }


    @ExceptionHandler(value = {MethodArgumentNotValidException.class})
    public Object handleMethodException(MethodArgumentNotValidException ex) throws JsonProcessingException {
        BindingResult bindingResult = ex.getBindingResult();
        FieldError fieldError = bindingResult.getFieldError();
        String message = fieldError.getDefaultMessage();
        ResponseDto responseDto = new ResponseDto(ApplicationConstant.FAILURE, ApplicationConstant.FAILURE_CODE, message, null);
        ex.printStackTrace();
        return new ResponseEntity<>(responseDto, HttpStatus.INTERNAL_SERVER_ERROR);
    }


}
