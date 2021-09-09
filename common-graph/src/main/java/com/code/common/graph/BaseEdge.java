package com.code.common.graph;

/**
 * @author Pan Jiebin
 * @date 2020-10-14 14:26
 */
public class BaseEdge extends BaseElement implements Edge {
    protected Vertex start;
    protected Vertex end;

    public BaseEdge(String id, String label, Vertex start, Vertex end) {
        super(id, label);
        this.start = start;
        this.end = end;
        this.start.addEdge(this);
        this.end.addEdge(this);
    }

    @Override
    public Vertex getStart() {
        return this.start;
    }

    @Override
    public Vertex getEnd() {
        return this.end;
    }

    public void setStart(Vertex start) {
        this.start = start;
    }

    public void setEnd(Vertex end) {
        this.end = end;
    }
}
