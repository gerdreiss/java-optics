package com.github.gerdreiss.optics.core;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

public abstract class TestModel {

    /**
     * TEST VIEWS
     */

    protected final View<RootObj, NestedObj> rootObjNestedObjView = View.of(RootObj::getNestedObj);
    protected final View<NestedObj, InnerObj> nestedObjInnerObjView = View.of(NestedObj::getInnerObj);
    protected final View<InnerObj, String> innerObjPropertyView = View.of(InnerObj::getProperty);


    protected final OptionalView<RootObj, NestedObj> rootObjNestedObjOptionalView = OptionalView.of(RootObj::getNestedObjOptional);
    protected final OptionalView<NestedObj, InnerObj> nestedObjInnerObjOptionalView = OptionalView.of(NestedObj::getInnerObjOptional);
    protected final OptionalView<InnerObj, String> innerObjPropertyOptionalView = OptionalView.of(InnerObj::getPropertyOptional);


    /**
     * TEST LENSES
     */

    protected final Lens<RootObj, NestedObj> rootObjNestedObjLens = Lens.of(
            RootObj::getNestedObj,
            (rootObj, innerObj) -> new RootObj(innerObj, rootObj.getNestedObjOptional(), rootObj.getNestedObjStream(), rootObj.getNestedObjMap()));

    protected final Lens<NestedObj, InnerObj> nestedObjInnerObjLens = Lens.of(
            NestedObj::getInnerObj,
            (nestedObj, innerInnerObj) -> new NestedObj(innerInnerObj, nestedObj.getInnerObjOptional(), nestedObj.getInnerObjStream()));

    protected final Lens<InnerObj, String> innerObjPropertyLens = Lens.of(
            InnerObj::getProperty,
            (innerObj, property) -> new InnerObj(property, innerObj.getPropertyOptional(), innerObj.getPropertyStream()));

    protected final StreamLens<InnerObj, String> innerObjPropertyStreamLens = StreamLens.of(
            InnerObj::getPropertyStream,
            (innerObj, propertyStream) -> new InnerObj(innerObj.getProperty(), innerObj.getPropertyOptional(), propertyStream));


    protected final OptionalLens<RootObj, NestedObj> rootObjNestedObjOptionalLens =
            OptionalLens.of(
                    RootObj::getNestedObjOptional,
                    (rootObj, maybeNestedObj) -> new RootObj(rootObj.getNestedObj(), maybeNestedObj, rootObj.getNestedObjStream()));

    protected final OptionalLens<NestedObj, InnerObj> nestedObjInnerObjOptionalLens =
            OptionalLens.of(
                    NestedObj::getInnerObjOptional,
                    (nestedObj, maybeInnerObj) -> new NestedObj(nestedObj.getInnerObj(), maybeInnerObj, nestedObj.getInnerObjStream()));

    protected final OptionalLens<InnerObj, String> innerObjPropertyOptionalLens =
            OptionalLens.of(
                    InnerObj::getPropertyOptional,
                    (innerObj, maybeProperty) -> new InnerObj(innerObj.getProperty(), maybeProperty, innerObj.getPropertyStream()));

    /**
     * TEST PROPERTY VALUES
     */
    protected static final String PROP = "prop";
    protected static final String MAYBE_PROP = "maybeProp";


    /**
     * The root object that contains
     * a nested obj,
     * an optional nested object,
     * a stream of nested objects,
     * and a map of strings to nested objects
     */
    static class RootObj {
        private final NestedObj nestedObj;
        private final Optional<NestedObj> nestedObjOptional;
        private final Stream<NestedObj> nestedObjStream;
        private final Map<String, NestedObj> nestedObjMap;

        public RootObj(NestedObj nestedObj) {
            this(nestedObj, Optional.empty(), Stream.empty());
        }

        public RootObj(
                NestedObj nestedObj,
                Optional<NestedObj> nestedObjOptional
        ) {
            this(nestedObj, nestedObjOptional, Stream.empty());
        }

        public RootObj(
                NestedObj nestedObj,
                Optional<NestedObj> nestedObjOptional,
                Stream<NestedObj> nestedObjStream
        ) {
            this(nestedObj, nestedObjOptional, nestedObjStream, Collections.emptyMap());
        }

        public RootObj(
                NestedObj nestedObj,
                Optional<NestedObj> nestedObjOptional,
                Stream<NestedObj> nestedObjStream,
                Map<String, NestedObj> nestedObjMap
        ) {
            this.nestedObj = nestedObj;
            this.nestedObjOptional = nestedObjOptional;
            this.nestedObjStream = nestedObjStream;
            this.nestedObjMap = nestedObjMap;
        }

        public NestedObj getNestedObj() {
            return nestedObj;
        }

        public Optional<NestedObj> getNestedObjOptional() {
            return nestedObjOptional;
        }

        public Stream<NestedObj> getNestedObjStream() {
            return nestedObjStream;
        }

        public Map<String, NestedObj> getNestedObjMap() {
            return nestedObjMap;
        }
    }

    /**
     * The nested object that contains
     * an inner obj,
     * an optional inner object,
     * a stream of inner objects,
     * and a map of strings to inner objects
     */
    static class NestedObj {

        private final InnerObj innerObj;
        private final Optional<InnerObj> innerObjOptional;
        private final Stream<InnerObj> innerObjStream;
        private final Map<String, InnerObj> innerObjMap;

        public NestedObj(InnerObj innerObj) {
            this(innerObj, Optional.empty());
        }

        public NestedObj(
                InnerObj innerObj,
                Optional<InnerObj> innerObjOptional
        ) {
            this(innerObj, innerObjOptional, Stream.empty(), Collections.emptyMap());
        }

        public NestedObj(
                InnerObj innerObj,
                Optional<InnerObj> innerObjOptional,
                Stream<InnerObj> innerObjStream
        ) {
            this(innerObj, innerObjOptional, innerObjStream, Collections.emptyMap());
        }

        public <T> NestedObj(
                InnerObj innerObj,
                Optional<InnerObj> innerObjOptional,
                Stream<InnerObj> innerObjStream,
                Map<String, InnerObj> innerObjMap
        ) {
            this.innerObj = innerObj;
            this.innerObjOptional = innerObjOptional;
            this.innerObjStream = innerObjStream;
            this.innerObjMap = innerObjMap;
        }

        public InnerObj getInnerObj() {
            return innerObj;
        }

        public Optional<InnerObj> getInnerObjOptional() {
            return innerObjOptional;
        }

        public Stream<InnerObj> getInnerObjStream() {
            return innerObjStream;
        }

        public Map<String, InnerObj> getInnerObjMap() {
            return innerObjMap;
        }
    }


    /**
     * The inner object that contains
     * a string property,
     * an optional string property,
     * a stream of string properties,
     * and a map of strings to string properties
     */
    static class InnerObj {
        private final String property;
        private final Optional<String> propertyOptional;
        private final Stream<String> propertyStream;
        private final Map<String, String> propertyMap;

        public InnerObj(String property) {
            this(property, Optional.empty());
        }

        public InnerObj(String property, Optional<String> propertyOptional) {
            this(property, propertyOptional, Stream.empty(), Collections.emptyMap());
        }

        public InnerObj(String property, Optional<String> propertyOptional, Stream<String> propertyStream) {
            this(property, propertyOptional, Stream.empty(), Collections.emptyMap());
        }

        public <T> InnerObj(
                String property,
                Optional<String> propertyOptional,
                Stream<String> propertyStream,
                Map<String, String> propertyMap
        ) {
            this.property = property;
            this.propertyOptional = propertyOptional;
            this.propertyStream = propertyStream;
            this.propertyMap = propertyMap;
        }

        public String getProperty() {
            return property;
        }

        public Optional<String> getPropertyOptional() {
            return propertyOptional;
        }

        public Stream<String> getPropertyStream() {
            return propertyStream;
        }

        public Map<String, String> getPropertyMap() {
            return propertyMap;
        }
    }

}
