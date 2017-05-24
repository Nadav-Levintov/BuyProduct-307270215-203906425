package db_utils;

import com.google.inject.AbstractModule;

/**
 * Created by Nadav on 19-May-17.
 */
public class DataBaseModule  extends AbstractModule {
    protected void configure() {
    this.bind(DataBaseFactory.class).to(DataBaseFactoryImpl.class);
    }
}
