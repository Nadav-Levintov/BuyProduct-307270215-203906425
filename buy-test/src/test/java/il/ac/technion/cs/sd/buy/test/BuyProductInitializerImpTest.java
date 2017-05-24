package il.ac.technion.cs.sd.buy.test;

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
        BuyProductInitializerImp testClass = new BuyProductInitializerImp();
        String fileContents =
                new Scanner(new File(BuyProductInitializerImpTest.class.getResource("small.json").getFile())).useDelimiter("\\Z").next();
        testClass.setupJson(fileContents);

    }

    @Test
    public void setupXml() throws Exception {
    }


}
