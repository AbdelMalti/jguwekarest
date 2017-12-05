package io.swagger.api.algorithm;

import io.swagger.api.NotFoundException;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.core.UriInfo;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;

@javax.annotation.Generated(value = "io.swagger.codegen.languages.JavaJerseyServerCodegen", date = "2017-09-11T12:03:46.572Z")
public abstract class BayesService {
    public abstract Response algorithmBayesNetPost(InputStream fileInputStream, FormDataContentDisposition fileDetail, String datasetURI, String estimator, BigDecimal estimatorParams, Integer useADTree, String searchAlgorithm, String searchParams, HttpHeaders headers, UriInfo ui, SecurityContext securityContext) throws NotFoundException, IOException;
}
