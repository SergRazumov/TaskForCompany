import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class TestWorkWithCurrency {

    private WorkWithCurrency testWorkWithCurrency;

    @Before
    public void setUp() {
        testWorkWithCurrency = new WorkWithCurrency();
    }

    @Test
    public void testGetJsonObjectBpi() {
        StringBuilder inputForTest = new StringBuilder("{\"time\":{\"updated\":\"Aug 31, 2019 09:57:00 UTC\",\"updatedISO\":\"2019-08-31T09:57:00+00:00\",\"updateduk\":\"Aug 31, 2019 at 10:57 BST\"},\"disclaimer\":\"This data was produced from the CoinDesk Bitcoin Price Index (USD). Non-USD currency data converted using hourly conversion rate from openexchangerates.org\",\"bpi\":{\"USD\":{\"code\":\"USD\",\"rate\":\"9,585.4583\",\"description\":\"United States Dollar\",\"rate_float\":9585.4583},\"RUB\":{\"code\":\"RUB\",\"rate\":\"639,469.9034\",\"description\":\"Russian Ruble\",\"rate_float\":639469.9034}}}");
        WorkWithCurrency workWithCurrency = new WorkWithCurrency();
        JsonObject actual = workWithCurrency.getJsonObjectBpi(inputForTest);
        JsonObject expected = (JsonObject) new JsonParser().parse("{\"time\":{\"updated\":\"Aug 31, 2019 09:57:00 UTC\",\"updatedISO\":\"2019-08-31T09:57:00+00:00\",\"updateduk\":\"Aug 31, 2019 at 10:57 BST\"},\"disclaimer\":\"This data was produced from the CoinDesk Bitcoin Price Index (USD). Non-USD currency data converted using hourly conversion rate from openexchangerates.org\",\"bpi\":{\"USD\":{\"code\":\"USD\",\"rate\":\"9,585.4583\",\"description\":\"United States Dollar\",\"rate_float\":9585.4583},\"RUB\":{\"code\":\"RUB\",\"rate\":\"639,469.9034\",\"description\":\"Russian Ruble\",\"rate_float\":639469.9034}}}");
        Assert.assertEquals(expected.get("bpi"), actual);
    }

    @Test
    public void testAddTemplateInList() {
        testWorkWithCurrency.addTemplateInList();
        Assert.assertTrue(!testWorkWithCurrency.getListT().isEmpty());
    }

    @Test
    public void testBuildLineFromTemplate() {
        List<String> testList = new ArrayList<String>();
        testList.add("1234<>7>9");
        List<String> myList = testWorkWithCurrency.buildLineFromTemplate(testList, "56");
        Assert.assertEquals("1234567>9", myList.get(0));
    }
}
//"bpi":{"USD":{"code":"USD","rate":"9,585.4583","description":"United States Dollar","rate_float":9585.4583},"RUB":{"code":"RUB","rate":"639,469.9034","description":"Russian Ruble","rate_float":639469.9034}}}