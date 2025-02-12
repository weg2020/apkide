/*
 * Copyright 2018, Google LLC
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

import com.apkide.smali.dexlib2.base.reference.BaseCallSiteReference;
import com.apkide.smali.dexlib2.iface.value.StringEncodedValue;
import com.apkide.smali.dexlib2.writer.DexWriter;
import com.apkide.smali.dexlib2.writer.builder.BuilderEncodedValues.BuilderArrayEncodedValue;
import com.apkide.smali.dexlib2.writer.builder.BuilderEncodedValues.BuilderEncodedValue;
import com.apkide.smali.dexlib2.writer.builder.BuilderEncodedValues.BuilderMethodHandleEncodedValue;
import com.apkide.smali.dexlib2.writer.builder.BuilderEncodedValues.BuilderMethodTypeEncodedValue;
import com.google.common.collect.ImmutableList;

import java.util.List;

public class BuilderCallSiteReference extends BaseCallSiteReference implements BuilderReference {
    @NonNull
    final String name;
    @NonNull final BuilderArrayEncodedValue encodedCallSite;
    int index = DexWriter.NO_INDEX;

    public BuilderCallSiteReference(@NonNull String name,
                                    @NonNull BuilderArrayEncodedValue encodedCallSite) {
        this.name = name;
        this.encodedCallSite = encodedCallSite;
    }

    @NonNull @Override public String getName() {
        return name;
    }

    @NonNull @Override public BuilderMethodHandleReference getMethodHandle() {
        return ((BuilderMethodHandleEncodedValue) encodedCallSite.elements.get(0)).getValue();
    }

    @NonNull @Override public String getMethodName() {
        return ((StringEncodedValue) encodedCallSite.elements.get(1)).getValue();
    }

    @NonNull @Override public BuilderMethodProtoReference getMethodProto() {
        return ((BuilderMethodTypeEncodedValue) encodedCallSite.elements.get(2)).getValue();
    }

    @NonNull @Override public List<? extends BuilderEncodedValue> getExtraArguments() {
        if (encodedCallSite.elements.size() <= 3) {
            return ImmutableList.of();
        }
        return encodedCallSite.elements.subList(3, encodedCallSite.elements.size());
    }

    @Override public int getIndex() {
        return index;
    }

    @Override public void setIndex(int index) {
        this.index = index;
    }
}
