package il.ac.technion.cs.sd.buy.test;

import com.google.inject.Guice;
import com.google.inject.Injector;
import db_utils.DataBaseModule;
import il.ac.technion.cs.sd.buy.app.BuyProductInitializerImp;
import org.junit.Test;

import java.io.File;
import java.util.Scanner;

/**
 * Created by benny on 24/05/2017.
 */

public class BuyProductInitializerImpTest {

    @Test
    public void setupJson() throws Exception {

        Injector injector= Guice.createInjector(new DataBaseModule(),new MockedFutureLineStorageModule(), new FakeBuyProductModule());
        BuyProductInitializerImp testClass= injector.getInstance(BuyProductInitializerImp.class);

      //  BuyProductInitializerImp testClass = new BuyProductInitializerImp();
        String fileContents =
                new Scanner(new File(BuyProductInitializerImpTest.class.getResource("small.json").getFile())).useDelimiter("\\Z").next();
        testClass.setupJson(fileContents);

    }

    @Test
    public void setupXml() throws Exception {

        Injector injector= Guice.createInjector(new DataBaseModule(),new MockedFutureLineStorageModule(), new FakeBuyProductModule());
        BuyProductInitializerImp testClass= injector.getInstance(BuyProductInitializerImp.class);
        String fileContents =
                new Scanner(new File(BuyProductInitializerImpTest.class.getResource("small.xml").getFile())).useDelimiter("\\Z").next();
        testClass.setupXml(fileContents);



    }


}
