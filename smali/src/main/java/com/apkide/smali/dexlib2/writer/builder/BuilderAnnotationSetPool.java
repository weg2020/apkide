/*
 * Copyright 2013, Google LLC
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 *
 *     * Redistributions of source code must retain the above copyright
 * notice, this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above
 * copyright notice, this list of conditions and the following disclaimer
 * in the documentation and/or other materials provided with the
 * distribution.
 *     * Neither the name of Google LLC nor the names of its
 * contributors may be used to endorse or promote products derived from
 * this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
 * OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package com.apkide.smali.dexlib2.writer.builder;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.apkide.smali.dexlib2.iface.Annotation;
import com.apkide.smali.dexlib2.writer.AnnotationSetSection;
import com.apkide.smali.dexlib2.writer.DexWriter;
import com.google.common.base.Function;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterators;
import com.google.common.collect.Maps;

import java.util.Collection;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;

class BuilderAnnotationSetPool extends BaseBuilderPool
        implements AnnotationSetSection<BuilderAnnotation, BuilderAnnotationSet> {
    @NonNull
    private final ConcurrentMap<Set<? extends Annotation>, BuilderAnnotationSet> internedItems =
            Maps.newConcurrentMap();

    public BuilderAnnotationSetPool(@NonNull DexBuilder dexBuilder) {
        super(dexBuilder);
    }

    @NonNull public BuilderAnnotationSet internAnnotationSet(@Nullable Set<? extends Annotation> annotations) {
        if (annotations == null) {
            return BuilderAnnotationSet.EMPTY;
        }

        BuilderAnnotationSet ret = internedItems.get(annotations);
        if (ret != null) {
            return ret;
        }

        BuilderAnnotationSet annotationSet = new BuilderAnnotationSet(
                ImmutableSet.copyOf(Iterators.transform(annotations.iterator(),
                        new Function<Annotation, BuilderAnnotation>() {
                            @Nullable @Override public BuilderAnnotation apply(Annotation input) {
                                return dexBuilder.annotationSection.internAnnotation(input);
                            }
                        })));

        ret = internedItems.putIfAbsent(annotationSet, annotationSet);
        return ret==null?annotationSet:ret;
    }

    @NonNull @Override
    public Collection<? extends BuilderAnnotation> getAnnotations(@NonNull BuilderAnnotationSet key) {
        return key.annotations; 
    }

    @Override public int getNullableItemOffset(@Nullable BuilderAnnotationSet key) {
        return key==null? DexWriter.NO_OFFSET:key.offset;
    }

    @Override public int getItemOffset(@NonNull BuilderAnnotationSet key) {
        return key.offset;
    }

    @NonNull @Override public Collection<? extends Entry<? extends BuilderAnnotationSet, Integer>> getItems() {
        return new BuilderMapEntryCollection<BuilderAnnotationSet>(internedItems.values()) {
            @Override protected int getValue(@NonNull BuilderAnnotationSet key) {
                return key.offset;
            }

            @Override protected int setValue(@NonNull BuilderAnnotationSet key, int value) {
                int prev = key.offset;
                key.offset = value;
                return prev;
            }
        };
    }
}
