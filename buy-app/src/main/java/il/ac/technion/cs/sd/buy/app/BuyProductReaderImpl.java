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

    @Override
    public CompletableFuture<Boolean> isValidOrderId(String orderId) throws ExecutionException, InterruptedException {
        return null;
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
