package aubank.retail.liabilities.domains;

import aubank.retail.liabilities.dtos.PanObject;
import aubank.retail.liabilities.dtos.PaymentObject;
import com.vladmihalcea.hibernate.type.json.JsonType;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.hibernate.envers.Audited;

import javax.persistence.*;

@Data
@Setter
@Getter
@Entity
@NoArgsConstructor
@Table(name = "Payment")
@Audited
@TypeDef(name = "json", typeClass = JsonType.class)
public class Payment extends BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private long id;

    @Type(type = "json")
    @Column(name ="object_data", columnDefinition = "jsonb")
    private PaymentObject paymentObject;
}
