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

import java.util.AbstractCollection;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

public abstract class BuilderMapEntryCollection<Key> extends AbstractCollection<Map.Entry<Key, Integer>> {
    @NonNull
    private final Collection<Key> keys;

    public BuilderMapEntryCollection(@NonNull Collection<Key> keys) {
        this.keys = keys;
    }

    private class MapEntry implements Map.Entry<Key, Integer> {
        @NonNull private Key key;

        @NonNull @Override public Key getKey() {
            return key;
        }

        @Override public Integer getValue() {
            return BuilderMapEntryCollection.this.getValue(key);
        }

        @Override public Integer setValue(Integer value) {
            return BuilderMapEntryCollection.this.setValue(key, value);
        }
    }

    @NonNull @Override public Iterator<Map.Entry<Key, Integer>> iterator() {
        final Iterator<Key> iter = keys.iterator();

        return new Iterator<Map.Entry<Key, Integer>>() {
            @Override public boolean hasNext() {
                return iter.hasNext();
            }

            @Override public Map.Entry<Key, Integer> next() {
                MapEntry entry = new MapEntry();
                entry.key = iter.next();
                return entry;
            }

            @Override public void remove() {
                throw new UnsupportedOperationException();
            }
        };
    }

    @Override public int size() {
        return keys.size();
    }

    protected abstract int getValue(@NonNull Key key);
    protected abstract int setValue(@NonNull Key key, int value);
}
