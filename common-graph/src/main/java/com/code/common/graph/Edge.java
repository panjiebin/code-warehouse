package com.code.common.graph;

/**
 * @author Pan Jiebin
 * @date 2020-10-14 13:35
 */
public interface Edge extends Element {

    Vertex getStart();

    Vertex getEnd();

}
