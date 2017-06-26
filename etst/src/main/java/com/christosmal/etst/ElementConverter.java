/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.christosmal.etst;

import org.jsoup.nodes.Element;

/**
 *
 * @author christo
 */
public class ElementConverter extends ElementConvertion implements Converter<Element, Object> {

    public ElementConverter(ConvertionFactory f) {
        super(f);
    }

    @Override
    public Object convert(Element c) throws Exception {
        return convertElement((Element)c);
    }
    
}
