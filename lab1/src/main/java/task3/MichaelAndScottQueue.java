package task3;

import java.util.concurrent.atomic.AtomicReference;

public class MichaelAndScottQueue<T> {

    private Node<T> dummy = new Node<>(null, new AtomicReference<>(null));
    private AtomicReference<Node<T>> head = new AtomicReference<>(dummy);
    private AtomicReference<Node<T>> tail = new AtomicReference<>(dummy);

    static class Node<T> {

        public T data;
        public AtomicReference<Node<T>> next;

        public Node(T data, AtomicReference<Node<T>> next) {
            this.data = data;
            this.next = next;
        }
    }

    public T pull() {
        while (true) {
            Node<T> firstNode = head.get();
            Node<T> lastNode = tail.get();
            Node<T> nextHeadNode = firstNode.next.get();
            if (firstNode == head.get()) {
                if (firstNode == lastNode) {
                    if (nextHeadNode == null) {
                        return null;
                    }
                } else {
                    T item = firstNode.data;
                    if (head.compareAndSet(firstNode, nextHeadNode)) {
                        return item;
                    }
                }
            }
        }
    }

    public void add(T data) {
        Node<T> newNode = new Node<>(data, new AtomicReference<>(null));
        boolean success = false;
        while (!success){
            Node<T> currentTail = tail.get();
            success = currentTail.next.compareAndSet(null, newNode);
            tail.compareAndSet(currentTail, newNode);
        }
    }

    public void print() {
        Node<T> current = head.get();

        while (current != null) {
            System.out.println(current.data);
            current = current.next.get();
        }
    }
}
