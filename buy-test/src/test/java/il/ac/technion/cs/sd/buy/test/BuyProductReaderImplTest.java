package il.ac.technion.cs.sd.buy.test;

import com.google.inject.Guice;
import com.google.inject.Injector;
import db_utils.DataBaseFactory;
import il.ac.technion.cs.sd.buy.app.BuyProductInitializer;
import il.ac.technion.cs.sd.buy.app.BuyProductReader;
import il.ac.technion.cs.sd.buy.app.BuyProductReaderImpl;
import org.json.JSONException;
import org.junit.Test;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.util.Scanner;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static org.junit.Assert.assertTrue;

/**
 * Created by Nadav on 26-May-17.
 */
public class BuyProductReaderImplTest {

    private static Injector setupAndGetInjector(String fileName) throws IOException, JSONException, SAXException, ParserConfigurationException, ExecutionException, InterruptedException {
        String fileContents =
                new Scanner(new File(ExampleTest.class.getResource(fileName).getFile())).useDelimiter("\\Z").next();

        Injector injector = Guice.createInjector(new FakeBuyProductModule(), new MockedFutureLineStorageModule());

        BuyProductInitializer bpi = injector.getInstance(BuyProductInitializer.class);
        if (fileName.endsWith("xml"))
            bpi.setupXml(fileContents);
        else {
            assert fileName.endsWith("json");
            bpi.setupJson(fileContents);
        }
        return injector;
    }

    public BuyProductReader SetupAndBuildBookScoreReader(String file_name) throws IOException, SAXException, ParserConfigurationException, InterruptedException, ExecutionException, JSONException {

        Injector injector= setupAndGetInjector(file_name);
        FakeBuyProductInitializerImpl fakeBuyProductInitializer= injector.getInstance(FakeBuyProductInitializerImpl.class);

        DataBaseFactory dbf = fakeBuyProductInitializer.get_DataBaseFactory();
        BuyProductReader buyProductReader= new BuyProductReaderImpl(dbf);

        return buyProductReader;
    }
    @Test
    public void isValidOrderId() throws Exception {
        BuyProductReader buyProductReader = SetupAndBuildBookScoreReader("small.xml");

        CompletableFuture<Boolean> val1 = buyProductReader.isValidOrderId("1");

        Boolean res = val1.get();
        assertTrue(res);
    }

    @Test
    public void isCanceledOrder() throws Exception {
    }

    @Test
    public void isModifiedOrder() throws Exception {
    }

    @Test
    public void getNumberOfProductOrdered() throws Exception {
    }

    @Test
    public void getHistoryOfOrder() throws Exception {
    }

    @Test
    public void getOrderIdsForUser() throws Exception {
    }

    @Test
    public void getTotalAmountSpentByUser() throws Exception {
    }

    @Test
    public void getUsersThatPurchased() throws Exception {
    }

    @Test
    public void getOrderIdsThatPurchased() throws Exception {
    }

    @Test
    public void getTotalNumberOfItemsPurchased() throws Exception {
    }

    @Test
    public void getAverageNumberOfItemsPurchased() throws Exception {
    }

    @Test
    public void getCancelRatioForUser() throws Exception {
    }

    @Test
    public void getModifyRatioForUser() throws Exception {
    }

    @Test
    public void getAllItemsPurchased() throws Exception {
    }

    @Test
    public void getItemsPurchasedByUsers() throws Exception {
    }

}