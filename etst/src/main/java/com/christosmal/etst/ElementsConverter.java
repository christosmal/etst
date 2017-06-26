/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.christosmal.etst;

import javax.json.JsonObject;
import org.jsoup.select.Elements;

/**
 *
 * @author christo
 */
public class ElementsConverter extends ElementConvertion implements Converter<Elements, JsonObject> {

    public ElementsConverter(ConvertionFactory f) {
        super(f);
    }

    @Override
    public JsonObject convert(Elements c) throws Exception {
        return (JsonObject)convertElements((Elements)c);
    }
    
}
