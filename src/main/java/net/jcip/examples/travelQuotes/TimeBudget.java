package net.jcip.examples.travelQuotes;

import java.util.*;
import java.util.concurrent.*;

/**
 * Requesting travel quotes under a time budget
 */
public class TimeBudget {
    private static ExecutorService exec = Executors.newCachedThreadPool();

    public List<TravelQuote> getRankedTravelQuotes(
            TravelInfo travelInfo,
            Set<TravelCompany> companies,
            Comparator<TravelQuote> ranking,
            long time,
            TimeUnit unit
    ) throws InterruptedException {

        List<QuoteTask> n1tasks = new ArrayList<>();

        for (TravelCompany company : companies) {
            // заполняет массив Callable
            n1tasks.add(new QuoteTask(company, travelInfo));
        }

        // invokeAll -- добавляет в том же порядке, как и исходная коллекция
        List<Future<TravelQuote>> n2futures = exec.invokeAll(n1tasks, time, unit);

        List<TravelQuote> offerResults = new ArrayList<>(n1tasks.size());
        Iterator<QuoteTask> n1taskIterator = n1tasks.iterator();

        for (Future<TravelQuote> f : n2futures) {
            // порядок между 2мя итераторами будет соответствовать друг другу
            QuoteTask task = n1taskIterator.next();
            try {
                // т.к. вызвали  invokeAll(TimeOut) то какие-то задачи успели выполниться, другие - нет.
                // f.get() вернёт норм результат для той, что успела выполнится
                offerResults.add(f.get());
                // а которые не успели - выбросяк какой-то из этих Exception-ов
                // и мы возьмём дефолтные значения из того-же QuoteTask для этой Future
            } catch (ExecutionException e) {
                // случалиась какоя-то ошибка во время выполенения (или была сетевая проблема)
                offerResults.add(task.getFailureQuote(e.getCause()));
            } catch (CancellationException e) {
                // задача отменилась из-за истечения отведённого времени
                offerResults.add(task.getTimeoutQuote(e));
            }
        }
        offerResults.sort(ranking);
        return offerResults;
    }

}
