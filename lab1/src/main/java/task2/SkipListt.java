package task2;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

public class SkipListt<T extends Comparable<? super T>> {

    public static final int LEVEL_ONE = 1;
    private final int height;
    private final double probability;
    private final Node<T> head;

    static class Node<T> {

        public T data;
        public AtomicReference<Node<T>> right;
        public Node<T> down;
        public AtomicReference<Thread> markedOnDeleteBy = new AtomicReference<>(null);

        public Node(T data, AtomicReference<Node<T>> right, Node<T> down) {
            this.data = data;
            this.right = right;
            this.down = down;
        }
    }

    public SkipListt(int height, double probability) {
        this.height = height;
        this.probability = probability;

        Node<T> initHeadNode = new Node<>(null, new AtomicReference<>(null), null);
        head = initHeadNode;

        for (int i = 0; i < height - 1; i++) {
            Node<T> newElementHead = new Node<>(null, new AtomicReference<>(null), null);
            initHeadNode.down = newElementHead;
            initHeadNode = newElementHead;
        }
    }

    public boolean remove(T data) {
        if (isNull(data)) {
            throw new IllegalArgumentException("Data can not be null");
        }

        Node<T> currNode = head;
        int currentLevel = height;
        boolean isRemoveHappened = false;

        while (currentLevel > 0) {
            Node<T> rightNode = currNode.right.get();
            if (rightNode != null && rightNode.data.compareTo(data) == 0) {
                Node<T> afterRightNode = rightNode.right.get();
                Node<T> toDeleteNode = rightNode;
                while (true){
                    if (toDeleteNode.markedOnDeleteBy.compareAndSet(null, Thread.currentThread())){
                        Thread.yield();
                    }else if (toDeleteNode.markedOnDeleteBy.get().equals(Thread.currentThread())){
                        toDeleteNode.right.compareAndSet(toDeleteNode.right.get(), null);
                        currNode.right.compareAndSet(rightNode, afterRightNode);
                        isRemoveHappened = true;
                        break;
                    }else {
                        return false;
                    }
                }

            }

            if (rightNode != null && rightNode.data.compareTo(data) < 0) {
                currNode = rightNode;
            } else {
                currNode = currNode.down;
                currentLevel--;
            }
        }

        return isRemoveHappened;
    }

    public boolean add(T data) {
        if (data == null) {
            throw new IllegalArgumentException("Data can not be null");
        }

        final List<Node<T>> nodesInLeft = new ArrayList<>();
        final List<Node<T>> nodesOnRight = new ArrayList<>();
        final int levelOfElementAppear = computeHeightToElementsAppeare();

        collectLeftAndRightNodes(data, nodesInLeft, nodesOnRight, levelOfElementAppear);

        return tryAddElements(data, nodesInLeft, nodesOnRight);
    }

    private boolean tryAddElements(T data, List<Node<T>> nodesOnLeft, List<Node<T>> nodesOnRight) {
        Node<T> DownNode = null;
        for (int i = nodesOnLeft.size() - 1; i >= 0; i--) {
            Node<T> newNode = new Node<>(data, new AtomicReference<>(nodesOnRight.get(i)), null);

            if (DownNode != null) {
                newNode.down = DownNode;
            }

            if (!nodesOnLeft.get(i).right.compareAndSet(nodesOnRight.get(i), newNode)) {
                return false;
            }

            DownNode = newNode;
        }

        return true;
    }

    private void collectLeftAndRightNodes(T data, List<Node<T>> nodesInLeft, List<Node<T>> nodesOnRight, int levelOfElementAppear) {
        Node<T> currentElement = head;
        int currentLevel = height;
        while (currentLevel > 0) {
            Node<T> rightEl = currentElement.right.get();

            if (currentLevel <= levelOfElementAppear) {
                if (isNull(rightEl) || rightEl.data.compareTo(data) >= 0) {
                    nodesInLeft.add(currentElement);
                    nodesOnRight.add(rightEl);
                }
            }

            if (nonNull(rightEl) && rightEl.data.compareTo(data) < 0) {
                currentElement = rightEl;
            } else {
                currentElement = currentElement.down;
                currentLevel--;
            }
        }
    }

    public boolean contains(T data) {
        Node<T> currNode = head;

        while (currNode != null) {
            Node<T> rightNode = currNode.right.get();
            if (currNode.data != null && currNode.data.compareTo(data) == 0) {
                return true;
            } else if (nonNull(rightNode) && rightNode.data.compareTo(data) <= 0) {
                currNode = rightNode;
            } else {
                currNode = currNode.down;
            }
        }

        return false;
    }

    public void printAllList() {
        Node<T> downLeftNode = findLovestLeftestElement(head);
        printGivenElementAndAllElementsOnRight(downLeftNode);
    }

    private void printGivenElementAndAllElementsOnRight(Node<T> curr) {
        curr = curr.right.get();
        while (nonNull(curr)) {
            System.out.println(curr.data);
            curr = curr.right.get();
        }
    }

    private Node<T> findLovestLeftestElement(Node<T> curr) {
        while (curr.down != null) {
            curr = curr.down;
        }
        return curr;
    }

    private int computeHeightToElementsAppeare() {
        int lvl = LEVEL_ONE;
        while (lvl < height && Math.random() < probability) {
            lvl++;
        }
        return lvl;
    }
}
