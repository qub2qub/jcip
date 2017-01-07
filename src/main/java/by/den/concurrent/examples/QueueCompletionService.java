package by.den.concurrent.examples;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.*;

/**
 * Created by Denis on 07 Январь 2017
 */
public class QueueCompletionService {

    void solve1(Executor e, Collection<Callable<Result>> solvers) throws InterruptedException, ExecutionException {
        CompletionService<Result> ecs = new ExecutorCompletionService<Result>(e);
        int n = solvers.size();

        for (Callable<Result> s : solvers)
            ecs.submit(s);

        for (int i = 0; i < n; ++i) {
            // берёт первый готовый результат. Блокируеся пока такого нет.
            Result r = ecs.take().get(); // и так ждёт все N результов.
            if (r != null) use(r);
        }
    }

    /**
     * Suppose instead that you would like to use the first non-null result
     * of the set of tasks, ignoring any that encounter exceptions,
     * and cancelling all other tasks when the first one is ready:
     */
    void solve2(Executor e, Collection<Callable<Result>> solvers) throws InterruptedException {
        CompletionService<Result> ecs  = new ExecutorCompletionService<Result>(e);
        int n = solvers.size();
        Result result = null;
        List<Future<Result>> futures = new ArrayList<Future<Result>>(n);
        try {

            for (Callable<Result> s : solvers)
                futures.add(ecs.submit(s));

            for (int i = 0; i < n; ++i) {
                try {
                    Result r = ecs.take().get();
                    if (r != null) {
                        result = r;
                        break;
                    }
                } catch (ExecutionException ignore) {}
            }
        }
        finally {
            for (Future<Result> f : futures)
                f.cancel(true);
        }

        if (result != null)
            use(result);
    }

    void use(Result r) {

    }

    private class Result {

    }
}
