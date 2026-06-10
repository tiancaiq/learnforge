package com.learnforge.common.validate;

/**
 * Implement this interface after, and when accessing the interface, if the interface implements this interface
 * It will be automatically validated by the interface check
 **/
public interface Checker<T> {

    /**
     * Used to implement validation logic that cannot be validated
     */
    default void check(){

    }

    default void check(T data){
    }
}