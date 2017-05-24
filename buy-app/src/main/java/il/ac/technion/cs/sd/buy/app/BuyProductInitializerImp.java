package il.ac.technion.cs.sd.buy.app;
import db_utils.DataBase;
import db_utils.DataBaseFactory;
import org.json.JSONArray;
import org.json.JSONException;

import javax.inject.Inject;
import javax.lang.model.type.NullType;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.CompletableFuture;

/**
 * Created by benny on 24/05/2017.
 */
public class BuyProductInitializerImp implements BuyProductInitializer {
   private DataBaseFactory dataBaseFactory;

   @Inject
    public BuyProductInitializerImp(DataBaseFactory dataBaseFactory) {

        this.dataBaseFactory = dataBaseFactory;
    }



    //TODO catch JSONException inside the function
    @Override
    public CompletableFuture<Void> setupJson(String jsonData) throws JSONException {
        CompletableFuture<DataBase> orders;
        CompletableFuture<DataBase> products;
        CompletableFuture<DataBase> modifed_orders;
        CompletableFuture<DataBase> canceld_orders;


        Integer num_of_keys = 3;
        List<String> names_of_columns1 = new ArrayList<>();
        names_of_columns1.add("order");
        names_of_columns1.add("user");
        names_of_columns1.add("product");
        orders = dataBaseFactory.setNames_of_columns(names_of_columns1)
                .setNum_of_keys(num_of_keys)
                .setDb_name("Orders")
                .build();

        num_of_keys = 1;
        List<String> names_of_columns2 = new ArrayList<>();
        names_of_columns2.add("product");
        names_of_columns2.add("price");
        products = dataBaseFactory.setNames_of_columns(names_of_columns2)
                .setNum_of_keys(num_of_keys)
                .setDb_name("Products")
                .build();

        num_of_keys = 2;
        List<String> names_of_columns3 = new ArrayList<>();
        names_of_columns3.add("order");
        names_of_columns3.add("number");
        names_of_columns3.add("amount");
        products = dataBaseFactory.setNames_of_columns(names_of_columns3)
                .setNum_of_keys(num_of_keys)
                .setDb_name("Modified")
                .build();

        num_of_keys = 1;
        List<String> names_of_columns4 = new ArrayList<>();
        names_of_columns4.add("order");
        products = dataBaseFactory.setNames_of_columns(names_of_columns4)
                .setNum_of_keys(num_of_keys)
                .setDb_name("Canceled")
                .build();

/////////////////////////////////////
        Map<String,NullType> OrderIDs = new TreeMap<>();
        try {
            JSONArray arr = new JSONArray(jsonData);
            for (int i = 0; i < arr.length(); i++) {
                String type = arr.getJSONObject(i).getString("type");

                switch (type){
                    case "order":
                        System.out.println(type + ":");
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
