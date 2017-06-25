/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.christosmal.etst;

/**
 * An Exception that occurs when a convertion cannot happen
 * @author christo
 */
public class ConvertionException extends Exception {
    
    public ConvertionException(Exception cause) {
        super(cause);
    }
    
    public ConvertionException(String s) {
        super(s);
    }
    
}
