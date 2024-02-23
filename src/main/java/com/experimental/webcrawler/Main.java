package com.experimental.webcrawler;

public class Main {
    public static void main(String[] args) {
        DataCalculator dataCalculator = new DataCalculator();
        
        // Create a new thread
        Thread thread = new Thread(dataCalculator);

        // Start the thread
        thread.start();

        // Continue doing other work in the main thread
        while (thread.isAlive()) {
            // Get the calculated data so far
            int calculatedData = dataCalculator.getCalculatedData();
            System.out.println("Calculated data so far: " + calculatedData);

            try {
                // Sleep for a while before checking again
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    static class DataCalculator implements Runnable {
        private volatile int calculatedData;

        @Override
        public void run() {
            // Simulate some work to calculate data
            for (int i = 1; i <= 10; i++) {
                // Calculate data
                calculatedData += i;

                try {
                    // Simulate some processing time
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        public int getCalculatedData() {
            return calculatedData;
        }
    }

}
