package io.swagger.api.data;


import io.swagger.annotations.*;
import io.swagger.api.ApiException;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.util.Map;

@Path("/")
@Api(description = "Model API")

public class Model {

    @GET
    @Path("/model")
    @Consumes({ "multipart/form-data" })
    @Produces({ "text/uri-list", "application/json" })
    @ApiOperation(
            value = "List all Models",
            notes = "List all Models.",
            tags={ "model", },
            response = void.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK", response = void.class),
            @ApiResponse(code = 400, message = "Bad Request", response = void.class),
            @ApiResponse(code = 401, message = "Unauthorized", response = void.class),
            @ApiResponse(code = 403, message = "Forbidden", response = void.class),
            @ApiResponse(code = 404, message = "Resource Not Found", response = void.class) })
    public Response getModelList(
            @ApiParam(value = "Authorization token" )@HeaderParam("subjectid") String subjectid,
            @Context UriInfo ui, @Context HttpHeaders headers) throws ApiException {

        String accept = headers.getRequestHeaders().getFirst("accept");
        Object model_list = ModelService.listModels(ui, accept, subjectid);

        return Response
                .ok(model_list)
                .status(Response.Status.OK)
                .build();
    }


    @GET
    @Path("/model/{id}")
    @Consumes({ "multipart/form-data" })
    @Produces({ "text/plain" })
    @ApiOperation(
            value = "Get representation of a model.",
            notes = "Get representation of a model.",
            tags={ "model", },
            response = void.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK", response = void.class),
            @ApiResponse(code = 400, message = "Bad Request", response = void.class),
            @ApiResponse(code = 401, message = "Unauthorized", response = void.class),
            @ApiResponse(code = 403, message = "Forbidden", response = void.class),
            @ApiResponse(code = 404, message = "Resource Not Found", response = void.class) })
    public Response getModel(
            @ApiParam(value = "model ID" )@PathParam("id") String id,
            @ApiParam(value = "Authorization token" )@HeaderParam("subjectid") String subjectid, @Context UriInfo ui) throws ApiException {

        String out = ModelService.getModel(id);

        return Response
                .ok(out)
                .status(Response.Status.OK)
                .build();
    }


    public Map<String, String> meta;
    public String hasSources;
    public Dataset dataset;

    public class MetaData {
        public String info;
        public String className;
        public String options;
    }

    public void setMeta(Map<String, String> meta) {
        this.meta = meta;
    }

    public byte[] model;

    public String validation;




}
