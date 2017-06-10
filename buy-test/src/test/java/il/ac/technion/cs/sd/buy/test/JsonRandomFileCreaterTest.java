package il.ac.technion.cs.sd.buy.test;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by benny on 09/06/2017.
 */
public class JsonRandomFileCreaterTest {
    @Test
    public void createFile() throws Exception {
        JsonRandomFileCreator file = new JsonRandomFileCreator("aa");   //choose name for file
        file.createFile();

    }

}