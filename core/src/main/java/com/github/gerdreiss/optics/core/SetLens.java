package com.github.gerdreiss.optics.core;

import static com.github.gerdreiss.optics.core.util.CollectionUtils.zip;
import static java.util.stream.Collectors.toSet;

import java.util.Set;
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
        return target == null ? null : fset.apply(target, Set.of(value));
    }

    public Function<Set<B>, A> set(A target) {
        return (Set<B> value) -> set(target, value);
    }

    public A modify(A target, Function<B, B> modifier) {
        return set(target, getSet(target).stream().map(modifier).collect(toSet()));
    }

    public <C> SetLens<A, C> andThen(Lens<B, C> that) {
        return SetLens.of(
                (A a) -> getSet(a).stream().map(that::get).collect(toSet()),
                (A a, Set<C> cs) -> set(a, zip(getSet(a), cs).stream()
                        .map(entry -> that.set(entry.getKey(), entry.getValue()))
                        .collect(toSet()))
        );
    }

    public <C> SetLens<A, C> andThen(SetLens<B, C> that) {
        return SetLens.of(
                (A a) -> getSet(a).stream().flatMap(b -> that.getSet(b).stream()).collect(toSet()),
                (A a, Set<C> cs) -> set(a, getSet(a).stream().map(b -> that.set(b, cs)).collect(toSet())));
    }

    public <C> SetLens<C, B> compose(Lens<C, A> that) {
        return that.andThen(this);
    }

    public <C> SetLens<C, B> compose(SetLens<C, A> that) {
        return that.andThen(this);
    }

}

