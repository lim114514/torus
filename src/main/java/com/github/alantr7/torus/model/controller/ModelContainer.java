package com.github.alantr7.torus.model.controller;

import com.github.alantr7.torus.model.ModelTemplate;

import java.util.List;

public class ModelContainer {

    public final List<ModelCase> matches;

    public final ModelTemplate compositeModel;

    public ModelContainer(List<ModelCase> matches, ModelTemplate compositeModel) {
        this.matches = matches;
        this.compositeModel = compositeModel;
    }

}
