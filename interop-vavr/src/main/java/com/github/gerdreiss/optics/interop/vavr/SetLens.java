package com.github.gerdreiss.optics.interop.vavr;

import com.github.gerdreiss.optics.core.Lens;
import io.vavr.collection.HashSet;
import io.vavr.collection.Set;
import java.util.function.BiFunction;
import java.util.function.Function;

public class SetLens<A, B> extends SetView<A, B> {

    private final BiFunction<A, Set<B>, A> fset;

    SetLens(Function<A, Set<B>> fget, BiFunction<A, Set<B>, A> fset) {
        super(fget);
        this.fset = fset;
    }

    public static <A, B> SetLens<A, B> of(
            Function<A, Set<B>> fget, BiFunction<A, Set<B>, A> fset) {
        return new SetLens<>(fget, fset);
    }

    public A set(A target, Set<B> values) {
        return target == null ? null : fset.apply(target, values);
    }

    public A set(A target, B value) {
        return target == null ? null : fset.apply(target, HashSet.of(value));
    }

    public Function<Set<B>, A> set(A target) {
        return (Set<B> value) -> set(target, value);
    }

    public A modify(A target, Function<B, B> modifier) {
        return set(target, getSet(target).map(modifier));
    }

    public <C> SetLens<A, C> andThen(Lens<B, C> that) {
        return SetLens.of(
                (A a) -> getSet(a).map(that::get),
                (A a, Set<C> cs) -> set(a, getSet(a).zip(cs)
                        .map(tuple -> that.set(tuple._1, tuple._2)))
        );
    }

    public <C> SetLens<A, C> andThen(SetLens<B, C> that) {
        return SetLens.of(
                (A a) -> getSet(a).flatMap(that::getSet),
                (A a, Set<C> cs) -> set(a, getSet(a).map(b -> that.set(b, cs))));
    }

    public <C> SetLens<C, B> compose(SetLens<C, A> that) {
        return that.andThen(this);
    }

}

