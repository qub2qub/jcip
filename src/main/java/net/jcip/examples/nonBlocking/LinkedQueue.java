package net.jcip.examples.nonBlocking;

import java.util.concurrent.atomic.*;

import net.jcip.annotations.*;

/**
 * Insertion in the Michael-Scott nonblocking queue algorithm
 */
@ThreadSafe
public class LinkedQueue <E> {

    private static class Node <E> {
        final E item;
        final AtomicReference<LinkedQueue.Node<E>> next;

        public Node(E item, LinkedQueue.Node<E> next) {
            this.item = item;
            this.next = new AtomicReference<LinkedQueue.Node<E>>(next);
        }
    }

    private final LinkedQueue.Node<E> dummy = new LinkedQueue.Node<E>(null, null);
    private final AtomicReference<LinkedQueue.Node<E>> head
            = new AtomicReference<LinkedQueue.Node<E>>(dummy);
    private final AtomicReference<LinkedQueue.Node<E>> tail
            = new AtomicReference<LinkedQueue.Node<E>>(dummy);

    public boolean put(E item) {
        LinkedQueue.Node<E> newNode = new LinkedQueue.Node<E>(item, null);
        while (true) {
            LinkedQueue.Node<E> curTail = tail.get();
            LinkedQueue.Node<E> tailNext = curTail.next.get();
            /*
            A) LinkedQueue.put() first checks to see if the queue is in the
            intermediate state before attempting to insert a new element (step A).

            B) If it is, then some other thread is already in the process of
            inserting an element (between its steps C and D).
            Rather than wait for that thread to finish, the current thread helps it
            by finishing the operation for it, advancing(продвигая вперёд)
             the tail pointer (step B).

            C) It then repeats this check in case another thread has started inserting
            a new element, advancing the tail pointer until it finds the queue in the quiescent
            state so it can begin its own insertion.
             */
            if (curTail == tail.get()) { // если последний нод уже изменился -- запускай всё сначала
                if (tailNext != null) { // AAAAA
                    // Queue in intermediate state, advance tail
                    tail.compareAndSet(curTail, tailNext); // BBBBB
                } else {
                    // In quiescent state, try inserting new node
                    // [если поток сможет добавить новый нод -- то в этом if будет true --
                    // и, значит, поток уже по-любому выйдет из while(true),
                    // даже если он не успеет передвинуть указатель хвоста.
                    // это за него сделает другой поток.]
                    if (curTail.next.compareAndSet(null, newNode)) { // CCCCC
                        // Insertion succeeded, try advancing tail
                        tail.compareAndSet(curTail, newNode); // DDDDD
                        // А в любом ли случае поток дойдёт до выхода из while (true) ?
                        // всегда??, т.к. раз уж он попал в этот if -- то он и передвинет? НЕ ФАКТ!!
                        // тогда (если не пердвинет -- то метод tail.compareAndSet(..) вернёт false)
                        // но это неважно, т.к. текущий поток всё равно здесь выйдет из while (true).
                        // главное что в этом if он уже добавил свой новый нод
                        return true;
                    }
                } // else -- tailNext != null
            } // curTail == tail.get()
        } //while
    }
}
