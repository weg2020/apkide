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

package com.apkide.smali.dexlib2.writer.io;

import androidx.annotation.NonNull;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;

public class MemoryDataStore implements DexDataStore {
    private byte[] buf;
    private int size = 0;

    public MemoryDataStore() {
        this(0);
    }

    public MemoryDataStore(int initialCapacity) {
        buf = new byte[initialCapacity];
    }

    public byte[] getBuffer() {
        return buf;
    }

    public int getSize() {
        return size;
    }

    public byte[] getData() {
        return Arrays.copyOf(buf, size);
    }

    @NonNull
    @Override public OutputStream outputAt(final int offset) {
        if (offset < 0) throw new IllegalArgumentException();
        return new OutputStream() {
            private int position = offset;
            @Override public void write(int b) throws IOException {
                growBufferIfNeeded(position + 1);
                buf[position++] = (byte)b;
            }

            @Override public void write(byte[] b) throws IOException {
                growBufferIfNeeded(position + b.length);
                System.arraycopy(b, 0, buf, position, b.length);
                position += b.length;
            }

            @Override public void write(byte[] b, int off, int len) throws IOException {
                growBufferIfNeeded(position + len);
                System.arraycopy(b, off, buf, position, len);
                position += len;
            }
        };
    }

    private void growBufferIfNeeded(int minSize) {
        if (minSize > size) {
            if (minSize > buf.length) {
                int newSize = getNewBufferSize(buf.length, minSize);
                if (newSize < minSize) throw new IndexOutOfBoundsException();
                buf = Arrays.copyOf(buf, newSize);
            }
            size = minSize;
        }
    }

    protected int getNewBufferSize(int currentSize, int newMinSize) {
        final int MIN_GROWTH_STEP = 256 * 1024;
        return Math.max(newMinSize + (newMinSize >> 2), currentSize + MIN_GROWTH_STEP);
    }

    @NonNull @Override public InputStream readAt(final int offset) {
        if (offset < 0) throw new IllegalArgumentException();
        return new InputStream() {
            private int position = offset;
            private int mark = offset;

            @Override public int read() throws IOException {
                if (position >= size) {
                    return -1;
                }
                return buf[position++];
            }

            @Override public int read(byte[] b) throws IOException {
                int readLength = Math.min(b.length, size - position);
                if (readLength <= 0) {
                    if (position >= size) {
                        return -1;
                    }
                    return 0;
                }
                System.arraycopy(buf, position, b, 0, readLength);
                position += readLength;
                return readLength;
            }

            @Override public int read(byte[] b, int off, int len) throws IOException {
                int readLength = Math.min(len, size - position);
                if (readLength <= 0) {
                    if (position >= size) {
                        return -1;
                    }
                    return 0;
                }
                System.arraycopy(buf, position, b, off, readLength);
                position += readLength;
                return readLength;
            }

            @Override public long skip(long n) throws IOException {
                int skipLength = (int)Math.max(0, Math.min(n, size - position));
                position += skipLength;
                return skipLength;
            }

            @Override public int available() throws IOException {
                return Math.max(0, size - position);
            }

            @Override public void mark(int i) {
                mark = position;
            }

            @Override public void reset() throws IOException {
                position = mark;
            }

            @Override public boolean markSupported() {
                return true;
            }
        };
    }

    @Override public void close() throws IOException {
        // no-op
    }
}
