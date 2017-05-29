package il.ac.technion.cs.sd.buy.app;

import com.google.inject.Inject;
import db_utils.DataBase;
import db_utils.DataBaseFactory;
import javafx.util.Pair;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
 * Created by Nadav on 24-May-17.
 */
public class BuyProductReaderImpl implements BuyProductReader {

    private final DataBase ordersDB;
    private final DataBase productsDB;
    private final DataBase modified_ordersDB;

    /* Private Methods */
    private CompletableFuture<List<String>> get_orders_with_key_from_db(String key_id,String key_column,DataBase db) {

        List<String> names_of_keys = new ArrayList<>();
        names_of_keys.add(key_column);
        List<String> keys = new ArrayList<>();
        keys.add(key_id);

        return db.get_lines_for_keys(names_of_keys,keys);
    }

    private CompletableFuture<Boolean> does_db_contains_order_id(String orderId, DataBase db) {
        CompletableFuture<List<String>> line_list = get_orders_with_key_from_db(orderId,"order",db);

        return line_list.thenApply(List::isEmpty).thenApply(r -> !r);
    }

    private CompletableFuture<Map<String, Long>> get_items_map_from_order_list(CompletableFuture<List<String>> order_line_list) {
        return order_line_list.thenApply(lines ->
        {
            return lines.stream()
                    .map(line -> {
                        String line_values[] = line.split(",");
                        String order_id = line_values[0];
                        String product_id = line_values[1];
                        CompletableFuture<Long> curr_amount = CompletableFuture.completedFuture(Long.parseLong(line_values[2]));
                        Boolean is_moded = Boolean.parseBoolean(line_values[3]);
                        Boolean is_canceled = Boolean.parseBoolean(line_values[4]);


                        if (!is_canceled) {
                            if(is_moded)
                            {
                                CompletableFuture<List<String>> moded_line_list = get_orders_with_key_from_db(order_id,"order",modified_ordersDB);

                                curr_amount = moded_line_list.thenApply(mod_list -> Long.parseLong(mod_list.get(mod_list.size() - 1).split(",")[0]));

                            }
                        }
                        return new Pair<CompletableFuture<String>,CompletableFuture<Long>>(CompletableFuture.completedFuture(product_id),curr_amount);
                    })
                    .collect(Collectors.toList());
        }).thenCompose(list ->
        {
            List<CompletableFuture<String>> key_list = list.stream().map(Pair::getKey).collect(Collectors.toList());
            List<CompletableFuture<Long>> val_list = list.stream().map(Pair::getValue).collect(Collectors.toList());

            CompletableFuture<List<String>> completableFuture_key_list = CompletableFuture.allOf(key_list.toArray(new CompletableFuture[key_list.size()]))
                    .thenApply(v -> key_list.stream()
                            .map(CompletableFuture::join)
                            .collect(Collectors.toList()));

            CompletableFuture<List<Long>> completableFuture_val_list = CompletableFuture.allOf(val_list.toArray(new CompletableFuture[val_list.size()]))
                    .thenApply(v -> val_list.stream()
                            .map(CompletableFuture::join)
                            .collect(Collectors.toList()));

            return completableFuture_key_list.thenCombine(completableFuture_val_list,(keys_list,values_list) -> {
                Map<String,Long> temp_map = new TreeMap<>();

                for(int i=0;i<key_list.size();i++)
                {
                    String curr_key = keys_list.get(i);
                    Long curr_val = values_list.get(i);
                    if(temp_map.containsKey(curr_key))
                    {
                        curr_val+=temp_map.get(curr_key);
                    }
                    temp_map.put(curr_key,curr_val);
                }
                return temp_map;
            });

        });
    }
    /* public Methods */

    @Inject
    public BuyProductReaderImpl(DataBaseFactory dataBaseFactoryCompletableFuture) {

        Integer num_of_keys_ordersDB = 3;

        List<String> names_of_columns_OrdersDB = new ArrayList<>();
        names_of_columns_OrdersDB.add("order");
        names_of_columns_OrdersDB.add("user");
        names_of_columns_OrdersDB.add("product");
        names_of_columns_OrdersDB.add("amount");
        names_of_columns_OrdersDB.add("modified");
        names_of_columns_OrdersDB.add("canceled");

        this.ordersDB = dataBaseFactoryCompletableFuture
                .setDb_name("Orders")
                .setNames_of_columns(names_of_columns_OrdersDB)
                .setNum_of_keys(num_of_keys_ordersDB)
                .setAllow_Multiples(Boolean.FALSE)
                .build();


        Integer num_of_keys_productsDB = 1;
        List<String> names_of_columns_productsDB = new ArrayList<>();
        names_of_columns_productsDB.add("product");
        names_of_columns_productsDB.add("price");
        this.productsDB = dataBaseFactoryCompletableFuture
                .setDb_name("Products")
                .setNames_of_columns(names_of_columns_productsDB)
                .setNum_of_keys(num_of_keys_productsDB)
                .setAllow_Multiples(Boolean.FALSE)
                .build();

        Integer num_of_keys_modified_ordersDB = 1;
        List<String> names_of_columns_modified_ordersDB = new ArrayList<>();
        names_of_columns_modified_ordersDB.add("order");
        names_of_columns_modified_ordersDB.add("amount");
        this.modified_ordersDB = dataBaseFactoryCompletableFuture
                .setDb_name("Modified")
                .setNames_of_columns(names_of_columns_modified_ordersDB)
                .setNum_of_keys(num_of_keys_modified_ordersDB)
                .setAllow_Multiples(Boolean.TRUE)
                .build();

    }

    @Override
    public CompletableFuture<Boolean> isValidOrderId(String orderId) {
        return does_db_contains_order_id(orderId,ordersDB);
    }

    @Override
    public CompletableFuture<Boolean> isCanceledOrder(String orderId) {

        CompletableFuture<List<String>> line_list = get_orders_with_key_from_db(orderId, "order",ordersDB);

        return line_list.thenApply(list ->
                {
                     if(list.isEmpty())
                    {
                        return Boolean.FALSE;
                    }
                    return Boolean.parseBoolean(list.get(0).split(",")[4]);
                }
        );
    }

    @Override
    public CompletableFuture<Boolean> isModifiedOrder(String orderId) {
        return does_db_contains_order_id(orderId,modified_ordersDB);
    }

    @Override
    public CompletableFuture<OptionalInt> getNumberOfProductOrdered(String orderId) {

        CompletableFuture<List<String>> order_line_list = get_orders_with_key_from_db(orderId,"order",ordersDB);

        CompletableFuture<List<String>> mod_line_list = get_orders_with_key_from_db(orderId,"order",modified_ordersDB);

        return order_line_list.thenCombine(mod_line_list,(order_list,mod_order_list) ->
        {
            String[] order = order_list.get(0).split(",");
            Boolean is_mod = Boolean.parseBoolean(order[3]);
            Boolean is_canceled = Boolean.parseBoolean(order[4]);
            Integer amount = Integer.parseInt(order[2]);
            if(order_list.isEmpty())
            {
                return OptionalInt.empty();
            }

            if(is_mod)
            {
                amount = Integer.parseInt(mod_order_list.get(mod_order_list.size()-1).split(",")[0]);
            }

            if(is_canceled)
            {
                amount*=-1;
            }

            return OptionalInt.of(amount);
        });
    }

    @Override
    public CompletableFuture<List<Integer>> getHistoryOfOrder(String orderId) {

        CompletableFuture<List<Integer>> res_list = CompletableFuture.completedFuture(new ArrayList<>());

        CompletableFuture<List<String>> order_line_list = get_orders_with_key_from_db(orderId,"order",ordersDB);

        CompletableFuture<List<String>> mod_line_list = get_orders_with_key_from_db(orderId,"order",modified_ordersDB);

        order_line_list.thenApply(lines -> lines
                .stream()
                .map(line -> res_list
                        .thenApply(list -> list
                                .add(Integer.parseInt(line.split(",")[2]))))); // amount from orders table

        mod_line_list.thenApply(lines -> lines
                .stream()
                .map(line -> res_list
                        .thenApply(list -> list
                                .add(Integer.parseInt(line.split(",")[1]))))); //amount from modified orders table

        order_line_list.thenApply(lines -> lines
                .stream()
                .map(line -> res_list
                        .thenApply(list ->
                        {
                            Boolean is_order_canceled =Boolean.parseBoolean(line.split(",")[4]);
                            if(is_order_canceled) {
                                list.add(-1);
                            }
                            return list;
                        })));

        return res_list;
    }

    @Override
    public CompletableFuture<List<String>> getOrderIdsForUser(String userId) {

        CompletableFuture<List<String>> order_line_list = get_orders_with_key_from_db(userId,"user",ordersDB);

        return order_line_list.thenApply(lines -> lines
                .stream()
                .map(line -> line.split(",")[0])
                .sorted()
                .collect(Collectors.toList()));

    }

    @Override
    public CompletableFuture<Long> getTotalAmountSpentByUser(String userId) {

        CompletableFuture<List<String>> future_orders_list =  get_orders_with_key_from_db(userId,"user",ordersDB);


        CompletableFuture<List<Integer>> transactions_prices = future_orders_list.thenCompose(orders_list ->
        {
            List<CompletableFuture<Integer>> priceList = new ArrayList<>();
            for (String order_string: orders_list)
            {
                String line_values[] = order_string.split(",");
                String order_id = line_values[0];
                String product_id = line_values[1];
                String order_amount = line_values[2];
                Boolean is_modified = Boolean.parseBoolean(line_values[3]);
                Boolean is_canceled = Boolean.parseBoolean(line_values[4]);

                CompletableFuture<Integer> price = productsDB.get_val_from_column_by_name(new ArrayList<String>(Arrays.asList(product_id)),"price")
                        .thenApply(price_optional ->
                                Integer.parseInt(price_optional.get()));

                CompletableFuture<Integer> amount = CompletableFuture.completedFuture(Integer.parseInt(order_amount));
                if(!is_canceled)
                {
                    if(is_modified)
                    {
                        amount= get_orders_with_key_from_db(order_id,"order",modified_ordersDB).thenApply(modified_lines ->
                                modified_lines.get(modified_lines.size()-1).split(",")[0]).thenApply(Integer::parseInt);
                    }
                }
                CompletableFuture<Integer> orderPrice = price.thenCombine(amount, (price_t,amount_t) -> price_t*amount_t);
                priceList.add(orderPrice);
            }
            return CompletableFuture.allOf(priceList.toArray(new CompletableFuture[priceList.size()]))
                    .thenApply(v -> priceList.stream()
                            .map(CompletableFuture::join)
                            .collect(Collectors.toList())
                    );
        });

        return transactions_prices.thenApply(list -> list.stream()
                .mapToLong(Integer::longValue).sum());
    }

    @Override
    public CompletableFuture<List<String>> getUsersThatPurchased(String productId) {
        CompletableFuture<List<String>> order_line_list = get_orders_with_key_from_db(productId,"product",ordersDB);

        return order_line_list.thenApply(lines -> lines.stream()
        .map(line ->{
            String line_values[] = line.split(",");
            String user_id = line_values[1];
            Boolean is_canceled = Boolean.parseBoolean(line_values[4]);

            String res = "";
            if(!is_canceled)
            {
                res =  user_id;
            }
            return res;
        })
        .distinct()
        .filter(s -> !s.isEmpty())
        .collect(Collectors.toList()));
    }

    @Override
    public CompletableFuture<List<String>> getOrderIdsThatPurchased(String productId) {

        CompletableFuture<List<String>> order_line_list = get_orders_with_key_from_db(productId,"product",ordersDB);

        return order_line_list.thenApply(lines -> lines
                .stream()
                .map(line -> {
                    String arr[] = line.split(",");
                    return arr[0]; //order_id
                })
                .distinct()
                .collect(Collectors.toList()));
    }

    @Override
    public CompletableFuture<OptionalLong> getTotalNumberOfItemsPurchased(String productId) {
        CompletableFuture<List<Long>> res_list;
        CompletableFuture<Long> sum;

        CompletableFuture<List<String>> order_line_list = get_orders_with_key_from_db(productId,"product",ordersDB);

        res_list = order_line_list.thenCompose(lines ->
                {
                    List<CompletableFuture<Long>> temp_list =lines.stream()
                            .map(line -> {
                                String line_values[] = line.split(",");
                                String order_id = line_values[0];
                                String user_id = line_values[1];
                                String amount = line_values[2];
                                Boolean is_canceled = Boolean.parseBoolean(line_values[4]);
                                Boolean is_modified = Boolean.parseBoolean(line_values[3]);

                                CompletableFuture<Long> res = CompletableFuture.completedFuture(0L);
                                if (!is_canceled) {
                                    if (is_modified) {

                                        CompletableFuture<List<String>> mod_order_line_list = get_orders_with_key_from_db(order_id,"order",modified_ordersDB);

                                        res = mod_order_line_list.thenApply(mod_list -> Long.parseLong(mod_list.get(mod_list.size() - 1).split(",")[0]));
                                    } else {
                                        res = CompletableFuture.completedFuture(Long.parseLong(amount));
                                    }
                                }


                                return res;
                            })
                            .collect(Collectors.toList());

                    return CompletableFuture.allOf(temp_list.toArray(new CompletableFuture[temp_list.size()]))
                            .thenApply(v -> temp_list.stream()
                                    .map(CompletableFuture::join)
                                    .collect(Collectors.toList()));
                } );

        sum =res_list.thenApply(list -> list.stream().mapToLong(Long::longValue).sum());

        return sum.thenApply(val ->
        {
            if(val.equals(0L))
            {
                return OptionalLong.empty();
            }
            else
            {
                return OptionalLong.of(val);
            }
        });
    }

    @Override
    public CompletableFuture<OptionalDouble> getAverageNumberOfItemsPurchased(String productId) {

        CompletableFuture<OptionalLong> numberOfItemsPurchased = getTotalNumberOfItemsPurchased(productId);
        CompletableFuture<List<String>> order_ids = getOrderIdsThatPurchased(productId);
        CompletableFuture<Integer> numOfOrders = order_ids.thenApply(List::size);

        return numberOfItemsPurchased.thenCombine(numOfOrders, (purchased_amount,orders_num) -> {
            if(orders_num == 0)
            {
                return OptionalDouble.empty();
            }
            else
            {
               return OptionalDouble.of((double) (purchased_amount.getAsLong() / orders_num));
            }
        });

    }

    @Override
    public CompletableFuture<OptionalDouble> getCancelRatioForUser(String userId) {
        CompletableFuture<List<String>> order_line_list = get_orders_with_key_from_db(userId,"user",ordersDB);

        return order_line_list.thenApply(lines ->
        {
            if(lines.isEmpty())
            {
                return OptionalDouble.empty();
            }

            return OptionalDouble.of(lines.stream()
                    .mapToDouble(line -> {
                        String line_values[] = line.split(",");
                        Boolean is_canceled = Boolean.parseBoolean(line_values[4]);

                        Double res = 0d;
                        if (is_canceled) {
                            res = (double) 1 / lines.size();
                        }
                        return res;
                    })
                    .sum());
        });

    }

    @Override
    public CompletableFuture<OptionalDouble> getModifyRatioForUser(String userId) {

        CompletableFuture<List<String>> order_line_list = get_orders_with_key_from_db(userId,"user",ordersDB);

        return order_line_list.thenApply(lines ->
        {
            if(lines.isEmpty())
            {
                return OptionalDouble.empty();
            }

            return OptionalDouble.of(lines.stream()
                    .mapToDouble(line -> {
                        String line_values[] = line.split(",");
                        Boolean is_moded = Boolean.parseBoolean(line_values[3]);

                        Double res = 0d;
                        if (is_moded) {
                            res = (double) 1 / lines.size();
                        }
                        return res;
                    })
                    .sum());
        });
    }

    @Override
    public CompletableFuture<Map<String, Long>> getAllItemsPurchased(String userId) {

        CompletableFuture<List<String>> order_line_list = get_orders_with_key_from_db(userId,"user",ordersDB);

        return get_items_map_from_order_list(order_line_list);
    }

    @Override
    public CompletableFuture<Map<String, Long>> getItemsPurchasedByUsers(String productId) {

        CompletableFuture<List<String>> order_line_list = get_orders_with_key_from_db(productId, "product", ordersDB);

        return get_items_map_from_order_list(order_line_list);
    }
}
