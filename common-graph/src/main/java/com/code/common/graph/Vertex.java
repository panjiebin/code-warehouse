package com.code.common.graph;

import java.util.Map;

/**
 * @author Pan Jiebin
 * @date 2020-10-14 13:34
 */
public interface Vertex extends Element {

    void addEdge(Edge edge);

    Map<String, Edge> getInEdges();

    default Edge getFirstInEdge() {
        Map<String, Edge> inEdges = this.getInEdges();
        if (inEdges == null || inEdges.isEmpty()) {
            return null;
        }
        return inEdges.values().stream().findFirst().get();
    }

    Map<String, Edge> getOutEdges();

    default Edge getFirstOutEdge() {
        Map<String, Edge> outEdges = this.getOutEdges();
        if (outEdges == null || outEdges.isEmpty()) {
            return null;
        }
        return outEdges.values().stream().findFirst().get();
    }

    Map<String, Edge> getAllEdges();
}
