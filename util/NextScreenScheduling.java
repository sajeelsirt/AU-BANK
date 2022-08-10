package aubank.retail.liabilities.util;

import org.springframework.stereotype.Service;

import java.util.Calendar;

@Service
public class NextScreenScheduling {

    public String getNextScreen(String nextScreen) {
        String newScreen1;
        switch (nextScreen) {
            case "Aof":
                newScreen1 = "Pan";
                break;
            case "Pan":
                newScreen1 = "Aadhaar";
                break;
            default:
                newScreen1 = "Aof";
        }
        return newScreen1;
    }
}
