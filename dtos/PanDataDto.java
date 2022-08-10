package aubank.retail.liabilities.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
public class PanDataDto {

    @JsonProperty(value = "First_Name")
    private String firstName;

    @JsonProperty(value = "Last_Name")
    private String lastName;

    @JsonProperty(value = "Middle_Name")
    private String middleName;

    @JsonProperty(value = "PAN_title")
    private String title;

    @JsonProperty(value = "Seeding_Status")
    private String seedingStatus;
}
