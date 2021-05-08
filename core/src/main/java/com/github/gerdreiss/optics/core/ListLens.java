package com.github.gerdreiss.optics.core;

import static com.github.gerdreiss.optics.core.util.IterableUtils.zipStream;
import static java.util.stream.Collectors.toList;

import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;

public class ListLens<A, B> extends ListView<A, B> {

    private final BiFunction<A, List<B>, A> fset;

    ListLens(Function<A, List<B>> fget, BiFunction<A, List<B>, A> fset) {
        super(fget);
        this.fset = fset;
    }

    public static <A, B> ListLens<A, B> of(
            Function<A, List<B>> fget, BiFunction<A, List<B>, A> fset) {
        return new ListLens<>(fget, fset);
    }

    public A set(A target, List<B> values) {
        return target == null ? null : fset.apply(target, values);
    }

    public A set(A target, B value) {
        return target == null ? null : fset.apply(target, List.of(value));
    }

    public Function<List<B>, A> set(A target) {
        return (List<B> value) -> set(target, value);
    }

    public A modify(A target, Function<B, B> modifier) {
        return set(target, getList(target).stream().map(modifier).collect(toList()));
    }

    public <C> ListLens<A, C> andThen(Lens<B, C> that) {
        return ListLens.of(
                (A a) -> getList(a).stream().map(that::get).collect(toList()),
                (A a, List<C> cs) -> set(a, zipStream(getList(a), cs)
                        .map(entry -> that.set(entry.getKey(), entry.getValue()))
                        .collect(toList()))
        );
    }

    public <C> ListLens<A, C> andThen(ListLens<B, C> that) {
        return ListLens.of(
                (A a) -> getList(a).stream().flatMap(b -> that.getList(b).stream()).collect(toList()),
                (A a, List<C> cs) -> set(a, getList(a).stream().map(b -> that.set(b, cs)).collect(toList())));
    }

    public <C> ListLens<C, B> compose(Lens<C, A> that) {
        return that.andThen(this);
    }

    public <C> ListLens<C, B> compose(ListLens<C, A> that) {
        return that.andThen(this);
    }

}

