package ru.netology;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class Main {
    private static final String STRING_PATTERN = "abc";
    private static final int STRING_QUANTITY = 10_000;
    private static final int STRING_SIZE = 100_000;
    public static final int QUEUE_SIZE = 100;

    public static final List<BlockingQueue<String>> queues = Arrays.asList(
            new ArrayBlockingQueue<>(QUEUE_SIZE),
            new ArrayBlockingQueue<>(QUEUE_SIZE),
            new ArrayBlockingQueue<>(QUEUE_SIZE)
    );

    public static void main(String[] args) throws InterruptedException {
        Thread generateStrings = new Thread(() -> {
            for (int i = 0; i < STRING_QUANTITY; i++) {
                String string = generateText(STRING_PATTERN, STRING_SIZE);
                try {
                    for (BlockingQueue<String> queue : queues) {
                        queue.put(string);
                    }
                } catch (InterruptedException e) {
                    return;
                }
            }
        });

        generateStrings.start();

        List<Thread> consumers = new ArrayList<>();
        for (int i = 0; i < queues.size(); i++) {
            int index = i;
            char c = (char) ('a' + i);
            Thread consumer = new Thread(() -> {
                BlockingQueue<String> queue = queues.get(index);
                String maxString = "";
                long maxCount = 0;
                for (int j = 0; j < STRING_QUANTITY; j++) {
                    try{String s = queue.take();
                        long count = countChar(s, c);
                        if (count > maxCount) {
                            maxCount = count;
                            maxString = s;
                        }
                    } catch (InterruptedException e) {
                        return;
                    }
                }
                System.out.printf("Max count of letter %c - %d - found in the following string: " +
                        "%s\n", c, maxCount, maxString.substring(0, 75) + "...");
            });
            consumer.start();
            consumers.add(consumer);
        }

        for (Thread consumer : consumers) {
            consumer.join();
        }
    }

    public static String generateText(String letters, int length) {
        Random random = new Random();
        StringBuilder text = new StringBuilder();
        for (int i = 0; i < length; i++) {
            text.append(letters.charAt(random.nextInt(letters.length())));
        }
        return text.toString();
    }

    public static int countChar(String string, char charToFind) {
        int count = 0;
        char[] chars = string.toCharArray();
        for (char ch : chars) {
            if (ch == charToFind) count++;
        }
        return count;
    }
}