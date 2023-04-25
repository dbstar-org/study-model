package io.github.dbstarll.study;

@FunctionalInterface
public interface ThreeConsumer<T, U, V> {
    void accept(T t, U u, V v);
}
