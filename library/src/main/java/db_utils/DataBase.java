package db_utils;



import java.util.*;
import java.util.concurrent.CompletableFuture;


public interface DataBase {

    CompletableFuture<Void> build_db(String csv_data);

    CompletableFuture<Optional<String>> get_val_from_column_by_name(List<String> keys, String column);

    CompletableFuture<List<String>> get_lines_for_keys(List<String> keysNameList, List<String> keysIDList);

    CompletableFuture<Optional<String>> get_val_from_column_by_column_number(List<String> keys, Integer column);

    Integer getNum_of_columns();

    List<String> getNames_of_columns();

    Integer getNum_of_keys();

    OptionalInt get_num_of_column(String col_name);
}
