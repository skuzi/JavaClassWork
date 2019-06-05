package ru.hse.kuzyaka.findapair;

import java.util.Arrays;
import java.util.Random;

/** Class for storing game logic**/
public class BoardLogic {
    private static final Random RAND = new Random();
    private static int boardSize;
    private static boolean[][] opened;
    private static int[][] cells;
    private static Pair<Integer, Integer> firstOpened;
    private static Pair<Integer, Integer> secondOpened;
    private static int filled;

    /**
     * Initializes a board with the given number of cells in a row (so as in a column)
     *
     * @param n number of cells in a row
     */
    public static void init(int n) {
        boardSize = n;
        opened = new boolean[n][n];
        cells = new int[n][n];
        int[] used = new int[n * n / 2];
        firstOpened = null;
        secondOpened = null;
        Arrays.fill(used, 0);
        for (int i = 0; i < n; i++) {
            for(int j = 0; j < n; j++) {
                opened[i][j] = true;
                int tryNext;
                while (used[tryNext = RAND.nextInt(n * n / 2)] == 2);
                cells[i][j] = tryNext;
                used[tryNext]++;
            }
        }
    }

    /**
     * Shows whether this cell is free to open
     *
     * @param position a pair containing coordinates of a cell in range [0..{@code boardSize)
     * @return {@code true} if this cell is free to open; {@code false} otherwise
     */
    public static boolean isOpened(Pair<Integer, Integer> position) {
        return opened[position.getFirst()][position.getSecond()];
    }

    /**
     * Returns a number which the given cell contains
     *
     * @param position a pair containing coordinates of a cell in range [0..{@code boardSize)
     * @return number written in this cell
     */
    public static int getNumber(Pair<Integer, Integer> position) {
        return cells[position.getFirst()][position.getSecond()];
    }

    /**
     * Opens the given cell
     *
     * @param position a pair containing coordinates of a cell in range [0..{@code boardSize)
     */
    public static void open(Pair<Integer, Integer> position) {
        opened[position.getFirst()][position.getSecond()] = false;
        if (firstOpened == null) {
            firstOpened = position;
        } else {
            secondOpened = position;
        }
    }

    /**
     * Shows whether this game is finished or not
     *
     * @return {@code true} if all matches were found; {@code false} otherwise
     */
    public static boolean isFinished() {
        return filled == boardSize * boardSize;
    }

    /**
     * Return current number of opened cells
     *
     * @return current number of opened cells
     */
    public static int currentlyOpened() {
        return (firstOpened != null ? 1 : 0) + (secondOpened != null ? 1 : 0);
    }

    /** Closes two currently opened cells. This method must be called iff two cells were opened **/
    public static void close() {
        opened[firstOpened.getFirst()][firstOpened.getSecond()] = true;
        opened[secondOpened.getFirst()][secondOpened.getSecond()] = true;
        firstOpened = null;
        secondOpened = null;
    }

    /**
     * Check if two currently opened cells contain the same number.
     * This method must be called iff two cells were opened
     *
     * @return {@code true} if numbers are equal; {@code false} otherwise
     */
    public static boolean checkEqual() {
        boolean areEqual = getNumber(firstOpened) == getNumber(secondOpened);
        if (areEqual) {
            filled += 2;
        }
        return areEqual;
    }

    /**
     * Returns first opened cell
     *
     * @return first opened cell. If no such, returns null
     */
    public static Pair<Integer, Integer> getFirstOpened() {
        return firstOpened;
    }

    /**
     * Returns second opened cell
     *
     * @return second opened cell. If no such, returns null
     */
    public static Pair<Integer, Integer> getSecondOpened() {
        return secondOpened;
    }
}
