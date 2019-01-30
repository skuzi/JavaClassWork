package ru.hse.brainlets.maybe;

import java.util.NoSuchElementException;
import java.util.function.Function;

public class Maybe<T> {
    private T value;
    private boolean nothing;

    private Maybe(T value) {
        this.value = value;
        nothing = false;
    }

    private Maybe() {
        nothing = true;
    }

    public static <T> Maybe<T> just(T t) {
        return new Maybe<T>(t);
    }

    public static <T> Maybe<T> nothing() {
        return new Maybe<T>();
    }

    public T get() throws NoSuchElementException {
        if (nothing) {
            throw new NoSuchElementException();
        } else {
            return value;
        }
    }

    public boolean isPresent() {
        return !nothing;
    }

    public <U> Maybe<U> map(Function<? super T, ? extends U> mapper) {
        if (nothing) {
            return Maybe.nothing();
        } else {
            return new Maybe.just(mapper.apply(value));
        }
    }
}
