package com.github.gerdreiss.optics;

import java.util.function.Function;

/**
 * "A lens is basically a getter/setter that can be used for deep updates of immutable data."
 * http://davids-code.blogspot.de/2014/02/immutable-domain-and-lenses-in-java-8.html
 *
 * @param <A> The object into whose property we want to view
 * @param <B> The object property which we want to view
 */
public class View<A, B> implements Function<A, B> {

    private final Function<A, B> fget;

    View(Function<A, B> fget) {
        this.fget = fget;
    }

    public static <A, B> View<A, B> of(Function<A, B> fget) {
        return new View<>(fget);
    }

    @Override
    public B apply(A a) {
        return get(a);
    }

    public B get(A a) {
        return a == null ? null : fget.apply(a);
    }

    public <C> View<A, C> andThen(View<B, C> that) {
        return new View<>((A a) -> that.get(get(a)));
    }

    public <C> View<C, B> compose(final View<C, A> that) {
        return that.andThen(this);
    }
}
