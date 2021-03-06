/*
 * Copyright 2021 DiffPlug
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.gerdreiss.optics.interop.vavr;

import com.github.gerdreiss.optics.core.View;
import io.vavr.Tuple;
import io.vavr.collection.HashMap;
import io.vavr.collection.Map;
import io.vavr.collection.Traversable;
import java.util.function.Function;

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
        return a == null ? HashMap.empty() : fget.apply(a);
    }

    public <V1> MapView<A, K, V1> andThen(final View<V, V1> that) {
        return MapView.of((A a) -> getMap(a)
                .map(t -> Tuple.of(t._1, that.get(t._2)))
                .toMap(t -> t._1, t -> t._2));
    }

    public <K1, V1> MapView<A, K, Traversable<V1>> andThen(final MapView<V, K1, V1> that) {
        return MapView.of((A a) -> getMap(a)
                .map(t -> Tuple.of(t._1, that.getMap(t._2).values()))
                .toMap(t -> t._1, t -> t._2));
    }

    public <V1> MapView<V1, K, Traversable<V>> compose(final MapView<V1, K, A> that) {
        return that.andThen(this);
    }
}
