package aubank.retail.liabilities.repositories;

import aubank.retail.liabilities.domains.Vkyc;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VkycRepo extends CrudRepository<Vkyc, Long> {
    Vkyc findByRecordIdentifier1(String recordIdentifier1);
}
