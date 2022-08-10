package aubank.retail.liabilities.domains;

import aubank.retail.liabilities.dtos.AofObject;
import aubank.retail.liabilities.dtos.PanObject;
import com.vladmihalcea.hibernate.type.json.JsonType;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.hibernate.envers.Audited;

@Data
@Entity
@NoArgsConstructor
@Table(name = "AOF")
@Audited
@TypeDef(name = "json", typeClass = JsonType.class)
public class Aof extends BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private long id;

    @Type(type = "json")
    @Column(name ="object_data", columnDefinition = "jsonb")
    private AofObject aofObject;
}
