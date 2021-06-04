package com.cpd.proiect.control;

public class TokenManager {
    private static Boolean tokenFlag = Boolean.FALSE;
    private static Integer tokenCounter = 0;

    /**
     * Returneaza True daca detinem token, False altfel
     * @return token
     */
    public static synchronized Boolean hasToken(){
        return tokenFlag;
    }

    /**
     * Seteaza sau reseteaza token-ul, trebuie apelata la primirea token-ului
     * si la eliberarea lui
     */
    public static synchronized void toggleTokenFlag(){
        tokenFlag = !tokenFlag;
    }

    /**
     * Seteaza counterul pentru detinerea token-ului
     */
    public static synchronized void setTokenCounter(){
        tokenCounter = 15;
    }

    /**
     * Returneaza valoarea timer-ului, 0 daca nu avem token-ul, >0 daca detinem token-ul
     * @return valoarea timer-ului
     */
    public static synchronized Integer getTokenCounter(){
        return tokenCounter;
    }

    /**
     * Apelata de un timer la fiecare secunda care decrementeaza timer-ul
     * daca nu tokenCounter > 0 inca detinem token-ul
     */
    public static synchronized void decrementTokenCounter(){
        if(tokenCounter > 0){
            tokenCounter--;
            System.out.println(tokenCounter);
        }
    }




}
