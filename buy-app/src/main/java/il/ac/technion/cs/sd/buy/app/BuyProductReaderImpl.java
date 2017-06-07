package il.ac.technion.cs.sd.buy.app;

import com.google.inject.Inject;
import db_utils.DataBase;
import db_utils.DataBaseElement;
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
    private String getUser(DataBaseElement order) {
        return order.get("user");
    }

    private Integer getAmount(DataBaseElement order) {
        return Integer.parseInt(order.get("amount"));
    }

    private String getProduct(DataBaseElement order) {
        return order.get("product");
    }

    private boolean isModified(DataBaseElement order) {
        return !order.get("modified").equals("0");
    }

    private Boolean isCanceled(DataBaseElement order) {
        return order.get("canceled").equals("1");
    }

    private CompletableFuture<Boolean> does_db_contains_order_id(String orderId, DataBase db) {
        CompletableFuture<List<DataBaseElement>> line_list = db.get_lines_for_single_key(orderId,"order");

        return line_list.thenApply(List::isEmpty).thenApply(r -> !r);
    }

    private String getOrderId(DataBaseElement order) {
        return order.get("order");
    }

    private CompletableFuture<Map<String, Long>> get_items_map_from_order_list(CompletableFuture<List<DataBaseElement>> order_line_list) {
        return order_line_list.thenApply(lines ->
                lines.stream()
                        .map(order -> {

                            String order_id = getOrderId(order);
                            String product_id = getProduct(order);
                            CompletableFuture<Long> curr_amount = CompletableFuture.completedFuture(getAmount(order).longValue());
                            Boolean is_moded = isModified(order);
                            Boolean is_canceled = isCanceled(order);


                            if (!is_canceled) {
                                if(is_moded)
                                {
                                    CompletableFuture<List<DataBaseElement>> moded_line_list = modified_ordersDB.get_lines_for_single_key(order_id,"order");

                                    curr_amount = moded_line_list.thenApply(mod_list -> getAmount(mod_list.get(mod_list.size() - 1)).longValue());

                                }
                            }
                            return new Pair<>(CompletableFuture.completedFuture(product_id), curr_amount);
                        })
                        .collect(Collectors.toList())).thenCompose(list ->
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

        CompletableFuture<List<DataBaseElement>> line_list = ordersDB.get_lines_for_single_key(orderId, "order");

        return line_list.thenApply(list ->
                {
                     if(list.isEmpty())
                    {
                        return Boolean.FALSE;
                    }
                    return isCanceled(list.get(0));
                }
        );
    }

    @Override
    public CompletableFuture<Boolean> isModifiedOrder(String orderId) {
        return does_db_contains_order_id(orderId,modified_ordersDB);
    }

    @Override
    public CompletableFuture<OptionalInt> getNumberOfProductOrdered(String orderId) {

        CompletableFuture<List<DataBaseElement>> order_line_list = ordersDB.get_lines_for_single_key(orderId, "order");

        CompletableFuture<List<DataBaseElement>> mod_line_list = modified_ordersDB.get_lines_for_single_key(orderId, "order");

        return order_line_list.thenCombine(mod_line_list,(order_list,mod_order_list) ->
        {
            if(order_list.isEmpty())
            {
                return OptionalInt.empty();
            }

            DataBaseElement order = order_list.get(0);
            Boolean is_mod = isModified(order);
            Boolean is_canceled = isCanceled(order);
            Integer amount = getAmount(order);

            if(is_mod)
            {
                amount = getAmount(mod_order_list.get(mod_order_list.size()-1));
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

        CompletableFuture<List<DataBaseElement>> order_line_list = ordersDB.get_lines_for_single_key(orderId, "order");

        CompletableFuture<List<DataBaseElement>> mod_line_list = modified_ordersDB.get_lines_for_single_key(orderId, "order");

        CompletableFuture<List<Integer>>  res_list = order_line_list.thenApply(lines ->
                lines.stream()
                .map(this::getAmount)
                .collect(Collectors.toList()));

        res_list = res_list.thenCombine(mod_line_list,(list,lines) -> {
            list.addAll(
                    lines.stream()
                            .map(this::getAmount)//amount from modified orders table
                            .collect(Collectors.toList()));
            return list;
        }).thenApply(i -> i);

        res_list = res_list.thenCombine(order_line_list,(list,lines) -> {
            if(lines.size()!=0)
            {
                Boolean is_order_canceled = isCanceled(lines.get(0));
                if (is_order_canceled) {
                    list.add(-1);
                }
            }
            return list;
        }).thenApply(i->i);

        return res_list;
    }

    @Override
    public CompletableFuture<List<String>> getOrderIdsForUser(String userId) {

        CompletableFuture<List<DataBaseElement>> order_line_list = ordersDB.get_lines_for_single_key(userId,"user");

        return order_line_list.thenApply(lines -> lines
                .stream()
                .map(this::getOrderId)
                .sorted()
                .collect(Collectors.toList()));

    }

    @Override
    public CompletableFuture<Long> getTotalAmountSpentByUser(String userId) {

        CompletableFuture<List<DataBaseElement>> future_orders_list =  ordersDB.get_lines_for_single_key(userId,"user");


        CompletableFuture<List<Integer>> transactions_prices = future_orders_list.thenCompose(orders_list ->
        {
            List<CompletableFuture<Integer>> priceList = new ArrayList<>();
            for (DataBaseElement order: orders_list)
            {
                String order_id = getOrderId(order);
                String product_id = getProduct(order);
                Integer order_amount = getAmount(order);
                Boolean is_modified = isModified(order);
                Boolean is_canceled = isCanceled(order);

                CompletableFuture<Integer> price = productsDB.get_val_from_column_by_name(new ArrayList<String>(Arrays.asList(product_id)),"price")
                        .thenApply(price_optional ->
                                Integer.parseInt(price_optional.get()));

                CompletableFuture<Integer> amount = CompletableFuture.completedFuture(order_amount);
                if(!is_canceled)
                {
                    if(is_modified)
                    {
                        amount= modified_ordersDB.get_lines_for_single_key(order_id,"order").thenApply(modified_lines ->
                                modified_lines.get(modified_lines.size()-1).get("amount")).thenApply(Integer::parseInt);
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
        CompletableFuture<List<DataBaseElement>> order_line_list = ordersDB.get_lines_for_single_key(productId,"product");

        return order_line_list.thenApply(lines -> lines.stream()
        .map(order ->{
            String user_id = getUser(order);
            Boolean is_canceled = isCanceled(order);

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

        CompletableFuture<List<DataBaseElement>> order_line_list = ordersDB.get_lines_for_single_key(productId,"product");

        //order_id
        return order_line_list.thenApply(lines -> lines
                .stream()
                .map(this::getOrderId)
                .distinct()
                .collect(Collectors.toList()));
    }



    @Override
    public CompletableFuture<OptionalLong> getTotalNumberOfItemsPurchased(String productId) {
        CompletableFuture<List<Long>> res_list;
        CompletableFuture<Long> sum;

        CompletableFuture<List<DataBaseElement>> order_line_list = ordersDB.get_lines_for_single_key(productId,"product");

        res_list = order_line_list.thenCompose(lines ->
                {
                    List<CompletableFuture<Long>> temp_list =lines.stream()
                            .map(order -> {
                                String order_id = getOrderId(order);
                                Integer order_amount = getAmount(order);
                                Boolean is_modified = isModified(order);
                                Boolean is_canceled = isCanceled(order);

                                CompletableFuture<Long> res = CompletableFuture.completedFuture(0L);
                                if (!is_canceled) {
                                    if (is_modified) {

                                        CompletableFuture<List<DataBaseElement>> mod_order_line_list = modified_ordersDB.get_lines_for_single_key(order_id,"order");

                                        res = mod_order_line_list.thenApply(mod_list -> Long.parseLong(mod_list.get(mod_list.size() - 1).get("amount")));
                                    } else {
                                        res = CompletableFuture.completedFuture(order_amount.longValue());
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
        CompletableFuture<List<DataBaseElement>> order_list = ordersDB.get_lines_for_single_key(productId,"product");


        return numberOfItemsPurchased.thenCombine(order_list, (purchased_amount,list) -> {
            Integer orders_num =0;
            for (DataBaseElement aList : list) {
                if (isCanceled(aList))
                    orders_num++;
            }
            if(orders_num == 0)
            {
                return OptionalDouble.empty();
            }
            else
            {
               return OptionalDouble.of((double) (purchased_amount.getAsLong() / (double)orders_num));
            }
        });

    }

    @Override
    public CompletableFuture<OptionalDouble> getCancelRatioForUser(String userId) {
        CompletableFuture<List<DataBaseElement>> order_line_list = ordersDB.get_lines_for_single_key(userId,"user");

        return order_line_list.thenApply(lines ->
        {
            if(lines.isEmpty())
            {
                return OptionalDouble.empty();
            }

            return OptionalDouble.of(lines.stream()
                    .mapToDouble(order -> {
                        Boolean is_canceled = isCanceled(order);

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

        CompletableFuture<List<DataBaseElement>> order_line_list = ordersDB.get_lines_for_single_key(userId,"user");

        return order_line_list.thenApply(lines ->
        {
            if(lines.isEmpty())
            {
                return OptionalDouble.empty();
            }

            return OptionalDouble.of(lines.stream()
                    .mapToDouble(order -> {
                        Boolean is_moded = isModified(order);

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

        CompletableFuture<List<DataBaseElement>> order_line_list = ordersDB.get_lines_for_single_key(userId,"user");
        return get_items_map_from_order_list(order_line_list);
    }

    @Override
    public CompletableFuture<Map<String, Long>> getItemsPurchasedByUsers(String productId) {

        CompletableFuture<List<DataBaseElement>> order_line_list = ordersDB.get_lines_for_single_key(productId, "product");

        return get_items_map_from_order_list(order_line_list);
    }
}
