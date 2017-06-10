package db_utils;

import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Class: DataBaseFactory
 * ----------------------
 *  Example:
 *
 *   Key1: "Order" | Key2: "User" | Key3: "Product"  | Value1: "Amount" | Value2: "Price"
 *   --------------|--------------|------------------|------------------|----------------
 *     "order123"  |    "Benny"   |       "BMW"      |      "1"         |    "250000"
 *     "order124"  |    "Nadav"   |       "Bimba"    |      "1"         |    "100"
 *     "order125"  |    "Eli"     |       "House"    |      "1"         |    "4500000"
 *     "order126"  |    "Guy"     |       "Bugs"     |      "345"       |     "2"
 *
 *     .setNum_of_keys3)
 *     .setNames_of_columns(Arrays.asList("Order", "User", "Product", "Amount" ,"Price" ))
 *     .setAllow_Multiples(false)
 *     .setDb_name("Store")
 *     .build
 *
 *     - You can fetch entries in database using every key in every order you want
 *     - Name for every dataBase in same folder need to be unique in order to not to override files
 *
 *
 */
public interface DataBaseFactory {

    /*  Method: setNum_of_keys
    *       Input: num_of_keys
    *       define the amount of keys in the table - can organize data by every key
    * */
    public DataBaseFactory setNum_of_keys(Integer num_of_keys);

    public DataBaseFactory setNames_of_columns(List<String> names_of_columns);

    public DataBaseFactory setAllow_Multiples(Boolean allow_multiples);

    public DataBaseFactory setDb_name(String db_name);

    public DataBase build();
}


