package aubank.retail.liabilities.repositories;

import aubank.retail.liabilities.domains.Pan;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PanRepo extends CrudRepository<Pan, Long> {
    Pan findByRecordIdentifier1(String recordIdentifier1);
}
