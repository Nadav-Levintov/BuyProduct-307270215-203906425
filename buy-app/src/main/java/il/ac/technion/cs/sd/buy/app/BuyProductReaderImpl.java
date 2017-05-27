package il.ac.technion.cs.sd.buy.app;

import com.google.inject.Inject;
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

        this.ordersDB = dataBaseFactoryCompletableFuture
                .thenApply(dbf-> dbf.setDb_name("Orders"))
                .thenApply(dbf -> dbf.setNames_of_columns(names_of_columns_OrdersDB))
                .thenApply(dbf -> dbf.setNum_of_keys(num_of_keys_ordersDB))
                .thenCompose(dbf -> dbf.build());


        Integer num_of_keys_productsDB = 1;
        List<String> names_of_columns_productsDB = new ArrayList<>();
        names_of_columns_productsDB.add("product");
        names_of_columns_productsDB.add("price");
        this.productsDB = dataBaseFactoryCompletableFuture
                .thenApply(dbf-> dbf.setDb_name("Products"))
                .thenApply(dbf -> dbf.setNames_of_columns(names_of_columns_productsDB))
                .thenApply(dbf -> dbf.setNum_of_keys(num_of_keys_productsDB))
                .thenCompose(dbf -> dbf.build());

        Integer num_of_keys_modified_ordersDB = 2;
        List<String> names_of_columns_modified_ordersDB = new ArrayList<>();
        names_of_columns_modified_ordersDB.add("order");
        names_of_columns_modified_ordersDB.add("number");
        names_of_columns_modified_ordersDB.add("amount");
        this.modified_ordersDB = dataBaseFactoryCompletableFuture
                .thenApply(dbf-> dbf.setDb_name("Modified"))
                .thenApply(dbf -> dbf.setNames_of_columns(names_of_columns_modified_ordersDB))
                .thenApply(dbf -> dbf.setNum_of_keys(num_of_keys_modified_ordersDB))
                .thenCompose(dbf -> dbf.build());

        Integer num_of_keys_canceled_ordersDB = 1;
        List<String> names_of_columns_canceled_ordersDB = new ArrayList<>();
        names_of_columns_canceled_ordersDB.add("order");
        this.canceled_ordersDB = dataBaseFactoryCompletableFuture
                .thenApply(dbf-> dbf.setDb_name("Canceled"))
                .thenApply(dbf -> dbf.setNames_of_columns(names_of_columns_canceled_ordersDB))
                .thenApply(dbf -> dbf.setNum_of_keys(num_of_keys_canceled_ordersDB))
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

        CompletableFuture<List<String>> canceld_line_list = canceled_ordersDB.thenCompose(orders -> orders
                .get_lines_for_keys(names_of_keys,keys));
        CompletableFuture<Boolean> is_order_canceld = canceld_line_list.thenApply(lines -> lines.isEmpty());

        CompletableFuture<List<String>> mod_line_list = modified_ordersDB.thenCompose(orders -> orders
                .get_lines_for_keys(names_of_keys,keys));


        try {
                order_line_list.thenApply(lines -> lines
                        .stream()
                        .map(line -> res_list
                                .thenApply(list -> list
                                        .add(Integer.parseInt(line.split(",")[2])))));

                mod_line_list.thenApply(lines -> lines
                        .stream()
                        .map(line -> res_list
                                .thenApply(list -> list
                                        .add(Integer.parseInt(line.split(",")[1])))));


                if(is_order_canceld.get())
                {
                    res_list.thenApply(list -> list.add(-1));
                }
        } catch (InterruptedException e) {
            throw new RuntimeException();
        } catch (ExecutionException e) {
            throw new RuntimeException();
        }
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

        CompletableFuture<List<String>> orderIds =  getOrderIdsForUser(userId);

        //TODO: need to under how to work with CompletableFuture<List<String>>

    return res;
    }

    @Override
    public CompletableFuture<List<String>> getUsersThatPurchased(String productId) {
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

        List<CompletableFuture<String>> orders = order_line_list.thenCompose(lines -> lines
                .stream()
                .map(line -> line).collect(Collectors.toList()));

        res_list = order_line_list.thenCompose(lines ->
                lines
                .stream()
                .map(line -> {
                    String line_values[] = line.split(",");

                    return isCanceledOrder(line_values[0]).thenApply(canceled -> //order_id
                    {
                        String ret_val = new String();

                        if(canceled)
                        {
                            ret_val =  line_values[1];//user-id
                        }

                        return ret_val;
                    });
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
