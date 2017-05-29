package il.ac.technion.cs.sd.buy.app;

import com.google.inject.Guice;
import com.google.inject.Injector;
import db_utils.DataBaseFactory;
import db_utils.DataBaseFactoryImpl;
import db_utils.DataBaseModule;
import org.junit.Test;

import java.util.concurrent.CompletableFuture;

import static org.junit.Assert.*;

/**
 * Created by Nadav on 26-May-17.
 */
public class BuyProductReaderImplTest {
    @Test
    public void isValidOrderId() throws Exception {
        Injector injector= Guice.createInjector(new MockedFutureLineStorageModule(), new DataBaseModule());
        DataBaseFactory dbf =injector.getInstance(DataBaseFactoryImpl.class);
        BuyProductReader buyProductReader= new BuyProductReaderImpl(dbf);
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