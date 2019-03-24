package net.jcip.examples.nonBlocking;

import java.util.concurrent.atomic.*;

import net.jcip.annotations.*;

/**
 * Insertion in the Michael & Scott nonblocking queue algorithm, which is used by ConcurrentLinkedQueue
 */
@ThreadSafe
public class LinkedQueue <E> {

    private static class LinkedQueueNode<E> {
        final E item;
        final AtomicReference<LinkedQueueNode<E>> next;

        public LinkedQueueNode(E item, LinkedQueueNode<E> next) {
            this.item = item;
            this.next = new AtomicReference<>(next);
        }
    }

    private final LinkedQueueNode<E> dummy = new LinkedQueueNode<>(null, null);
    private final AtomicReference<LinkedQueueNode<E>> head = new AtomicReference<>(dummy);
    private final AtomicReference<LinkedQueueNode<E>> tail = new AtomicReference<>(dummy);

    public boolean put(E item) {
        LinkedQueueNode<E> newNode = new LinkedQueueNode<E>(item, null);
        while (true) {
            LinkedQueueNode<E> curTail = tail.get();
            LinkedQueueNode<E> tailNext = curTail.next.get();
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
                    // это за него уже сделал другой поток.]
                    if (curTail.next.compareAndSet(null, newNode)) { // CCCCC
                        // Insertion succeeded, try advancing tail, could fail but still return true
                        tail.compareAndSet(curTail, newNode); // DDDDD
                        // В любом случае поток выйдет из while (true) даже если не пердвинет хвост на новый нод;
                        // главное что в этом if он уже добавил ссылку на свой новый нод в curTail.next
                        return true;
                    }
                } // else -- tailNext != null
            } // curTail == tail.get()
        } //while
    }
}
