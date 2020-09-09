/*
 * Panda - A Program Analysis Framework for Java
 *
 * Copyright (C) 2020 Tian Tan <tiantan@nju.edu.cn>
 * Copyright (C) 2020 Yue Li <yueli@nju.edu.cn>
 * All rights reserved.
 *
 * This software is designed for the "Static Program Analysis" course at
 * Nanjing University, and it supports a subset of Java features.
 * Panda is only for educational and academic purposes, and any form of
 * commercial use is disallowed.
 */

package pascal.panda.pta.core.context;

import pascal.panda.pta.core.cs.CSCallSite;
import pascal.panda.pta.core.cs.CSMethod;
import pascal.panda.pta.core.cs.CSObj;
import pascal.panda.pta.element.Method;
import pascal.panda.pta.element.Obj;

/**
 * 1-object-sensitivity with no heap context.
 */
public class OneObjectSelector extends AbstractContextSelector {

    @Override
    public Context selectContext(CSCallSite callSite, Method callee) {
        return callSite.getContext();
    }

    @Override
    public Context selectContext(CSCallSite callSite, CSObj recv, Method callee) {
        return new OneContext<>(recv.getObject());
    }

    @Override
    protected Context doSelectHeapContext(CSMethod method, Obj obj) {
        return getDefaultContext();
    }
}