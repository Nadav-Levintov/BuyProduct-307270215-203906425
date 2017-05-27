package db_utils;

import com.google.inject.Inject;
import il.ac.technion.cs.sd.buy.ext.FutureLineStorageFactory;

import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Created by Nadav on 17-May-17.
 */
public class DataBaseFactoryImpl implements DataBaseFactory {
    private Integer num_of_keys;
    private List<String> names_of_columns;
    private String db_name;
    private Boolean allow_multiples;
    private final FutureLineStorageFactory futureLineStorageFactory;

    @Inject
    public DataBaseFactoryImpl(FutureLineStorageFactory futureLineStorageFactory) {
        this.futureLineStorageFactory = futureLineStorageFactory;
        names_of_columns = null;
        num_of_keys = null;
        db_name = null;
        allow_multiples = null;
    }


    public DataBaseFactory setNum_of_keys(Integer num_of_keys) {
        this.num_of_keys = num_of_keys;
        return this;
    }

    public DataBaseFactory setNames_of_columns(List<String> names_of_columns) {
        this.names_of_columns = names_of_columns;
        return this;
    }

    @Override
    public DataBaseFactory setAllow_Multiples(Boolean allow_multiples) {
        this.allow_multiples=allow_multiples;
        return this;
    }

    public CompletableFuture<DataBase> build()
    {
        return CompletableFuture.completedFuture(new DataBaseImpl(db_name,
                num_of_keys,
                names_of_columns,
                futureLineStorageFactory,
                allow_multiples));
    }

    public DataBaseFactory setDb_name(String db_name) {
        this.db_name = db_name;
        return this;
    }
}


