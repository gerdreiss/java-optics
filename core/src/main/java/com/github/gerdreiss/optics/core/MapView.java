package com.github.gerdreiss.optics.core;

import java.util.AbstractMap;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class MapView<A, K, V> implements Function<A, Map<K, V>> {

    private final Function<A, Map<K, V>> fget;

    MapView(Function<A, Map<K, V>> fget) {
        this.fget = fget;
    }

    public static <A, K, V> MapView<A, K, V> of(final Function<A, Map<K, V>> fget) {
        return new MapView<>(fget);
    }

    @Override
    public Map<K, V> apply(A a) {
        return getMap(a);
    }

    public Map<K, V> getMap(A a) {
        return a == null ? Collections.emptyMap() : fget.apply(a);
    }

    public <V1> MapView<A, K, V1> andThen(final View<V, V1> that) {
        return MapView.of((A a) ->
                getMap(a).entrySet().stream()
                        .map(e -> new AbstractMap.SimpleEntry<>(e.getKey(), that.get(e.getValue())))
                        .filter(e -> e.getValue() != null)
                        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)));
    }

    public <K1, V1> MapView<A, K, Collection<V1>> andThen(final MapView<V, K1, V1> that) {
        return MapView.of((A a) ->
                getMap(a).entrySet().stream()
                        .map(e -> new AbstractMap.SimpleEntry<>(e.getKey(), that.getMap(e.getValue()).values()))
                        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)));
    }

    public <V1> MapView<V1, K, V> compose(final View<V1, A> that) {
        return that.andThen(this);
    }

    public <V1> MapView<V1, K, Collection<V>> compose(final MapView<V1, K, A> that) {
        return that.andThen(this);
    }
}
