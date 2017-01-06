package net.jcip.examples;

import java.util.*;
import java.util.concurrent.ScheduledThreadPoolExecutor;

import static java.util.concurrent.TimeUnit.SECONDS;

/**
 * OutOfTime
 * <p/>
 * Class illustrating confusing Timer behavior
 *
 * @author Brian Goetz and Tim Peierls
 */

public class OutOfTime {
    public static void main(String[] args) throws Exception {
        Timer timer = new Timer();
        timer.schedule(new ThrowTask("t1"), 1);
        SECONDS.sleep(1);
        timer.schedule(new ThrowTask("t2"), 1);
        SECONDS.sleep(5);
    }

    static class ThrowTask extends TimerTask {
        private String name;
        ThrowTask(String name) {
            this.name = name;
        }
        public void run() {
            System.out.println("Task = " + name + " >> started.");
            throw new RuntimeException();
        }
    }

}
