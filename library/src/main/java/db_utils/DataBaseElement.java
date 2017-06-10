package db_utils;

import java.util.Optional;

/*
*   Name: DataBaseElement
*   Usage: Used in some methods in DaraBase (get_lines_for_single_key, get_lines_for__keys)
*   Example:
*
*   Key1: "Order" | Key2: "User" | Key3: "Product"  | Value1: "Amount" | Value2: "Price"
*   --------------|--------------|------------------|------------------|----------------
*     "order123"  |    "Benny"   |       "BMW"      |      "1"         |    "250000"
*     "order124"  |    "Nadav"   |       "Bimba"    |      "1"         |    "100"
*     "order125"  |    "Eli"     |       "House"    |      "1"         |    "4500000"
*     "order126"  |    "Guy"     |       "Bugs"     |      "345"       |     "2"
*
*   - Every DataElement represent one entry in table
*   - The names of the columns are defined when the data base is created
*   - The order of the names are same as the order of the keys list given to the the functions above
*   - The names that was not in the key list are ordered as default (same as th data base)
*   - obj.get("User") == "Benny"    : can use the names (recommended)
*   - obj.get(4) == "250000"        : can use column position
*
*   -In case of Illegal value will throw IllegalArgumentException()
*
* */
public interface DataBaseElement {
    public String get(Integer column_num);
    public String get(String column_name);
}
