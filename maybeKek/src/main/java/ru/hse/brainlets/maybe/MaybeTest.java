package ru.hse.brainlets.maybe;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

class MaybeTest {
    Maybe<Integer> justInt;
    Maybe<ArrayList<Integer>> justArr;
    @BeforeEach
    void init() {
        justInt = Maybe.just(5);
        justArr = Maybe.just(new ArrayList<Integer>());
    }

    @Test
    void just() {
        assertTrue(justInt.isPresent());
        assertFalse(Maybe.nothing().isPresent());
        assertEquals(Integer.valueOf(5), justInt.get());
    }

    @Test
    void nothing() {
        assertFalse(Maybe.nothing().isPresent());
    }

    @Test
    void get() {
        assertEquals(Integer.valueOf(5), justInt.get());
    }

    @Test
    void isPresent() {
        assertFalse(Maybe.nothing().isPresent());
        assertTrue(justArr.isPresent());
    }

    @Test
    void map() {
    }
}