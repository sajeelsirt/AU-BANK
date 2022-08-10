package aubank.retail.liabilities.repositories;

import aubank.retail.liabilities.domains.Aof;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AofRepo extends CrudRepository<Aof, Long> {
    Aof findByRecordIdentifier1(String recordIdentifier1);

    Aof findByText1AndText2(String mobileNo,String product);
}
