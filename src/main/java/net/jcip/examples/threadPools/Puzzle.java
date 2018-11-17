package net.jcip.examples.threadPools;

import java.util.*;

/**
 * Abstraction for puzzles like the 'sliding blocks puzzle'
 */
public interface Puzzle <P, M> {
    P initialPosition();

    boolean isGoal(P position);

    Set<M> legalMoves(P position);

    P move(P position, M move);
}
