package io.swagger.api.impl;

import io.swagger.api.WekaUtils;
import io.swagger.api.cluster.ClusterService;
import io.swagger.api.data.DatasetService;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import weka.clusterers.ClusterEvaluation;
import weka.clusterers.EM;
import weka.core.Instances;
import weka.filters.unsupervised.attribute.StringToNominal;

import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import java.io.InputStream;

public class ClusterImpl extends ClusterService {
    @Override
    @Produces("text/plain")
    public Response clusterEMPost(InputStream fileInputStream, FormDataContentDisposition fileDetail, String datasetUri, SecurityContext securityContext, String subjectid) throws Exception {
        String txtStr = DatasetService.getArff(fileInputStream, fileDetail, datasetUri, subjectid);
        Instances instances = WekaUtils.instancesFromString(txtStr, true);
        String[] options = new String[2];
        options[0] = "-I";
        options[1] = "100";
        EM clusterer = new EM();
        clusterer.setOptions(options);


        StringToNominal s2n = new StringToNominal();
        s2n.setAttributeRange("first-last");

        s2n.setInputFormat(instances);
        Instances newData = new Instances(StringToNominal.useFilter(instances, s2n));

        clusterer.buildClusterer(newData);

        // evaluate clusterer
        ClusterEvaluation eval = new ClusterEvaluation();
        eval.setClusterer(clusterer);
        eval.evaluateClusterer(newData);

        return Response.ok(eval.clusterResultsToString()).build();
    }
}
