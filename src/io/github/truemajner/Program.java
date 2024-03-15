package io.github.truemajner;

public class Program {

    public static void main(String[] args) {
        while (true) {
            byte value = Utils.getRandomByte(0, 64);
            System.out.println(value);
            if(value == 63) {
                break;
            }
        }
    }
}