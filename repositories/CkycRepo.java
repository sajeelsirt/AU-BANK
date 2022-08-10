package aubank.retail.liabilities.repositories;

import aubank.retail.liabilities.domains.Ckyc;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CkycRepo extends CrudRepository<Ckyc, Long> {
    Ckyc findByRecordIdentifier1(String recordIdentifier1);
}
