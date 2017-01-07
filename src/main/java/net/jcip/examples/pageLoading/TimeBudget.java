package net.jcip.examples.pageLoading;

import java.util.*;
import java.util.concurrent.*;

/**
 * QuoteTask
 * <p/>
 * Requesting travel quotes under a time budget
 *
 * @author Brian Goetz and Tim Peierls
 */
public class TimeBudget {
    private static ExecutorService exec = Executors.newCachedThreadPool();

    public List<TravelQuote> getRankedTravelQuotes(
            TravelInfo travelInfo, Set<TravelCompany> companies, Comparator<TravelQuote> ranking, long time, TimeUnit unit)
            throws InterruptedException {

        List<QuoteTask> n1tasks = new ArrayList<QuoteTask>();

        for (TravelCompany company : companies)
            // заполняет массив Callable
            n1tasks.add(new QuoteTask(company, travelInfo));

        // invokeAll -- добавляет в том же порядке, как и исходная коллекция
        List<Future<TravelQuote>> n2futures = exec.invokeAll(n1tasks, time, unit);

        List<TravelQuote> offerResults = new ArrayList<TravelQuote>(n1tasks.size());
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

        Collections.sort(offerResults, ranking);
        return offerResults;
    }

}

class QuoteTask implements Callable<TravelQuote> {
    private final TravelCompany company;
    private final TravelInfo travelInfo;

    public QuoteTask(TravelCompany company, TravelInfo travelInfo) {
        this.company = company;
        this.travelInfo = travelInfo;
    }

    /**
     * вернёт дефолтное значение при ошибке
     */
    TravelQuote getFailureQuote(Throwable t) {
        return null;
    }

    /**
     * вернёт дефолтное значение по истечению времени выполненияы
     */
    TravelQuote getTimeoutQuote(CancellationException e) {
        return null;
    }

    public TravelQuote call() throws Exception {
        // требовать; запрашивать квоты у компании ( Коммерческое предложение)
        return company.solicitQuote(travelInfo);
    }
}

interface TravelCompany {
    TravelQuote solicitQuote(TravelInfo travelInfo) throws Exception;
}

interface TravelQuote {
}

interface TravelInfo {
}

