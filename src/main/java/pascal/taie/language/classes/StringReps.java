/*
 * Tai-e: A Program Analysis Framework for Java
 *
 * Copyright (C) 2020 Tian Tan <tiantan@nju.edu.cn>
 * Copyright (C) 2020 Yue Li <yueli@nju.edu.cn>
 * All rights reserved.
 *
 * This software is designed for the "Static Program Analysis" course at
 * Nanjing University, and it supports a subset of Java features.
 * Tai-e is only for educational and academic purposes, and any form of
 * commercial use is disallowed.
 */

package pascal.taie.language.classes;

import pascal.taie.ir.proginfo.MethodRef;
import pascal.taie.language.types.Type;
import pascal.taie.util.AnalysisException;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Utility class for compute string representations of various program
 * elements, such as class name, method descriptor, method signature, etc.
 */
public class StringReps {

    // Names of special important classes
    public static final String OBJECT = "java.lang.Object";

    public static final String CLASS = "java.lang.Class";

    public static final String SERIALIZABLE = "java.lang.Serializable";

    public static final String CLONEABLE = "java.lang.Cloneable";

    public static final String STRING = "java.lang.String";

    public static final String STRING_BUILDER = "java.lang.StringBuilder";

    public static final String STRING_BUFFER ="java.lang.StringBuffer";

    public static final String THREAD = "java.lang.Thread";

    public static final String THREAD_GROUP = "java.lang.ThreadGroup";

    public static final String THROWABLE = "java.lang.Throwable";

    public static final String METHOD_HANDLE = "java.lang.invoke.MethodHandle";

    public static final String VAR_HANDLE = "java.lang.invoke.VarHandle";

    // Subsignatures of special methods
    public static final String CLINIT = "void <clinit>()";

    // Signature of special methods
    public static final String FINALIZE = "<java.lang.Object: void finalize()>";

    public static final String FINALIZER_REGISTER = "<java.lang.ref.Finalizer: void register(java.lang.Object)>";

    public static final String REFERENCE_INIT = "<java.lang.ref.Reference: void <init>(java.lang.Object,java.lang.ref.ReferenceQueue)>";

    // Signature of special fields
    public static final String REFERENCE_PENDING = "<java.lang.ref.Reference: java.lang.ref.Reference pending>";

    // Suppresses default constructor, ensuring non-instantiability.
    private StringReps() {
    }

    public static String getClassNameOf(String signature) {
        validateSignature(signature);
        int index = signature.indexOf(":");
        return signature.substring(1, index);
    }

    public static String getSignatureOf(JMethod method) {
        return getMethodSignature(method.getDeclaringClass(), method.getName(),
                method.getParamTypes(), method.getReturnType());
    }

    public static String getMethodSignature(
            JClass declaringClass, String methodName,
            List<Type> parameterTypes, Type returnType) {
        return "<" +
                declaringClass + ": " +
                toSubsignature(methodName, parameterTypes, returnType) +
                ">";
    }

    public static String getSignatureOf(JField field) {
        return getFieldSignature(field.getDeclaringClass(),
                field.getName(), field.getType());
    }

    public static String getFieldSignature(
            JClass declaringClass, String fieldName, Type fieldType) {
        return "<" + declaringClass + ": " + fieldType + " " + fieldName + ">";
    }

    public static String getFieldNameOf(String fieldSig) {
        validateSignature(fieldSig);
        int index = fieldSig.lastIndexOf(' ');
        return fieldSig.substring(index + 1, fieldSig.length() - 1);
    }

    public static String getSubsignatureOf(JMethod method) {
        return toSubsignature(method.getName(),
                method.getParamTypes(), method.getReturnType());
    }

    public static String getSubsignatureOf(MethodRef methodRef) {
        throw new UnsupportedOperationException();
    }

    public static String getSubsignatureOf(String methodSig) {
        validateSignature(methodSig);
        int index = methodSig.indexOf(":");
        return methodSig.substring(index + 2, methodSig.length() - 1);
    }

    public static String getDescriptorOf(JMethod method) {
        throw new UnsupportedOperationException();
    }

    public static String getDescriptorOf(MethodRef methodRef) {
        throw new UnsupportedOperationException();
    }

    public static String toDescriptor(List<Type> parameterTypes, Type returnType) {
        throw new UnsupportedOperationException();
    }

    public static String toSubsignature(String name, List<Type> parameterTypes, Type returnType) {
        return returnType + " " +
                name +
                "(" +
                parameterTypes.stream()
                        .map(Type::toString)
                        .collect(Collectors.joining(",")) +
                ")";
    }

    private static void validateSignature(String signature) {
        if (signature.charAt(0) != '<' &&
                signature.charAt(signature.length() - 1) != '>') {
            throw new AnalysisException(signature + " is not valid signature");
        }
        int index = signature.indexOf(":");
        if (index < 0) {
            throw new AnalysisException(signature + " is not valid signature");
        }
    }
}