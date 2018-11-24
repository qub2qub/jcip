package net.jcip.examples.travelQuotes;

import java.util.concurrent.Callable;
import java.util.concurrent.CancellationException;

class QuoteTask implements Callable<TravelQuote> {
    private final TravelCompany company;
    private final TravelInfo travelInfo;

    public QuoteTask(TravelCompany company, TravelInfo travelInfo) {
        this.company = company;
        this.travelInfo = travelInfo;
    }

    /** вернёт дефолтное значение при ошибке */
    TravelQuote getFailureQuote(Throwable t) {
        return null;
    }

    /** вернёт дефолтное значение по истечению времени выполненияы */
    TravelQuote getTimeoutQuote(CancellationException e) {
        return null;
    }

    public TravelQuote call() throws Exception {
        // требовать; запрашивать квоты у компании (Коммерческое предложение)
        return company.solicitQuote(travelInfo);
    }
}
