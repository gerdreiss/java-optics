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
import io.vavr.collection.HashSet;
import io.vavr.collection.Set;
import io.vavr.control.Option;
import java.util.Optional;
import java.util.function.Function;

public class SetView<A, B> implements Function<A, Set<B>> {

    private final Function<A, Set<B>> fget;

    SetView(Function<A, Set<B>> fget) {
        this.fget = fget;
    }

    public static <A, B> SetView<A, B> of(final Function<A, Set<B>> fget) {
        return new SetView<>(fget);
    }

    @Override
    public Set<B> apply(A a) {
        return getSet(a);
    }

    public Set<B> getSet(A a) {
        return a == null ? HashSet.empty() : fget.apply(a);
    }

    public <C> SetView<A, C> andThen(final View<B, C> that) {
        return SetView.of((A a) -> getSet(a).map(that::get));
    }

    public <C> SetView<A, Optional<C>> andThen(final OptionalView<B, C> that) {
        return SetView.of((A a) -> getSet(a).map(that::getOptional));
    }

    public <C> SetView<A, Option<C>> andThen(final OptionView<B, C> that) {
        return SetView.of((A a) -> getSet(a).map(that::getOption));
    }

    public <C> SetView<A, C> andThen(final SetView<B, C> that) {
        return SetView.of((A a) -> getSet(a).flatMap(that::getSet));
    }

    public <C> SetView<C, B> compose(final OptionView<C, A> that) {
        return that.andThen(this);
    }

    public <C> SetView<C, B> compose(final SetView<C, A> that) {
        return that.andThen(this);
    }
}
