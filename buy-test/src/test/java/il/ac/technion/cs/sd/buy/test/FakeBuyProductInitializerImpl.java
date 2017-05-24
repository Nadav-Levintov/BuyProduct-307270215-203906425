package il.ac.technion.cs.sd.buy.test;

import db_utils.DataBaseFactory;
import il.ac.technion.cs.sd.buy.app.BuyProductInitializer;
import il.ac.technion.cs.sd.buy.app.BuyProductInitializerImp;

import javax.inject.Inject;

/**
 * Created by benny on 24/05/2017.
 */
public class FakeBuyProductInitializerImpl extends BuyProductInitializerImp implements BuyProductInitializer {

    @Inject
    public FakeBuyProductInitializerImpl(DataBaseFactory dataBaseFactory){
        super(dataBaseFactory);
    }
    public DataBaseFactory get_DataBaseFactory()
    {
        return dataBaseFactory;
    }

}
