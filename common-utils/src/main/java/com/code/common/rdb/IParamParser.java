package com.code.common.rdb;

import java.util.List;

/**
 * @author Pan Jiebin
 * @date 2020-09-14 23:33
 */
public interface IParamParser<T> {

    List<Object> parse(T t);
}
