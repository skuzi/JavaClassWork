package ru.hse.kuzyaka.findapair;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.function.BinaryOperator;

import static org.junit.jupiter.api.Assertions.*;

class BoardLogicTest {
    static private Pair<Integer, Integer> pos1;
    static private Pair<Integer, Integer> pos2;
    static private Pair<Integer, Integer> pos3;
    static private Pair<Integer, Integer> pos4;

    @BeforeAll
    static void setUp() {
        pos1 = new Pair<>(0, 0);
        pos2 = new Pair<>(0, 1);
        pos3 = new Pair<>(1, 0);
        pos4 = new Pair<>(1, 1);
    }

    @Test
    void isOpened() {
        BoardLogic.init(2);
        assertTrue(BoardLogic.isOpened(pos1));
        BoardLogic.open(pos1);
        assertFalse(BoardLogic.isOpened(pos1));
        assertTrue(BoardLogic.isOpened(pos2));
    }

    @Test
    void getNumber() {
        BoardLogic.init(2);
        int[] used = new int[4];
        used[BoardLogic.getNumber(pos1)]++;
        used[BoardLogic.getNumber(pos2)]++;
        used[BoardLogic.getNumber(pos3)]++;
        used[BoardLogic.getNumber(pos4)]++;
        assertEquals(2, used[0]);
        assertEquals(2, used[1]);
    }

    @Test
    void isFinished() {
        BoardLogic.init(2);
        BoardLogic.open(pos1);
        BoardLogic.open(pos2);
        BoardLogic.open(pos1);
        BoardLogic.open(pos3);
    }

    @Test
    void currentlyOpened() {
        BoardLogic.init(2);
        BoardLogic.open(pos1);
        assertEquals(1, BoardLogic.currentlyOpened());
        assertEquals(pos1, BoardLogic.getFirstOpened());
        BoardLogic.open(pos2);
        assertEquals(2, BoardLogic.currentlyOpened());
        assertEquals(pos2, BoardLogic.getSecondOpened());
        BoardLogic.close();
        assertEquals(0, BoardLogic.currentlyOpened());
        assertNull(BoardLogic.getSecondOpened());
        assertNull(BoardLogic.getFirstOpened());
    }

    @Test
    void close() {
        BoardLogic.init(2);
        BoardLogic.open(pos1);
        BoardLogic.open(pos2);
        assertEquals(pos1, BoardLogic.getFirstOpened());
        assertEquals(pos2, BoardLogic.getSecondOpened());
        BoardLogic.close();
        assertNull(BoardLogic.getSecondOpened());
        assertNull(BoardLogic.getFirstOpened());
    }

    @Test
    void checkEqual() {
        BoardLogic.init(2);
        int first = BoardLogic.getNumber(pos1);
        int second = BoardLogic.getNumber(pos2);
        BoardLogic.open(pos1);
        BoardLogic.open(pos2);
        assertEquals(first == second, BoardLogic.checkEqual());
    }

    @Test
    void getFirstOpened() {
        BoardLogic.init(2);
        assertNull(BoardLogic.getFirstOpened());
        assertNull(BoardLogic.getSecondOpened());
        BoardLogic.open(pos1);
        assertEquals(pos1, BoardLogic.getFirstOpened());
        assertNull(BoardLogic.getSecondOpened());
        BoardLogic.open(pos2);
        assertEquals(pos2, BoardLogic.getSecondOpened());
    }

    @Test
    void finished() {
        BoardLogic.init(2);
        BoardLogic.open(pos1);
        BoardLogic.open(pos2);
        if (BoardLogic.checkEqual()) {
            BoardLogic.close();
            BoardLogic.open(pos3);
            BoardLogic.open(pos4);
            BoardLogic.checkEqual();
            assertTrue(BoardLogic.isFinished());
        } else {
            BoardLogic.close();
            BoardLogic.open(pos1);
            BoardLogic.open(pos3);
            if (BoardLogic.checkEqual()) {
                BoardLogic.close();
                BoardLogic.open(pos2);
                BoardLogic.open(pos4);
                BoardLogic.checkEqual();
                assertTrue(BoardLogic.isFinished());
            } else {
                BoardLogic.close();
                BoardLogic.open(pos1);
                BoardLogic.open(pos4);
                BoardLogic.checkEqual();
                BoardLogic.close();
                BoardLogic.open(pos2);
                BoardLogic.open(pos3);
                BoardLogic.checkEqual();
                BoardLogic.close();
                assertTrue(BoardLogic.isFinished());
            }
        }
    }
}