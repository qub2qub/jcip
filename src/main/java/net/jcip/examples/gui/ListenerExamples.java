package net.jcip.examples.gui;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;
import java.util.concurrent.*;
import javax.swing.*;

public class ListenerExamples {
    private static ExecutorService exec = Executors.newCachedThreadPool();

    private final JButton colorButton = new JButton("Change color");
    private final Random random = new Random();

    private void backgroundRandom() {
        colorButton.addActionListener(e -> colorButton.setBackground(new Color(random.nextInt())));
    }

    private final JButton computeButton = new JButton("Big computation");

    private void longRunningTask() {
        computeButton.addActionListener(e -> {
            // выполняет в кэше и результат потом никак не обрабатывается
            exec.execute(() -> { /* Do some big computation */ });
        });
    }

    //*************************************** part 1 *********************************************

    private final JButton button = new JButton("Do");
    private final JLabel label = new JLabel("idle");

    private void longRunningTaskWithFeedback() {
        button.addActionListener(e -> {
            // 1) The first subtask updates the user interface to show
            // that a long-running operation has begun and
            button.setEnabled(false);
            label.setText("busy");

            // 2) starts the second subtask in a background thread.
            // результат потом в конце запускает подзадачу№3 для обновлению UI
            // для этого использует утилиту чтобы передать ивэнт в event thread.
            exec.execute(new Runnable() {
                public void run() {
                    try {
                        /* Do big computation */
                    } finally {
                        // 3)  Upon completion, the second subtask queues the third subtask
                        // to run again in the event thread, which updates the user interface
                        // to reflect that the operation has completed.
                        GuiExecutor.instance().execute(() -> {
                            button.setEnabled(true);
                            label.setText("idle");
                        });
                    }
                }
            });
        });
    }

    //*************************************** part 2 *********************************************

    private final JButton startButton = new JButton("Start");
    private final JButton cancelButton = new JButton("Cancel");
    private Future<?> runningTask = null; // thread-confined

    private void longRunningTaskWithCancellation() {
        // actionPerformed
        startButton.addActionListener(e -> {
            if (runningTask == null) {
                // в каком потоке будет выполняться задача?
                runningTask = exec.submit(new Runnable() {
                    private boolean moreWork() { return false; }
                    private void cleanUpPartialWork() { }
                    private void doSomeWork() { }

                    public void run() {
                        while (moreWork()) {
                            if (Thread.currentThread().isInterrupted()) {
                                cleanUpPartialWork();
                                break;
                            }
                            // и как получаются результаты?
                            doSomeWork();
                        }
                    } // run
                }); // submit
            }; // if
        });

        cancelButton.addActionListener(event -> {
            if (runningTask != null)
                runningTask.cancel(true);
        });
    }

    //*************************************** part 3 *********************************************

    private void runInBackground(final Runnable task) {
        startButton.addActionListener(e -> {
            class CancelListener implements ActionListener {
                private BackgroundTask<?> backgroundTask; // он явл-ся и future и runnable одновременно
                public void actionPerformed(ActionEvent event) {
                    if (backgroundTask != null) {
                        backgroundTask.cancel(true);
                    }
                }
            }
            // создадим экземпляр локального класса
            final CancelListener cancelListener = new CancelListener();

            // generic-ом задали Void, это значит что ничего из compute() не возвращается
            cancelListener.backgroundTask = new BackgroundTask<Void>() {
                @Override
                public Void compute() {
                    while (moreWork() && !isCancelled()) {
                        doSomeWork();
                    }
                    return null;
                }
                private boolean moreWork() {
                    return false;
                }
                private void doSomeWork() {
                    // какие-то вычисления
                    // например обновляем прогресс после начала
                    setProgress(25, 100);
                    // .. другие вычисления
                    // .. и снова обновляем прогресс
                    setProgress(75, 100);
                    // и т.п., делаем оставшиеся вычисления
                    // .. и снова обновляем прогресс
                    setProgress(100, 100);
                }
                @Override
                protected void onCompletion(Void result, Throwable exception, boolean cancelled) {
                    cancelButton.removeActionListener(cancelListener);
                    label.setText("done");
                }

            }; // end of new BackgroundTask<Void>()
            cancelButton.addActionListener(cancelListener);
            exec.execute(task);
        }); //addActionListener
    }
}
