package aubank.retail.liabilities.util;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class EventData {
    private Object request;
    private Object response;
    private Map<String, String> headers;
    private Object otherInfo;

}
