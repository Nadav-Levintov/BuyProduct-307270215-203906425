package il.ac.technion.cs.sd.buy.test;

import com.google.inject.AbstractModule;
import il.ac.technion.cs.sd.buy.app.BuyProductInitializer;
import il.ac.technion.cs.sd.buy.app.BuyProductReader;
import il.ac.technion.cs.sd.buy.app.BuyProductReaderImpl;

/**
 * Created by benny on 24/05/2017.
 */
public class FakeBuyProductModule extends AbstractModule {

  @Override
  protected void configure() {
    this.bind(BuyProductInitializer.class).to(FakeBuyProductInitializerImpl.class);
    this.bind(BuyProductReader.class).to(BuyProductReaderImpl.class);
  }

}


