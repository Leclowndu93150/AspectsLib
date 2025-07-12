package com.leclowndu93150.aspectslib.codec;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;

import java.util.function.Supplier;

public class CodecUtils {

    /**
     * Implementation of withAlternative for 1.20.1 - creates a codec that tries alternative on decode failure
     */
    public static <A> Codec<A> withAlternative(Codec<A> primary, Codec<A> alternative) {
        return new Codec<A>() {
            @Override
            public <T> DataResult<Pair<A, T>> decode(DynamicOps<T> ops, T input) {
                DataResult<Pair<A, T>> primaryResult = primary.decode(ops, input);
                if (primaryResult.result().isPresent()) {
                    return primaryResult;
                }
                return alternative.decode(ops, input);
            }

            @Override
            public <T> DataResult<T> encode(A input, DynamicOps<T> ops, T prefix) {
                return primary.encode(input, ops, prefix);
            }
        };
    }

    /**
     * Overload for withAlternative that uses a mapping function for alternative codec
     */
    public static <A, B> Codec<A> withAlternative(Codec<A> primary, Codec<B> alternative, java.util.function.Function<B, A> mapper) {
        return new Codec<A>() {
            @Override
            public <T> DataResult<Pair<A, T>> decode(DynamicOps<T> ops, T input) {
                DataResult<Pair<A, T>> primaryResult = primary.decode(ops, input);
                if (primaryResult.result().isPresent()) {
                    return primaryResult;
                }
                return alternative.decode(ops, input).map(pair -> 
                    Pair.of(mapper.apply(pair.getFirst()), pair.getSecond()));
            }

            @Override
            public <T> DataResult<T> encode(A input, DynamicOps<T> ops, T prefix) {
                return primary.encode(input, ops, prefix);
            }
        };
    }

    /**
     * Implementation of lazy codec for 1.20.1
     */
    public static <A> Codec<A> lazy(Supplier<Codec<A>> supplier) {
        return new Codec<A>() {
            private Codec<A> delegate = null;

            private Codec<A> getDelegate() {
                if (delegate == null) {
                    delegate = supplier.get();
                }
                return delegate;
            }

            @Override
            public <T> DataResult<Pair<A, T>> decode(DynamicOps<T> ops, T input) {
                return getDelegate().decode(ops, input);
            }

            @Override
            public <T> DataResult<T> encode(A input, DynamicOps<T> ops, T prefix) {
                return getDelegate().encode(input, ops, prefix);
            }
        };
    }
}