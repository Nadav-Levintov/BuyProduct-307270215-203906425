package il.ac.technion.cs.sd.buy.app;

import com.google.inject.Inject;
import db_utils.DataBase;
import db_utils.DataBaseFactory;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

/**
 * Created by Nadav on 24-May-17.
 */
public class BuyProductReaderImpl implements BuyProductReader {

    private CompletableFuture<DataBase> ordersDB;
    private CompletableFuture<DataBase> productsDB;
    private CompletableFuture<DataBase> modified_ordersDB;
    private CompletableFuture<DataBase> canceled_ordersDB;

    @Inject
    public BuyProductReaderImpl(CompletableFuture<DataBaseFactory> dataBaseFactoryCompletableFuture) {


        Integer num_of_keys_ordersDB = new Integer(3);

        List<String> names_of_columns_OrdersDB = new ArrayList<>();
        names_of_columns_OrdersDB.add("order");
        names_of_columns_OrdersDB.add("user");
        names_of_columns_OrdersDB.add("product");

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
    public CompletableFuture<List<Integer>> getHistoryOfOrder(String orderId) {
        return null;
    }

    @Override
    public CompletableFuture<List<String>> getOrderIdsForUser(String userId) {
        return null;
    }

    @Override
    public CompletableFuture<Long> getTotalAmountSpentByUser(String userId) {
        return null;
    }

    @Override
    public CompletableFuture<List<String>> getUsersThatPurchased(String productId) {
        return null;
    }

    @Override
    public CompletableFuture<List<String>> getOrderIdsThatPurchased(String productId) {
        return null;
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
