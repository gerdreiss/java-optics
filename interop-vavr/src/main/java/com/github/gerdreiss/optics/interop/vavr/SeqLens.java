package com.github.gerdreiss.optics.interop.vavr;

import com.github.gerdreiss.optics.core.Lens;
import io.vavr.collection.List;
import io.vavr.collection.Seq;
import java.util.function.BiFunction;
import java.util.function.Function;

public class SeqLens<A, B> extends SeqView<A, B> {

    private final BiFunction<A, Seq<B>, A> fSeq;

    SeqLens(Function<A, Seq<B>> fget, BiFunction<A, Seq<B>, A> fSeq) {
        super(fget);
        this.fSeq = fSeq;
    }

    public static <A, B> SeqLens<A, B> of(
            Function<A, Seq<B>> fget, BiFunction<A, Seq<B>, A> fSeq) {
        return new SeqLens<>(fget, fSeq);
    }

    public A Seq(A target, Seq<B> values) {
        return target == null ? null : fSeq.apply(target, values);
    }

    public A Seq(A target, B value) {
        return target == null ? null : fSeq.apply(target, List.of(value));
    }

    public Function<Seq<B>, A> Seq(A target) {
        return (Seq<B> value) -> Seq(target, value);
    }

    public A modify(A target, Function<B, B> modifier) {
        return Seq(target, getSeq(target).map(modifier));
    }

    public <C> SeqLens<A, C> andThen(Lens<B, C> that) {
        return SeqLens.of(
                (A a) -> getSeq(a).map(that::get),
                (A a, Seq<C> cs) -> Seq(a, getSeq(a).zip(cs)
                        .map(tuple -> that.set(tuple._1, tuple._2)))
        );
    }

    public <C> SeqLens<A, C> andThen(SeqLens<B, C> that) {
        return SeqLens.of(
                (A a) -> getSeq(a).flatMap(that::getSeq),
                (A a, Seq<C> cs) -> Seq(a, getSeq(a).map(b -> that.Seq(b, cs))));
    }

    public <C> SeqLens<C, B> compose(SeqLens<C, A> that) {
        return that.andThen(this);
    }

}

