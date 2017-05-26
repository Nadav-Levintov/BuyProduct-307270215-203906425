package il.ac.technion.cs.sd.buy.app;

import com.google.inject.AbstractModule;
import il.ac.technion.cs.sd.buy.ext.FutureLineStorageFactory;

/**
 * Created by Nadav on 19-May-17.
 */
public class MockedFutureLineStorageModule extends AbstractModule {
    public MockedFutureLineStorageModule() {
    }

    protected void configure() {
        this.bind(FutureLineStorageFactory.class).to(MockFutureLineStorageFactory.class);
    }
}
