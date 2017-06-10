package il.ac.technion.cs.sd.buy.test;

import com.google.inject.Guice;
import com.google.inject.Injector;
import db_utils.DataBaseFactory;
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

import static org.junit.Assert.*;

/**
 * Created by Nadav on 26-May-17.
 */
public class BuyProductReaderImplTest {


    private BuyProductReader SetupAndBuildBookScoreReader(String file_name) throws IOException, SAXException, ParserConfigurationException, InterruptedException, ExecutionException, JSONException {

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

        return new BuyProductReaderImpl(dbf);
    }
    @Test
    public void isValidOrderId_xml() throws Exception {
        BuyProductReader buyProductReader = SetupAndBuildBookScoreReader("small.xml");

        CompletableFuture<Boolean> val1 = buyProductReader.isValidOrderId("1");
        CompletableFuture<Boolean> val2 = buyProductReader.isValidOrderId("2");
        CompletableFuture<Boolean> val3 = buyProductReader.isValidOrderId("3");
        CompletableFuture<Boolean> val4 = buyProductReader.isValidOrderId("4");

        assertTrue(val1.get());
        assertTrue(val2.get());
        assertTrue(val3.get());
        assertFalse(val4.get());
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
        CompletableFuture<?> val4 = buyProductReader.getNumberOfProductOrdered("4");

        assertEquals(OptionalInt.of(-10),val1.get());
        assertEquals(OptionalInt.of(5),val2.get());
        assertEquals(OptionalInt.of(12),val3.get());
        assertEquals(OptionalInt.empty(),val4.get());

    }

    @Test
    public void getHistoryOfOrder_xml() throws Exception {
        BuyProductReader buyProductReader = SetupAndBuildBookScoreReader("small.xml");
        CompletableFuture<List<Integer>> list = buyProductReader.getHistoryOfOrder("1");
        assertEquals(3, list.get().size());
        assertEquals(5, (long)(list.get()).get(0));
        assertEquals(10, (long)(list.get()).get(1));
        assertEquals(-1, (long)(list.get()).get(2));

        list = buyProductReader.getHistoryOfOrder("2");
        assertEquals(1, list.get().size());
        assertEquals(5, (long)(list.get()).get(0));



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
        CompletableFuture<?> val3 = buyProductReader.getTotalAmountSpentByUser("3");

        assertEquals(500L*10L,val1.get());
        assertEquals(0L,val2.get());
        assertEquals(500L*12L,val3.get());

    }

    @Test
    public void getUsersThatPurchased() throws Exception {
        BuyProductReader buyProductReader = SetupAndBuildBookScoreReader("small.xml");

        CompletableFuture<?> val1 = buyProductReader.getUsersThatPurchased("android");
        CompletableFuture<?> val2 = buyProductReader.getUsersThatPurchased("iphone");
        CompletableFuture<?> val3 = buyProductReader.getUsersThatPurchased("benny");

        List<String> list1 = new ArrayList<>();
        list1.add("1");
        list1.add("3");
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
        list1.add("3");
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

        assertEquals(OptionalLong.of(17),val1.get());
        assertEquals(OptionalLong.empty(),val2.get());
        assertEquals(OptionalLong.empty(),val3.get());

    }

    @Test
    public void getAverageNumberOfItemsPurchased() throws Exception {
        BuyProductReader buyProductReader = SetupAndBuildBookScoreReader("small.xml");

        CompletableFuture<?> val1 = buyProductReader.getAverageNumberOfItemsPurchased("android");
        CompletableFuture<?> val2 = buyProductReader.getAverageNumberOfItemsPurchased("iphone");
        CompletableFuture<?> val3 = buyProductReader.getAverageNumberOfItemsPurchased("benny");

        assertEquals(OptionalDouble.of(8.5),val1.get());
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
        BuyProductReader buyProductReader = SetupAndBuildBookScoreReader("small.xml");
        Map<String, Long> allItems1 = buyProductReader.getAllItemsPurchased("1").get();
        Map<String, Long> allItems2 = buyProductReader.getAllItemsPurchased("2").get();
        Map<String, Long> allItems3 = buyProductReader.getAllItemsPurchased("3").get();

        assertEquals(1,allItems1.size());
        assertEquals(0,allItems2.size());
        assertEquals(1,allItems3.size());

        assertTrue(allItems1.containsKey("android"));
        assertFalse(allItems2.containsKey("android"));
        assertTrue(allItems3.containsKey("android"));

        assertEquals((long) 5, (long)allItems1.get("android"));
        assertEquals((long) 12, (long)allItems3.get("android"));



    }

    @Test
    public void getItemsPurchasedByUsers() throws Exception {
        BuyProductReader buyProductReader = SetupAndBuildBookScoreReader("small.xml");
        Map<String, Long> allUsers1 = buyProductReader.getItemsPurchasedByUsers("android").get();
        Map<String, Long> allUsers2 = buyProductReader.getItemsPurchasedByUsers("iphone").get();

        assertEquals(2,allUsers1.size());
        assertEquals(0,allUsers2.size());
        assertEquals((long)5, (long)allUsers1.get("1"));
        assertEquals((long)12, (long)allUsers1.get("3"));


    }

    @Test
    public void bigFileTestJson() throws Exception {
       BuyProductReader buyProductReader = SetupAndBuildBookScoreReader("bigOurs.json");

        CompletableFuture<Boolean> val1 = buyProductReader.isModifiedOrder("gg0t");
        CompletableFuture<Boolean> val2 = buyProductReader.isCanceledOrder("0b0n");
        CompletableFuture<List<Integer>> list = buyProductReader.getHistoryOfOrder("3c13");
        CompletableFuture<Map<String,Long>> val3 = buyProductReader.getAllItemsPurchased("0b0n");
        assertFalse(val1.get());
        assertTrue(val2.get());
        assertEquals( 0, (long) list.get().size()); //Does not has product -> order does not spouse to exist;
        assertEquals( 0, (long) val3.get().size());//Does not has product -> order does not spouse to exist;



    }

    @Test
    public void bigFileTestXml() throws Exception {
       BuyProductReader buyProductReader = SetupAndBuildBookScoreReader("bigOurs.xml");

        CompletableFuture<Boolean> val1 = buyProductReader.isModifiedOrder("m3a");
        CompletableFuture<Boolean> val2 = buyProductReader.isCanceledOrder("m3a");
        CompletableFuture<List<Integer>> list = buyProductReader.getHistoryOfOrder("m3a");
        assertTrue(val1.get());
        assertFalse(val2.get());
        assertEquals( 3, (long) list.get().size());
        assertEquals( 83, (long) list.get().get(0));
        assertEquals( 952, (long) list.get().get(1));
        assertEquals( 591, (long) list.get().get(2));
    }
}