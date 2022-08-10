package aubank.retail.liabilities.service;

import aubank.retail.liabilities.dtos.AofRequestDto;
import org.springframework.stereotype.Service;

@Service
public interface TransactionService {

     Object insertTransaction(AofRequestDto requestDto);
     Object getTransaction(AofRequestDto requestDto);
     Object updateTransaction(AofRequestDto requestDto);

}
