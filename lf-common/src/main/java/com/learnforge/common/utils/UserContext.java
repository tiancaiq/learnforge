package com.learnforge.common.utils;

public class UserContext {
    private static final ThreadLocal<Long> TL = new ThreadLocal<>();

    /**
     * Save user information
     * @param userId user id
     */
    public static void setUser(Long userId){
        TL.set(userId);
    }

    /**
     * Get user
     * @return user id
     */
    public static Long getUser(){
        return TL.get();
    }

    /**
     * Remove user information
     */
    public static void removeUser(){
        TL.remove();
    }
}
