package dev.dockyu.voronoidiagram.datastruct;

import java.util.Iterator;
import java.util.LinkedList;

public class CircularLinkedList<E> extends LinkedList<E> {

    public Iterator<E> circularIterator() {
        return new Iterator<E>() {
            private int currentIndex = 0;

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
        };
    }
}
