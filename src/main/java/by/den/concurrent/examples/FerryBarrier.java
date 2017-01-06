package by.den.concurrent.examples;

import java.util.concurrent.CyclicBarrier;

/**
 *  Существует паромная переправа.
 *  Паром может переправлять одновременно по три автомобиля.
 *  Чтобы не гонять паром лишний раз, нужно отправлять его,
 *  когда у переправы соберется минимум три автомобиля.
 */
public class FerryBarrier {
    private static final CyclicBarrier BARRIER = new CyclicBarrier(3, new FerryBoat());
    //Инициализируем барьер на три потока и таском, который будет выполняться, когда
    //у барьера соберется три потока. После этого, они будут освобождены.

    public static void main(String[] args) throws InterruptedException {
        for (int i = 0; i < 9; i++) {
            new Thread(new Car(i)).start();
            Thread.sleep( (int) (Math.random() * 5000));
        }
    }

    //Таск, который будет выполняться при достижении сторонами барьера
    public static class FerryBoat implements Runnable {
        int tripCount = 0;
        @Override
        public void run() {
            try {
                tripCount++;
                System.out.println("(*) --> (*) Паром поехал! ["+tripCount+"]");
                Thread.sleep(600);
                System.out.println("(*) !--! (*) Паром переправил автомобили!");
                Thread.sleep(700);
                System.out.println("(*) --> (*) Паром вернулся!\n----------------------------");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    //Стороны, которые будут достигать барьера
    public static class Car implements Runnable {
        private int carNumber;

        public Car(int carNumber) {
            this.carNumber = carNumber;
        }

        @Override
        public void run() {
            try {
                System.out.printf(" --> (*) Автомобиль №%d подъехал к паромной переправе. Стало (%d) из Нужных [%d].\n",
                        carNumber, BARRIER.getNumberWaiting()+1, BARRIER.getParties());
                //Для указания потоку о том что он достиг барьера, нужно вызвать метод await()
                //После этого данный поток блокируется, и ждет пока остальные стороны достигнут барьера
                BARRIER.await();
                // вопрос: как сделать чтобы авто уехал не дождавшись переправы?
                // ответ: так нельзя, т.к. это нарушит барьер и условия для остальных ожидающих потоков.
                System.out.printf(" (*) --> Автомобиль №%d продолжил движение.\n", carNumber);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}