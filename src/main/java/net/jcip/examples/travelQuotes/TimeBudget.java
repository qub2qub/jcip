package net.jcip.examples.travelQuotes;

import java.util.*;
import java.util.concurrent.*;

/**
 * Requesting travel quotes under a time budget
 */
public class TimeBudget {
    private static ExecutorService exec = Executors.newCachedThreadPool();

    public List<TravelQuote> getRankedTravelQuotes(
            TravelInfo travelInfo, Set<TravelCompany> companies, Comparator<TravelQuote> ranking,
            long time, TimeUnit unit) throws InterruptedException {

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
                offerResults.add(f.get());
                // т.е. смотря в какой future будет ошибка -->
                // возьмём дефолтные данные из того-же callable task
            } catch (ExecutionException e) {
                // например, была ошибка по время выполенения (или сетевая проблема)
                offerResults.add(task.getFailureQuote(e.getCause()));
            } catch (CancellationException e) {
                // например, отменилась задача по истечению времени
                offerResults.add(task.getTimeoutQuote(e));
            }
        }
        offerResults.sort(ranking);
        return offerResults;
    }

}
