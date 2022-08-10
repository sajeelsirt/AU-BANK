package aubank.retail.liabilities.repositories;

import aubank.retail.liabilities.domains.Account;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountRepo extends CrudRepository<Account, Long> {
    Account findByRecordIdentifier1(String recordIdentifier1);
}
