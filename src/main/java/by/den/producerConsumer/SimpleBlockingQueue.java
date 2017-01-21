package by.den.producerConsumer;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

class SimpleBlockingQueue {

    class ProducerBQ implements Runnable {
        private final BlockingQueue queue;
        ProducerBQ(BlockingQueue q) { queue = q; }

        Object produce() { return new Object(); }

        public void run() {
            try {
                while (true) { queue.put(produce()); }
            } catch (InterruptedException ex) {/* ... handle ...*/}
        }
    }

    class ConsumerBQ implements Runnable {
        private final BlockingQueue queue;
        ConsumerBQ(BlockingQueue q) { queue = q; }

        void consume(Object x) { /* Do smth */ }

        public void run() {
            try {
                while (true) { consume(queue.take()); }
            } catch (InterruptedException ex) { /*... handle ...*/}
        }

    }

    public static void main(String[] args) {
        SimpleBlockingQueue example = new SimpleBlockingQueue();
        BlockingQueue q = new LinkedBlockingQueue();
        ProducerBQ p = example.new ProducerBQ(q);
        ConsumerBQ c1 = example.new ConsumerBQ(q);
        ConsumerBQ c2 = example.new ConsumerBQ(q);
        new Thread(p).start();
        new Thread(c1).start();
        new Thread(c2).start();
    }
}