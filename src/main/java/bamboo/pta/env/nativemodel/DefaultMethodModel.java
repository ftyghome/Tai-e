/*
 * Bamboo - A Program Analysis Framework for Java
 *
 * Copyright (C) 2020 Tian Tan <tiantan@nju.edu.cn>
 * Copyright (C) 2020 Yue Li <yueli@nju.edu.cn>
 * All rights reserved.
 *
 * This software is designed for the "Static Program Analysis" course at
 * Nanjing University, and it supports a subset of Java features.
 * Bamboo is only for educational and academic purposes, and any form of
 * commercial use is disallowed.
 */

package bamboo.pta.env.nativemodel;

import bamboo.pta.core.ProgramManager;
import bamboo.pta.element.Field;
import bamboo.pta.element.Method;
import bamboo.pta.element.Obj;
import bamboo.pta.element.Variable;
import bamboo.pta.env.EnvObj;
import bamboo.pta.env.Environment;
import bamboo.pta.options.Options;
import bamboo.pta.statement.Allocation;
import bamboo.pta.statement.Assign;
import bamboo.pta.statement.StaticStore;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

// TODO: for correctness, record which methods have been processed?
class DefaultMethodModel implements NativeMethodModel {

    private final ProgramManager pm;
    private final Environment env;
    // Use String as key is to avoid cyclic dependence during the
    // initialization of ProgramManager.
    // TODO: use Method as key to improve performance?
    private final Map<String, Consumer<Method>> handlers;

    DefaultMethodModel(ProgramManager pm, Environment env) {
        this.pm = pm;
        this.env = env;
        handlers = new HashMap<>();
        initHandlers();
    }

    @Override
    public void process(Method method) {
        Consumer<Method> handler = handlers.get(method.getSignature());
        if (handler != null) {
            handler.accept(method);
        }
    }

    private void initHandlers() {
        /**********************************************************************
         * java.lang.Object
         *********************************************************************/
        /**
         * <java.lang.Object: java.lang.Object clone()>
         *
         * TODO: could throw CloneNotSupportedException
         *
         * TODO: should check if the object is Cloneable.
         *
         * TODO: should return a clone of the heap allocation (not
         *      identity). The behaviour implemented here is based on Soot.
         */
        registerHandler("<java.lang.Object: java.lang.Object clone()>", method ->
            method.getReturnVariables().forEach(ret ->
                    method.addStatement(new Assign(ret, method.getThis())))
        );

        /**********************************************************************
         * java.lang.System
         *********************************************************************/
        /**
         * <java.lang.System: void setIn0(java.io.InputStream)>
         */
        registerHandler("<java.lang.System: void setIn0(java.io.InputStream)>", method -> {
            Field systemIn = pm.getUniqueFieldBySignature(
                    "<java.lang.System: java.io.InputStream in>");
            Variable param0 = method.getParam(0).get();
            method.addStatement(new StaticStore(systemIn, param0));
        });

        /**
         * <java.lang.System: void setOut0(java.io.PrintStream)>
         */
        registerHandler("<java.lang.System: void setOut0(java.io.PrintStream)>", method -> {
            Field systemIn = pm.getUniqueFieldBySignature(
                    "<java.lang.System: java.io.PrintStream out>");
            Variable param0 = method.getParam(0).get();
            method.addStatement(new StaticStore(systemIn, param0));
        });

        /**
         * <java.lang.System: void setErr0(java.io.PrintStream)>
         */
        registerHandler("<java.lang.System: void setErr0(java.io.PrintStream)>", method -> {
            Field systemIn = pm.getUniqueFieldBySignature(
                    "<java.lang.System: java.io.PrintStream err>");
            Variable param0 = method.getParam(0).get();
            method.addStatement(new StaticStore(systemIn, param0));
        });

        /**********************************************************************
         * java.io.FileSystem
         *********************************************************************/
        final List<String> concreteFileSystems = Arrays.asList(
                "java.io.UnixFileSystem",
                "java.io.WinNTFileSystem",
                "java.io.Win32FileSystem"
        );
        /**
         * <java.io.FileSystem: java.io.FileSystem getFileSystem()>
         */
        registerHandler("<java.io.FileSystem: java.io.FileSystem getFileSystem()>", method -> {
            if (Options.get().jdkVersion() < 7) {
                for (String fsName : concreteFileSystems) {
                    pm.tryGetUniqueTypeByName(fsName).ifPresent(fs -> {
                        Obj fsObj = new EnvObj(fs.getName(), fs, method);
                        method.getReturnVariables().forEach(ret -> {
                            method.addStatement(new Allocation(ret, fsObj));
                        });
                    });
                }
            }
        });
    }

    private void registerHandler(String signature, Consumer<Method> handler) {
        handlers.put(signature, handler);
    }
}