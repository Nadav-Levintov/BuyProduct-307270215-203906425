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
 * Created by benny on 07/06/2017.
 */
final class JsonRandomFileCreator {

    private String fileName;
    JsonRandomFileCreator(String fileName)
    {
        this.fileName = fileName;
    }

    public void createFile() throws IOException {
        List<String> lines = new ArrayList<>();
        lines.add("[");

        StringBuilder lineBuild = new StringBuilder();
        String line;
        RandomString randName = new RandomString(4);
        Integer amount;
        for(int i=0; i<1000000; i++)
        {
            lineBuild.delete(0,lineBuild.length());
            lineBuild.append("  {").append("\"type\": ").append("\"order\"");
            lineBuild.append(", \"order-id\": \"").append(randName.nextString());
            lineBuild.append("\", \"user-id\": \"").append(randName.nextString());
            lineBuild.append("\", \"product-id\": \"").append(randName.nextString());
            amount = ThreadLocalRandom.current().nextInt(1, 1000);
            lineBuild.append("\", \"amount\": \"").append(amount).append("\"},");
            lines.add(lineBuild.toString());

        }
        for(int i=0; i<100000; i++)
        {
            lineBuild.delete(0,lineBuild.length());
            lineBuild.append("  {").append("\"type\": ").append("\"product\"");
            lineBuild.append(", \"id\": \"").append(randName.nextString());
            amount = ThreadLocalRandom.current().nextInt(1, 1000);
            lineBuild.append("\", \"price\": \"").append(amount).append("\"},");
            lines.add(lineBuild.toString());
        }
        for(int i=0; i<100000; i++)
        {
            lineBuild.delete(0,lineBuild.length());
            lineBuild.append("  {").append("\"type\": ").append("\"modify-order\"");
            lineBuild.append(", \"order-id\": \"").append(randName.nextString());
            amount = ThreadLocalRandom.current().nextInt(1, 1000);
            lineBuild.append(",\", \"amount\": \"").append(amount).append("\"},");
            lines.add(lineBuild.toString());
        }

        for(int i=0; i<100000; i++)
        {
            lineBuild.delete(0,lineBuild.length());
            lineBuild.append("  {").append("\"type\": ").append("\"cancel-order\"");
            lineBuild.append(", \"order-id\": \"").append(randName.nextString());
            lineBuild.append("\"},");
            lines.add(lineBuild.toString());
        }
        lineBuild.delete(0,lineBuild.length());
        lineBuild.append("  {").append("\"type\": ").append("\"cancel-order\"");
        lineBuild.append(", \"order-id\": \"").append(randName.nextString());
        lineBuild.append("\"}");
        lines.add(lineBuild.toString());

        lines.add("]");
        Path file = Paths.get(fileName);
        Files.write(file, lines, Charset.forName("UTF-8"));
    }

}
