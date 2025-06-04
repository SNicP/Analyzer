package ru.netology;

import java.util.Random;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class Main {
    private static final String stringPattern = "abc";
    private static final int stringCount = 10_000;
    private static final int stringLength = 100_000;

    private static final BlockingQueue<String> stringsA = new ArrayBlockingQueue<>(100);
    private static final BlockingQueue<String> stringsB = new ArrayBlockingQueue<>(100);
    private static final BlockingQueue<String> stringsC = new ArrayBlockingQueue<>(100);

    public static void main(String[] args) {
        Thread generateStrings = new Thread(() -> {
            for (int i = 0; i < stringCount; i++) {
                String string = generateText(stringPattern, stringLength);
                try {
                    stringsA.put(string);
                    stringsB.put(string);
                    stringsC.put(string);
                } catch (InterruptedException e) {
                    return;
                }
            }
        });

        generateStrings.start();

        Thread countCharA = new Thread(() -> {
            int counterCharA;
            int max = 0;
            char charToFind = 'a';
            for (int i = 0; i < stringCount; i++) {
                try {
                    String stringA = stringsA.take();
                    counterCharA = counter(stringA, charToFind);
                    if (counterCharA > max){
                        max = counterCharA;
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            System.out.println("Максимальное количество символа 'a' " + max);
        });

        countCharA.start();

        Thread countCharB = new Thread(() -> {
            int counterCharB;
            int max = 0;
            char charToFind = 'b';
            for (int i = 0; i < stringCount; i++) {
                try {
                    String stringB = stringsB.take();
                    counterCharB = counter(stringB, charToFind);
                    if (counterCharB > max){
                        max = counterCharB;
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            System.out.println("Максимальное количество символа 'b' " + max);
        });

        countCharB.start();

        Thread countCharC = new Thread(() -> {
            int counterCharC;
            int max = 0;
            char charToFind = 'c';
            for (int i = 0; i < stringCount; i++) {
                try {
                    String stringC = stringsC.take();
                    counterCharC = counter(stringC, charToFind);
                    if (counterCharC > max){
                        max = counterCharC;
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            System.out.println("Максимальное количество символа 'c' " + max);
        });

        countCharC.start();


    }

    public static String generateText(String letters, int length) {
        Random random = new Random();
        StringBuilder text = new StringBuilder();
        for (int i = 0; i < length; i++) {
            text.append(letters.charAt(random.nextInt(letters.length())));
        }
        return text.toString();
    }

    public static int counter(String string, char charToFind) {
        int count = 0;
        char[] chars = string.toCharArray();
        for (char ch : chars) {
            if (ch == charToFind) count++;
        }
        return count;
    }
}