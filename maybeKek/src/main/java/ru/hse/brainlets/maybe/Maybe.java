package ru.hse.brainlets.maybe;

import java.util.function.Function;

public class Maybe<T> {
    private T value;
    private boolean nothing;

    Maybe (T value) {
        this.value = value;
        nothing = false;
    }

    Maybe () {
        nothing = true;
    }

    public static <T> Maybe<T> just(T t) {
        return new Maybe<T>(t);
    }

    public T get() {
        return value;
    }

    public boolean isPresent() {
        return !nothing;
    }

    public <U> Maybe<U> map(Function<T, U> mapper) {
        if(nothing) {
            return new Maybe<U>();
        } else {
            return new Maybe<U>(mapper.apply(value));
        }
    }
}
