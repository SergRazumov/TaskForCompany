import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

/**
 * Сlass of work with currency and requests
 */
public class WorkWithCurrency {

    public final static WorkWithCurrency workWithCurrency = new WorkWithCurrency();

    private List<String> listT = new ArrayList<String>();
    private List<URL> listURL = new ArrayList<URL>();

    /**
     * Menu-based and responsible for the logic of calling methods and issuing
     */
    public void myMenu() {
        addTemplateInList();
        while (true) {
            System.out.println("Enter the code of the currency you are interested in or click q for exit: ");
            checkListURL();
            Scanner scanner = new Scanner(System.in);
            String currency = scanner.nextLine().toUpperCase();
            if (currency.equals("Q")) {
                return;
            }
            List<String> resultLine = buildLineFromTemplate(listT, currency);
            generateListURLFromLine(resultLine);

            for (URL url : listURL) {
                try {
                    StringBuilder resultJson = new StringBuilder();
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("GET");
                    int status = connection.getResponseCode();
                    if (status < 300) {
                        scanner = new Scanner(connection.getInputStream());
                        while (scanner.hasNext()) {
                            resultJson.append(scanner.nextLine());
                        }
                        if (url.toString().endsWith(".json")) {
                            bitcoinRateInCurrentCurrency(resultJson, currency);
                        } else {
                            bitcoinMaxAndMinLast30day(resultJson);
                        }
                    } else {
                        incorrectInput(connection);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Adds templates to the list
     */

    public void addTemplateInList() {
        listT.add("https://api.coindesk.com/v1/bpi/currentprice/<CODE>.json");
        listT.add("https://api.coindesk.com/v1/bpi/historical/close.json?currency=<VALUE>");
    }

    /**
     * Checks if there is anything in the list of URLs
     */

    public void checkListURL() {
            if (!listURL.isEmpty()) {
                listURL.clear();
            }
        }

    /**
     * There is a line from request templates and writes to the list
     * @param listT    list of query patterns
     * @param currency currency code
     * @return list of lines
     */
    public List<String> buildLineFromTemplate(List<String> listT, String currency) {
        List<String> resultLine = new ArrayList<String>();
        for (String lineTemplate : listT) {
            StringBuilder stringBuilder = new StringBuilder(lineTemplate);
            int start = lineTemplate.indexOf("<");
            int end = lineTemplate.indexOf(">") + 1;
            stringBuilder.replace(start, end, currency);
            resultLine.add(stringBuilder.toString());
        }
        return resultLine;
    }

    /**
     * Costs a URL from a list of strings and writes to a list of URLs
     * @param resultLine list of lines
     */
    private void generateListURLFromLine(List<String> resultLine) {
        for (String line : resultLine) {
            try {
                listURL.add(new URL(line));
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Prints the maximum and minimum value in 30 days
     * @param resultJson JSON body as a string
     */
    private void bitcoinMaxAndMinLast30day(StringBuilder resultJson) {
        JsonObject fieldBpi = getJsonObjectBpi(resultJson);
        List<Double> listValue = new ArrayList<Double>();
        int count = 0;
        for (Map.Entry<String, JsonElement> elementMap : fieldBpi.entrySet()) {
            if (count != 0) {
                listValue.add(Double.valueOf(elementMap.getValue().toString()));
            }
            count++;
        }
        Collections.sort(listValue);
        System.out.println("Max value bitcoin 30 day: " + listValue.get(listValue.size() - 1) + " an min value bitcoin 30 day " + listValue.get(0));
    }

    /**
     * Prints the field value to the screen "rate_float"
     * @param resultJson JSON body as a string
     * @param currency currency code
     */
    private void bitcoinRateInCurrentCurrency(StringBuilder resultJson, String currency) {
        JsonObject fieldBpi = getJsonObjectBpi(resultJson);
        JsonObject fieldCurrency = (JsonObject) fieldBpi.get(currency);
        System.out.println(currency + ": " + fieldCurrency.get("rate_float"));
    }

    /**
     * Parses a string and forms from it JsonObject
     * @param resultJson JSON body as a string
     * @return field value "bpi"
     */
    public JsonObject getJsonObjectBpi(StringBuilder resultJson) {
        Object myCurrently = new JsonParser().parse(resultJson.toString());
        JsonObject jo = (JsonObject) myCurrently;
        return (JsonObject) jo.get("bpi");
    }

    /**
     * Prints a stream of errors
     * @param connection server response object
     */
    private void incorrectInput(HttpURLConnection connection) {
        InputStream is = connection.getErrorStream();
        Scanner scanner = new Scanner(is);
        while (scanner.hasNext()) {
            System.out.println(scanner.nextLine());
        }
    }

    public List<String> getListT() {
        return listT;
    }

    public List<URL> getListURL() {
        return listURL;
    }
}





