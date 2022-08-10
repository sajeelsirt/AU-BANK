package aubank.retail.liabilities.repositories;

import aubank.retail.liabilities.domains.Appointment;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AppointmentRepo extends CrudRepository<Appointment, Long> {

    Appointment findByRecordIdentifier1(String recordIdentifier1);
}
