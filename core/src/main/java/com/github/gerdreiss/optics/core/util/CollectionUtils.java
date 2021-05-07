package com.github.gerdreiss.optics.core.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;

public final class CollectionUtils {

    private CollectionUtils() {
    }

    public static <A, B> Collection<Entry<A, B>> zip(Collection<A> as, Collection<B> bs) {
        var iteratorA = as.iterator();
        var iteratorB = bs.iterator();
        var result = new ArrayList<Entry<A, B>>();
        while (iteratorA.hasNext() && iteratorB.hasNext()) {
            result.add(Map.entry(iteratorA.next(), iteratorB.next()));
        }
        return result;
    }
}
