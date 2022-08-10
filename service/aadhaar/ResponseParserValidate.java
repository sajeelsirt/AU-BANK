package aubank.retail.liabilities.service.aadhaar;

import aubank.retail.liabilities.dtos.AadhaarRequestDto;
import lombok.extern.java.Log;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.StringReader;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;

@Service
@Log
public class ResponseParserValidate {

    public static JSONObject parseResponse(String decode, AadhaarRequestDto auRequestDto) throws JSONException {
        log.info("DECODE " + decode);
        String dob = "";
        String gender = "";
        String name = "";
        String co = "";
        String country = "";
        String dist = "";
        String house = "";
        String loc = "";
        String pc = "";
        String state = "";
        String vtc = "";
        String pht = "";
        String subdist = "";
        String lm = "";
        String street = "";
        String po = "";
        JSONObject jsonObject = new JSONObject();
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        try {
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            InputSource is = new InputSource(new StringReader(decode));
            Document doc = dBuilder.parse(is);
            doc.getDocumentElement().normalize();
            NodeList nListPoi = doc.getElementsByTagName("Poi");
            for (int temp = 0; temp < nListPoi.getLength(); temp++) {
                Node nNode = nListPoi.item(temp);
                if (nNode.getNodeType() == 1) {
                    Element eElement = (Element) nNode;
                    dob = eElement.getAttribute("dob");
                    gender = eElement.getAttribute("gender");
                    name = eElement.getAttribute("name");
                }
            }

            NodeList nListPoa = doc.getElementsByTagName("Poa");
            for (int temp = 0; temp < nListPoa.getLength(); temp++) {
                Node nNode = nListPoa.item(temp);
                if (nNode.getNodeType() == 1) {
                    Element eElement = (Element) nNode;
                    co = eElement.getAttribute("co");
                    country = eElement.getAttribute("country");
                    dist = eElement.getAttribute("dist");
                    house = eElement.getAttribute("house");
                    loc = eElement.getAttribute("loc");
                    pc = eElement.getAttribute("pc");
                    state = eElement.getAttribute("state");
                    vtc = eElement.getAttribute("vtc");
                    subdist = eElement.getAttribute("subdist");
                    street = eElement.getAttribute("street");
                    lm = eElement.getAttribute("lm");
                    po = eElement.getAttribute("po");
                }
            }

            NodeList nListKycRes = doc.getElementsByTagName("KycRes");
            for (int temp = 0; temp < nListKycRes.getLength(); temp++) {
                Node nNode = nListKycRes.item(temp);
                if (nNode.getNodeType() == 1) {
                    Element eElement = (Element) nNode;
                    pht = eElement.getElementsByTagName("Pht").item(0)
                            .getTextContent();
                }
            }
            NodeList nListUidData = doc.getElementsByTagName("UidData");
            for (int temp = 0; temp < nListUidData.getLength(); temp++) {
                Node nNode = nListUidData.item(temp);
                if (nNode.getNodeType() == 1) {
                    Element eElement = (Element) nNode;
                    eElement.getAttribute("enrolementDate");
                    eElement.getAttribute("uid");
                    jsonObject.put("uid", eElement.getAttribute("uid"));
                }
            }

            String[] nameArray = name.split(" ");
            String firstName = nameArray[0];
            String lastName = "";
            if (nameArray.length - 1 != 0) {
                lastName = nameArray[(nameArray.length - 1)];
            } else {
                lastName = ".";
            }
            StringBuilder middleNa = new StringBuilder();

            for (int i = 1; i < nameArray.length - 1; i++) {
                middleNa.append(nameArray[i] + " ");
            }
            String middleName = middleNa.toString().trim();
            StringBuilder sb = new StringBuilder();
            String careOf = "";
            if ((co.contains("S/O")) || (co.contains("D/O"))
                    || (co.contains("S/o")) || (co.contains("D/o"))
                    || (co.contains("s/O")) || (co.contains("d/O"))
                    || (co.contains("s/o")) || (co.contains("d/o"))) {
                String fname = co.replaceAll("[-+.^:, ]", "8");
                String[] father = fname.split("8");

                for (int i = 1; i <= father.length - 1; i++) {
                    sb.append(father[i] + " ");
                }
                log.info("Father's name from Aadhaar" + sb);
                careOf = sb.toString().trim();
                jsonObject.put("FATHER_NAME", careOf);
                jsonObject.put("AADHAAR_OTHER_NAME", "");


            } else if ((co.contains("W/O")) || (co.contains("C/O"))
                    || (co.contains("W/o")) || (co.contains("C/o"))
                    || (co.contains("w/O")) || (co.contains("c/O"))
                    || (co.contains("w/o")) || (co.contains("c/o"))) {
                String fname = co.replaceAll("[-+.^:, ]", "8");
                String[] father = fname.split("8");

                for (int i = 1; i <= father.length - 1; i++) {
                    sb.append(father[i] + " ");
                }
                log.info("Other name from Aadhaar" + sb);
                jsonObject.put("FATHER_NAME", "");
                careOf = sb.toString().trim();
                jsonObject.put("AADHAAR_OTHER_NAME", careOf);
            } else {
                careOf = co;
                jsonObject.put("FATHER_NAME", "");
                jsonObject.put("AADHAAR_OTHER_NAME", co);
            }

            jsonObject.put("DOB", dob);
            jsonObject.put("FIRST_NAME", firstName);
            jsonObject.put("LAST_NAME", lastName);
            jsonObject.put("MIDDLE_NAME", middleName.toString());
            jsonObject.put("PHOTOGRAPH", pht);
            jsonObject.put("GENDER", gender);
            jsonObject.put("COUNTRY", country);
            jsonObject.put("STATE", state);
            jsonObject.put("CITY", vtc);
            jsonObject.put("PIN", pc);

         /*   String addr1 = house + " " + street + " " + loc;
            String addr2 = lm + " " + subdist + " " + dist;
            String addr3 = pc + " " + po;
            String addr4="";
*/

            // doing changes according to AUV-509
            String addr1;
            if (house.isEmpty() && street.isEmpty()) {
                addr1 = careOf + " " + lm;
            } else {
                addr1 = house + " " + street + " " + lm;

            }
            String addr2 = loc + " " + vtc + " " + subdist;
            String addr3 = dist + " " + po;
            String addr4 = "";

            addr1 = addr1.replaceAll(" +", " ").replaceAll("#", "").trim();
            addr2 = addr2.replaceAll(" +", " ").replaceAll("#", "").trim();
            addr3 = addr3.replaceAll(" +", " ").replaceAll("#", "").trim();

            String temp1 = addr1;
            String temp2 = addr2;
            String temp3 = addr3;

            if (addr1.length() > 35) {
                addr1 = temp1.substring(0,
                        temp1.substring(0, 35).lastIndexOf(" "));
                addr2 = temp1.substring(temp1.substring(0, 35).lastIndexOf(" "))
                        + " " + temp2;
            }
            if (addr2.length() > 35) {
                String temp4 = addr2;
                addr2 = temp4.substring(0,
                        temp4.substring(0, 35).lastIndexOf(" "));
                addr3 = temp4.substring(temp4.substring(0, 35).lastIndexOf(" "))
                        + " " + temp3;

            }
            addr1 = addr1.replaceAll(" +", " ").trim();
            addr2 = addr2.replaceAll(" +", " ").trim();
            addr3 = addr3.replaceAll(" +", " ").trim();

            log.info("AddressLine1 : " + addr1);
            log.info("AddressLine2 : " + addr2);
            log.info("AddressLine3 : " + addr3);

            jsonObject.put("ADDRESS_LINE_1", addr1);
            jsonObject.put("ADDRESS_LINE_2", addr2);
            //jsonObject.put("ADDRESS_LINE_3", addr3);
            // make characters 35 only as CBS Apis allow max 35 character.
            if (addr3.length() > 35) {
                String temp5 = addr3;
                addr3 = temp5.substring(0, 35);
                addr4 = temp5.substring(35);
                jsonObject.put("ADDRESS_LINE_4", addr4);
            }
            jsonObject.put("ADDRESS_LINE_3", addr3);
            LocalDate generatedDate = LocalDate.parse(dob,
                    DateTimeFormatter.ofPattern("dd-MM-yyyy"));
            jsonObject.put("AGE", String.valueOf(
                    Period.between(generatedDate, LocalDate.now()).getYears()));

        } catch (Exception e) {
            e.printStackTrace();
        }
        log.info("final json" + jsonObject);
        return jsonObject;
    }
}
