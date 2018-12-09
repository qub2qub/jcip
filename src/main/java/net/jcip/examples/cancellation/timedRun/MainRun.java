package net.jcip.examples.cancellation.timedRun;

import java.math.BigInteger;

class MainRun implements Runnable {
    @Override
    public void run() {
        String out = ".";
        System.out.println("__MainRun__ start");
        try {
            Thread.sleep(1000);
            System.out.println("__MainRun__ middle1 before exc, thread active count=" + Thread.activeCount());
            // закоментируй чтобы дождаться таймаута из timedRun()
            // иначе пойдём по пути RethrowableTask.rethrow()
//            if(true) throw new RuntimeException(" unexpected unchecked exc  in Main Task !!");
            Thread.sleep(1000);
            System.out.println("__MainRun__ middle2 after exc");
            Thread.sleep(1000);
            System.out.println("__MainRun__ middle3 middle fin");
            BigInteger one = BigInteger.ONE;
            // to get timeout -- use "while (true)" но при этом поток всё равно продолжит выполнение
//                    while (true) {
            // а тут прервётся, т.к. отреагирует на taskThread.interrupt() из timedRun
            while (!Thread.currentThread().isInterrupted()) {
                if (".".equals(out)) out = ""; else out = ".";
                System.out.print(out);
                one = one.nextProbablePrime();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            System.out.println("__MainRun__ finally end");
        }
    }
}
