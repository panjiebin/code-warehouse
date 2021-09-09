package com.code.common.graph;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author Pan Jiebin
 * @date 2020-10-14 14:09
 */
public class BaseGraph<V extends Vertex, E extends Edge> extends BaseElement implements Graph<V, E> {

    protected Map<String, V> vertices = new LinkedHashMap<>();
    protected Map<String, E> edges = new LinkedHashMap<>();

    public BaseGraph(String id, String label) {
        super(id, label);
    }

    @Override
    public void addVertex(V vertex) {
        this.vertices.put(vertex.getId(), vertex);
    }

    @Override
    public void addEdge(E edge) {
        this.edges.put(edge.getId(), edge);
    }

    @Override
    public Map<String, V> getVertices() {
        return this.vertices;
    }

    @Override
    public Map<String, E> getEdges() {
        return this.edges;
    }

    public Map<String, V> searchInitVertices() {
        Map<String, V> starts = new LinkedHashMap<>();
        for (Map.Entry<String, V> entry : vertices.entrySet()) {
            if (entry.getValue().getInEdges().isEmpty()) {
                starts.put(entry.getKey(), entry.getValue());
            }
        }
        return starts;
    }

    public Map<String, V> searchEndVertices() {
        Map<String, V> ends = new LinkedHashMap<>();
        for (Map.Entry<String, V> entry : vertices.entrySet()) {
            if (entry.getValue().getOutEdges().isEmpty()) {
                ends.put(entry.getKey(), entry.getValue());
            }
        }
        return ends;
    }
}
