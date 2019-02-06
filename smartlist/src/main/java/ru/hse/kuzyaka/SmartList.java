package ru.hse.kuzyaka;

import java.util.*;

public class SmartList<E> extends AbstractList<E> {

    private int size;
    private Object data;


    public SmartList() {
        size = 0;
        data = null;
    }

    public SmartList(Collection<? extends E> c) {
        for(Iterator<? extends E> i = c.iterator(); i.hasNext();) {
            add(i.next());
        }
    }

    @Override
    public E get(int index) {
        if (index < 0 || index >= size) {
            throw new IllegalArgumentException();
        }
        if(size == 1) {
            return (E) data;
        } else if (size < 5) {
            return ((E[])data)[index];
        } else {
            return (E) ((ArrayList<Object>)data).get(index);
        }
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public void add(int index, E e) {
        if(index < 0 || index > size()) {
            throw new IllegalArgumentException();
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

    @Override
    public E remove(int index) {
        if(index < 0 || index >= size) {
            throw new IllegalArgumentException();
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
            throw new IllegalArgumentException();
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
