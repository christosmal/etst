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
    
    private static JsonValue convertArrays(CVArrays cv) {
        JsonObjectBuilder jbuilder = Json.createObjectBuilder();
        for (int i = 0; i < cv.getColumns().length; i++) {
            jbuilder.add(cv.getColumns()[i], cv.getValues()[i]);
        }
        return jbuilder.build();
    }
    
    private static String[] convertToArray(Elements cells) {
        String[] ret = new String[cells.size()];
        for (int i = 0; i < cells.size(); i++) {
            Element cell = cells.get(i);
            ret[i] = cell.text();
        }
        return ret;
    }
    
    private Object convertElement(Element elem) throws ConvertionException {
        Map config = (Map)getConfig(Element.class).getConfiguration(elem);
        String op = (String)config.get(elem.nodeName());
        if (op.equals("return"))
            return elem.text();
        else if (op.equals("cacheStoreChildren")) {
            for (Element child : elem.children()) {
                cache.put(elem.nodeName(), convertToArray(child.children()));
            }
            return null;
        } else if (op.startsWith("cacheLookupChildren.")) {
            Object cachedValue = cache.get(op.substring("cacheLookupChildren.".length()));
            JsonArrayBuilder jarray = Json.createArrayBuilder();
            for (Element child : elem.children()) {
                try {
                    Object value = convertToArray(child.children());
                    CVArrays cv = new CVArrays();
                    cv.setColumns((String[])cachedValue);
                    cv.setValues((String[])value);
                    JsonValue jv = getConverter(CVArrays.class, JsonValue.class).convert(cv);
                    jarray.add(jv);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
            return jarray.build();
        } else if (op.equals("recurse"))
            return convertElements(elem.children());
        else 
            return null;
    }
    
    private JsonValue convertElements(Elements elements) throws ConvertionException {
        JsonObjectBuilder jbuilder = Json.createObjectBuilder();
        String parent = "";
        if (elements.isEmpty())
            throw new ConvertionException("There are no elements to be converted");
        for (Element elem : elements) {
            try {
                Object o = getConverter(Element.class, Object.class).convert(elem);
                if (o == null)
                    continue;
                else if (o instanceof String) 
                    parent = (String)o;
                else if (o instanceof CVArrays) {
                    JsonValue jv = getConverter(CVArrays.class, JsonValue.class).convert((CVArrays)o);
                    if (parent.equals(""))
                        return jv;
                    jbuilder.add(parent, jv);
                }
                else if (o instanceof JsonValue) {
                    JsonValue jv = (JsonValue)o;
                    if (parent.equals(""))
                        return jv;
                    jbuilder.add(parent, jv);
                }
            } catch (Exception x) {
                x.printStackTrace();
            }
        }
        return jbuilder.build();
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
            return (T elems) -> (U)convertElements((Elements)elems);
        else if (t.equals(Element.class) && u.equals(Object.class)) 
            return (T elem) -> (U)convertElement((Element)elem);
        else if (t.equals(CVArrays.class) && u.equals(JsonValue.class))
            return (T cv) -> (U)convertArrays((CVArrays)cv);
        // More Converter implemenations to be added here
        throw new ConvertionException("No convertion exists to convert from "+t.getName()+" to "+u.getName());
    }
    
}


