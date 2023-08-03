package com.limethecoder.data.service;

import com.limethecoder.data.domain.Constants;

import java.util.List;


public interface ConstantsService {
    String GENRE_TYPES = "genres";

    Constants getInstance();
    Constants addConstant(String  element, String type);
    void deleteConstant(String element, String type);
    boolean isExistsConstant(String element, String type);
    long getCnt(String type);
    List<String> getConstantsByType(String type);
}
