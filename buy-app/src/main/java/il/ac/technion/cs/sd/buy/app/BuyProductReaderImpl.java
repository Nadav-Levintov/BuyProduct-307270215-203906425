package il.ac.technion.cs.sd.buy.app;

import db_utils.DataBase;
import db_utils.DataBaseFactory;

import javax.inject.Inject;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

/**
 * Created by Nadav on 24-May-17.
 */
public class BuyProductReaderImpl implements BuyProductReader {
    private final CompletableFuture<DataBase> ordersDB;
    private final CompletableFuture<DataBase> productsDB;
    private final CompletableFuture<DataBase> modified_ordersDB;
    private final CompletableFuture<DataBase> canceled_ordersDB;


    @Inject
    public BuyProductReaderImpl(DataBaseFactory dataBaseFactory){
        Integer num_of_keys = 3;

        List<String> names_of_columns1 = new ArrayList<>();
        names_of_columns1.add("order");
        names_of_columns1.add("user");
        names_of_columns1.add("product");
        ordersDB = dataBaseFactory.setNames_of_columns(names_of_columns1)
                .setNum_of_keys(num_of_keys)
                .setDb_name("Orders")
                .build();

        num_of_keys = 1;
        List<String> names_of_columns2 = new ArrayList<>();
        names_of_columns2.add("product");
        names_of_columns2.add("price");
        productsDB = dataBaseFactory.setNames_of_columns(names_of_columns2)
                .setNum_of_keys(num_of_keys)
                .setDb_name("Products")
                .build();

        num_of_keys = 2;
        List<String> names_of_columns3 = new ArrayList<>();
        names_of_columns3.add("order");
        names_of_columns3.add("number");
        names_of_columns3.add("amount");
        modified_ordersDB = dataBaseFactory.setNames_of_columns(names_of_columns3)
                .setNum_of_keys(num_of_keys)
                .setDb_name("Modified")
                .build();

        num_of_keys = 1;
        List<String> names_of_columns4 = new ArrayList<>();
        names_of_columns4.add("order");
        canceled_ordersDB = dataBaseFactory.setNames_of_columns(names_of_columns4)
                .setNum_of_keys(num_of_keys)
                .setDb_name("Canceled")
                .build();

    }

    @Override
    public CompletableFuture<Boolean> isValidOrderId(String orderId) throws ExecutionException, InterruptedException {
        CompletableFuture<Boolean> isValid = new CompletableFuture<>();
        List<String> keys_names= new ArrayList<>();
        keys_names.add("order");

        List<String> keys= new ArrayList<>();
        keys.add(orderId);
        if((ordersDB.get()).get_lines_for_keys(keys_names,keys).get().isEmpty())
        {
            isValid.complete(false);
        } else
        {
            isValid.complete(false);
        }
        return isValid;
    }

    @Override
    public CompletableFuture<Boolean> isCanceledOrder(String orderId) throws ExecutionException, InterruptedException {
        return null;
    }

    @Override
    public CompletableFuture<Boolean> isModifiedOrder(String orderId) {
        return null;
    }

    @Override
    public CompletableFuture<OptionalInt> getNumberOfProductOrdered(String orderId) {
        return null;
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
