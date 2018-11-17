package net.jcip.examples.threadPools;

import java.util.concurrent.atomic.*;

/**
 * Solver that recognizes when no solution exists
 */
public class PuzzleSolver <P,M> extends ConcurrentPuzzleSolver<P, M> {

    PuzzleSolver(Puzzle<P, M> puzzle) {
        super(puzzle);
    }

    private final AtomicInteger taskCount = new AtomicInteger(0);

    @Override
    protected Runnable newTask4Puzzle(P pos, M move, PuzzleNode<P, M> node) {
        return new CountingSolverTask(pos, move, node);
    }

    /**
     * Отдельная задача 2
     */
    class CountingSolverTask extends SolverTask {

        CountingSolverTask(P pos, M move, PuzzleNode<P, M> prev) {
            super(pos, move, prev);
            taskCount.incrementAndGet();
        }

        @Override
        public void run() {
            try {
                super.run();
            } finally {
                if (taskCount.decrementAndGet() == 0)
                    solution.setValue(null);
            }
        }
    }
}
