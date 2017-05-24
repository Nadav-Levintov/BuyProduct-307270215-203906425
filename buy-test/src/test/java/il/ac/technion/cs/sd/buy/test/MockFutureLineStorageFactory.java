package il.ac.technion.cs.sd.buy.test;

import il.ac.technion.cs.sd.buy.ext.FutureLineStorage;
import il.ac.technion.cs.sd.buy.ext.FutureLineStorageFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * Created by Nadav on 19-May-17.
 */
public class MockFutureLineStorageFactory implements FutureLineStorageFactory {
    private List<MockFutureLineStorage> files = new ArrayList<>();
    @Override
    public CompletableFuture<FutureLineStorage> open(String s) throws IndexOutOfBoundsException {

        List<MockFutureLineStorage> file_list = files.stream()
                .filter( file -> file.getMockedFileName().equals(s))
                .collect(Collectors.toList());
        MockFutureLineStorage file;
        if(file_list.size() == 0)
        {
            file = new MockFutureLineStorage(s);
            files.add(file);
        }
        else
        {
            file = file_list.get(0);
        }

        try {
            TimeUnit.MILLISECONDS.sleep(files.size()*100);
        } catch (InterruptedException e) {
            throw new RuntimeException();
        }

        return CompletableFuture.completedFuture(file);
    }
}
