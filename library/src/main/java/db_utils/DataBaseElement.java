package db_utils;

import java.util.Optional;

/*
*   Name: DataBaseElement
*   Usage: Used in some methods in DaraBase
*   Example:
*
* */
public interface DataBaseElement {
    public String get(Integer column_num);
    public String get(String column_name);
}
