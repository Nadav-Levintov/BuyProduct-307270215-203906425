package il.ac.technion.cs.sd.buy.test;

import com.google.inject.Guice;
import com.google.inject.Injector;
import db_utils.DataBaseModule;
import il.ac.technion.cs.sd.buy.app.BuyProductInitializerImpl;
import org.junit.Test;

import java.io.File;
import java.util.Scanner;

/**
 * Created by benny on 24/05/2017.
 */

public class BuyProductInitializerImplTest {

    @Test
    public void setupJson() throws Exception {

        Injector injector= Guice.createInjector(new DataBaseModule(),new MockedFutureLineStorageModule(), new FakeBuyProductModule());
        BuyProductInitializerImpl testClass= injector.getInstance(BuyProductInitializerImpl.class);
        String fileContents =
                new Scanner(new File(BuyProductInitializerImplTest.class.getResource("small.json").getFile())).useDelimiter("\\Z").next();
        testClass.setupJson(fileContents);

    }

    @Test
    public void setupXml() throws Exception {

        Injector injector= Guice.createInjector(new DataBaseModule(),new MockedFutureLineStorageModule(), new FakeBuyProductModule());
        BuyProductInitializerImpl testClass= injector.getInstance(BuyProductInitializerImpl.class);
        String fileContents =
                new Scanner(new File(BuyProductInitializerImplTest.class.getResource("small.xml").getFile())).useDelimiter("\\Z").next();
        testClass.setupXml(fileContents);



    }


}
