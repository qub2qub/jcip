package net.jcip.examples.memoization;

interface Computable <A, V> {
    V compute(A arg) throws InterruptedException;
}