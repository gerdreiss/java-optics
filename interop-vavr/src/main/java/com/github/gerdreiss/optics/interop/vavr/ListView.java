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
import io.vavr.control.Option;
import java.util.Optional;
import java.util.function.Function;

public class ListView<A, B> implements Function<A, List<B>> {

    private final Function<A, List<B>> fget;

    ListView(Function<A, List<B>> fget) {
        this.fget = fget;
    }

    public static <A, B> ListView<A, B> of(final Function<A, List<B>> fget) {
        return new ListView<>(fget);
    }

    @Override
    public List<B> apply(A a) {
        return getList(a);
    }

    public List<B> getList(A a) {
        return a == null ? List.empty() : fget.apply(a);
    }

    public <C> ListView<A, C> andThen(final View<B, C> that) {
        return ListView.of((A a) -> getList(a).map(that::get));
    }

    public <C> ListView<A, Optional<C>> andThen(final OptionalView<B, C> that) {
        return ListView.of((A a) -> getList(a).map(that::getOptional));
    }

    public <C> ListView<A, Option<C>> andThen(final OptionView<B, C> that) {
        return ListView.of((A a) -> getList(a).map(that::getOption));
    }

    public <C> ListView<A, C> andThen(final ListView<B, C> that) {
        return ListView.of((A a) -> getList(a).flatMap(that::getList));
    }

    public <C> ListView<C, B> compose(final OptionView<C, A> that) {
        return that.andThen(this);
    }

    public <C> ListView<C, B> compose(final ListView<C, A> that) {
        return that.andThen(this);
    }
}
