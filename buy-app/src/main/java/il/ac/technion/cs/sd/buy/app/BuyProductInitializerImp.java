package il.ac.technion.cs.sd.buy.app;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import javax.lang.model.type.NullType;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.CompletableFuture;

/**
 * Created by benny on 24/05/2017.
 */
public class BuyProductInitializerImp implements BuyProductInitializer {

    //TODO catch JSONException inside the function
    @Override
    public CompletableFuture<Void> setupJson(String jsonData) throws JSONException {
        Map<String,NullType> OrderIDs = new TreeMap<>();
        try {
            JSONArray arr = new JSONArray(jsonData);
            for (int i = 0; i < arr.length(); i++) {
                String type = arr.getJSONObject(i).getString("type");

                switch (type){
                    case "order":
                       /* System.out.println(type + ":");
                        System.out.println("    " + "order-id: " + arr.getJSONObject(i).getString("order-id"));
                        System.out.println("    " + "product-id: " + arr.getJSONObject(i).getString("product-id"));
                        System.out.println("    " +"amount: "+ arr.getJSONObject(i).getInt("amount"));*/
                       OrderIDs
                        break;
                    case "product":
                        System.out.println(type + ":");
                        System.out.println("    " +"price: "+arr.getJSONObject(i).getInt("price"));
                        break;
                    case "modify-order":
                        System.out.println(type + ":");
                        System.out.println("    " + "order-id: " + arr.getJSONObject(i).getString("order-id"));
                        System.out.println("    " +"amount: "+ arr.getJSONObject(i).getInt("amount"));
                        break;
                    case "cancel-order":
                        System.out.println(type + ":");
                        System.out.println("    " + "order-id: " + arr.getJSONObject(i).getString("order-id"));
                        break;


                    default:
                        System.out.println("JSON file is not good");
                }
            }
        }catch(JSONException e){
            System.out.println("not good");
        }
        return new CompletableFuture<>();
    }

    @Override
    public CompletableFuture<Void> setupXml(String xmlData){

        return new CompletableFuture<>();
    }
}
