package aubank.retail.liabilities.alerts;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 * @author Naveen Gupta
 * @since 1.0
 */
@Repository
public interface OtpCacheRepo extends CrudRepository<OtpData, String> {

}
