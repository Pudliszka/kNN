import weka.classifiers.Evaluation;
import weka.classifiers.lazy.IBk;
import weka.core.*;
import weka.core.converters.ConverterUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class Main {

    public static void main(String[] args) throws Exception {
        ManhattanDistance manDistance = new ManhattanDistance();
        EuclideanDistance euDistance = new EuclideanDistance();
        ChebyshevDistance cheDistance = new ChebyshevDistance();

        MyExcelUtils myExcel = new MyExcelUtils("data/Excel.xlsx", "Data1");
        for (int typeDistance = 1; typeDistance <= 3; typeDistance++) {
            int finalTypeDistance = typeDistance;
            new Thread(() -> {
                String fileName = "";
                switch (finalTypeDistance) {
                    case 1:
                        fileName = "ManhattanDistance";
                        break;
                    case 2:
                        fileName = "EuclideanDistance";
                        break;
                    case 3:
                        fileName = "ChebyshevDistance";
                        break;
                }
                for (int k = 1; k <= 116; k++) {
                    try {
                        ConverterUtils.DataSource source = new ConverterUtils.DataSource("data/Dry_Bean_Dataset.arff");
                        Instances data = source.getDataSet();
                        if (data.classIndex() == -1) {
                            data.setClassIndex(data.numAttributes() - 1);
                        }
                        IBk classifier = new IBk();
                        classifier.buildClassifier(data);
                        String[] options = Utils.splitOptions("-K " + k + " -W 0");
                        classifier.setOptions(options);
                        classifier.setCrossValidate(false);
                        classifier.setBatchSize("100");
                        classifier.setDebug(false);
                        classifier.setDoNotCheckCapabilities(false);
                        classifier.setMeanSquared(false);
                        switch (finalTypeDistance) {
                            case 1:
                                classifier.getNearestNeighbourSearchAlgorithm().setDistanceFunction(manDistance);
                                break;
                            case 2:
                                classifier.getNearestNeighbourSearchAlgorithm().setDistanceFunction(euDistance);
                                break;
                            case 3:
                                classifier.getNearestNeighbourSearchAlgorithm().setDistanceFunction(cheDistance);
                                break;
                        }
                        classifier.setNumDecimalPlaces(2);
                        classifier.setWindowSize(0);

                        System.out.println("START K = " + k + " for " + fileName);
                        classifier.buildClassifier(data);
                        Evaluation eval = new Evaluation(data);
                        var startDate = new Date();
                        eval.crossValidateModel(classifier, data, 10, new Random(1));
                        var endDate = new Date();
                        var second = Math.abs(endDate.getTime() - startDate.getTime()) / 1000;

                        myExcel.setData(k - 1, 0, Double.valueOf(k), fileName);
                        myExcel.setData(k - 1, 1, Double.valueOf(String.valueOf(new BigDecimal(eval.truePositiveRate(0)).setScale(6, RoundingMode.HALF_UP))), fileName);
                        myExcel.setData(k - 1, 2, Double.valueOf(String.valueOf(new BigDecimal(eval.truePositiveRate(1)).setScale(6, RoundingMode.HALF_UP))), fileName);
                        myExcel.setData(k - 1, 3, Double.valueOf(String.valueOf(new BigDecimal(eval.truePositiveRate(2)).setScale(6, RoundingMode.HALF_UP))), fileName);
                        myExcel.setData(k - 1, 4, Double.valueOf(String.valueOf(new BigDecimal(eval.truePositiveRate(3)).setScale(6, RoundingMode.HALF_UP))), fileName);
                        myExcel.setData(k - 1, 5, Double.valueOf(String.valueOf(new BigDecimal(eval.truePositiveRate(4)).setScale(6, RoundingMode.HALF_UP))), fileName);
                        myExcel.setData(k - 1, 6, Double.valueOf(String.valueOf(new BigDecimal(eval.truePositiveRate(5)).setScale(6, RoundingMode.HALF_UP))), fileName);
                        myExcel.setData(k - 1, 7, Double.valueOf(String.valueOf(new BigDecimal(eval.truePositiveRate(6)).setScale(6, RoundingMode.HALF_UP))), fileName);
                        myExcel.setData(k - 1, 8, Double.valueOf(String.valueOf(new BigDecimal(eval.weightedTruePositiveRate()).setScale(6, RoundingMode.HALF_UP))), fileName);
                        myExcel.setData(k - 1, 9, Double.valueOf(String.valueOf(second)), fileName);
                    } catch (Exception e) {
                        System.err.println(e);
                    }

                }
            }).start();

        }
    }

}
