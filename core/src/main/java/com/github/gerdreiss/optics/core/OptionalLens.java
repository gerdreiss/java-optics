package com.github.gerdreiss.optics.core;

import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Stream;

/**
 * "A lens is basically a getter/setter that can be used for deep updates of immutable data."
 * http://davids-code.blogspot.de/2014/02/immutable-domain-and-lenses-in-java-8.html
 *
 * @param <A> The object into whose property we want to view or update
 * @param <B> The object property which we want to view or update
 */
public class OptionalLens<A, B> extends OptionalView<A, B> {

    private final BiFunction<A, Optional<B>, A> fset;

    OptionalLens(Function<A, Optional<B>> fget, BiFunction<A, Optional<B>, A> fset) {
        super(fget);
        this.fset = fset;
    }

    public static <A, B> OptionalLens<A, B> of(Function<A, Optional<B>> fget, BiFunction<A, Optional<B>, A> fset) {
        return new OptionalLens<>(fget, fset);
    }

    public A set(A target, Optional<B> value) {
        return target == null ? null : fset.apply(target, value);
    }

    public A set(A target, B value) {
        return target == null ? null : fset.apply(target, Optional.ofNullable(value));
    }

    public Function<Optional<B>, A> set(A target) {
        return (Optional<B> value) -> set(target, value);
    }

    public A modify(A target, Function<B, B> modifier) {
        return set(target, getOptional(target).map(modifier));
    }

    public <C> OptionalLens<A, C> andThen(Lens<B, C> that) {
        return OptionalLens.of(
                (A a) -> getOptional(a).map(that::get),
                (A a, Optional<C> maybeC) -> set(a, getOptional(a).flatMap(b -> maybeC.map(c -> that.set(b, c))))
        );
    }

    public <C> OptionalLens<A, C> andThen(OptionalLens<B, C> that) {
        return OptionalLens.of(
                (A a) -> getOptional(a).flatMap(that::getOptional),
                (A a, Optional<C> maybeC) -> set(a, getOptional(a).map(b -> that.set(b, maybeC)))
        );
    }

    public <C> StreamLens<A, C> andThen(StreamLens<B, C> that) {
        return StreamLens.of(
                (A a) -> getOptional(a).map(that::getStream).orElse(Stream.empty()),
                (A a, Stream<C> cStream) -> set(a, getOptional(a).map(b -> that.set(b, cStream)))
        );
    }

    public <C> OptionalLens<C, B> compose(Lens<C, A> that) {
        return that.andThen(this);
    }

    public <C> OptionalLens<C, B> compose(OptionalLens<C, A> that) {
        return that.andThen(this);
    }

    public <C> StreamLens<C, Optional<B>> compose(StreamLens<C, A> that) {
        return that.andThen(this);
    }

}
