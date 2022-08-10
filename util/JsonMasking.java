package aubank.retail.liabilities.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class JsonMasking {


    public JsonMasking() {
    }

    public static String maskMessage(String message, List<String> keysToMask) {
        String maskedMessage = null;
        if (message != null && !message.isEmpty()) {
            maskedMessage = message.replaceAll("\\\\", "").replace("\"{", "{").replace("}\"", "}").replace("\"[", "[").replace("]\"", "]");
        }

        String keyToMask;
        if (keysToMask != null) {
            for(Iterator var3 = keysToMask.iterator(); var3.hasNext(); maskedMessage = maskJsonData(maskedMessage, keyToMask)) {
                keyToMask = (String)var3.next();
            }
        }

        return maskedMessage;
    }

    private static String maskJsonData(String message, String keyToMask) {
        String maskedMessage = message;

        try {
            if (maskedMessage != null && !maskedMessage.isEmpty()) {
                Iterator var3 = getRegexPattern(keyToMask).iterator();

                while(var3.hasNext()) {
                    String getRegex = (String)var3.next();
                    Pattern pattern = Pattern.compile(getRegex, 2);
                    Matcher matcher = pattern.matcher(message);

                    while(matcher.find()) {
                        String keyName = matcher.group(1).replaceAll("\"", "");
                        String maskMessage = matcher.group(4).replaceAll("\"", "");
                        if (maskMessage != null && keyToMask.equalsIgnoreCase(keyName) && !maskMessage.isEmpty() && !maskMessage.equalsIgnoreCase("\"\"")) {
                            maskedMessage = maskedMessage.replace(maskMessage, "*****");
                        }
                    }
                }
            }
        } catch (Exception var9) {
            var9.printStackTrace();
        }

        return maskedMessage;
    }

    private static List<String> getRegexPattern(String key) {
        ArrayList regex = new ArrayList();

        try {
            regex.add("(\"" + key + "\")(\\s*:\\s*)(\")([a-zA-Z0-9_~\\-! /.@#+$%^&*\\\\r\\\\n(|)]*\")");
        } catch (Exception var3) {
            var3.printStackTrace();
        }

        return regex;
    }
}
