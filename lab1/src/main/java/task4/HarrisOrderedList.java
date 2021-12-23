package task4;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

public class HarrisOrderedList<T extends Comparable<? super T>> {

    private Node<T> head = new Node<>(null, new AtomicReference<>(null));

    static class Node<T> {

        public T data;
        public AtomicReference<Node<T>> next;
        public AtomicReference<Thread> markedOnDeleteBy = new AtomicReference<>(null);

        public Node(T data, AtomicReference<Node<T>> next) {
            this.data = data;
            this.next = next;
        }
    }

    public boolean remove(T data) {
        if (data == null) {
            throw new IllegalArgumentException("Argument should not be null");
        }

        Node<T> prevNode = head;
        while (prevNode.next.get() != null) {
            Node<T> currNode = prevNode.next.get();
            Node<T> nextNode = currNode.next.get();

            if (currNode.data.compareTo(data) == 0) {
                while (true){
                    if (currNode.markedOnDeleteBy.compareAndSet(null, Thread.currentThread())){
                        Thread.yield();
                    }else if (currNode.markedOnDeleteBy.get().equals(Thread.currentThread())){
                        currNode.next.compareAndSet(nextNode, null);
                        prevNode.next.compareAndSet(currNode, nextNode);
                        return true;
                    }else {
                        return false;
                    }
                }
            } else {
                prevNode = currNode;
            }
        }
        return false;
    }

    public void add(T data) {
        if (isNull(data)) {
            throw new IllegalArgumentException("Argument should not be null");
        }

        Node<T> newNode = new Node<>(data, new AtomicReference<>(null));
        Node<T> currentNode = head;

        while (true) {
            Node<T> nextNode = currentNode.next.get();

            if (nextNode != null) {
                if (nextNode.data.compareTo(data) >= 0) {
                    newNode.next = new AtomicReference<>(nextNode);
                    if (currentNode.next.compareAndSet(nextNode, newNode)) {
                        return;
                    }
                } else {
                    currentNode = nextNode;
                }
            } else if (currentNode.next.compareAndSet(null, newNode)) {
                return;
            }
        }
    }

    public boolean contains(T data) {
        Node<T> currentNode = head.next.get();
        while (currentNode != null) {
            if (currentNode.data.compareTo(data) == 0) {
                return true;
            }
            currentNode = currentNode.next.get();
        }
        return false;
    }

    public void nonSafePrint() {
        Node<T> current = head.next.get();
        while (nonNull(current)) {
            System.out.println(current.data);
            current = current.next.get();
        }
    }
}
