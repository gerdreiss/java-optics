package com.github.gerdreiss.optics.interop.vavr;

import com.github.gerdreiss.optics.core.View;
import io.vavr.collection.List;
import io.vavr.collection.Traversable;
import io.vavr.control.Option;

import java.util.function.Function;
import java.util.function.Predicate;

public class TraversableView<A, B> implements Function<A, Traversable<B>> {

    private final Function<A, Traversable<B>> fget;

    TraversableView(Function<A, Traversable<B>> fget) {
        this.fget = fget;
    }

    public static <A, B> TraversableView<A, B> of(final Function<A, Traversable<B>> fget) {
        return new TraversableView<>(fget);
    }

    @Override
    public Traversable<B> apply(A a) {
        return null;
    }

    public Traversable<B> getTraversable(A a) {
        return a == null ? List.empty() : fget.apply(a);
    }

    public Option<B> getFirst(A a) {
        return a == null ? Option.none() : fget.apply(a).headOption();
    }

    public Option<B> getLast(A a) {
        return a == null ? Option.none() : fget.apply(a).lastOption();
    }

    public Option<B> getAt(A a, int n) {
        return a == null ? Option.none() : fget.apply(a).drop(n).headOption();
    }

    public Traversable<B> find(A a, Predicate<B> predicate) {
        return a == null ? List.empty() : fget.apply(a).filter(predicate);
    }

    public Option<B> findFirst(A a, Predicate<B> predicate) {
        return find(a, predicate).headOption();
    }

    public <C> TraversableView<A, C> andThen(final View<B, C> that) {
        return TraversableView.of((A a) -> getTraversable(a).map(that::get));
    }

    public <C> TraversableView<A, C> andThen(final TraversableView<B, C> that) {
        return TraversableView.of((A a) -> getTraversable(a).flatMap(that::getTraversable));
    }

    public <C> TraversableView<C, B> compose(final View<C, A> that) {
        return TraversableView.of((C c) -> getTraversable(that.get(c)));
    }

    public <C> TraversableView<C, B> compose(final TraversableView<C, A> that) {
        return that.andThen(this);
    }
}
