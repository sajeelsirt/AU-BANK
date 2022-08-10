package aubank.retail.liabilities.repositories;

import aubank.retail.liabilities.domains.Payment;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentRepo extends CrudRepository<Payment, Long> {
    Payment findByRecordIdentifier1(String recordIdentifier1);
}
