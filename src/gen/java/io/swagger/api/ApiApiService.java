package io.swagger.api;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
@javax.annotation.Generated(value = "io.swagger.codegen.languages.JavaJerseyServerCodegen", date = "2017-09-11T12:03:46.572Z")
public abstract class ApiApiService {
    public abstract Response apiApiJsonGet(SecurityContext securityContext) throws NotFoundException;
}
