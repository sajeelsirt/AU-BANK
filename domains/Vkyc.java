package aubank.retail.liabilities.domains;

import aubank.retail.liabilities.dtos.PanObject;
import aubank.retail.liabilities.dtos.VkycObject;
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
@Table(name = "Vkyc")
@Audited
@TypeDef(name = "json", typeClass = JsonType.class)
public class Vkyc extends BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private long id;

    @Type(type = "json")
    @Column(name ="object_data", columnDefinition = "jsonb")
    private VkycObject vkycObject;
}
