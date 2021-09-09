package com.code.common.graph;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author Pan Jiebin
 * @date 2020-10-14 14:20
 */
public class BaseVertex extends BaseElement implements Vertex {

    protected Map<String, Edge> edges = new LinkedHashMap<>();

    public BaseVertex(String id, String label) {
        super(id, label);
    }

    @Override
    public void addEdge(Edge edge) {
        this.edges.put(edge.getId(), edge);
    }

    @Override
    public Map<String, Edge> getInEdges() {
        Map<String, Edge> inEdges = new LinkedHashMap<>();
        for (Map.Entry<String, Edge> entry : edges.entrySet()) {
            if (this.equals(entry.getValue().getEnd())) {
                inEdges.put(entry.getKey(), entry.getValue());
            }
        }
        return inEdges;
    }

    @Override
    public Map<String, Edge> getOutEdges() {
        Map<String, Edge> outEdges = new LinkedHashMap<>();
        for (Map.Entry<String, Edge> entry : edges.entrySet()) {
            if (this.equals(entry.getValue().getStart())) {
                outEdges.put(entry.getKey(), entry.getValue());
            }
        }
        return outEdges;
    }

    @Override
    public Map<String, Edge> getAllEdges() {
        return this.edges;
    }
}
