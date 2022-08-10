package aubank.retail.liabilities.service.impl;

import aubank.retail.liabilities.dtos.AofRequestDto;
import aubank.retail.liabilities.service.TransactionService;
import org.springframework.stereotype.Service;

import static org.springframework.beans.BeanUtils.copyProperties;

@Service
public class TransactionServiceImpl implements TransactionService {


    @Override
    public Object insertTransaction(AofRequestDto requestDto) {
        return null;
    }

    @Override
    public Object getTransaction(AofRequestDto requestDto) {
        return null;
    }

    @Override
    public Object updateTransaction(AofRequestDto requestDto) {
        return null;
    }
}
