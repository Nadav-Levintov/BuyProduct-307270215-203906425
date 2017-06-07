package db_utils;

import java.util.List;
import java.util.Optional;


/**
 * Created by benny on 07/06/2017.
 */
public class DataBaseElementImpl implements DataBaseElement{
    private List<String> columnNames;
    private List<String> values;

    DataBaseElementImpl(List<String> columnNames, List<String> values){
        this.columnNames = columnNames;
        this.values = values;
    }

    public String get(Integer column_num)
    {
        if(column_num == null || column_num <0 || column_num >=columnNames.size()) throw new IllegalArgumentException();

        return values.get(column_num);
    }
    public String get(String column_name)
    {
        if (columnNames.contains(column_name))
        {
            Integer index = columnNames.indexOf(column_name);
            return values.get(index);
        }else
        {
            throw new IllegalArgumentException();
        }

    }




}
