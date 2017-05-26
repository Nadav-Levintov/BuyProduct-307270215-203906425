package il.ac.technion.cs.sd.buy.app;
import db_utils.DataBase;
import db_utils.DataBaseFactory;
import org.json.JSONArray;
import org.json.JSONException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.inject.Inject;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

/**
 * Created by benny on 24/05/2017.
 */
public class BuyProductInitializerImp implements BuyProductInitializer {
    protected DataBaseFactory dataBaseFactory;

   @Inject
    public BuyProductInitializerImp(DataBaseFactory dataBaseFactory) {

        this.dataBaseFactory = dataBaseFactory;
    }

    @Override
    public CompletableFuture<Void> setupJson(String jsonData){

        Integer num_of_keys = 3;

        List<String> names_of_columns1 = new ArrayList<>();
        names_of_columns1.add("order");
        names_of_columns1.add("user");
        names_of_columns1.add("product");
        CompletableFuture<DataBase> ordersDB = dataBaseFactory.setNames_of_columns(names_of_columns1)
                .setNum_of_keys(num_of_keys)
                .setDb_name("Orders")
                .build();

        num_of_keys = 1;
        List<String> names_of_columns2 = new ArrayList<>();
        names_of_columns2.add("product");
        names_of_columns2.add("price");
        CompletableFuture<DataBase> productsDB = dataBaseFactory.setNames_of_columns(names_of_columns2)
                .setNum_of_keys(num_of_keys)
                .setDb_name("Products")
                .build();

        num_of_keys = 2;
        List<String> names_of_columns3 = new ArrayList<>();
        names_of_columns3.add("order");
        names_of_columns3.add("number");
        names_of_columns3.add("amount");
        CompletableFuture<DataBase> modified_ordersDB = dataBaseFactory.setNames_of_columns(names_of_columns3)
                .setNum_of_keys(num_of_keys)
                .setDb_name("Modified")
                .build();

        num_of_keys = 1;
        List<String> names_of_columns4 = new ArrayList<>();
        names_of_columns4.add("order");
        CompletableFuture<DataBase> canceled_ordersDB = dataBaseFactory.setNames_of_columns(names_of_columns4)
                .setNum_of_keys(num_of_keys)
                .setDb_name("Canceled")
                .build();

        // create the csv string from json string
        CsvStrings csvStrings = new CsvStrings(jsonData).invoke();
        String csvOrders = csvStrings.getCsvOrders();
        String csvProducts = csvStrings.getCsvProducts();
        String csvModified = csvStrings.getCsvModified();
        String csvCanceled = csvStrings.getCsvCanceled();

        // build the data bases
        try {
            ordersDB.get().build_db(csvOrders);
            productsDB.get().build_db(csvProducts);
            modified_ordersDB.get().build_db(csvModified);
            canceled_ordersDB.get().build_db(csvCanceled);
        } catch (InterruptedException e) {
            System.out.println("catch InterruptedException");
            e.printStackTrace();
        } catch (ExecutionException e) {
            System.out.println("catch ExecutionException");
            e.printStackTrace();
        }

        return new CompletableFuture<>();
    }

    @Override
    public CompletableFuture<Void> setupXml(String xmlData) throws ParserConfigurationException, IOException, SAXException {

        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db=dbf.newDocumentBuilder();
        InputSource is = new InputSource((new StringReader(xmlData)));
        Document doc = db.parse(is);
        doc.getDocumentElement().normalize();


        String csvOrders = new String();
        String csvProducts = new String();
        String csvModified = new String();
        String csvCanceled = new String();
        Map<String,Integer> OrderIDs = new TreeMap<>();

        Node n = doc.getFirstChild();   //The root
        NodeList nListElements = n.getChildNodes();
        for (int temp = 0; temp < nListElements.getLength(); temp++)
        {
            Node elementNode = nListElements.item(temp);
            if (elementNode.getNodeType() == Node.ELEMENT_NODE)
            {
                Element element = (Element) elementNode;
                switch (elementNode.getNodeName()){
                    case "Order":
                        csvOrders += element.getElementsByTagName("order-id").item(0).getTextContent() + "," +
                                element.getElementsByTagName("user-id") .item(0).getTextContent() + "," +
                                element.getElementsByTagName("product-id").item(0).getTextContent() + "," +
                                element.getElementsByTagName("amount").item(0).getTextContent() + "\n";
                        OrderIDs.put(element.getElementsByTagName("user-id") .item(0).getTextContent(),1); //In order to discard canceled/modified orders that does not exist
                        break;

                    case "Product":
                        csvProducts += element.getElementsByTagName("id").item(0).getTextContent() + "," +
                                element.getElementsByTagName("price") .item(0).getTextContent()+ "\n";
                        break;

                    case "ModifyOrder":
                        if(OrderIDs.containsKey(element.getElementsByTagName("order-id").item(0).getTextContent()))
                        {
                            csvModified += element.getElementsByTagName("order-id").item(0).getTextContent() + "," +
                                    element.getElementsByTagName("new-amount").item(0).getTextContent() + "\n";
                        }
                        break;

                    case "CancelOrder":
                        if(OrderIDs.containsKey(element.getElementsByTagName("order-id").item(0).getTextContent()))
                        {
                             csvCanceled += element.getElementsByTagName("order-id").item(0).getTextContent() + "\n";
                        }
                        break;

                    default:
                        System.out.println("XML file is not legal");
                }
            }
        }
        System.out.println(csvOrders);
        System.out.println(csvProducts);
        System.out.println(csvModified);
        System.out.println(csvCanceled);

        return new CompletableFuture<>();
    }

    private class CsvStrings {
        private String jsonData;
        private String csvOrders;
        private String csvProducts;
        private String csvModified;
        private String csvCanceled;

        public CsvStrings(String jsonData) {
            this.jsonData = jsonData;
        }

        public String getCsvOrders() {
            return csvOrders;
        }

        public String getCsvProducts() {
            return csvProducts;
        }

        public String getCsvModified() {
            return csvModified;
        }

        public String getCsvCanceled() {
            return csvCanceled;
        }

        public CsvStrings invoke() {
            Map<String,Integer> OrderIDs = new TreeMap<>();
            csvOrders = new String();
            csvProducts = new String();
            csvModified = new String();
            csvCanceled = new String();


            try {
                JSONArray arr = new JSONArray(jsonData);
                for (int i = 0; i < arr.length(); i++) {
                    String type = arr.getJSONObject(i).getString("type");

                    switch (type){
                        case "order":
                            csvOrders += arr.getJSONObject(i).getString("order-id") + "," +
                                    arr.getJSONObject(i).getString("user-id") + "," +
                                    arr.getJSONObject(i).getString("product-id") + "," +
                                    arr.getJSONObject(i).getInt("amount") + "\n";
                            OrderIDs.put(arr.getJSONObject(i).getString("order-id"),1); //In order to discard canceled/modified orders that does not exist
                            break;
                        case "product":
                            csvProducts += arr.getJSONObject(i).getString("id") + "," +
                                    arr.getJSONObject(i).getInt("price") + "\n";
                           break;
                        case "modify-order":
                          if(OrderIDs.containsKey(arr.getJSONObject(i).getString("order-id")))
                             {
                                 csvModified += arr.getJSONObject(i).getString("order-id") + "," +
                                    arr.getJSONObject(i).getInt("amount") + "\n";
                             }
                          break;
                        case "cancel-order":
                          if(OrderIDs.containsKey(arr.getJSONObject(i).getString("order-id")))
                            {
                                csvCanceled += arr.getJSONObject(i).getString("order-id") + "\n";
                            }

                          break;
                        default:
                            System.out.println("JSON file is not legal");
                    }
                }

            }catch(JSONException e){
                System.out.println("catch JSONException");
            }
            return this;
        }
    }
}
