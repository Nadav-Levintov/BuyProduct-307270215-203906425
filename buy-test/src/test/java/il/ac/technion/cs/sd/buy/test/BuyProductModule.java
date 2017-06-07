package il.ac.technion.cs.sd.buy.test;

import com.google.inject.AbstractModule;
import db_utils.DataBaseFactory;
import db_utils.DataBaseFactoryImpl;
import il.ac.technion.cs.sd.buy.app.BuyProductInitializer;
import il.ac.technion.cs.sd.buy.app.BuyProductInitializerImpl;
import il.ac.technion.cs.sd.buy.app.BuyProductReader;
import il.ac.technion.cs.sd.buy.app.BuyProductReaderImpl;
import il.ac.technion.cs.sd.buy.ext.FutureLineStorageFactory;

/**
 * Created by Nadav on 19-May-17.
 */
public class BuyProductModule extends AbstractModule {
    public BuyProductModule() {
    }

    protected void configure() {

        this.bind(DataBaseFactory.class).to(DataBaseFactoryImpl.class);
        this.bind(BuyProductInitializer.class).to(BuyProductInitializerImpl.class);
        this.bind(BuyProductReader.class).to(BuyProductReaderImpl.class);
    }
}
