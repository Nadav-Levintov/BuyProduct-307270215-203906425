package db_utils;

import java.util.Optional;

/**
 * Created by Nadav on 07-Jun-17.
 */
public interface DataBaseElement {
    public String get(Integer column_num);
    public String get(String column_name);
}
