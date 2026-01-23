package com.priyakdey;

/**
 * @author Priyak Dey
 */
public class Main {

    static boolean a() {
        System.out.println("a is executed");
        return false;
    }

    static boolean b() {
        System.out.println("b is executed");
        return true;
    }

    static void main() {
        if  (a() && b()) {
            System.out.println("hello");
        }

    }

}
