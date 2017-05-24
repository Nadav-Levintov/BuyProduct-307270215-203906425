package db_utils;

import il.ac.technion.cs.sd.grades.ext.LineStorage;

/**
 * Created by Nadav on 22-Apr-17.
 */
public interface StorageInterface {
    public default void appendLine(String s) {
        LineStorage.appendLine(s);
    }

    public default String read(int lineNumber) throws InterruptedException {
        return LineStorage.read(lineNumber);
    }

    public default int numberOfLines() throws InterruptedException {
        return LineStorage.numberOfLines();
    }
}


