package au.revamp.journey.domains;

import au.revamp.journey.util.BusinessConstant;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import java.io.Serializable;
import java.sql.Timestamp;

@MappedSuperclass
@Setter
@Getter
@NoArgsConstructor
public class BaseEntity implements Serializable {

    private static final long serialVersionUID = 7836876157751179100L;

    @JsonFormat(pattern = BusinessConstant.DATE_FORMAT_V1)
    @Column(name = "created_on", columnDefinition = BusinessConstant.TIMESTAMP_COLUMN_DEF)
    private Timestamp createdOn;

    @Column(name = "created_by")
    private String createdBy;

    @JsonFormat(pattern = BusinessConstant.DATE_FORMAT_V1)
    @Column(name = "modified_on", columnDefinition = BusinessConstant.TIMESTAMP_COLUMN_DEF)
    private Timestamp modifiedOn;

    @Column(name = "modified_by")
    private String modifiedBy;

}