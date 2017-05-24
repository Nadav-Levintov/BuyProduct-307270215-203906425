package db_utils;

import com.google.inject.Inject;
import il.ac.technion.cs.sd.book.ext.LineStorageFactory;

import java.util.List;

/**
 * Created by Nadav on 17-May-17.
 */
public class DataBaseFactoryImpl implements DataBaseFactory {
    private Integer num_of_keys;
    private List<String> names_of_columns;
    private final LineStorageFactory lineStorageFactory;

    @Inject
    public DataBaseFactoryImpl(LineStorageFactory lineStorageFactory) {
        this.lineStorageFactory = lineStorageFactory;
        names_of_columns = null;
        num_of_keys = null;
    }


    public DataBaseFactoryImpl setNum_of_keys(Integer num_of_keys) {
        this.num_of_keys = num_of_keys;
        return this;
    }

    public DataBaseFactoryImpl setNames_of_columns(List<String> names_of_columns) {
        this.names_of_columns = names_of_columns;
        return this;
    }

    public DataBase build()
    {
        return new DataBaseImpl(num_of_keys,names_of_columns,lineStorageFactory);
    }
}


