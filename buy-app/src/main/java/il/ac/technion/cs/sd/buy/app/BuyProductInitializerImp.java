package il.ac.technion.cs.sd.buy.app;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Multimap;
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
import java.util.*;
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

        // create the csv string from json string
        CsvStringsFromJson csvStrings = new CsvStringsFromJson(jsonData).invoke();
        String csvOrders = csvStrings.getCsvOrders();
        String csvProducts = csvStrings.getCsvProducts();
        String csvModified = csvStrings.getCsvModified();
        String csvCanceled = csvStrings.getCsvCanceled();

        createDataBasesFromCsvStrings(csvOrders, csvProducts, csvModified, csvCanceled);

        return new CompletableFuture<>();
    }

    @Override
    public CompletableFuture<Void> setupXml(String xmlData) throws ParserConfigurationException, IOException, SAXException {



        CsvStringsFromXml csvStringsFromXml = new CsvStringsFromXml(xmlData).invoke();
        String csvOrders = csvStringsFromXml.getCsvOrders();
        String csvProducts = csvStringsFromXml.getCsvProducts();
        String csvModified = csvStringsFromXml.getCsvModified();
        String csvCanceled = csvStringsFromXml.getCsvCanceled();
        createDataBasesFromCsvStrings(csvOrders, csvProducts, csvModified, csvCanceled);


        return new CompletableFuture<>();
    }

    private void createDataBasesFromCsvStrings(String csvOrders, String csvProducts, String csvModified, String csvCanceled) {
        // build the data bases
        Integer num_of_keys = 3;

        List<String> names_of_columns1 = new ArrayList<>();
        names_of_columns1.add("order");
        names_of_columns1.add("user");
        names_of_columns1.add("product");
        names_of_columns1.add("amount");
        names_of_columns1.add("modified");
        names_of_columns1.add("canceled");
        CompletableFuture<DataBase> ordersDB = dataBaseFactory.setNames_of_columns(names_of_columns1)
                .setNum_of_keys(num_of_keys)
                .setDb_name("Orders")
                .setAllow_Multiples(false)
                .build();

        num_of_keys = 1;
        List<String> names_of_columns2 = new ArrayList<>();
        names_of_columns2.add("product");
        names_of_columns2.add("price");
        CompletableFuture<DataBase> productsDB = dataBaseFactory.setNames_of_columns(names_of_columns2)
                .setNum_of_keys(num_of_keys)
                .setDb_name("Products")
                .setAllow_Multiples(false)
                .build();

        num_of_keys = 2;
        List<String> names_of_columns3 = new ArrayList<>();
        names_of_columns3.add("order");
        names_of_columns3.add("number");
        names_of_columns3.add("amount");
        CompletableFuture<DataBase> modified_ordersDB = dataBaseFactory.setNames_of_columns(names_of_columns3)
                .setNum_of_keys(num_of_keys)
                .setDb_name("Modified")
                .setAllow_Multiples(true)
                .build();

        num_of_keys = 1;
        List<String> names_of_columns4 = new ArrayList<>();
        names_of_columns4.add("order");
        CompletableFuture<DataBase> canceled_ordersDB = dataBaseFactory.setNames_of_columns(names_of_columns4)
                .setNum_of_keys(num_of_keys)
                .setDb_name("Canceled")
                .setAllow_Multiples(false)
                .build();

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
    }

    private class CsvStringsFromJson {
        private String jsonData;
        private String csvOrders;
        private String csvProducts;
        private String csvModified;
        private String csvCanceled;

        public CsvStringsFromJson(String jsonData) {
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

        public CsvStringsFromJson invoke() {
            Map<String,Integer> OrderIDs = new TreeMap<>();
            csvOrders = new String();
            csvProducts = new String();
            csvModified = new String();
            csvCanceled = new String();
            int number =1;

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
                                         number + "," +
                                         arr.getJSONObject(i).getInt("amount") + "\n";
                                 number++;
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

        public CsvStringsFromJson help1() {
            Map<String,String> ordersMap = new TreeMap<>();
            Map<String,String> productsMap = new TreeMap<>();
            ListMultimap<String,String> modifiedOrdersMap = ArrayListMultimap.create();
            HashSet<String> canceldOrders = new HashSet<>();

            try {
                JSONArray arr = new JSONArray(jsonData);
                for (int i = 0; i < arr.length(); i++) {
                    String type = arr.getJSONObject(i).getString("type");

                    switch (type){
                        case "order":
                            // add to map - (will remove old)
                            String orderId = new String(arr.getJSONObject(i).getString("order-id"));

                            csvOrders = arr.getJSONObject(i).getString("order-id") + "," +
                                    arr.getJSONObject(i).getString("user-id") + "," +
                                    arr.getJSONObject(i).getString("product-id") + "," +
                                    arr.getJSONObject(i).getInt("amount") + "\n";
                            ordersMap.put(orderId,csvOrders);
                            // remove from canceled
                            canceldOrders.remove(orderId);
                            // remove from modified
                            modifiedOrdersMap.removeAll(orderId);
                            break;

                        case "product":
                            // add to map of the products - remove old ones
                            String productId = new String(arr.getJSONObject(i).getString("id"));
                            csvProducts = arr.getJSONObject(i).getString("id") + "," +
                                    arr.getJSONObject(i).getInt("price") + "\n";
                            productsMap.put(productId, csvProducts);
                            break;
                        case "modify-order":
                            String mOrdertId = new String(arr.getJSONObject(i).getString("order-id"));
                            if(ordersMap.containsKey(mOrdertId))
                            {

                                csvModified = arr.getJSONObject(i).getString("order-id") +"," +
                                        arr.getJSONObject(i).getInt("amount") + "\n";
                            }
                            // check if order exist - if not -> do nothing
                            // check if there are a canceld order - if there is -> remove it
                            // add to the multi map od modified

                            break;
                        case "cancel-order":
                            // check if order exist -> if not do nothing
                            // insert to canceld orders set (remove old versions)

                            break;
                        default:
                            System.out.println("JSON file is not legal");
                    }
                }

            }catch(JSONException e){
                System.out.println("catch JSONException");
            }


            // foreach order check that product exist -> if not don put in string
            //  foreach modified order add secondary key aa>zz (max of 100 )

            return this;
        }
    }

    private class CsvStringsFromXml {
        private String xmlData;
        private String csvOrders;
        private String csvProducts;
        private String csvModified;
        private String csvCanceled;

        public CsvStringsFromXml(String xmlData) {
            this.xmlData = xmlData;
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

        public CsvStringsFromXml invoke() throws ParserConfigurationException, SAXException, IOException {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db=dbf.newDocumentBuilder();
            InputSource is = new InputSource((new StringReader(xmlData)));
            Document doc = db.parse(is);
            doc.getDocumentElement().normalize();

            csvOrders = new String();
            csvProducts = new String();
            csvModified = new String();
            csvCanceled = new String();
            int number=0;
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
                                        number + "," +
                                        element.getElementsByTagName("new-amount").item(0).getTextContent() + "\n";
                                number++;
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
            return this;
        }
    }
}
