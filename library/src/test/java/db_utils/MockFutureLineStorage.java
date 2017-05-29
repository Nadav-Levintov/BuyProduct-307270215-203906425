package db_utils;

import il.ac.technion.cs.sd.buy.ext.FutureLineStorage;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

/**
 * Created by Nadav on 19-May-17.
 */
public class MockFutureLineStorage implements FutureLineStorage {

    private List<String> mockedFile;
    private String mockedFileName;

    public MockFutureLineStorage(String mockedFileName) {
        this.mockedFile = new ArrayList<>();
        this.mockedFileName = mockedFileName;
    }

    @Override
    public CompletableFuture<Void> appendLine(String s) {
        mockedFile.add(s);
        return CompletableFuture.completedFuture(null);
    }

    @Override
    public CompletableFuture<String> read(int i) {
        try {
            TimeUnit.MILLISECONDS.sleep(mockedFile.get(i).length());
        } catch (InterruptedException e) {
            throw new RuntimeException();
        }
        return CompletableFuture.completedFuture(mockedFile.get(i));
    }

    @Override
    public CompletableFuture<Integer> numberOfLines() {
        try {
            TimeUnit.MILLISECONDS.sleep(100);
        } catch (InterruptedException e) {
            throw new RuntimeException();
        }
        return CompletableFuture.completedFuture(mockedFile.size());
    }

    public String getMockedFileName() {
        return mockedFileName;
    }
}
