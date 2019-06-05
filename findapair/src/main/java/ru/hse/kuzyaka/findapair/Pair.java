package ru.hse.kuzyaka.findapair;

import java.util.Objects;

/** Class for storing pair **/
public class Pair<T, U> {
    private T first;
    private U second;

    /**
     * Constructs a pair
     *
     * @param first first element
     * @param second second element
     */
    public Pair(T first, U second) {
        this.first = first;
        this.second = second;
    }

    public T getFirst() {
        return first;
    }

    public U getSecond() {
        return second;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Pair<?, ?> pair = (Pair<?, ?>) o;
        return first.equals(pair.first) &&
                second.equals(pair.second);
    }

    @Override
    public int hashCode() {
        return Objects.hash(first, second);
    }
}
