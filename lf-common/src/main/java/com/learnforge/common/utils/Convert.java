package com.learnforge.common.utils;

/**
 * Perform calculation on the original object and set it to the target object
 **/
public interface Convert<R,T>{
    void convert(R origin, T target);
}