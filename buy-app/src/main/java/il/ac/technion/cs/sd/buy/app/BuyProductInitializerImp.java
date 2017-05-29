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
    public CompletableFuture<Void> setupJson(String jsonData) {

        // create the csv string from json string
        CsvStringsFromJson csvStrings = new CsvStringsFromJson(jsonData).invoke();
        String csvOrders = csvStrings.getCsvOrders();
        String csvProducts = csvStrings.getCsvProducts();
        String csvModified = csvStrings.getCsvModified();

        System.out.println("csvOrders:\n" + csvOrders);
        System.out.println("csvProducts:\n" + csvProducts);
        System.out.println("csvModified:\n" + csvModified);

        createDataBasesFromCsvStrings(csvOrders, csvProducts, csvModified);

        return new CompletableFuture<>();
    }

    @Override
    public CompletableFuture<Void> setupXml(String xmlData) throws ParserConfigurationException, IOException, SAXException {


        CsvStringsFromXml csvStringsFromXml = new CsvStringsFromXml(xmlData).invoke();
        String csvOrders = csvStringsFromXml.getCsvOrders();
        String csvProducts = csvStringsFromXml.getCsvProducts();
        String csvModified = csvStringsFromXml.getCsvModified();

        System.out.println("csvOrders:\n" + csvOrders);
        System.out.println("csvProducts:\n" + csvProducts);
        System.out.println("csvModified:\n" + csvModified);

        return createDataBasesFromCsvStrings(csvOrders, csvProducts, csvModified);

    }

    private CompletableFuture<Void> createDataBasesFromCsvStrings(String csvOrders, String csvProducts, String csvModified) {
        // build the data bases
        Integer num_of_keys = 3;

        List<String> names_of_columns1 = new ArrayList<>();
        names_of_columns1.add("order");
        names_of_columns1.add("user");
        names_of_columns1.add("product");
        names_of_columns1.add("amount");
        names_of_columns1.add("modified");
        names_of_columns1.add("canceled");
        DataBase ordersDB = dataBaseFactory.setNames_of_columns(names_of_columns1)
                .setNum_of_keys(num_of_keys)
                .setDb_name("Orders")
                .setAllow_Multiples(false)
                .build();

        num_of_keys = 1;
        List<String> names_of_columns2 = new ArrayList<>();
        names_of_columns2.add("product");
        names_of_columns2.add("price");
        DataBase productsDB = dataBaseFactory.setNames_of_columns(names_of_columns2)
                .setNum_of_keys(num_of_keys)
                .setDb_name("Products")
                .setAllow_Multiples(false)
                .build();

        num_of_keys = 1;
        List<String> names_of_columns3 = new ArrayList<>();
        names_of_columns3.add("order");
        names_of_columns3.add("amount");
        DataBase modified_ordersDB = dataBaseFactory.setNames_of_columns(names_of_columns3)
                .setNum_of_keys(num_of_keys)
                .setDb_name("Modified")
                .setAllow_Multiples(true)
                .build();


        CompletableFuture<Void> order_build = ordersDB.build_db(csvOrders);
        CompletableFuture<Void> product_build = productsDB.build_db(csvProducts);
        CompletableFuture<Void> mod_build = modified_ordersDB.build_db(csvModified);

        // will finish build when all build finish
        CompletableFuture<Void> order_product = order_build.thenCombine(product_build,(a,b)-> a);
        return order_product.thenCombine(mod_build,(a,b)-> a);

    }

    private class CsvStringsFromJson {
        private String jsonData;
        private String csvOrders;
        private String csvProducts;
        private String csvModified;

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


        public CsvStringsFromJson invoke() {
            Map<String, String> ordersMap = new TreeMap<>();
            Map<String, String> productsMap = new TreeMap<>();
            ListMultimap<String, String> modifiedOrdersMap = ArrayListMultimap.create();
            Map<String, String> canceldOrders = new TreeMap<>();
            String csvCanceled;

            try {
                JSONArray arr = new JSONArray(jsonData);
                for (int i = 0; i < arr.length(); i++) {
                    String type = arr.getJSONObject(i).getString("type");

                    switch (type) {
                        case "order":
                            // add to map - (will remove old)
                            String orderId = new String(arr.getJSONObject(i).getString("order-id"));

                            csvOrders = arr.getJSONObject(i).getString("order-id") + "," +
                                    arr.getJSONObject(i).getString("user-id") + "," +
                                    arr.getJSONObject(i).getString("product-id") + "," +
                                    arr.getJSONObject(i).getInt("amount") + ",";
                            ordersMap.put(orderId, csvOrders);
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
                            String mOrderId = new String(arr.getJSONObject(i).getString("order-id"));
                            // check if order exist - if not -> do nothing
                            if (ordersMap.containsKey(mOrderId)) {
                                csvModified = arr.getJSONObject(i).getString("order-id") + "," +
                                        arr.getJSONObject(i).getInt("amount") + "\n";
                                //  there are a canceled order  -> remove it
                                canceldOrders.remove(mOrderId);
                                // add to the multi map of modified
                                modifiedOrdersMap.put(mOrderId, csvModified);
                            }
                            break;
                        case "cancel-order":
                            String cOrderId = new String(arr.getJSONObject(i).getString("order-id"));
                            csvCanceled = cOrderId + "\n";
                            // check if order exist - if not -> do nothing
                            if (ordersMap.containsKey(cOrderId)) {
                                // insert to canceld orders set (remove old versions)
                                canceldOrders.put(cOrderId, csvCanceled);
                            }
                            break;
                        default:
                            System.out.println("JSON file is not legal");
                    }
                }

            } catch (JSONException e) {
                System.out.println("catch JSONException");
            }


            // foreach order check that product exist -> if not don put in string
            csvOrders = "";
            auxBuildCsv(ordersMap, productsMap, modifiedOrdersMap, canceldOrders, this.csvOrders, this.csvProducts, this.csvModified);
            return this;
        }

    }

    private class CsvStringsFromXml {
        private String xmlData;
        private String csvOrders;
        private String csvProducts;
        private String csvModified;

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

        public CsvStringsFromXml invoke() throws ParserConfigurationException, IOException, SAXException {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            InputSource is = new InputSource((new StringReader(xmlData)));
            Document doc = db.parse(is);
            doc.getDocumentElement().normalize();

            csvOrders = new String();
            csvProducts = new String();
            csvModified = new String();
            String csvCanceled;

            Map<String, String> ordersMap = new TreeMap<>();
            Map<String, String> productsMap = new TreeMap<>();
            ListMultimap<String, String> modifiedOrdersMap = ArrayListMultimap.create();
            Map<String, String> canceldOrders = new TreeMap<>();

            Node n = doc.getFirstChild();   //The root
            NodeList nListElements = n.getChildNodes();
            for (int temp = 0; temp < nListElements.getLength(); temp++) {
                Node elementNode = nListElements.item(temp);
                if (elementNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element element = (Element) elementNode;
                    switch (elementNode.getNodeName()) {
                        case "Order":
                            // add to map - (will remove old)
                            String orderId = element.getElementsByTagName("order-id").item(0).getTextContent();
                            csvOrders = element.getElementsByTagName("order-id").item(0).getTextContent() + "," +
                                    element.getElementsByTagName("user-id").item(0).getTextContent() + "," +
                                    element.getElementsByTagName("product-id").item(0).getTextContent() + "," +
                                    element.getElementsByTagName("amount").item(0).getTextContent() + ",";
                            ordersMap.put(orderId, csvOrders);
                            // remove from canceled
                            canceldOrders.remove(orderId);
                            // remove from modified
                            modifiedOrdersMap.removeAll(orderId);
                            break;

                        case "Product":
                            // add to map of the products - remove old ones
                            String productId = element.getElementsByTagName("id").item(0).getTextContent();
                            csvProducts = element.getElementsByTagName("id").item(0).getTextContent() + "," +
                                    element.getElementsByTagName("price").item(0).getTextContent() + "\n";
                            productsMap.put(productId, csvProducts);
                            break;

                        case "ModifyOrder":
                            String mOrderId = element.getElementsByTagName("order-id").item(0).getTextContent();
                            // check if order exist - if not -> do nothing
                            if (ordersMap.containsKey(mOrderId)) {
                                csvModified = element.getElementsByTagName("order-id").item(0).getTextContent() + "," +
                                        element.getElementsByTagName("new-amount").item(0).getTextContent() + "\n";
                                //  there are a canceled order  -> remove it
                                canceldOrders.remove(mOrderId);
                                // add to the multi map of modified
                                modifiedOrdersMap.put(mOrderId, csvModified);
                            }

                            break;

                        case "CancelOrder":
                            String cOrderId = element.getElementsByTagName("order-id").item(0).getTextContent();
                            csvCanceled = cOrderId + "\n";
                            // check if order exist - if not -> do nothing
                            if (ordersMap.containsKey(cOrderId)) {
                                // insert to canceled orders set (remove old versions)
                                canceldOrders.put(cOrderId, csvCanceled);
                            }

                            break;
                        default:
                            System.out.println("XML file is not legal");
                    }
                }
            }
            // foreach order check that product exist -> if not don put in string
            csvOrders = "";
            auxBuildCsv(ordersMap, productsMap, modifiedOrdersMap, canceldOrders, this.csvOrders, this.csvProducts, this.csvModified);

            return this;
        }

    }

    void auxBuildCsv(Map<String, String> ordersMap, Map<String, String> productsMap,
                     ListMultimap<String, String> modifiedOrdersMap, Map<String, String> canceldOrders,
                     String csvOrders, String csvProducts, String csvModified) {
        String csvCanceled;
        for (Map.Entry<String, String> entry : ordersMap.entrySet()) {
            String product = entry.getValue().split(",")[2];
            if (productsMap.containsKey(product)) {
                csvOrders += entry.getValue();
                Integer modifiedAmount = 0;
                Integer canceled = 0;
                if (modifiedOrdersMap.containsKey(entry.getKey())) {
                    modifiedAmount = modifiedOrdersMap.get(entry.getKey()).size();
                }
                if (canceldOrders.containsKey(entry.getKey())) {
                    canceled = 1;
                }
                csvOrders += modifiedAmount.toString() + "," + canceled.toString() + "\n";
            }
        }

        //insert Canceled orders to string
        csvCanceled = "";
        for (Map.Entry<String, String> entry : canceldOrders.entrySet()) {
            csvCanceled += entry.getValue();
        }

        //insert products to string
        csvProducts = "";
        for (Map.Entry<String, String> entry : productsMap.entrySet()) {
            csvProducts += entry.getValue();
        }


        //insert Modified orders to string
        csvModified = "";
        for(String key : modifiedOrdersMap.keySet()){
            Collection<String> values = modifiedOrdersMap.get(key);
            for (String Value : values) {
                csvModified += Value;
            }
        }
    }



}



