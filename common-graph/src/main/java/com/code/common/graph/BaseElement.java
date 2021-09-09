package com.code.common.graph;

/**
 * @author Pan Jiebin
 * @date 2020-10-14 14:06
 */
public class BaseElement implements Element {
    protected String id;
    protected String label;

    public BaseElement(String id, String label) {
        this.id = id;
        this.label = label;
    }

    @Override
    public String getId() {
        return this.id;
    }

    @Override
    public String getLabel() {
        return this.label;
    }
}
