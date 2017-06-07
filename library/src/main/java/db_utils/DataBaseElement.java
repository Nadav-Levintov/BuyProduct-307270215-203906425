package db_utils;

import java.util.List;
import java.util.Optional;


/**
 * Created by benny on 07/06/2017.
 */
public class DataBaseElement {
    private List<String> columnNames;
    private List<String> values;

    DataBaseElement(List<String> columnNames, List<String> values){
        this.columnNames = columnNames;
        this.values = values;
    }

    public Optional<String> get(String keyName)
    {
        if (columnNames.contains(keyName))
        {
            Integer index = columnNames.indexOf(keyName);
            return Optional.of(values.get(index));
        }else
        {
            throw new IllegalArgumentException();
        }

    }




}
