package aubank.retail.liabilities.repositories;

import aubank.retail.liabilities.domains.Contact;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ContactRepo extends CrudRepository<Contact, Long> {
    Contact findByRecordIdentifier1(String recordIdentifier1);
}
