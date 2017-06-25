/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.christosmal.etst;

/**
 * Abstract definition to convert something to something else
 * @author christo
 */
public interface Converter<T extends Object, U extends Object> {
    
    // Usually throwing Exception is not best practice. But due to the very generic nature of the convert method and not having
    // to re-define wrapping methods for all the lambda's used in the ConvertionFactory, we will let this
    // one slide for now. To fix later if time permits ...
    U convert(T c) throws Exception;
    
}
