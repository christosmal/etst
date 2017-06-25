package com.christosmal.etst;

import java.io.StringWriter;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonWriter;
import javax.json.JsonWriterFactory;
import javax.json.stream.JsonGenerator;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

/**
 * New OO version. Esentially all we do is convertion. This demonstrates a ConvertionFactory that will for a specific type 
 * convertion return an applicable Converter
 * @author christo
 */
public class App {
    
    private ConvertionFactory convertionFactory;
    
    public App(String documentConfig, Map elementConfig) {
         convertionFactory = ConvertionFactory.getDocumentConvertionFactory(documentConfig, elementConfig);
    }
    
    private static void prettyPrint(JsonObject obj) {
        Map<String, Boolean> config = new HashMap<String, Boolean>();
        config.put(JsonGenerator.PRETTY_PRINTING, true);
        JsonWriterFactory jwf = Json.createWriterFactory(config);
        StringWriter sw = new StringWriter();
        JsonWriter jsonWriter = jwf.createWriter(sw);
        jsonWriter.writeObject(obj);
        System.out.println(sw.toString());
    }

    private <T extends Object, U extends Object> U convert(T t, Class<U> u) throws Exception {
        Converter conv = convertionFactory.getConverter(t.getClass(), u);
        return (U)conv.convert(t);
    }
    
    public JsonObject convert(String address) throws Exception {
        URL url = convert(address, URL.class);
        Document doc = convert(url, Document.class);
        Elements elements = convert(doc, Elements.class);
        JsonObject jsonObj = convert(elements, JsonObject.class);
        return jsonObj;
    }
    
    public static int main(String surl) {
        try {
            String documentConfig = "h2,h2+table";
            
            Map elementConfig = new HashMap();
            elementConfig.put("h2", "return");
            elementConfig.put("table", "recurse");
            elementConfig.put("thead", "cacheStoreChildren");
            elementConfig.put("tbody", "cacheLookupChildren.thead");
            
            JsonObject jsonObj = new App(documentConfig, elementConfig).convert(surl);
            prettyPrint(jsonObj);
            return 0;
        } catch (Exception ex) {
            ex.printStackTrace();
            System.out.println("Application abording with exit code -1");
            return -1;
        }
    }
    
    public static void main(String[] args) {
        String surl;
        if (args == null || args.length < 1)
            surl = "https://github.com/egis/handbook/blob/master/Tech-Stack.md";
        else 
            surl = args[0];
        System.exit(main(surl));
    }
    
}
