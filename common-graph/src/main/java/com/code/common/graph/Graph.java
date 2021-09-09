package com.code.common.graph;

import java.util.Map;

/**
 * @author Pan Jiebin
 * @date 2020-10-14 13:35
 */
public interface Graph<V extends Vertex, E extends Edge> extends Element {

    void addVertex(V vertex);

    default void addVertices(V...vertices) {
        for (V vertex : vertices) {
            this.addVertex(vertex);
        }
    }

    void addEdge(E edge);

    default void addEdges(E... edges) {
        for (E edge : edges) {
            this.addEdge(edge);
        }
    }

    Map<String, V> getVertices();

    Map<String, E> getEdges();
}
