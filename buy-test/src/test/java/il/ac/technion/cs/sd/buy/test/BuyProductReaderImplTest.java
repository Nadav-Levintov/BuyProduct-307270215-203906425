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
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Created by Nadav on 26-May-17.
 */
public class BuyProductReaderImplTest {


    public BuyProductReader SetupAndBuildBookScoreReader(String file_name) throws IOException, SAXException, ParserConfigurationException, InterruptedException, ExecutionException, JSONException {

        String fileContents =
                new Scanner(new File(ExampleTest.class.getResource(file_name).getFile())).useDelimiter("\\Z").next();

        Injector injector = Guice.createInjector(new FakeBuyProductModule(), new MockedFutureLineStorageModule());

        FakeBuyProductInitializerImpl fakeBuyProductInitializer = injector.getInstance(FakeBuyProductInitializerImpl.class);
        if (file_name.endsWith("xml"))
            fakeBuyProductInitializer.setupXml(fileContents);
        else {
            assert file_name.endsWith("json");
            fakeBuyProductInitializer.setupJson(fileContents);
        }

        DataBaseFactory dbf = fakeBuyProductInitializer.get_DataBaseFactory();
        BuyProductReader buyProductReader= new BuyProductReaderImpl(dbf);

        return buyProductReader;
    }
    @Test
    public void isValidOrderId_xml() throws Exception {
        BuyProductReader buyProductReader = SetupAndBuildBookScoreReader("small.xml");

        CompletableFuture<Boolean> val1 = buyProductReader.isValidOrderId("1");
        CompletableFuture<Boolean> val2 = buyProductReader.isValidOrderId("2");
        CompletableFuture<Boolean> val3 = buyProductReader.isValidOrderId("3");


        assertTrue(val1.get());
        assertTrue(val2.get());
        assertFalse(val3.get());
    }

    @Test
    public void isCanceledOrder_xml() throws Exception {
        BuyProductReader buyProductReader = SetupAndBuildBookScoreReader("small.xml");

        CompletableFuture<Boolean> val1 = buyProductReader.isCanceledOrder("1");
        CompletableFuture<Boolean> val2 = buyProductReader.isCanceledOrder("2");
        CompletableFuture<Boolean> val3 = buyProductReader.isCanceledOrder("3");


        assertTrue(val1.get());
        assertFalse(val2.get());
        assertFalse(val3.get());
    }

    @Test
    public void isModifiedOrder_xml() throws Exception {
        BuyProductReader buyProductReader = SetupAndBuildBookScoreReader("small.xml");

        CompletableFuture<Boolean> val1 = buyProductReader.isModifiedOrder("1");
        CompletableFuture<Boolean> val2 = buyProductReader.isModifiedOrder("2");
        CompletableFuture<Boolean> val3 = buyProductReader.isModifiedOrder("3");


        assertTrue(val1.get());
        assertFalse(val2.get());
        assertFalse(val3.get());
    }

    @Test
    public void getNumberOfProductOrdered_xml() throws Exception {
        BuyProductReader buyProductReader = SetupAndBuildBookScoreReader("small.xml");

        CompletableFuture<?> val1 = buyProductReader.getNumberOfProductOrdered("1");
        CompletableFuture<?> val2 = buyProductReader.getNumberOfProductOrdered("2");
        CompletableFuture<?> val3 = buyProductReader.getNumberOfProductOrdered("3");


        assertEquals(OptionalInt.of(-10),val1.get());
        assertEquals(OptionalInt.of(5),val2.get());
        assertEquals(OptionalInt.empty(),val3.get());

    }

    @Test
    public void getHistoryOfOrder_xml() throws Exception {
        BuyProductReader buyProductReader = SetupAndBuildBookScoreReader("small.xml");

        CompletableFuture<?> val1 = buyProductReader.getHistoryOfOrder("1");
        CompletableFuture<?> val2 = buyProductReader.getHistoryOfOrder("2");
        CompletableFuture<?> val3 = buyProductReader.getHistoryOfOrder("3");

        List<Integer> list1 = new ArrayList<>();
        list1.add(5);
        list1.add(10);
        list1.add(-1);
        List<Integer> list2 = new ArrayList<>();
        list2.add(5);
        List<Integer> list3 = new ArrayList<>();


        assertEquals(list1,val1.get());
        assertEquals(list2,val2.get());
        assertEquals(list3,val3.get());
    }

    @Test
    public void getOrderIdsForUser() throws Exception {
        BuyProductReader buyProductReader = SetupAndBuildBookScoreReader("small.xml");

        CompletableFuture<?> val1 = buyProductReader.getOrderIdsForUser("1");
        CompletableFuture<?> val2 = buyProductReader.getOrderIdsForUser("2");

        List<String> list1 = new ArrayList<>();
        list1.add("1");
        list1.add("2");
        List<String> list2 = new ArrayList<>();



        assertEquals(list1,val1.get());
        assertEquals(list2,val2.get());

    }

    @Test
    public void getTotalAmountSpentByUser() throws Exception {
        BuyProductReader buyProductReader = SetupAndBuildBookScoreReader("small.xml");

        CompletableFuture<?> val1 = buyProductReader.getTotalAmountSpentByUser("1");
        CompletableFuture<?> val2 = buyProductReader.getTotalAmountSpentByUser("2");




        assertEquals(500L*10L,val1.get());
        assertEquals(0L,val2.get());
    }

    @Test
    public void getUsersThatPurchased() throws Exception {
        BuyProductReader buyProductReader = SetupAndBuildBookScoreReader("small.xml");

        CompletableFuture<?> val1 = buyProductReader.getUsersThatPurchased("android");
        CompletableFuture<?> val2 = buyProductReader.getUsersThatPurchased("iphone");
        CompletableFuture<?> val3 = buyProductReader.getUsersThatPurchased("benny");

        List<String> list1 = new ArrayList<>();
        list1.add("1");
        List<String> list2 = new ArrayList<>();



        assertEquals(list1,val1.get());
        assertEquals(list2,val2.get());
        assertEquals(list2,val3.get());
    }

    @Test
    public void getOrderIdsThatPurchased() throws Exception {
        BuyProductReader buyProductReader = SetupAndBuildBookScoreReader("small.xml");

        CompletableFuture<?> val1 = buyProductReader.getOrderIdsThatPurchased("android");
        CompletableFuture<?> val2 = buyProductReader.getOrderIdsThatPurchased("iphone");
        CompletableFuture<?> val3 = buyProductReader.getOrderIdsThatPurchased("benny");

        List<String> list1 = new ArrayList<>();
        list1.add("1");
        list1.add("2");
        List<String> list2 = new ArrayList<>();



        assertEquals(list1,val1.get());
        assertEquals(list2,val2.get());
        assertEquals(list2,val3.get());
    }

    @Test
    public void getTotalNumberOfItemsPurchased() throws Exception {
        BuyProductReader buyProductReader = SetupAndBuildBookScoreReader("small.xml");

        CompletableFuture<?> val1 = buyProductReader.getTotalNumberOfItemsPurchased("android");
        CompletableFuture<?> val2 = buyProductReader.getTotalNumberOfItemsPurchased("iphone");
        CompletableFuture<?> val3 = buyProductReader.getTotalNumberOfItemsPurchased("benny");




        assertEquals(OptionalLong.of(5),val1.get());
        assertEquals(OptionalLong.empty(),val2.get());
        assertEquals(OptionalLong.empty(),val3.get());

    }

    @Test
    public void getAverageNumberOfItemsPurchased() throws Exception {
        BuyProductReader buyProductReader = SetupAndBuildBookScoreReader("small.xml");

        CompletableFuture<?> val1 = buyProductReader.getAverageNumberOfItemsPurchased("android");
        CompletableFuture<?> val2 = buyProductReader.getAverageNumberOfItemsPurchased("iphone");
        CompletableFuture<?> val3 = buyProductReader.getAverageNumberOfItemsPurchased("benny");




        assertEquals(OptionalDouble.of(5),val1.get());
        assertEquals(OptionalDouble.empty(),val2.get());
        assertEquals(OptionalDouble.empty(),val3.get());

    }

    @Test
    public void getCancelRatioForUser() throws Exception {
        BuyProductReader buyProductReader = SetupAndBuildBookScoreReader("small.xml");

        CompletableFuture<?> val1 = buyProductReader.getCancelRatioForUser("1");
        CompletableFuture<?> val2 = buyProductReader.getCancelRatioForUser("2");

        assertEquals(OptionalDouble.of(0.5),val1.get());
        assertEquals(OptionalDouble.empty(),val2.get());

    }

    @Test
    public void getModifyRatioForUser() throws Exception {
        BuyProductReader buyProductReader = SetupAndBuildBookScoreReader("small.xml");

        CompletableFuture<?> val1 = buyProductReader.getModifyRatioForUser("1");
        CompletableFuture<?> val2 = buyProductReader.getModifyRatioForUser("2");

        assertEquals(OptionalDouble.of(0.5),val1.get());
        assertEquals(OptionalDouble.empty(),val2.get());
    }

    @Test
    public void getAllItemsPurchased() throws Exception {
    }

    @Test
    public void getItemsPurchasedByUsers() throws Exception {
    }

}