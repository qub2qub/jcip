package net.jcip.examples.threadPools;

import java.util.*;
import java.util.concurrent.*;

/**
 * Concurrent version of puzzle solver
 */
public class ConcurrentPuzzleSolver <P, M> {
    private final Puzzle<P, M> puzzle;
    private final ExecutorService executor;
    private final ConcurrentMap<P, Boolean> seen;
    protected final ValueLatch<PuzzleNode<P, M>> solution = new ValueLatch<>();

    public ConcurrentPuzzleSolver(Puzzle<P, M> puzzle) {
        this.puzzle = puzzle;
        this.executor = initThreadPool();
        this.seen = new ConcurrentHashMap<>();
        if (executor instanceof ThreadPoolExecutor) {
            ThreadPoolExecutor tpe = (ThreadPoolExecutor) executor;
            tpe.setRejectedExecutionHandler(new ThreadPoolExecutor.DiscardPolicy());
        }
    }

    private ExecutorService initThreadPool() {
        return Executors.newCachedThreadPool();
    }

    public List<M> solve() throws InterruptedException {
        try {
            P p = puzzle.initialPosition();
            executor.execute(newTask4Puzzle(p, null, null));
            // block until solution found
            PuzzleNode<P, M> solnPuzzleNode = solution.getValue();
            return (solnPuzzleNode == null) ? null : solnPuzzleNode.asMoveList();
        } finally {
            executor.shutdown();
        }
    }

    protected Runnable newTask4Puzzle(P pos, M move, PuzzleNode<P, M> node) {
        return new SolverTask(pos, move, node);
    }

    /** Отдельная задача 1 */
    protected class SolverTask extends PuzzleNode<P, M> implements Runnable {
        SolverTask(P pos, M move, PuzzleNode<P, M> prev) {
            super(pos, move, prev);
        }

        public void run() {
            if (solution.isSet() || seen.putIfAbsent(pos, true) != null) {
                return; // already solved or seen this position
            }
            if (puzzle.isGoal(pos)) {
                solution.setValue(this);
            } else {
                for (M m : puzzle.legalMoves(pos)) {
                    executor.execute(newTask4Puzzle(puzzle.move(pos, m), m, this));
                }
            }
        }
    } // SolverTask
}
