package io.swagger.api.factories;

import io.swagger.api.AlgorithmService;
import io.swagger.api.impl.AlgorithmImpl;

@javax.annotation.Generated(value = "io.swagger.codegen.languages.JavaJerseyServerCodegen", date = "2017-09-11T12:03:46.572Z")
public class AlgorithmFactory {
    private final static AlgorithmService service = new AlgorithmImpl();

    public static AlgorithmService getAlgorithm() {
        return service;
    }
}
