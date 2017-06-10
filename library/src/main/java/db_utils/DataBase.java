package db_utils;



import java.util.*;
import java.util.concurrent.CompletableFuture;

/*
* Class: DataBase
* ---------------
*  Explanation:
*      - This data base organize data in tables as you can see below,
*      - when defined you require to define the columns names and the amount of keys
*      - amount of keys must be <= number of columns.
*      - The data base create n! different tables ( n== number of keys ) when every table
*      - is organized in unique order of keys, this way you can extract information quickly,
*      - and for every key as you wish, you can get list of values that has s certain key
*      - or get on value in entry using a list of keys and the name of the column you need.
*
*
*  Example:
*
*   Key1: "Order" | Key2: "User" | Key3: "Product"  | Value1: "Amount" | Value2: "Price"
*   --------------|--------------|------------------|------------------|----------------
*     "order123"  |    "Benny"   |       "BMW"      |      "1"         |    "250000"
*     "order124"  |    "Nadav"   |       "Bimba"    |      "1"         |    "100"
*     "order125"  |    "Eli"     |       "House"    |      "1"         |    "4500000"
*     "order126"  |    "Benny"   |       "BMW"      |      "3"         |    "250000"
*
*     .setNum_of_keys3)
*     .setNames_of_columns(Arrays.asList("Order", "User", "Product", "Amount" ,"Price" ))
*     .setAllow_Multiples(false)
*     .setDb_name("Store")
*     .build
*
*     - The Data base will have in this case 6 tables when every table will be arranged by different keys order
*     - Every table will be sorted by it's keys order
*
*    Table 1:       Key1: "Order"   | Key2: "User"    | Key3: "Product"  | Value1: "Amount" | Value2: "Price"
*    Table 2:       Key1: "Order"   | Key3: "Product" | Key2: "User"     | Value1: "Amount" | Value2: "Price"
*    Table 3:       Key2: "User"    | Key1: "Order"   | Key3: "Product"  | Value1: "Amount" | Value2: "Price"
*    Table 4:       Key2: "User"    | Key3: "Product" | Key1: "Order"    | Value1: "Amount" | Value2: "Price"
*    Table 5:       Key3: "Product" | Key1: "Order"   | Key2: "User"     | Value1: "Amount" | Value2: "Price"
*    Table 6:       Key3: "Product" | Key2: "User"    | Key1: "Order"    | Value1: "Amount" | Value2: "Price"
*
*   *** We have not used get() method of CompletableFuture (or eny method that will "wait"),
*       used only thenApply, thenCompose, thenCombine in order to make it more efficient
*
*/

public interface DataBase {

/* Method: build_db
*   Input: List of strings when every value in table is separated with semicolon (",")
*      example of a string: "order123,Benny,BMW,1,250000"
*
*      If setAllow_Multiples is defined with false:
*           For set of identical keys will be only one entry -> the last in list (rest is discarded)
*
*      If setAllow_Multiples is defined with true:
*           For set of identical keys will save all entries is same order as the input order
* */

    CompletableFuture<Void> build_db(List<String> dataList);

/* Method: get_lines_for_keys
*       Input:
*           keysList     - list of keys
*                           - must be in same length as keysNameList
*                           - order need to be compatible with keysNameList
*           keysNameList - list of keys names (columns names)
*                           - must be in same length as keysList
*                           - order need to be compatible with keysList
*      Output:
*           List<DataBaseElement>
 *                          - list of all the the entries compatible with the keys
 *                          - each entry represented using DataBaseElement
*                           - if data not found return empty list
*
*      Example:(from above)
*            List keysList = Arrays.asList("BMW","Benny");
*            List keysNameList = Arrays.asList("Product","User");
*            get_lines_for_keys(keysList,keysNameList)
*            will return: list of size 2 when every element represent entry (in this case first and last entry)
* */

    CompletableFuture<List<DataBaseElement>> get_lines_for_keys(List<String> keysList, List<String> keysNameList);

/* Method: get_lines_for_single_key
*       Input:
*           key             - key
*
*           column    - key's column name
*
*      Output:
*           List<DataBaseElement>
*                          - list of all the the entries compatible with the key
*                          - each entry represented using DataBaseElement
*                          - if data not found return empty list
*
*      Similar for using get_lines_for_keys method using on key in keyList and one name in keyListName
*
*      Example:(from above)
*            String key = "Nadav";
*            String keyName = "User";
*            get_lines_for_single_key(key,keyName)
*            will return: list of size 1 when every element represent entry (in this case the second entry)
* */

    CompletableFuture<List<DataBaseElement>> get_lines_for_single_key(String key, String keyName);
/* Method: get_val_from_column_by_name
*       Input:
*           keys - the order of key must be in this case as the original order and the same amount
*              (original == the order when the data base was created)
*           column - the name of the column required
*
*      Output:
*           Optional String - in case data is noy fount will return empty()
*                           - found = return the value
*
*      Example:(from above)
*            get_val_from_column_by_name( Arrays.asList("order123","Benny","BMW"), "Price")
*            will return: "250000"
* */
    CompletableFuture<Optional<String>> get_val_from_column_by_name(List<String> keys, String column);

/* Method: get_val_from_column_by_column_number
*       Very similar to get_val_from_column_by_name (using column number instead it's name)
*       Input:
*           keys - the order of key must be in this case as the original order and the same amount
*              (original == the order when the data base was created)
*           column - the number of the column required
*
*      Output:
*           Optional String - in case data is noy fount will return empty()
*                           - found = return the value
*                           - in case multiples is allowed will return one of the values (not guaranteed witch one)
*
*      Example:(from above)
*            get_val_from_column_by_name( Arrays.asList("order123","Benny","BMW"), 4)
*            will return: "250000"
* */

    CompletableFuture<Optional<String>> get_val_from_column_by_column_number(List<String> keys, Integer column);

/* Method: getNum_of_columns
*      Output:
*           number of columns defined in data base
*
*      Example:(from above)
*            getNum_of_columns()
*            will return: 5
* */
    Integer getNum_of_columns();

/* Method: getNames_of_columns
*      Output:
*           List of the columns names as defined
*
*      Example:(from above)
*            etNames_of_columns()
*            will return: list{"Order", "User", Product", "Amount", "Price"}
* */

    List<String> getNames_of_columns();

/* Method: getNum_of_keys
*      Output:
*           number of keys defined in data base
*
*      Example:(from above)
*            getNum_of_keys()
*            will return: 3
* */

    Integer getNum_of_keys();

/* Method: get_num_of_column
*      Input:
*           column's name
*      Output:
*           column number in original order
*           (original == the one defined in the data base creation)
*
*      Example:(from above)
*            get_num_of_column("Order")
*            will return: 0
*            get_num_of_column("Product")
*            will return: 2
* */
    OptionalInt get_num_of_column(String col_name);

/* Method: getDb_name
*      Output:
*           data base name as given in initialization
*
*      Example:(from above)
*            getDb_name()
*            will return: "Store"
* */

    String getDb_name();

/* Method: is_multiples_allowed
*      Output:
*           true - case allowed
*           false - case not allowed
*
*      Example:(from above)
*            is_multiples_allowed()
*            will return: false
* */

    Boolean is_multiples_allowed();

}
