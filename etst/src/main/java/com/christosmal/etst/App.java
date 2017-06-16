package com.christosmal.etst;

import java.io.IOException;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonWriter;
import javax.json.JsonWriterFactory;
import javax.json.stream.JsonGenerator;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class App {
    
    private static String[] handleCells(Elements cells) {
        String[] ret = new String[cells.size()];
        for (int i = 0; i < cells.size(); i++) {
            Element cell = cells.get(i);
            ret[i] = cell.text();
        }
        return ret;
    }
    
    private static String[] handleThead(Elements rows) {
        if (rows.size() > 1) 
            throw new RuntimeException("More than 1 thead is unexpected");
        return handleCells(rows.get(0).children());
    }
    
    private static JsonArray handleTbody(String[] titles, Elements rows) {
        JsonArrayBuilder jarray = Json.createArrayBuilder();
        for (Element row : rows) {
            JsonObjectBuilder jbuilder = Json.createObjectBuilder();
            String[] values = handleCells(row.children());
            for (int i = 0; i < titles.length; i++) {
                jbuilder.add(titles[i], values[i]);
            }
            JsonObject jrow = jbuilder.build();
            jarray.add(jrow);
        }
        return jarray.build();
    }
    
    private static JsonArray handleTable(Elements rows) {
        JsonArray ret = null;
        String[] titles = null;
        for (Element row : rows) {
            if (row.nodeName().equals("thead")) {
                if (titles != null)
                    throw new RuntimeException("More than 1 thead is unexpected");
                titles = handleThead(row.children());
            } else if (row.nodeName().equals("tbody")) {
                if (ret != null)
                    throw new RuntimeException("More than 1 tbody is unexpected");
                ret = handleTbody(titles, row.children());
            } else {
                throw new RuntimeException("Elements of table is "+row.nodeName()+". It is neither thead nor tbody");
            }
        }
        return ret;
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

    public static void main(String[] args) {
        try {
            Document doc = Jsoup.connect("https://github.com/egis/handbook/blob/master/Tech-Stack.md").get();
            Elements links = doc.select("h2,h2+table");
            JsonObjectBuilder jbuilder = Json.createObjectBuilder();
            String parent = "";
            for (Element elem : links) {
                if (elem.nodeName().equals("h2")) {
                    parent = elem.text();
                } else if (elem.nodeName().equals("table")) {
                    jbuilder.add(parent, handleTable(elem.children()));
                }
            }
            prettyPrint(jbuilder.build());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
