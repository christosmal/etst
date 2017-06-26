/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.christosmal.etst;

import java.util.Map;
import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObjectBuilder;
import javax.json.JsonValue;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 *
 * @author christo
 */
public abstract class ElementConvertion {
    
    private ConvertionFactory factory;
    
    protected ElementConvertion(ConvertionFactory f) {
        factory = f;
    }

    public static JsonValue convertArrays(CVArrays cv) {
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
    
    protected Object convertElement(Element elem) throws ConvertionException {
        Map config = (Map)factory.getConfig(Element.class).getConfiguration(elem);
        String op = (String)config.get(elem.nodeName());
        if (op.equals("return"))
            return elem.text();
        else if (op.equals("cacheStoreChildren")) {
            for (Element child : elem.children()) {
                factory.getCache().put(elem.nodeName(), convertToArray(child.children()));
            }
            return null;
        } else if (op.startsWith("cacheLookupChildren.")) {
            Object cachedValue = factory.getCache().get(op.substring("cacheLookupChildren.".length()));
            JsonArrayBuilder jarray = Json.createArrayBuilder();
            for (Element child : elem.children()) {
                try {
                    Object value = convertToArray(child.children());
                    CVArrays cv = new CVArrays();
                    cv.setColumns((String[])cachedValue);
                    cv.setValues((String[])value);
                    JsonValue jv = factory.getConverter(CVArrays.class, JsonValue.class).convert(cv);
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
    
    protected JsonValue convertElements(Elements elements) throws ConvertionException {
        JsonObjectBuilder jbuilder = Json.createObjectBuilder();
        String parent = "";
        if (elements.isEmpty())
            throw new ConvertionException("There are no elements to be converted");
        for (Element elem : elements) {
            try {
                Object o = factory.getConverter(Element.class, Object.class).convert(elem);
                if (o == null)
                    continue;
                else if (o instanceof String) 
                    parent = (String)o;
                else if (o instanceof CVArrays) {
                    JsonValue jv = factory.getConverter(CVArrays.class, JsonValue.class).convert((CVArrays)o);
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
    
}
