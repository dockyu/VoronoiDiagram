package dev.dockyu.voronoidiagram.datastruct;

import java.util.Iterator;
import java.util.LinkedList;

public class CircularLinkedList<E> extends LinkedList<E> {

    public interface CircularIterator<E> extends Iterator<E> {
        E previous();
        E peek();
        int getCurrentIndex();

    }

    public CircularIterator<E> circularIterator() {
        return circularIterator(0);  // 預設從索引0開始
    }

    public CircularIterator<E> circularIterator(int startIndex) {
        if (startIndex < 0 || startIndex >= size()) {
            throw new IndexOutOfBoundsException("索引超出範圍");
        }

        return new CircularIterator<E>() {
            private int currentIndex = startIndex;

            @Override
            public boolean hasNext() {
                return !isEmpty();  // 只要列表不是空的，就永遠有下一個元素
            }

            @Override
            public E next() {
                E element = get(currentIndex);
                currentIndex = (currentIndex + 1) % size();  // 循環到列表開頭
                return element;
            }

            @Override
            public E previous() {
                currentIndex = (currentIndex - 1 + size()) % size();  // 循環到列表末尾
                return get(currentIndex);
            }

            @Override
            public E peek() {
                return get(currentIndex);
            }

            @Override
            public int getCurrentIndex() {
                return currentIndex;
            }
        };
    }
}
