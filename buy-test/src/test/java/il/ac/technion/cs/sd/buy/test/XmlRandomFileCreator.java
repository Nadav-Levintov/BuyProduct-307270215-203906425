package il.ac.technion.cs.sd.buy.test;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by benny on 10/06/2017.
 */
public class XmlRandomFileCreator {
    private String fileName;
    XmlRandomFileCreator(String fileName)
    {
        this.fileName = fileName;
    }

    public void createFile() throws IOException {
        List<String> lines = new ArrayList<>();
        lines.add("<Root>");

        RandomString randName = new RandomString(3);
        Integer amount;

        for(int i=0; i<1000000; i++)    //Order
        {
            lines.add("  <Order>");
            lines.add("    <user-id>"+randName.nextString()+"</user-id>");
            lines.add("    <order-id>"+randName.nextString()+"</order-id>");
            lines.add("    <product-id>"+randName.nextString()+"</product-id>");
            amount = ThreadLocalRandom.current().nextInt(1, 1000);
            lines.add("    <amount>"+amount+"</amount>");
            lines.add("  </Order>");
        }
        for(int i=0; i<100000; i++)    //Product
        {
            lines.add("  <Product>");
            lines.add("    <id>"+randName.nextString()+"</id>");
            amount = ThreadLocalRandom.current().nextInt(1, 1000);
            lines.add("    <price>"+amount+"</price>");
            lines.add("  </Product>");
        }

        for(int i=0; i<100000; i++)    //Modified
        {
            lines.add("  <ModifyOrder>");
            lines.add("    <order-id>"+randName.nextString()+"</order-id>");
            amount = ThreadLocalRandom.current().nextInt(1, 1000);
            lines.add("    <new-amount>"+amount+"</new-amount>");
            lines.add("  </ModifyOrder>");
        }
        for(int i=0; i<1000; i++)    //Cancel
        {
            lines.add("  <CancelOrder>");
            lines.add("    <order-id>"+randName.nextString()+"</order-id>");
            lines.add("  </CancelOrder>");
        }

        lines.add("</Root>");
        Path file = Paths.get(fileName);
        Files.write(file, lines, Charset.forName("UTF-8"));
    }

}
