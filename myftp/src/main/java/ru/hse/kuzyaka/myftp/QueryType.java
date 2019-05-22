package ru.hse.kuzyaka.myftp;

public enum QueryType {
    LIST_QUERY(1), GET_QUERY(2);

    private int value;

    QueryType(int i) {
        value = i;
    }

    public int value() {
        return value;
    }
}
