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
import io.vavr.collection.HashSet;
import io.vavr.collection.List;
import io.vavr.control.Option;
import java.util.function.Function;

public class OptionView<A, B> implements Function<A, Option<B>> {

    private final Function<A, Option<B>> fget;

    OptionView(Function<A, Option<B>> fget) {
        this.fget = fget;
    }

    public static <A, B> OptionView<A, B> of(final Function<A, Option<B>> fget) {
        return new OptionView<>(fget);
    }

    @Override
    public Option<B> apply(A a) {
        return a == null ? Option.none() : getOption(a);
    }

    public Option<B> getOption(A a) {
        return a == null ? Option.none() : fget.apply(a);
    }

    public <C> OptionView<A, C> andThen(final View<B, C> that) {
        return OptionView.of((A a) -> getOption(a).map(that::get));
    }

    public <C> OptionView<A, C> andThen(final OptionView<B, C> that) {
        return OptionView.of((A a) -> getOption(a).flatMap(that::getOption));
    }

    public <C> ListView<A, C> andThen(final ListView<B, C> that) {
        return ListView.of((A a) -> getOption(a).map(that::getList).getOrElse(List.empty()));
    }

    public <C> SetView<A, C> andThen(final SetView<B, C> that) {
        return SetView.of((A a) -> getOption(a).map(that::getSet).getOrElse(HashSet.empty()));
    }

    public <C> SeqView<A, C> andThen(final SeqView<B, C> that) {
        return SeqView.of((A a) -> getOption(a).map(that::getSeq).getOrElse(List.empty()));
    }

    public <C> OptionView<C, B> compose(final View<C, A> that) {
        return OptionView.of((C c) -> getOption(that.get(c)));
    }

    public <C> OptionView<C, B> compose(final OptionView<C, A> that) {
        return that.andThen(this);
    }
}
