package by.den.concurrent.examples;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * https://habrahabr.ru/post/277669/
 * Существует парковка, которая одновременно может вмещать не более 5 автомобилей.
 * Если парковка заполнена полностью, то вновь прибывший автомобиль должен подождать
 * пока не освободится хотя бы одно место. После этого он сможет припарковаться.
 */
public class ParkingSemaphore {

    // Парковочное место занято - true, свободно - false
    private static final boolean[] PARKING_PLACES = new boolean[5];

    // Устанавливаем флаг "справедливый", в таком случае метод
    // aсquire() будет раздавать разрешения в порядке очереди
    private static final Semaphore SEMAPHORE = new Semaphore(5, true);

    private static DateTimeFormatter time = DateTimeFormatter.ofPattern("HH:mm:ss.SSS");

    private static final int N_CARS = 15;
    private static final AtomicInteger carsLeft = new AtomicInteger(0);

    public static void main(String[] args) throws InterruptedException {
        // Запуск чекера
        new Thread(new Checker()).start();

        for (int i = 1; i <= N_CARS; i++) {
            new Thread(new Car(i)).start();
            Thread.sleep( (int) (Math.random() * 2000));
        }
    }

    public static class Car implements Runnable {
        private int carNumber;

        public Car(int carNumber) {
            this.carNumber = carNumber;
        }

        @Override
        public void run() {
            System.out.printf(LocalTime.now().format(time)
                    +" +++ Автомобиль №%d подъехал к парковке.\n",
                    carNumber);
//            System.out.printf("СВОБОДНО (%d), Очередь из [%d]\n",
//                    SEMAPHORE.availablePermits(), SEMAPHORE.getQueueLength());
            try {
                //acquire() запрашивает доступ к следующему за вызовом этого метода блоку кода,
                //если доступ не разрешен, поток вызвавший этот метод блокируется до тех пор,
                //пока семафор не разрешит доступ
                SEMAPHORE.acquire();

                int parkingNumber = -1;

                //Ищем свободное место и паркуемся
                synchronized (PARKING_PLACES){
                    for (int i = 0; i < 5; i++)
                        if (!PARKING_PLACES[i]) {      //Если место свободно
                            PARKING_PLACES[i] = true;  //занимаем его
                            parkingNumber = i;         //Наличие свободного места, гарантирует семафор
                            System.out.printf(LocalTime.now().format(time)
                                    +" *"+i+"* Автомобиль №%d припарковался на месте %d.\n", carNumber, i);
                            break;
                        }
                }

                //Уходим за покупками, к примеру
                Thread.sleep( 3000 + (int) (Math.random() * 7000));

                synchronized (PARKING_PLACES) {
                    PARKING_PLACES[parkingNumber] = false;//Освобождаем место
                }
                
                //release(), напротив, освобождает ресурс
                SEMAPHORE.release();
                System.out.printf(LocalTime.now().format(time)
                        +" --- Автомобиль №%d покинул парковку.\n", carNumber);
                carsLeft.incrementAndGet(); // засчитываем что авто уехало.
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public static class Checker implements Runnable {
        @Override
        public void run() {
            while(true) {
                System.out.printf("_____ free= (%d), queue=[%d] _____\n",
                        SEMAPHORE.availablePermits(), SEMAPHORE.getQueueLength());
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                // а если бы надо было проверить что уехало только половина?
                if (N_CARS == carsLeft.get()) {
                    System.out.println(" Все уехали :( ");
                    break; // exit infinite loop
                }
            }
        }
    }

}