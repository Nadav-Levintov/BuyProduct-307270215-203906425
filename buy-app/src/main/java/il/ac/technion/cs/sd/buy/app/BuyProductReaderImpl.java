package il.ac.technion.cs.sd.buy.app;

import com.google.inject.Inject;
import com.google.inject.internal.cglib.core.$CollectionUtils;
import db_utils.DataBase;
import db_utils.DataBaseFactory;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

/**
 * Created by Nadav on 24-May-17.
 */
public class BuyProductReaderImpl implements BuyProductReader {

    private final CompletableFuture<DataBase> ordersDB;
    private final CompletableFuture<DataBase> productsDB;
    private final CompletableFuture<DataBase> modified_ordersDB;
    private final CompletableFuture<DataBase> canceled_ordersDB;

    @Inject
    public BuyProductReaderImpl(CompletableFuture<DataBaseFactory> dataBaseFactoryCompletableFuture) {

        Integer num_of_keys_ordersDB = new Integer(3);

        List<String> names_of_columns_OrdersDB = new ArrayList<>();
        names_of_columns_OrdersDB.add("order");
        names_of_columns_OrdersDB.add("user");
        names_of_columns_OrdersDB.add("product");
        names_of_columns_OrdersDB.add("amount");
        names_of_columns_OrdersDB.add("modified");
        names_of_columns_OrdersDB.add("canceled");

        this.ordersDB = dataBaseFactoryCompletableFuture
                .thenApply(dbf-> dbf.setDb_name("Orders"))
                .thenApply(dbf -> dbf.setNames_of_columns(names_of_columns_OrdersDB))
                .thenApply(dbf -> dbf.setNum_of_keys(num_of_keys_ordersDB))
                .thenApply(dbf -> dbf.setAllow_Multiples(Boolean.FALSE))
                .thenCompose(dbf -> dbf.build());


        Integer num_of_keys_productsDB = 1;
        List<String> names_of_columns_productsDB = new ArrayList<>();
        names_of_columns_productsDB.add("product");
        names_of_columns_productsDB.add("price");
        this.productsDB = dataBaseFactoryCompletableFuture
                .thenApply(dbf-> dbf.setDb_name("Products"))
                .thenApply(dbf -> dbf.setNames_of_columns(names_of_columns_productsDB))
                .thenApply(dbf -> dbf.setNum_of_keys(num_of_keys_productsDB))
                .thenApply(dbf -> dbf.setAllow_Multiples(Boolean.FALSE))
                .thenCompose(dbf -> dbf.build());

        Integer num_of_keys_modified_ordersDB = 1;
        List<String> names_of_columns_modified_ordersDB = new ArrayList<>();
        names_of_columns_modified_ordersDB.add("order");
        names_of_columns_modified_ordersDB.add("amount");
        this.modified_ordersDB = dataBaseFactoryCompletableFuture
                .thenApply(dbf-> dbf.setDb_name("Modified"))
                .thenApply(dbf -> dbf.setNames_of_columns(names_of_columns_modified_ordersDB))
                .thenApply(dbf -> dbf.setNum_of_keys(num_of_keys_modified_ordersDB))
                .thenApply(dbf -> dbf.setAllow_Multiples(Boolean.TRUE))
                .thenCompose(dbf -> dbf.build());

        Integer num_of_keys_canceled_ordersDB = 1;
        List<String> names_of_columns_canceled_ordersDB = new ArrayList<>();
        names_of_columns_canceled_ordersDB.add("order");
        this.canceled_ordersDB = dataBaseFactoryCompletableFuture
                .thenApply(dbf-> dbf.setDb_name("Canceled"))
                .thenApply(dbf -> dbf.setNames_of_columns(names_of_columns_canceled_ordersDB))
                .thenApply(dbf -> dbf.setNum_of_keys(num_of_keys_canceled_ordersDB))
                .thenApply(dbf -> dbf.setAllow_Multiples(Boolean.FALSE))
                .thenCompose(dbf -> dbf.build());

    }

    @Override
    public CompletableFuture<Boolean> isValidOrderId(String orderId) {
        List<String> names_of_keys = new ArrayList<>();
        names_of_keys.add("order");
        List<String> keys = new ArrayList<>();
        keys.add(orderId);


        CompletableFuture<List<String>> line_list = ordersDB.thenCompose(orders -> orders
                .get_lines_for_keys(names_of_keys,keys));
        CompletableFuture<Boolean> res = line_list.thenApply(lines -> lines.isEmpty());

        return res.thenApply(r -> !r);
    }

    @Override
    public CompletableFuture<Boolean> isCanceledOrder(String orderId) {
        List<String> names_of_keys = new ArrayList<>();
        names_of_keys.add("order");
        List<String> keys = new ArrayList<>();
        keys.add(orderId);


        CompletableFuture<List<String>> line_list = canceled_ordersDB.thenCompose(orders -> orders
                .get_lines_for_keys(names_of_keys,keys));
        CompletableFuture<Boolean> res = line_list.thenApply(lines -> lines.isEmpty());

        return res.thenApply(r -> !r);
    }

    @Override
    public CompletableFuture<Boolean> isModifiedOrder(String orderId) {
        List<String> names_of_keys = new ArrayList<>();
        names_of_keys.add("order");
        List<String> keys = new ArrayList<>();
        keys.add(orderId);


        CompletableFuture<List<String>> line_list = modified_ordersDB.thenCompose(orders -> orders
                .get_lines_for_keys(names_of_keys,keys));
        CompletableFuture<Boolean> res = line_list.thenApply(lines -> lines.isEmpty());

        return res.thenApply(r -> !r);
    }

    @Override
    public CompletableFuture<OptionalInt> getNumberOfProductOrdered(String orderId) {

        CompletableFuture<Boolean> valid = isValidOrderId(orderId);
        CompletableFuture<Boolean> modified = isModifiedOrder(orderId);
        CompletableFuture<Boolean> canceled = isCanceledOrder(orderId);

        List<String> names_of_keys = new ArrayList<>();
        names_of_keys.add("order");
        List<String> keys = new ArrayList<>();
        keys.add(orderId);

        CompletableFuture<List<String>> order_line_list = ordersDB.thenCompose(orders -> orders
                .get_lines_for_keys(names_of_keys,keys));

        CompletableFuture<List<String>> mod_line_list = modified_ordersDB.thenCompose(orders -> orders
                .get_lines_for_keys(names_of_keys,keys));

        CompletableFuture<Integer> res;
        CompletableFuture<String> line;

        //TODO: can we use compose to eliminate the "get()"?

        try {
            if(valid.get())
            {
                if(modified.get())
                {
                    line = mod_line_list.thenApply(lines -> lines.get(lines.size()-1));
                    res = line.thenApply(l -> Integer.parseInt(l.split(",")[1]));
                }
                else
                {
                    line = order_line_list.thenApply(lines -> lines.get(lines.size()-1));
                    res = line.thenApply(l -> Integer.parseInt(l.split(",")[2]));
                }

                if(canceled.get())
                {
                    res = res.thenApply(r -> r*(-1));
                }

                return res.thenApply(r -> OptionalInt.of(r));
            }
        } catch (InterruptedException e) {
            throw new RuntimeException();
        } catch (ExecutionException e) {
            throw new RuntimeException();
        }


        return CompletableFuture.completedFuture(OptionalInt.empty());
    }

    @Override
    public CompletableFuture<List<Integer>> getHistoryOfOrder(String orderId) {
        CompletableFuture<List<Integer>> res_list = CompletableFuture.completedFuture(new ArrayList<>());

        List<String> names_of_keys = new ArrayList<>();
        names_of_keys.add("order");
        List<String> keys = new ArrayList<>();
        keys.add(orderId);

        CompletableFuture<List<String>> order_line_list = ordersDB.thenCompose(orders -> orders
                .get_lines_for_keys(names_of_keys,keys));

        CompletableFuture<List<String>> canceled_line_list = canceled_ordersDB.thenCompose(orders -> orders
                .get_lines_for_keys(names_of_keys,keys));

        CompletableFuture<List<String>> mod_line_list = modified_ordersDB.thenCompose(orders -> orders
                .get_lines_for_keys(names_of_keys,keys));


        order_line_list.thenApply(lines -> lines
                .stream()
                .map(line -> res_list
                        .thenApply(list -> list
                                .add(Integer.parseInt(line.split(",")[2]))))); // amount

        mod_line_list.thenApply(lines -> lines
                .stream()
                .map(line -> res_list
                        .thenApply(list -> list
                                .add(Integer.parseInt(line.split(",")[1]))))); //amount

        order_line_list.thenApply(lines -> lines
                .stream()
                .map(line -> res_list
                        .thenApply(list ->
                        {
                            Boolean is_order_canceld =Boolean.parseBoolean(line.split(",")[4]);
                            if(is_order_canceld) {
                                list.add(-1);
                            }
                            return list;
                        })));

        return res_list;
    }

    @Override
    public CompletableFuture<List<String>> getOrderIdsForUser(String userId) {
        CompletableFuture<List<String>> res_list = CompletableFuture.completedFuture(new ArrayList<>());

        List<String> names_of_keys = new ArrayList<>();
        names_of_keys.add("user");
        List<String> keys = new ArrayList<>();
        keys.add(userId);

        CompletableFuture<List<String>> order_line_list = ordersDB.thenCompose(orders -> orders
                .get_lines_for_keys(names_of_keys,keys));

        res_list = order_line_list.thenApply(lines -> lines
                .stream()
                .map(line -> line.split(",")[0])
                .sorted()
                .collect(Collectors.toList()));
        return res_list;
    }

    @Override
    public CompletableFuture<Long> getTotalAmountSpentByUser(String userId) {

        CompletableFuture<Long> res = CompletableFuture.completedFuture(new Long(0));

        List<String> names_of_keys = new ArrayList<>();
        names_of_keys.add("user");
        List<String> keys = new ArrayList<>();
        keys.add(userId);

        CompletableFuture<List<String>> future_orders_list =  ordersDB.thenCompose(orders -> orders
                .get_lines_for_keys(names_of_keys,keys));


        CompletableFuture<List<Integer>> transactions_prices = future_orders_list.thenCompose(orders_list ->
        {
            CompletableFuture<List<Integer>> result = new CompletableFuture<>();
            List<CompletableFuture<Integer>> priceList = new ArrayList<>();
            for (String order_string: orders_list)
            {
                String line_values[] = order_string.split(",");
                String order_id = line_values[0];
                String product_id = line_values[1];
                String order_amount = line_values[2];
                Boolean is_modified = Boolean.parseBoolean(line_values[3]);
                Boolean is_canceled = Boolean.parseBoolean(line_values[4]);

                CompletableFuture<Integer> price = productsDB.thenCompose(products ->
                        products.get_val_from_column_by_name(new ArrayList<String>(Arrays.asList(product_id)),"price"))
                        .thenApply(price_optional ->
                                Integer.parseInt(price_optional.get()));

                CompletableFuture<Integer> amount = CompletableFuture.completedFuture(Integer.parseInt(order_amount));

                if(!is_canceled)
                {
                    if(is_modified)
                    {
                        amount= modified_ordersDB.thenCompose(modified_orders ->
                                modified_orders.get_lines_for_keys(new ArrayList<String>(Arrays.asList("order")),
                                        new ArrayList<String>(Arrays.asList(order_id)))).thenApply(modified_lines ->
                                modified_lines.get(modified_lines.size()-1).split(",")[0]).thenApply( amount_str ->
                                Integer.parseInt(amount_str));
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

  /*      CompletableFuture<List<Integer>> transactions_prices = future_orders_list.thenCompose(orders_list ->
        {
            return orders_list.stream()
                    .map(order_string ->
                    {
                        String line_values[] = order_string.split(",");

                        String order_id = line_values[0];
                        String product_id = line_values[1];
                        String order_amount = line_values[2];
                        Boolean is_modified = Boolean.parseBoolean(line_values[3]);
                        Boolean is_canceled = Boolean.parseBoolean(line_values[4]);

                        CompletableFuture<Integer> price = productsDB.thenCompose(products ->
                               products.get_val_from_column_by_name(new ArrayList<String>(Arrays.asList(product_id)),"price"))
                                .thenApply(price_optional ->
                                Integer.parseInt(price_optional.get()));

                        CompletableFuture<Integer> amount = CompletableFuture.completedFuture(Integer.parseInt(order_amount));

                        if(!is_canceled)
                        {
                            if(is_modified)
                            {
                                amount= modified_ordersDB.thenCompose(modified_orders ->
                                modified_orders.get_lines_for_keys(new ArrayList<String>(Arrays.asList("order")),
                                        new ArrayList<String>(Arrays.asList(order_id)))).thenApply(modified_lines ->
                                modified_lines.get(modified_lines.size()-1).split(",")[0]).thenApply( amount_str ->
                                        Integer.parseInt(amount_str));
                            }
                        }

                        return price.thenCombine(amount,(price_val, amount_val) -> price_val*amount_val);
                    }).collect(Collectors.toList());
        });
*/

    return res;
    }

    @Override
    public CompletableFuture<List<String>> getUsersThatPurchased(String productId) {
        CompletableFuture<List<String>> res_list = CompletableFuture.completedFuture(new ArrayList<>());

        List<String> names_of_keys = new ArrayList<>();
        names_of_keys.add("product");
        List<String> keys = new ArrayList<>();
        keys.add(productId);

        CompletableFuture<List<String>> order_line_list = ordersDB.thenCompose(orders -> orders
                .get_lines_for_keys(names_of_keys,keys));

        res_list = order_line_list.thenApply(lines -> lines.stream()
        .map(line ->{
            String line_values[] = line.split(",");
            String user_id = line_values[1];
            Boolean is_canceled = Boolean.parseBoolean(line_values[4]);

            String res = new String();
            if(!is_canceled)
            {
                res =  user_id;
            }
            return res;
        })
        .distinct()
        .filter(s -> !s.isEmpty())
        .collect(Collectors.toList()));

        return res_list;
    }

    @Override
    public CompletableFuture<List<String>> getOrderIdsThatPurchased(String productId) {
        CompletableFuture<List<String>> res_list = CompletableFuture.completedFuture(new ArrayList<>());
        CompletableFuture<List<String>> order_id_list = CompletableFuture.completedFuture(new ArrayList<>());

        List<String> names_of_keys = new ArrayList<>();
        names_of_keys.add("product");
        List<String> keys = new ArrayList<>();
        keys.add(productId);

        CompletableFuture<List<String>> order_line_list = ordersDB.thenCompose(orders -> orders
                .get_lines_for_keys(names_of_keys,keys));

        order_id_list = order_line_list.thenApply(lines -> lines
                .stream()
                .map(line -> line.split(",")[0])
                .distinct()
                .collect(Collectors.toList()));


        res_list = order_line_list.thenApply(lines -> lines
                .stream()
                .map(line -> {
                    String arr[] = line.split(",");
                    return arr[0];
                })
                .distinct()
                .collect(Collectors.toList()));

        return res_list;
    }

    @Override
    public CompletableFuture<OptionalLong> getTotalNumberOfItemsPurchased(String productId) {
        return null;
    }

    @Override
    public CompletableFuture<OptionalDouble> getAverageNumberOfItemsPurchased(String productId) {
        return null;
    }

    @Override
    public CompletableFuture<OptionalDouble> getCancelRatioForUser(String userId) {
        return null;
    }

    @Override
    public CompletableFuture<OptionalDouble> getModifyRatioForUser(String userId) {
        return null;
    }

    @Override
    public CompletableFuture<Map<String, Long>> getAllItemsPurchased(String userId) {
        return null;
    }

    @Override
    public CompletableFuture<Map<String, Long>> getItemsPurchasedByUsers(String productId) {
        return null;
    }
}
