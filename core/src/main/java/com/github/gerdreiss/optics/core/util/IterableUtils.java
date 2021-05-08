package com.github.gerdreiss.optics.core.util;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public final class IterableUtils {

    private IterableUtils() {
    }

    public static <A, B> Stream<Entry<A, B>> zipStream(Iterable<A> as, Iterable<B> bs) {
        return stream(zip(as, bs));
    }

    public static <A> Stream<A> stream(Iterable<A> as) {
        return StreamSupport.stream(Spliterators.spliteratorUnknownSize(as.iterator(), Spliterator.ORDERED), false);
    }

    public static <A, B> Iterable<Entry<A, B>> zip(Iterable<A> as, Iterable<B> bs) {
        var iteratorA = as.iterator();
        var iteratorB = bs.iterator();

        return () -> new Iterator<>() {
            @Override
            public boolean hasNext() {
                return iteratorA.hasNext() && iteratorB.hasNext();
            }

            @Override
            public Entry<A, B> next() {
                return Map.entry(iteratorA.next(), iteratorB.next());
            }
        };
    }

}
