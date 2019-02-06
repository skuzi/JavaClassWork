package ru.hse.kuzyaka;

import java.util.*;

/**
 * This class implements <code>SmartList</code> which is basically <code>List</code>, but it optimizes any work with small arrays
 * @param <E> generic type
 */
public class SmartList<E> extends AbstractList<E> {

    /** Stores the size of this list. */
    private int size;
    /** Field for storing data of the list.
     * It has four different modes: if the list is empty, it is <code>null</code>, if the list contains one element,
     * it contains the reference to this element, if the list has from 2 up to 5 elements, it contains an array of
     * 5 object with references to original elements, if the list has more then five objects, it contains a reference
     * to an <code>ArrayList</code> where original elements are stored. */
    private Object data;

    /** Constructs an empty list. */
    public SmartList() {
        size = 0;
        data = null;
    }

    /**
     * Constructs list with elements from the specific collection
     * @param collection collection where the elements are taken from
     */
    public SmartList(Collection<? extends E> collection) {
        for(Iterator<? extends E> i = collection.iterator(); i.hasNext();) {
            add(i.next());
        }
    }

    /**
     * Returns the element at the position specified by parameter.
     * Throws the <code>IndexOutOfBoundsException</code> if <code>index < 0 || index > size</code>
     * @param index index
     * @return element
     */
    @Override
    public E get(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException();
        }
        if(size == 1) {
            return (E) data;
        } else if (size < 5) {
            return ((E[])data)[index];
        } else {
            return (E) ((ArrayList<Object>)data).get(index);
        }
    }

    /**
     * Returns the size of the list
     * @return size of the list
     */
    @Override
    public int size() {
        return size;
    }

    /**
     * Adds the specified element at the specific position in the list. All element starting from this position
     * are shifted to the left. Throws the <code>IndexOutOfBoundsException</code> if <code>index < 0 || index >= size</code>
     * @param index position to insert an element
     * @param e the former element at this position
     */
    @Override
    public void add(int index, E e) {
        if(index < 0 || index > size()) {
            throw new IndexOutOfBoundsException();
        }
        if(size == 0) {
            data = e;
            size++;
        } else if(size == 1) {
            E last = (E) data;
            var newData = new Object[5];
            newData[1 - index] = last;
            newData[index] = e;
            data = newData;
            size++;
        } else if(size < 5) {
            var curData = (Object[]) data;
            for(int i = 4; i > index; i--) {
                curData[i] = curData[i - 1];
            }
            curData[index] = e;
            size++;
        } else if(size == 5) {
            var newData = new ArrayList<>(Arrays.asList((Object[]) data));
            newData.add(index, e);
            data = newData;
            size++;
        } else {
            var curData = (ArrayList<Object>) data;
            curData.add(index, e);
            size++;
        }
    }

    /**
     * Removes an element from the specific position at the list. 
     * Throws the <code>IndexOutOfBoundsException</code> if <code>index < 0 || index >= size</code>
     * @param index
     * @return
     */
    @Override
    public E remove(int index) {
        if(index < 0 || index >= size) {
            throw new IndexOutOfBoundsException();
        }
        if(size == 1) {
            E res = (E) data;
            data = null;
            size--;
            return res;
        } else if (size < 5) {
            var curData = (Object[]) data;
            E res = (E) curData[index];
            for(int i = index; i < 4; i++) {
                curData[i] = curData[i + 1];
            }
            size--;
            if(size == 1) {
                data = curData[0];
            }
            return res;
        } else if (size == 5) {
            E res = ((ArrayList<E>) data).remove(index);
            var newData = new Object[5];
            for(int i = 0; i < 3; i++) {
                newData[i] = ((ArrayList)data).get(i);
            }
            data = newData;
            size--;
            return res;
        } else {
            size--;
            return ((ArrayList<E>) data).remove(index);
        }
    }

    @Override
    public E set(int index, E element) {
        if(index < 0 || index >= size) {
            throw new IndexOutOfBoundsException();
        }
        if(size == 1) {
            E res = (E) data;
            data = element;
            return res;
        } else if(size < 5) {
            E res = (E) ((Object[])data)[index];
            ((Object[])data)[index] = element;
            return res;
        } else {
            return (E) ((ArrayList)data).set(index, element);
        }
    }


}
