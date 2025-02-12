/*
 * Copyright 2014, Google LLC
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

package com.apkide.smali.dexlib2.rewriter;

import androidx.annotation.NonNull;

import com.apkide.smali.dexlib2.iface.Annotation;
import com.apkide.smali.dexlib2.iface.AnnotationElement;
import com.apkide.smali.dexlib2.iface.ClassDef;
import com.apkide.smali.dexlib2.iface.DexFile;
import com.apkide.smali.dexlib2.iface.ExceptionHandler;
import com.apkide.smali.dexlib2.iface.Field;
import com.apkide.smali.dexlib2.iface.Method;
import com.apkide.smali.dexlib2.iface.MethodImplementation;
import com.apkide.smali.dexlib2.iface.MethodParameter;
import com.apkide.smali.dexlib2.iface.TryBlock;
import com.apkide.smali.dexlib2.iface.debug.DebugItem;
import com.apkide.smali.dexlib2.iface.instruction.Instruction;
import com.apkide.smali.dexlib2.iface.reference.FieldReference;
import com.apkide.smali.dexlib2.iface.reference.MethodReference;
import com.apkide.smali.dexlib2.iface.value.EncodedValue;

/**
 * Out-of-the box, this class does nothing except make a picture-perfect copy of a dex file.
 *
 * However, it provides many points where you can hook into this process and selectively modify
 * the dex file. For example, If you want to rename all instances (including definitions and references)
 * of the class Lorg/blah/MyBlah; to Lorg/blah/YourBlah;
 *
 * <pre>
 * {@code
 * DexRewriter rewriter = new DexRewriter(new RewriterModule() {
 *     public Rewriter<String> getTypeRewriter(Rewriters rewriters) {
 *         return new Rewriter<String>() {
 *             public String rewrite(String value) {
 *                 if (value.equals("Lorg/blah/MyBlah;")) {
 *                     return "Lorg/blah/YourBlah;";
 *                 }
 *                 return value;
 *             }
 *         };
 *     }
 * });
 * DexFile rewrittenDexFile = rewriter.rewriteDexFile(dexFile);
 * }
 * </pre>
 */
public class DexRewriter implements Rewriters {
    private final Rewriter<DexFile> dexFileRewriter;
    private final Rewriter<ClassDef> classDefRewriter;
    private final Rewriter<Field> fieldRewriter;
    private final Rewriter<Method> methodRewriter;
    private final Rewriter<MethodParameter> methodParameterRewriter;
    private final Rewriter<MethodImplementation> methodImplementationRewriter;
    private final Rewriter<Instruction> instructionRewriter;
    private final Rewriter<TryBlock<? extends ExceptionHandler>> tryBlockRewriter;
    private final Rewriter<ExceptionHandler> exceptionHandlerRewriter;
    private final Rewriter<DebugItem> debugItemRewriter;
    private final Rewriter<String> typeRewriter;
    private final Rewriter<FieldReference> fieldReferenceRewriter;
    private final Rewriter<MethodReference> methodReferenceRewriter;
    private final Rewriter<Annotation> annotationRewriter;
    private final Rewriter<AnnotationElement> annotationElementRewriter;
    private final Rewriter<EncodedValue> encodedValueRewriter;

    public DexRewriter(RewriterModule module) {
        this.dexFileRewriter = module.getDexFileRewriter(this);
        this.classDefRewriter = module.getClassDefRewriter(this);
        this.fieldRewriter = module.getFieldRewriter(this);
        this.methodRewriter = module.getMethodRewriter(this);
        this.methodParameterRewriter = module.getMethodParameterRewriter(this);
        this.methodImplementationRewriter = module.getMethodImplementationRewriter(this);
        this.instructionRewriter = module.getInstructionRewriter(this);
        this.tryBlockRewriter = module.getTryBlockRewriter(this);
        this.exceptionHandlerRewriter = module.getExceptionHandlerRewriter(this);
        this.debugItemRewriter = module.getDebugItemRewriter(this);
        this.typeRewriter = module.getTypeRewriter(this);
        this.fieldReferenceRewriter = module.getFieldReferenceRewriter(this);
        this.methodReferenceRewriter = module.getMethodReferenceRewriter(this);
        this.annotationRewriter = module.getAnnotationRewriter(this);
        this.annotationElementRewriter = module.getAnnotationElementRewriter(this);
        this.encodedValueRewriter = module.getEncodedValueRewriter(this);
    }

    @NonNull
    @Override public Rewriter<DexFile> getDexFileRewriter() { return dexFileRewriter; }
    @NonNull @Override public Rewriter<ClassDef> getClassDefRewriter() { return classDefRewriter; }
    @NonNull @Override public Rewriter<Field> getFieldRewriter() { return fieldRewriter; }
    @NonNull @Override public Rewriter<Method> getMethodRewriter() { return methodRewriter; }
    @NonNull @Override public Rewriter<MethodParameter> getMethodParameterRewriter() { return methodParameterRewriter; }
    @NonNull @Override public Rewriter<MethodImplementation> getMethodImplementationRewriter() { return methodImplementationRewriter; }
    @NonNull @Override public Rewriter<Instruction> getInstructionRewriter() { return instructionRewriter; }
    @NonNull @Override public Rewriter<TryBlock<? extends ExceptionHandler>> getTryBlockRewriter() { return tryBlockRewriter; }
    @NonNull @Override public Rewriter<ExceptionHandler> getExceptionHandlerRewriter() { return exceptionHandlerRewriter; }
    @NonNull @Override public Rewriter<DebugItem> getDebugItemRewriter() { return debugItemRewriter; }
    @NonNull @Override public Rewriter<String> getTypeRewriter() { return typeRewriter; }
    @NonNull @Override public Rewriter<FieldReference> getFieldReferenceRewriter() { return fieldReferenceRewriter; }
    @NonNull @Override public Rewriter<MethodReference> getMethodReferenceRewriter() { return methodReferenceRewriter; }
    @NonNull @Override public Rewriter<Annotation> getAnnotationRewriter() { return annotationRewriter; }
    @NonNull @Override public Rewriter<AnnotationElement> getAnnotationElementRewriter() { return annotationElementRewriter; }
    @NonNull @Override public Rewriter<EncodedValue> getEncodedValueRewriter() { return encodedValueRewriter; }
}
