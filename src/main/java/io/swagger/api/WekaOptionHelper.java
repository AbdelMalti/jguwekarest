package io.swagger.api;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Objects;

public class WekaOptionHelper {



    public static String[] getClassifierOptions(String classifier, HashMap params){
        String[] options = null;
        switch (classifier) {
            case "J48":
                options = getJ48Options(params);
                break;
            case "KNN":
                //options = getKNNOptions(params);
                break;
            case "BayesNet":
                options = getBayesNetOptions(params);
                break;
            case "LibSVM":
                //options = getLibSVMOptions(params);
                break;
            case "M5Rule":
                //options = getM5RuleOptions(params);
                break;
            case "AdaBoost":
                options = getAdaBoostOptions(params);
                break;
            case "Bagging":
                options = getBaggingOptions(params);
                break;
        }

        return options;
    }

    /**
     * Generate option string for J48
     * @return options array
     */
    public static String[] getJ48Options(HashMap params) {

                                         //Integer binarySplits, BigDecimal confidenceFactor, Integer minNumObj, Integer numFolds,
                                         //Integer reducedErrorPruning, Integer seed, Integer subtreeRaising, Integer unpruned, Integer useLaplace) {
        String parameters = "";
        String[] options;
        if (params.get("binarySplits") != null && params.get("binarySplits").toString().equals("1")) {
            parameters += " -B ";
        }

        parameters += getParamString(params.get("minNumObj"), "M", 2);

        if (params.get("reducedErrorPruning") != null && params.get("reducedErrorPruning").toString().equals("1")) {
            if (params.get("numFolds") != null) {
                parameters += " -R -N " + params.get("numFolds");
            } else {
                parameters += " -R -N 3 ";
            }
        }

        parameters += getParamString(params.get("seed"), "Q", 2);

        if (params.get("unpruned") != null && params.get("unpruned").toString().equals("1")) {
            parameters += " -U ";
        } else {
            if (params.get("subtreeRaising") != null && params.get("subtreeRaising").toString().equals("0")) {
                parameters += " -S ";
            }
            if (params.get("confidenceFactor") != null) {
                parameters += " -C " + params.get("confidenceFactor");
            } else {
                parameters += " -C 0.25 ";
            }
        }

        if (params.get("useLaplace") != null && params.get("useLaplace").toString().equals("1")) {
            parameters += " -A ";
        }

        System.out.println("parameterstring for weka: weka.classifiers.trees.J48 " + parameters);

        try {
            options = weka.core.Utils.splitOptions(parameters);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        return options;
    }

    /**
     * Generate option string for KNN
     * @return options array
     */
    public static String[] getKNNOptions(Integer windowSize, Integer KNN, Integer crossValidate, String distanceWeighting, Integer meanSquared,
                                         String nearestNeighbourSearchAlgorithm) {
        String parameters = "";
        String[] options;
        parameters += getParamString(windowSize, "W", 0);
        parameters += getParamString(KNN, "K", 1);
        parameters += ((crossValidate != null && crossValidate != 0) ? " -X " : "");

        if (distanceWeighting != null) {
            if (distanceWeighting.equals("F") || distanceWeighting.equals("I")) {
                parameters += " -" + distanceWeighting + " ";
            }
        }

        if (meanSquared != null && meanSquared != 0) parameters += " -E ";


        //if (nearestNeighbourSearchAlgorithm != null && !nearestNeighbourSearchAlgorithm.isEmpty()) {
            //use LinearNNSearch fixed
            parameters += " -A ";
            parameters += "\"weka.core.neighboursearch.LinearNNSearch -A \\\"weka.core.EuclideanDistance -R first-last\\\"\"";
        //}

        System.out.println("parameterstring for weka: IBk " + parameters.replaceAll("( )+", " "));

        try {
            options = weka.core.Utils.splitOptions(parameters);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        return options;
    }

    /**
     * Generate option string for BayesNet
     * @return options array
     */
    public static String[] getBayesNetOptions(HashMap params){
        //String estimator, BigDecimal estimatorParams, Integer useADTree, String searchAlgorithm, String searchParams

        String parameters = "";
        String[] options;
        if (params.get("useADTree") != null && !params.get("useADTree").toString().equals("0")) { parameters += " -D ";}

        // Set the parameter for the searchAlgo
        parameters += " -Q ";
        parameters += "weka.classifiers.bayes.net.search." + params.get("searchAlgorithm");
        System.out.println("searchAlgorithm is: " + params.get("searchAlgorithm"));
        // Set the search parameters
        if (params.get("searchParams") != null) {
            parameters += " -- ";
            parameters += params.get("searchParams");
        }
        // Set estimator
        if (params.get("estimator") != null) {
            parameters += " -E ";
            parameters += "weka.classifiers.bayes.net.estimate." + params.get("estimator");
        }
        // Set the parameters for the estimator
        if (params.get("estimatorParams") != null) {
            parameters += " -- ";
            parameters += " -A ";
            parameters += params.get("estimatorParams");
        }
        System.out.println("parameterstring for weka: " + parameters);

        try {
            options = weka.core.Utils.splitOptions(parameters);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return options;
    }

    /**
     * Generate option string for LibSVM
     * @return options array
     */
    public static String[] getLibSVMOptions(Integer svmType, Float coef0, Float cost, Integer degree, BigDecimal eps, BigDecimal gamma, Integer kernelType,
                                BigDecimal loss, Boolean normalize, BigDecimal nu, Boolean probabilityEstimates, Boolean shrinking,
                                String weights){
        String parameters = "";
        String[] options;
        parameters += getParamString(svmType, "S", 0);
        parameters += getParamString(coef0, "R", 0);
        parameters += getParamString(cost, "C", "1.0");
        parameters += getParamString(degree, "D", 3);
        parameters += getParamString(eps, "E", "0.001");
        parameters += getParamString(gamma, "G", "0.0");
        parameters += getParamString(kernelType, "K", 0);
        parameters += getParamString(loss, "P", "0.1");

        if(normalize != null && normalize) parameters += " -Z ";

        parameters += getParamString(nu, "N", "0.5");

        if (probabilityEstimates != null && probabilityEstimates) parameters +=  " -B ";

        if (shrinking != null && !shrinking) parameters +=  " -H ";

        if(weights != null && !Objects.equals(weights, "")) parameters +=  " -W \"" + weights + "\"";

        try {
            options = weka.core.Utils.splitOptions(parameters);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return options;
    }

    /**
     * Generate option string for M5Rule
     * @return options array
     */
    public static String[] getM5RuleOptions(Integer unpruned, Integer useUnsmoothed, Double minNumInstances, Integer buildRegressionTree) {

        String parameters = "";
        String[] options;
        // set unpruned
        if (unpruned != null && unpruned == 1) { parameters += " -N ";}

        // set use unsmoothed
        if (useUnsmoothed != null && useUnsmoothed == 1) { parameters += " -U ";}

        // Set minNumInstances
        parameters += WekaOptionHelper.getParamString(minNumInstances, "M", "4.0");

        // set buildRegressionTree
        if (buildRegressionTree != null && buildRegressionTree == 1) { parameters += " -R ";}

        System.out.println("parameterstring for weka: M5Rules " + parameters);

        try {
            options = weka.core.Utils.splitOptions(parameters);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return options;
    }

    /**
     * Generate option string for Adaboost M1
     * @return options array
     */
    public static String[] getAdaBoostOptions(HashMap params) {

        //Integer batchSize, Integer numIterations, Integer useResampling, Integer weightThreshold){
        String parameters = "";
        String[] options;

        parameters += WekaOptionHelper.getParamString(params.get("weightThreshold"), "P", "100");
        if (params.get("useResampling") != null && params.get("useResampling").toString().equals("1")) { parameters += " -Q ";}

        parameters += WekaOptionHelper.getParamString(params.get("numIterations"),"I", 10);
        parameters += WekaOptionHelper.getParamString(params.get("batchSize") ,"batch-size", 100);
        try {
            options = weka.core.Utils.splitOptions(parameters);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return options;
    }

    /**
     * Generate option string for Bagging
     * @return options array
     */
    public static String[] getBaggingOptions(HashMap params){
        //Integer bagSizePercent, Integer batchSize, Integer numIterations
        String parameters = "";
        String[] options;

        parameters += WekaOptionHelper.getParamString(params.get("batchSize") ,"batch-size", 100);
        parameters += WekaOptionHelper.getParamString(params.get("bagSizePercent") ,"P", 100);
        parameters += WekaOptionHelper.getParamString(params.get("numIterations"),"I", 10);
        try {
            options = weka.core.Utils.splitOptions(parameters);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return options;
    }

    /**
     * Option-string helper method for WEKA options build from option, value and defaultValue.
     * <ul>
     *     <li>sets a value to a given option</li>
     *     <li>sets a defaultValue to a given option when value is null</li>
     *     <li>sets an option when both values are null</li>
     * </ul>
     * <pre>{@code
     * WekaUtils.getParamString(100, "R", 2) => " -R 100 "
     * WekaUtils.getParamString(null, "H", 2) => " -H 2 "
     * WekaUtils.getParamString(null, "X", null) => " -X "
     * }</pre>
     * @param value        value of the option
     * @param option       option to set
     * @param defaultValue default value is set when value is null
     * @return String the resulting string
     */
     public static String getParamString(Object value, String option, Object defaultValue ){
         if (value == null && defaultValue == null){
             return " -" + option + " ";
         } else {
             return ((value != null) ? (" -" + option + " " + value + " ") : (" -" + option + " " + defaultValue + " ") );
         }
     }
}