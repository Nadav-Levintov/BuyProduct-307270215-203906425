package il.ac.technion.cs.sd.buy.app;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Multimap;
import db_utils.DataBase;
import db_utils.DataBaseFactory;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
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
public class BuyProductInitializerImpl implements BuyProductInitializer {
    protected DataBaseFactory dataBaseFactory;

    @Inject
    public BuyProductInitializerImpl(DataBaseFactory dataBaseFactory) {

        this.dataBaseFactory = dataBaseFactory;
    }

    @Override
    public CompletableFuture<Void> setupJson(String jsonData) {

        // create the csv string from json string
        DataListsFromJson dataLists = new DataListsFromJson(jsonData).invoke();
        LinkedList<String> ordersList = dataLists.getOrdersList();
        LinkedList<String> productsList = dataLists.getProductsList();
        LinkedList<String> modifiedList = dataLists.getModifiedList();

        return createDataBasesFromLists(ordersList, productsList, modifiedList);

    }

    @Override
    public CompletableFuture<Void> setupXml(String xmlData) throws ParserConfigurationException, IOException, SAXException {


        DataListsFromXml dataLists = new DataListsFromXml(xmlData).invoke();

        List<String> ordersList = dataLists.getOrdersList();
        List<String> productsList = dataLists.getProductsList();
        List<String> modifiedList = dataLists.getModifiedList();

        return createDataBasesFromLists(ordersList, productsList, modifiedList);
    }

    private CompletableFuture<Void> createDataBasesFromLists(List<String> ordersList, List<String>  productsList, List<String>  modifiedList) {
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


        CompletableFuture<Void> order_build = ordersDB.build_db(ordersList);
        CompletableFuture<Void> product_build = productsDB.build_db(productsList);
        CompletableFuture<Void> mod_build = modified_ordersDB.build_db(modifiedList);

        // will finish build when all build finish
        CompletableFuture<Void> order_product = order_build.thenCombine(product_build,(a,b)-> a);
        return order_product.thenCombine(mod_build,(a,b)-> a);

    }

    private class DataListsFromJson {
        private String jsonData;
        private LinkedList<String> ordersList;
        private LinkedList<String> productsList;
        private LinkedList<String> modifiedList;


        public DataListsFromJson(String jsonData) {
            this.jsonData = jsonData;
        }

        public LinkedList<String> getOrdersList() {
            return ordersList;
        }

        public LinkedList<String> getProductsList() {
            return productsList;
        }

        public LinkedList<String> getModifiedList() { return modifiedList; }


        public DataListsFromJson invoke() {
            Map<String, String> ordersMap = new TreeMap<>();
            Map<String, String> productsMap = new TreeMap<>();
            ListMultimap<String, String> modifiedOrdersMap = ArrayListMultimap.create();
            Map<String, String> canceledOrders = new TreeMap<>();
            String csvCanceled;

            String csvOrder;
            String csvProduct;
            String csvModified;

            try {
                JSONArray arr = new JSONArray(jsonData);
                int arraySize = arr.length();
                JSONObject obj;
                for (int i = 0; i < arraySize; i++) {
                    obj = arr.getJSONObject(i);
                    String type = obj.getString("type");
                    switch (type) {
                        case "order":
                            // add to map - (will remove old)
                            String orderId = new String(obj.getString("order-id"));

                            csvOrder = obj.getString("order-id") + "," +
                                    obj.getString("user-id") + "," +
                                    obj.getString("product-id") + "," +
                                    obj.getInt("amount") + ",";
                            ordersMap.put(orderId, csvOrder);
                            // remove from canceled
                            canceledOrders.remove(orderId);
                            // remove from modified
                            modifiedOrdersMap.removeAll(orderId);
                            break;

                        case "product":
                            // add to map of the products - remove old ones
                            String productId = new String(obj.getString("id"));
                            csvProduct = obj.getString("id") + "," +
                                    obj.getInt("price");
                            productsMap.put(productId, csvProduct);
                            break;
                        case "modify-order":
                            String mOrderId = new String(obj.getString("order-id"));
                            // check if order exist - if not -> do nothing
                            if (ordersMap.containsKey(mOrderId)) {
                                csvModified = obj.getString("order-id") + "," +
                                        obj.getInt("amount");
                                //  there are a canceled order  -> remove it
                                canceledOrders.remove(mOrderId);
                                // add to the multi map of modified
                                modifiedOrdersMap.put(mOrderId, csvModified);
                            }
                            break;
                        case "cancel-order":
                            String cOrderId = new String(obj.getString("order-id"));
                            csvCanceled = cOrderId;
                            // check if order exist - if not -> do nothing
                            if (ordersMap.containsKey(cOrderId)) {
                                // insert to canceld orders set (remove old versions)
                                canceledOrders.put(cOrderId, csvCanceled);
                            }
                            break;
                        default:
                            System.out.println("JSON file is not legal");
                    }
                }

            } catch (JSONException e) {
                System.out.println("catch JSONException");
            }


            auxBuildLists(ordersMap, productsMap, modifiedOrdersMap, canceledOrders, this.ordersList, this.productsList, this.modifiedList);
            return this;
        }

    }

    private class DataListsFromXml {
        private String xmlData;
      //  private String csvOrders;
     //   private String csvProducts;
       // private String csvModified;

        private LinkedList<String> ordersList;
        private LinkedList<String> productsList;
        private LinkedList<String> modifiedList;


        public DataListsFromXml(String xmlData) {
            this.xmlData = xmlData;
        }

        public LinkedList<String> getOrdersList() {
            return ordersList;
        }

        public LinkedList<String> getProductsList() {
            return productsList;
        }

        public LinkedList<String> getModifiedList() { return modifiedList; }

        public DataListsFromXml invoke() throws ParserConfigurationException, IOException, SAXException {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            InputSource is = new InputSource((new StringReader(xmlData)));
            Document doc = db.parse(is);
            doc.getDocumentElement().normalize();

            String csvOrders;
            String csvProducts;
            String csvModified;
            String csvCanceled;

            Map<String, String> ordersMap = new TreeMap<>();
            Map<String, String> productsMap = new TreeMap<>();
            ListMultimap<String, String> modifiedOrdersMap = ArrayListMultimap.create();
            Map<String, String> canceldOrders = new TreeMap<>();

            Node n = doc.getFirstChild();   //The root
            NodeList nListElements = n.getChildNodes(); //Elements List

            int elementsAmount = nListElements.getLength();
            Node elementNode = null;
            if(elementsAmount>0){
                elementNode = nListElements.item(0);
            }
            for (int temp = 0; temp < elementsAmount; temp++)
            {
                //Node elementNode = nListElements.item(temp);
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
                                    element.getElementsByTagName("price").item(0).getTextContent();
                            productsMap.put(productId, csvProducts);
                            break;

                        case "ModifyOrder":
                            String mOrderId = element.getElementsByTagName("order-id").item(0).getTextContent();
                            // check if order exist - if not -> do nothing
                            if (ordersMap.containsKey(mOrderId)) {
                                csvModified = element.getElementsByTagName("order-id").item(0).getTextContent() + "," +
                                        element.getElementsByTagName("new-amount").item(0).getTextContent();
                                //  there are a canceled order  -> remove it
                                canceldOrders.remove(mOrderId);
                                // add to the multi map of modified
                                modifiedOrdersMap.put(mOrderId, csvModified);
                            }

                            break;

                        case "CancelOrder":
                            String cOrderId = element.getElementsByTagName("order-id").item(0).getTextContent();
                            csvCanceled = cOrderId;
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
                elementNode = elementNode.getNextSibling();
            }
            auxBuildLists(ordersMap, productsMap, modifiedOrdersMap, canceldOrders, this.ordersList, this.productsList, this.modifiedList);
            return this;
        }

    }

    void auxBuildLists(Map<String, String> ordersMap, Map<String, String> productsMap,
                     ListMultimap<String, String> modifiedOrdersMap, Map<String, String> canceldOrders,
                     LinkedList<String> ordersList, LinkedList<String> productsList, LinkedList<String> modifiedList) {
        String tempString;
        for (Map.Entry<String, String> entry : ordersMap.entrySet()) {
            String product = entry.getValue().split(",")[2];
            if (productsMap.containsKey(product))
            {
                tempString = entry.getValue();
                Integer modifiedAmount = 0;
                Integer canceled = 0;
                if (modifiedOrdersMap.containsKey(entry.getKey()))
                {
                    modifiedAmount = modifiedOrdersMap.get(entry.getKey()).size();
                }
                if (canceldOrders.containsKey(entry.getKey()))
                {
                    canceled = 1;
                }
                tempString += modifiedAmount.toString() + "," + canceled.toString() + "\n";
                ordersList.add(tempString);
            }
        }

        //insert products to list
        for (Map.Entry<String, String> entry : productsMap.entrySet()) {
            productsList.add(entry.getValue());
        }

        //insert Modified orders to list
        for(String key : modifiedOrdersMap.keySet()){
            Collection<String> values = modifiedOrdersMap.get(key);
            for (String value : values) {
                modifiedList.add(value);
            }
        }
    }



}



