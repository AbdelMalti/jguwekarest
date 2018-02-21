package integration;

import helper.TestHelper;
import io.swagger.api.data.ModelService;
import org.glassfish.jersey.media.multipart.FormDataMultiPart;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.glassfish.jersey.media.multipart.file.FileDataBodyPart;
import org.testng.Assert;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import javax.ws.rs.client.*;
import javax.ws.rs.core.Response;
import java.io.File;

public class ModelTest {

    @Test(description = "Post an arff file to BayesNet algorithm and get a text representation.")
    @Parameters({"host"})
    public void algorithmBayesNetPost( @Optional  String host) throws Exception {

        String uri = host + "/algorithm/BayesNet";

        final Client client = ClientBuilder.newBuilder().register(MultiPartFeature.class).build();

        final FileDataBodyPart filePart = new FileDataBodyPart("file", new File( getClass().getClassLoader().getResource("weather.numeric.arff").getFile()));
        FormDataMultiPart formDataMultiPart = new FormDataMultiPart();
        formDataMultiPart.field("estimator", "SimpleEstimator")
                         .field("estimatorParams", "0.5")
                         .field("useADTree", "0")
                         .field("searchAlgorithm", "local.K2")
                         .field("searchParams", "-P 1 -S BAYES");

        final FormDataMultiPart multipart = (FormDataMultiPart) formDataMultiPart.field("save", "false").bodyPart(filePart);

        final WebTarget target = client.target(uri);
        Invocation.Builder request = target.request();
        request.accept("text/x-arff");

        final Response response = request.post(Entity.entity(multipart, multipart.getMediaType()));

        formDataMultiPart.close();
        multipart.close();

        Assert.assertTrue(response.getStatus() == 200);
        Assert.assertTrue(response.getMediaType().toString().equals("text/x-arff"));

    }

    @Test(description = "Post an arff file to BayesNet algorithm, save the model and do a prediction.")
    @Parameters({"host"})
    public void algorithmBayesNetPostAndPredict( @Optional  String host) throws Exception {

        String uri = host + "/algorithm/BayesNet";

        final Client client = ClientBuilder.newBuilder().register(MultiPartFeature.class).build();

        final FileDataBodyPart filePart = new FileDataBodyPart("file", new File( getClass().getClassLoader().getResource("weather.numeric.arff").getFile()));
        FormDataMultiPart formDataMultiPart = new FormDataMultiPart();
        formDataMultiPart.field("estimator", "SimpleEstimator")
                .field("estimatorParams", "0.5")
                .field("useADTree", "0")
                .field("searchAlgorithm", "local.K2")
                .field("searchParams", "-P 1 -S BAYES");

        final FormDataMultiPart multipart = (FormDataMultiPart) formDataMultiPart.bodyPart(filePart);

        final WebTarget target = client.target(uri);
        Invocation.Builder request = target.request();
        request.accept("text/uri-list");

        final Response response = request.post(Entity.entity(multipart, multipart.getMediaType()));

        formDataMultiPart.close();
        multipart.close();

        String model_uri = response.readEntity(String.class);

        Assert.assertTrue(response.getStatus() == 200);
        Assert.assertTrue(response.getMediaType().toString().equals("text/uri-list"));
        Assert.assertTrue(model_uri.matches(host + "/model/[a-fA-F\\d]{24}$"));

        // Prediction part

        final FileDataBodyPart filePartTestset = new FileDataBodyPart("file", new File( getClass().getClassLoader().getResource("weather.numeric.testset.arff").getFile()));
        FormDataMultiPart formDataMultiPartPrediction = new FormDataMultiPart();
        final FormDataMultiPart multipartPrediction = (FormDataMultiPart) formDataMultiPartPrediction.field("subjectid", "").bodyPart(filePartTestset);

        final WebTarget targetPrediction = client.target(model_uri);
        Invocation.Builder requestPrediction = targetPrediction.request();
        requestPrediction.accept("text/x-arff");

        final Response responsePrediction = requestPrediction.post(Entity.entity(multipartPrediction, multipartPrediction.getMediaType()));

        formDataMultiPartPrediction.close();
        multipartPrediction.close();

        String prediction_text = responsePrediction.readEntity(String.class);

        Assert.assertTrue(responsePrediction.getStatus() == 200);
        Assert.assertTrue(responsePrediction.getMediaType().toString().equals("text/x-arff"));
        Assert.assertTrue(prediction_text.contains("overcast,83,86,FALSE,yes,0.0"));
        Assert.assertTrue(prediction_text.contains("sunny,75,70,TRUE,yes,1.0"));
        Assert.assertFalse(prediction_text.contains("overcast,72,90,FALSE,yes,1.0"));
        String id = model_uri.substring(model_uri.length() - 24);
        Boolean resultDelete = ModelService.deleteModel(id);
        Assert.assertTrue(resultDelete);

    }


    @Test(description = "Post an arff file to J48 algorithm, save the model and do a prediction.")
    @Parameters({"host"})
    public void algorithmJ48( @Optional  String host) throws Exception {

        String uri = host + "/algorithm/J48";

        final Client client = ClientBuilder.newBuilder().register(MultiPartFeature.class).build();

        /*
        curl -X POST "https://cuttlefish.informatik.uni-mainz.de/algorithm/J48" -H  "accept: text/x-arff" -H  "Content-Type: multipart/form-data"
        -F "file=@weather.numeric.arff;type=" -F "binarySplits=0" -F "confidenceFactor=0.25" -F "minNumObj=2" -F "numFolds=3"
        -F "reducedErrorPruning=0" -F "seed=1" -F "subtreeRaising=1" -F "unpruned=1" -F "useLaplace=0"
        */

        final FileDataBodyPart filePart = new FileDataBodyPart("file", new File( getClass().getClassLoader().getResource("weather.numeric.arff").getFile()));
        FormDataMultiPart formDataMultiPart = new FormDataMultiPart();
        formDataMultiPart.field("binarySplits", "0")
                .field("confidenceFactor", "0.25")
                .field("minNumObj", "2")
                .field("numFolds", "3")
                .field("reducedErrorPruning", "0")
                .field("seed", "1")
                .field("subtreeRaising", "1")
                .field("unpruned", "1")
                .field("useLaplace", "0");

        final FormDataMultiPart multipart = (FormDataMultiPart) formDataMultiPart.bodyPart(filePart);

        final WebTarget target = client.target(uri);
        Invocation.Builder request = target.request();
        request.accept("text/uri-list");

        final Response response = request.post(Entity.entity(multipart, multipart.getMediaType()));

        formDataMultiPart.close();
        multipart.close();

        String model_uri = response.readEntity(String.class);

        Assert.assertTrue(response.getStatus() == 200);
        Assert.assertTrue(response.getMediaType().toString().equals("text/uri-list"));
        Assert.assertTrue(model_uri.matches(host + "/model/[a-fA-F\\d]{24}$"));

        // check new model String
        String savedModelString = TestHelper.getArff("J48.model");

        final WebTarget modelTarget = client.target(model_uri);
        Invocation.Builder modelRequest = modelTarget.request();
        modelRequest.accept("text/plain");

        final Response modelResponse = modelRequest.get();
        Assert.assertTrue(modelResponse.getStatus() == 200, "Model at host: " + model_uri + " not available.");
        Assert.assertTrue(modelResponse.getMediaType().toString().equals("text/plain"), "Model at host: " + model_uri + " not available in mime-type text/plain. Is: " + modelResponse.getMediaType().toString());
        Assert.assertEquals(modelResponse.readEntity(String.class).replaceAll("(?m) +$",""), savedModelString.replaceAll("(?m) +$",""));


        // Prediction part

        final FileDataBodyPart filePartTestset = new FileDataBodyPart("file", new File( getClass().getClassLoader().getResource("weather.numeric.testset.arff").getFile()));
        FormDataMultiPart formDataMultiPartPrediction = new FormDataMultiPart();
        final FormDataMultiPart multipartPrediction = (FormDataMultiPart) formDataMultiPartPrediction.field("subjectid", "").bodyPart(filePartTestset);

        final WebTarget targetPrediction = client.target(model_uri);
        Invocation.Builder requestPrediction = targetPrediction.request();
        requestPrediction.accept("text/x-arff");

        final Response responsePrediction = requestPrediction.post(Entity.entity(multipartPrediction, multipartPrediction.getMediaType()));

        formDataMultiPartPrediction.close();
        multipartPrediction.close();

        String prediction_text = responsePrediction.readEntity(String.class);

        Assert.assertTrue(responsePrediction.getStatus() == 200);
        Assert.assertTrue(responsePrediction.getMediaType().toString().equals("text/x-arff"));
        Assert.assertTrue(prediction_text.contains("sunny,85,85,FALSE,no,1.0\n" +
                "sunny,80,90,TRUE,no,1.0\n" +
                "overcast,83,86,FALSE,yes,0.0\n" +
                "rainy,70,96,FALSE,yes,0.0\n" +
                "rainy,68,80,FALSE,yes,0.0\n" +
                "rainy,65,70,TRUE,no,1.0\n" +
                "overcast,64,65,TRUE,yes,0.0\n" +
                "rainy,72,95,TRUE,no,1.0\n" +
                "sunny,69,70,FALSE,yes,0.0\n" +
                "rainy,75,80,FALSE,yes,0.0\n" +
                "sunny,75,70,TRUE,yes,0.0\n" +
                "overcast,72,90,FALSE,yes,0.0\n" +
                "overcast,81,75,FALSE,yes,0.0\n" +
                "overcast,71,91,TRUE,no,0.0"));
        String id = model_uri.substring(model_uri.length() - 24);
        Boolean resultDelete = ModelService.deleteModel(id);
        Assert.assertTrue(resultDelete);
    }


    @Test(description = "Post an arff file to J48 with Ada Boost M1 algorithm, save the model and do a prediction.")
    @Parameters({"host"})
    public void algorithmJ48AdaBoost( @Optional  String host) throws Exception {

        String uri = host + "/algorithm/J48/adaboost";

        final Client client = ClientBuilder.newBuilder().register(MultiPartFeature.class).build();

        /*
           curl -X POST "http://0.0.0.0:8081/algorithm/J48/adaboost" -H  "accept: text/x-arff" -H  "Content-Type: multipart/form-data"
           -F "file=@weather.numeric.arff;type=" -F "batchSize=100" -F "numIterations=10" -F "useResampling=0" -F "weightThreshold=100"
           -F "binarySplits=0" -F "confidenceFactor=0.25" -F "minNumObj=2" -F "numFolds=3" -F "reducedErrorPruning=0" -F "seed=1"
           -F "subtreeRaising=1" -F "unpruned=1" -F "useLaplace=0"
        */

        final FileDataBodyPart filePart = new FileDataBodyPart("file", new File( getClass().getClassLoader().getResource("weather.numeric.arff").getFile()));
        FormDataMultiPart formDataMultiPart = new FormDataMultiPart();
        formDataMultiPart
                .field("batchSize", "100")
                .field("numIterations", "10")
                .field("useResampling", "1")
                .field("weightThreshold", "100")
                .field("binarySplits", "0")
                .field("confidenceFactor", "0.25")
                .field("minNumObj", "2")
                .field("numFolds", "3")
                .field("reducedErrorPruning", "0")
                .field("seed", "1")
                .field("subtreeRaising", "1")
                .field("unpruned", "1")
                .field("useLaplace", "0");

        final FormDataMultiPart multipart = (FormDataMultiPart) formDataMultiPart.bodyPart(filePart);

        final WebTarget target = client.target(uri);
        Invocation.Builder request = target.request();
        request.accept("text/uri-list");

        final Response response = request.post(Entity.entity(multipart, multipart.getMediaType()));

        formDataMultiPart.close();
        multipart.close();

        String model_uri = response.readEntity(String.class);

        Assert.assertTrue(response.getStatus() == 200);
        Assert.assertTrue(response.getMediaType().toString().equals("text/uri-list"));
        Assert.assertTrue(model_uri.matches(host + "/model/[a-fA-F\\d]{24}$"));

        // check new model String
        String savedModelString = TestHelper.getArff("J48adaboost.model");

        final WebTarget modelTarget = client.target(model_uri);
        Invocation.Builder modelRequest = modelTarget.request();
        modelRequest.accept("text/plain");

        final Response modelResponse = modelRequest.get();
        Assert.assertTrue(modelResponse.getStatus() == 200, "Model at host: " + model_uri + " not available.");
        Assert.assertTrue(modelResponse.getMediaType().toString().equals("text/plain"), "Model at host: " + model_uri + " not available in mime-type text/plain. Is: " + modelResponse.getMediaType().toString());
        Assert.assertEquals(modelResponse.readEntity(String.class).replaceAll("(?m) +$",""), savedModelString.replaceAll("(?m) +$",""));

        // Prediction part

        final FileDataBodyPart filePartTestset = new FileDataBodyPart("file", new File( getClass().getClassLoader().getResource("weather.numeric.testset.arff").getFile()));
        FormDataMultiPart formDataMultiPartPrediction = new FormDataMultiPart();
        final FormDataMultiPart multipartPrediction = (FormDataMultiPart) formDataMultiPartPrediction.field("subjectid", "").bodyPart(filePartTestset);

        final WebTarget targetPrediction = client.target(model_uri);
        Invocation.Builder requestPrediction = targetPrediction.request();
        requestPrediction.accept("text/x-arff");

        final Response responsePrediction = requestPrediction.post(Entity.entity(multipartPrediction, multipartPrediction.getMediaType()));

        formDataMultiPartPrediction.close();
        multipartPrediction.close();

        String prediction_text = responsePrediction.readEntity(String.class);

        Assert.assertTrue(responsePrediction.getStatus() == 200);
        Assert.assertTrue(responsePrediction.getMediaType().toString().equals("text/x-arff"));
        Assert.assertTrue(prediction_text.contains("sunny,85,85,FALSE,no,1.0\n" +
                "sunny,80,90,TRUE,no,1.0\n" +
                "overcast,83,86,FALSE,yes,0.0\n" +
                "rainy,70,96,FALSE,yes,0.0\n" +
                "rainy,68,80,FALSE,yes,0.0\n" +
                "rainy,65,70,TRUE,no,1.0\n" +
                "overcast,64,65,TRUE,yes,0.0\n" +
                "rainy,72,95,TRUE,no,1.0\n" +
                "sunny,69,70,FALSE,yes,0.0\n" +
                "rainy,75,80,FALSE,yes,0.0\n" +
                "sunny,75,70,TRUE,yes,0.0\n" +
                "overcast,72,90,FALSE,yes,0.0\n" +
                "overcast,81,75,FALSE,yes,0.0\n" +
                "overcast,71,91,TRUE,no,0.0\n"));
        String id = model_uri.substring(model_uri.length() - 24);
        Boolean resultDelete = ModelService.deleteModel(id);
        Assert.assertTrue(resultDelete);
    }

    @Test(description = "Try to delete with a none existing model id")
    public void deleteModelFalse() throws Exception {
        String id = "1234567890abcdef12345678";
        Boolean resultDelete = ModelService.deleteModel(id);
        Assert.assertFalse(resultDelete);
    }
}
