package aubank.retail.liabilities.domains;

import aubank.retail.liabilities.util.BusinessConstant;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Type;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.MappedSuperclass;
import java.io.Serializable;
import java.sql.Timestamp;

@MappedSuperclass
@Data
@NoArgsConstructor
public class BaseEntity implements Serializable {

        private static final long serialVersionUID = 7836876157751179100L;

        @Column(name = "record_identifier_1")
        private String recordIdentifier1;

        @Column(name = "record_identifier_2")
        private String recordIdentifier2;

        @Column(name = "record_identifier_3")
        private String recordIdentifier3;

        @Column(name = "status")
        private String status;

        @Column(name = "text1")
        private String text1;

        @Column(name = "text2")
        private String text2;

        @Column(name = "text3")
        private String text3;

        @Column(name = "text4")
        private String text4;

        @Column(name = "text5")
        private String text5;

        @JsonFormat(pattern = BusinessConstant.DATE_FORMAT_V1)
        @Column(name = "effective_date")
        private Timestamp effectiveDate;

        @JsonFormat(pattern = BusinessConstant.DATE_FORMAT_V1)
        @Column(name = "ineffective_date")
        private Timestamp ineffectiveDate;

        @Column(name = "created_by")
        private String createdBy;

        @JsonFormat(pattern = BusinessConstant.DATE_FORMAT_V1)
        @Column(name = "created_timestamp", columnDefinition = BusinessConstant.TIMESTAMP_COLUMN_DEF)
        private Timestamp createdTimestamp;

        @Column(name = "modified_by")
        private String modifiedBy;

        @JsonFormat(pattern = BusinessConstant.DATE_FORMAT_V1)
        @Column(name = "modified_timestamp", columnDefinition = BusinessConstant.TIMESTAMP_COLUMN_DEF)
        private Timestamp modifiedTimestamp;
    }

