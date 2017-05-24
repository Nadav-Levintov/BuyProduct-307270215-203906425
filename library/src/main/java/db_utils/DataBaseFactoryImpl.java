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
    private final FutureLineStorageFactory futureLineStorageFactory;

    @Inject
    public DataBaseFactoryImpl(FutureLineStorageFactory futureLineStorageFactory) {
        this.futureLineStorageFactory = futureLineStorageFactory;
        names_of_columns = null;
        num_of_keys = null;
    }


    public CompletableFuture<DataBaseFactory> setNum_of_keys(Integer num_of_keys) {
        this.num_of_keys = num_of_keys;
        return CompletableFuture.completedFuture(this);
    }

    public CompletableFuture<DataBaseFactory> setNames_of_columns(List<String> names_of_columns) {
        this.names_of_columns = names_of_columns;
        return CompletableFuture.completedFuture(this);
    }

    public CompletableFuture<DataBase> build()
    {
        return CompletableFuture.completedFuture(new DataBaseImpl(num_of_keys,
                names_of_columns,
                futureLineStorageFactory));
    }
}


