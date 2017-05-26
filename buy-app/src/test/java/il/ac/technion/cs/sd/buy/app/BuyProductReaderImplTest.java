package il.ac.technion.cs.sd.buy.app;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

/**
 * Created by benny on 26/05/2017.
 */
@RunWith(Arquillian.class)
public class BuyProductReaderImplTest {
    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void isValidOrderId() throws Exception {
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

    @Deployment
    public static JavaArchive createDeployment() {
        return ShrinkWrap.create(JavaArchive.class)
                .addClass(BuyProductReaderImpl.class)
                .addAsManifestResource(EmptyAsset.INSTANCE, "beans.xml");
    }

}
