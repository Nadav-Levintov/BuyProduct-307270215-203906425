package db_utils;



import java.util.*;


public interface DataBase {

    public void build_db(String csv_data);

    public Optional<String> get_val_from_column_by_name(List<String> keys, String column);

    public List<String> get_lines_for_keys(List<String> keysNameList,List<String> keysIDList);

    public Optional<String> get_val_from_column_by_column_number(List<String> keys, Integer column);

    public Integer getNum_of_columns();

    public List<String> getNames_of_columns();

    public Integer getNum_of_keys();

    public OptionalInt get_num_of_column(String col_name);
}
