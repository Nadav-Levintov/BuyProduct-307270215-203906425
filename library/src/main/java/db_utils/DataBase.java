package db_utils;



import java.util.*;
import java.util.concurrent.CompletableFuture;


public interface DataBase {

    CompletableFuture<Void> build_db(List<String> dataList);

    CompletableFuture<Optional<String>> get_val_from_column_by_name(List<String> keys, String column);

    CompletableFuture<List<DataBaseElement>> get_lines_for_keys(List<String> keysList, List<String> keysNameList);

    CompletableFuture<Optional<String>> get_val_from_column_by_column_number(List<String> keys, Integer column);

    Integer getNum_of_columns();

    List<String> getNames_of_columns();

    Integer getNum_of_keys();

    OptionalInt get_num_of_column(String col_name);

    String getDb_name();

    Boolean is_multiples_allowed();

    CompletableFuture<List<DataBaseElement>> get_lines_for_single_key(String key, String column);



}
