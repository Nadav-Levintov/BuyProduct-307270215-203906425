package db_utils;




import com.google.common.collect.ArrayListMultimap;
import il.ac.technion.cs.sd.buy.ext.FutureLineStorageFactory;
import il.ac.technion.cs.sd.buy.ext.FutureLineStorage;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;


public class DataBaseImpl implements DataBase {

    private final String db_name;
    private final Integer num_of_columns;
    private final Integer num_of_keys;
    private final List<String> names_of_columns;
    private final FutureLineStorageFactory futureLineStorageFactory;
    private Boolean allow_multiples;


    //Private Functions

    private String createFileNameFromPermutation(List<String> keyList,
                                                 List<Integer> premutationIndexList)
    {

        String fileName = new String();
        fileName+=db_name+"_";
        fileName+=keyList.get(premutationIndexList.get(0));
        for (int index=1; index <keyList.size(); index++)
        {
            fileName+= "_" + keyList.get(premutationIndexList.get(index));
        }
        return fileName;
    }

    private void write_map_to_new_file(ArrayListMultimap<String,String> multiMap, String fileName)
    {

        CompletableFuture<FutureLineStorage> lineStorage = futureLineStorageFactory.open(fileName);
        for (String key : multiMap.keySet())
        {
            List<String> values = multiMap.get(key);
            String outputKey = key;
            if(this.allow_multiples)
            {
                for ( String valueStr: values)
                {
                    lineStorage.thenApply(storage -> storage.appendLine(outputKey + valueStr));
                }
            }else
            {
                String lastValueStr = values.get(values.size()-1);
                lineStorage.thenApply(storage -> storage.appendLine(outputKey + lastValueStr));
            }

        }
    }

    private String createFileName() {
        String fileName = new String();
        fileName+=db_name+"_";
        fileName+=names_of_columns.get(0);
        for(int i = 1; i< (this.getNum_of_keys()); i++)
        {
            fileName += "_" + names_of_columns.get(i);
        }
        return fileName;
    }

    private Boolean check_if_no_duplicates_in_list(List<String> list) {
        List<String> noDuplicates = new ArrayList<>();
        for (String str : list)
        {
            if(noDuplicates.contains(str))
                return false;
            noDuplicates.add(str);
        }
        return true;
    }

    private CompletableFuture<Integer> binary_search_future(final Integer high,final Integer low, CompletableFuture<FutureLineStorage> lineStorageCompletableFuture, String key,Integer keys_amount)
    {
        if (low>high) return CompletableFuture.completedFuture(new Integer(-1));

        CompletableFuture<String> curr_line;
        Integer mid = low +(high - low)/2;
        curr_line = lineStorageCompletableFuture.thenCompose(ls -> ls.read(mid));
        return curr_line.thenCompose(line ->
        {
            String[] values = line.split(",");
            String curr_key= create_string_seperated_with_comma(values, keys_amount);
            Integer compare=key.compareTo(curr_key);
            if      (compare < 0) return binary_search_future(mid-1,low,lineStorageCompletableFuture,key,keys_amount);
            else if (compare > 0) return binary_search_future(high,mid + 1,lineStorageCompletableFuture,key,keys_amount);
            else return CompletableFuture.completedFuture(mid);
        });
    }

    private CompletableFuture<Integer> find_index_in_file(String key, Integer keys_amount, CompletableFuture<FutureLineStorage> lineStorage) {

        CompletableFuture<Integer> low=CompletableFuture.completedFuture(new Integer(0));
        CompletableFuture<Integer> high, found_line_num;
        CompletableFuture<Integer> numberOfLines;


        numberOfLines = lineStorage.thenCompose(ls -> ls.numberOfLines());


        high = numberOfLines.thenApply(val -> val-1);

        return found_line_num=high.thenCombine(low,(high_val,low_val)->
                binary_search_future(high_val,low_val,lineStorage,key,keys_amount))
        .thenCompose(val -> val);

    }

    private String create_string_seperated_with_comma(String[] values, Integer length) {
        String curr_key = new String();
        for(int i = 0; i< length; i++)
        {
            curr_key += values[i] + ",";
        }
        return curr_key;
    }

    //function get list of <all> the keys in the order of sorting and will be saved on disk in that order
    private ArrayListMultimap<String,String> create_file_sorted_by_keys(String csv_data, List<String> keys, List<Integer> currentIndexKeyList) {

        String[] lines = csv_data.split("\n");
       // TreeMap<String,String> map = new TreeMap<>();
        ArrayListMultimap<String,String> map = ArrayListMultimap.create();

        for(String line : lines)
        {
            String[] curr_line = line.split(",");

            //create key for map
            String keysString =new String();
            for (int index=0; index <keys.size(); index++)
            {
                keysString+= curr_line[currentIndexKeyList.get(index)]+",";
            }

            //create value for map
            String value = new String();
            for(int i=num_of_keys;i<num_of_columns-1;i++)
            {
                value+=curr_line[i]+",";
            }

            value+=curr_line[num_of_columns-1];
            map.put(keysString,value);
        }
        return map;
    }

    private void get_lines_for_key_parameter_check(List<String> keysNameList, List<String> keysList) {
        if(keysNameList.size()!=keysList.size()){
            throw new IllegalArgumentException();
        }
        if(!this.getNames_of_columns().subList(0,this.num_of_keys).containsAll(keysNameList)) {
            throw new IllegalArgumentException();
        }
        if(!check_if_no_duplicates_in_list(keysNameList)) {
            throw new IllegalArgumentException();
        }
    }

    private String combine_file_name(List<String> keysNameList) {
        ArrayList<String> keysNameforFile = new ArrayList<>(keysNameList);
        while(keysNameforFile.size()<(this.num_of_keys))//file name is build from all the keys but one
        {
            for(int i=0; i < (this.num_of_keys); i++)
            {
                if(!keysNameforFile.contains(this.names_of_columns.get(i)))
                {
                    keysNameforFile.add(names_of_columns.get(i));
                    break;
                }
            }
        }

        String fileName = new String();
        fileName+=db_name+"_";
        fileName += keysNameforFile.get(0);
        for(int i = 1; i< (keysNameforFile.size()); i++)
        {
            fileName += "_" + keysNameforFile.get(i);
        }
        return fileName;
    }


    private CompletableFuture<Integer> get_first_line_with_key(List<String> keysList, FutureLineStorage lineStorage, String key, final Integer index) {
        if (index < 0 ) return CompletableFuture.completedFuture(0);

        CompletableFuture<Integer> res = CompletableFuture.completedFuture(index);

        return res.thenCompose(curr_index ->
        {
            CompletableFuture<String> curr_line;
            curr_index--;
            curr_line = lineStorage.read(curr_index);
            CompletableFuture<String> curr_key = curr_line.thenApply(curr_line_str -> create_string_seperated_with_comma(curr_line_str.split(","), keysList.size()));

            CompletableFuture<Integer> compare = curr_key.thenApply(curr_key_val -> key.compareTo(curr_key_val));
            return compare.thenCompose(compare_val ->
            {
                if(compare_val!= 0) return CompletableFuture.completedFuture(index+1);
                return get_first_line_with_key( keysList, lineStorage, key, index-1);
            });
        });
    }

    //Public Functions

    public DataBaseImpl(String db_name, Integer num_of_keys, List<String> names_of_columns, FutureLineStorageFactory futureLineStorageFactory, Boolean allow_multiples) {
        this.db_name = db_name;
        this.num_of_keys=num_of_keys;
        this.names_of_columns = names_of_columns;
        this.num_of_columns=names_of_columns.size();
        this.futureLineStorageFactory = futureLineStorageFactory;
        this.allow_multiples = allow_multiples;
    }

    public CompletableFuture<Void> build_db(String csv_data){

        List<String> keyList = new ArrayList<>();
        ArrayList<Integer> keyIndexList = new ArrayList<>();
        for(Integer i=0; i<this.num_of_keys; i++)       //create array of index that will make permutations of it
        {
            keyIndexList.add(i);
        }
        keyList.addAll(this.names_of_columns.subList(0,num_of_keys));  //create a list of keys names by order
        Permutations keysPermutation = new Permutations();
        List<List<Integer>> listOfAllPermutations = new ArrayList<>();
        listOfAllPermutations.addAll(keysPermutation.perm(keyIndexList));

        //now listOfAllPermutations has all possible permutations
        for (List<Integer> currentIndexKeyList: listOfAllPermutations)
        {
            String fileName = new String(createFileNameFromPermutation(keyList ,currentIndexKeyList));
            write_map_to_new_file(create_file_sorted_by_keys(csv_data, keyList, currentIndexKeyList), fileName);
        }

        return CompletableFuture.completedFuture(null);
    }

    public CompletableFuture<Optional<String>> get_val_from_column_by_name(List<String> keys, String column) {
        String fileName = createFileName();
        CompletableFuture<FutureLineStorage> lineStorage = futureLineStorageFactory.open(fileName);

        if(names_of_columns.indexOf(column) <0)
        {
            return CompletableFuture.completedFuture(Optional.empty());
        }

        String key=new String();
        for (String str: keys)
        {
            key+=str+",";
        }


        CompletableFuture<Integer> rowNumber = find_index_in_file(key, this.getNum_of_keys(), lineStorage);

        CompletableFuture<Optional<String>> res= rowNumber.thenCompose(row_number_val ->
        {
            if(row_number_val>=0)
            {
                CompletableFuture<String> curr_line;
                curr_line = lineStorage.thenCompose(ls -> ls.read(row_number_val));
                CompletableFuture<String> value = curr_line.thenApply(curr_line_val ->
                {
                    String[] arr = curr_line_val.split(",");
                   return  arr[names_of_columns.indexOf(column)];
                });
                return value.thenCompose(val -> Optional.of(new String(val)));

            }
            else
            {
                return Optional.empty();
            }

        });

        return res;
    }

    public CompletableFuture<Optional<String>> get_val_from_column_by_column_number(List<String> keys, Integer column) {
        if (column< 0  || column >= num_of_columns)
        {
            return CompletableFuture.completedFuture(Optional.empty());
        }
        return get_val_from_column_by_name(keys,names_of_columns.get(column));
    }

    public Integer getNum_of_columns() {
        return num_of_columns;
    }

    public List<String> getNames_of_columns() {
        return names_of_columns;
    }

    public Integer getNum_of_keys() {
        return num_of_keys;
    }

    public OptionalInt get_num_of_column(String col_name) {

        if(!names_of_columns.contains(col_name))
        {
            return OptionalInt.empty();
        }
        Integer index = names_of_columns.indexOf(col_name);
        return OptionalInt.of(index);

    }

    public CompletableFuture<List<String>> get_lines_for_keys(List<String> keysNameList, List<String> keysList) {

        get_lines_for_key_parameter_check(keysNameList, keysList);

        String fileName = combine_file_name(keysNameList);

        //here file name is legal

        CompletableFuture<FutureLineStorage> futureLineStorage = futureLineStorageFactory.open(fileName);

        List<String> results = new ArrayList<>();

        String curr_line;
        Integer compare;
        Integer numberOfLines;
        FutureLineStorage lineStorage = null;
        try {
            lineStorage = futureLineStorage.get();
        } catch (InterruptedException e) {
            throw new RuntimeException();
        } catch (ExecutionException e) {
            throw new RuntimeException();
        }

        try {
            numberOfLines = lineStorage.numberOfLines().get();
        } catch (InterruptedException e) {
            throw new RuntimeException();
        } catch (ExecutionException e) {
            throw new RuntimeException();
        }

        String key=new String();
        for (String str: keysList)
        {
            key+=str+",";
        }

        //here have the key to search
        Integer index = find_index_in_file(key,keysList.size(),lineStorage);

        if(index<0) //case key not found
        {
            return CompletableFuture.completedFuture(results);
        }

        //here find the first line in file with the right key
        index = get_first_line_with_key(keysList, lineStorage, key, index);

        //here it copies all the rows with the right key from the first
        do {
            try {
                curr_line = lineStorage.read(index).get();
            } catch (InterruptedException e) {
                throw new RuntimeException();
            } catch (ExecutionException e) {
                throw new RuntimeException();
            }
            String[] values = curr_line.split(",");
            String curr_key = create_string_seperated_with_comma(values,keysList.size() );
            compare = key.compareTo(curr_key);
            if (compare == 0) {
                String output = curr_line.substring(key.length());
                results.add(output);
            }
            index++;
        }
        while(compare == 0 && index < numberOfLines);

        return CompletableFuture.completedFuture(results);
    }

    public String getDb_name() {
        return db_name;
    }

    public Boolean is_multiples_allowed() {
        return allow_multiples;
    }
}
