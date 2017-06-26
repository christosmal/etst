/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.christosmal.etst;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonValue;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * Factory for Converters
 * @author christo
 */
public class ConvertionFactory {
    
    private Map<Class, Config> configs = new HashMap<>();
    private Map<String, Object> cache = new HashMap<>();
    
    // There are probabably various types of factories one would want to create, where each one could have a
    // different configuration ...
    public static ConvertionFactory getDocumentConvertionFactory(String documentConfig, Map elementConfig) {
        ConvertionFactory convertionFactory = new ConvertionFactory();
        convertionFactory.addConfig(Document.class, (String s) -> documentConfig);
        convertionFactory.addConfig(Element.class, (Element e) -> elementConfig);
        return convertionFactory;
    }
    
    private ConvertionFactory() {}
    
    public <C extends Object, D extends Object, E extends Object> void addConfig(Class<C> c, Config<D, E> config) {
        configs.put(c, config);
    }
    
    public Config getConfig(Class c) {
        return configs.get(c);
    }
    
    public Map<String, Object> getCache() {
        return cache;
    }
        
    public <T extends Object, U extends Object> Converter<T, U> getConverter(Class<T> t, Class<U> u) throws ConvertionException {
        if (t.equals(String.class) && u.equals(URL.class))
            return (T s) -> (U)new URL((String)s);
        else if (t.equals(URL.class) && u.equals(Document.class))
            return (T url) -> (U)Jsoup.connect(((URL)url).toString()).get();
        else if (t.equals(Document.class) && u.equals(Elements.class)) {
            String searchElems = (String)(getConfig(Document.class).getConfiguration(null));
            return (T doc) -> (U)((Document)doc).select(searchElems);
        } else if (t.equals(Elements.class) && u.equals(JsonObject.class)) 
            return (Converter<T, U>)new ElementsConverter(this);
        else if (t.equals(Element.class) && u.equals(Object.class)) 
            return (Converter<T, U>)new ElementConverter(this);
        else if (t.equals(CVArrays.class) && u.equals(JsonValue.class))
            return (T cv) -> (U)ElementConvertion.convertArrays((CVArrays)cv);
        // More Converter implemenations to be added here
        throw new ConvertionException("No convertion exists to convert from "+t.getName()+" to "+u.getName());
    }
    
}


