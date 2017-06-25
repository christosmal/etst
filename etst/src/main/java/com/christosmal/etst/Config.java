/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.christosmal.etst;

/**
 * Generic definition of a configuration that can be used in a ConvertionFactory
 * @author christo
 */
public interface Config<D extends Object, C extends Object> {
    
    D getConfiguration(C c);
    
}
