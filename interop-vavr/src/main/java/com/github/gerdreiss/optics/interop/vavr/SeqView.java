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

import com.github.gerdreiss.optics.core.OptionalView;
import com.github.gerdreiss.optics.core.View;
import io.vavr.collection.List;
import io.vavr.collection.Seq;
import io.vavr.control.Option;
import java.util.Optional;
import java.util.function.Function;

public class SeqView<A, B> implements Function<A, Seq<B>> {

    private final Function<A, Seq<B>> fget;

    SeqView(Function<A, Seq<B>> fget) {
        this.fget = fget;
    }

    public static <A, B> SeqView<A, B> of(final Function<A, Seq<B>> fget) {
        return new SeqView<>(fget);
    }

    @Override
    public Seq<B> apply(A a) {
        return getSeq(a);
    }

    public Seq<B> getSeq(A a) {
        return a == null ? List.empty() : fget.apply(a);
    }

    public <C> SeqView<A, C> andThen(final View<B, C> that) {
        return SeqView.of((A a) -> getSeq(a).map(that::get));
    }

    public <C> SeqView<A, Optional<C>> andThen(final OptionalView<B, C> that) {
        return SeqView.of((A a) -> getSeq(a).map(that::getOptional));
    }

    public <C> SeqView<A, Option<C>> andThen(final OptionView<B, C> that) {
        return SeqView.of((A a) -> getSeq(a).map(that::getOption));
    }

    public <C> SeqView<A, C> andThen(final SeqView<B, C> that) {
        return SeqView.of((A a) -> getSeq(a).flatMap(that::getSeq));
    }

    public <C> SeqView<C, B> compose(final OptionView<C, A> that) {
        return that.andThen(this);
    }

    public <C> SeqView<C, B> compose(final SeqView<C, A> that) {
        return that.andThen(this);
    }
}
