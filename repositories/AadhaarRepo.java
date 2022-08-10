package aubank.retail.liabilities.repositories;

import aubank.retail.liabilities.domains.Aadhaar;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AadhaarRepo extends CrudRepository<Aadhaar, Long> {
    Aadhaar findByRecordIdentifier1(String recordIdentifier1);
}
