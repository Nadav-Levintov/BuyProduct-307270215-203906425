package il.ac.technion.cs.sd.buy.app;

import org.json.JSONException;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public interface BuyProductInitializer {
  /** Saves the XML data persistently, so that it could be run using BuyProductReader. */
  CompletableFuture<Void> setupXml(String xmlData) throws ParserConfigurationException, IOException, SAXException;
  /** Saves the JSON data persistently, so that it could be run using BuyProductReader. */
  CompletableFuture<Void> setupJson(String jsonData) throws JSONException, ExecutionException, InterruptedException;
}
