/*
 * Tai-e: A Static Analysis Framework for Java
 *
 * Copyright (C) 2020-- Tian Tan <tiantan@nju.edu.cn>
 * Copyright (C) 2020-- Yue Li <yueli@nju.edu.cn>
 * All rights reserved.
 *
 * Tai-e is only for educational and academic purposes,
 * and any form of commercial use is disallowed.
 * Distribution of Tai-e is disallowed without the approval.
 */

package pascal.taie.analysis.pta.core.solver;

import pascal.taie.analysis.pta.core.cs.element.Pointer;
import pascal.taie.language.types.Type;

import java.util.Set;
import java.util.stream.Stream;

import static pascal.taie.util.collection.CollectionUtils.newSet;

public class PointerFlowGraph {

    private final Set<Pointer> pointers = newSet();

    public boolean addEdge(Pointer from, Pointer to,
                           PointerFlowEdge.Kind kind) {
        return addEdge(from, to, null, kind);
    }

    public boolean addEdge(Pointer from, Pointer to, Type type,
                           PointerFlowEdge.Kind kind) {
        if (from.addOutEdge(new PointerFlowEdge(kind, from, to, type))) {
            pointers.add(from);
            pointers.add(to);
            return true;
        } else {
            return false;
        }
    }

    public Stream<PointerFlowEdge> outEdgesOf(Pointer pointer) {
        return pointer.getOutEdges().stream();
    }

    public Stream<Pointer> pointer() {
        return pointers.stream();
    }
}