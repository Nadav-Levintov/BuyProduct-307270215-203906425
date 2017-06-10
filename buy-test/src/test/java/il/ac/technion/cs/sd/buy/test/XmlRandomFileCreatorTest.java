package il.ac.technion.cs.sd.buy.test;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by benny on 10/06/2017.
 */
public class XmlRandomFileCreatorTest {
    @Test
    public void createFile() throws Exception {
        XmlRandomFileCreator file = new XmlRandomFileCreator("bb");   //choose name for file
        file.createFile();

    }

}