package org.tum.thesis.experiment;

import org.tum.thesis.experiment.analysis.ExperimentResults;
import org.tum.thesis.experiment.cbf.CascadingBloomFilter;
import org.tum.thesis.experiment.cbf.CascadingFilter;
import org.tum.thesis.experiment.data.ExperimentData;
import org.tum.thesis.experiment.data.ExperimentDataGenerator;
import org.tum.thesis.experiment.data.VerifiableCredential;

import java.io.BufferedWriter;
import java.io.FileWriter;

public class ExperimentDriver {
    // TODO: 1. Extend experiment setup to execute N trials for a given configuration and collect averages.
    private static class ExperimentSize {
        private static final int BASE_DATA_SIZE = (int) Math.pow(2, 8);
        private static final int SUBSEQUENT_DATA_SIZE_MULTIPLIER = 2;
        private static final int MAX_DATA_SIZE = (int) Math.pow(2, 8);
        private static final int TRAILS_PER_DATA_SIZE = 1;
    }
    private static final double REVOCATION_RATE = 0.1;
    private static final String TARGET_DIRECTORY_NAME = "target";

    private static final String RESULT_FILE_NAME = "result1.csv";

    private static BufferedWriter bufferedWriter;

    public static void main(String[] args) throws Exception {
        FileWriter fileWriter  = new FileWriter(String.format("%s/%s", TARGET_DIRECTORY_NAME, RESULT_FILE_NAME));
        bufferedWriter = new BufferedWriter(fileWriter);
        writeResultHeader();
        int size = ExperimentSize.BASE_DATA_SIZE;
        while (size <= ExperimentSize.MAX_DATA_SIZE) {
            for (int i = 0; i < ExperimentSize.TRAILS_PER_DATA_SIZE; i++) {
                ExperimentData experimentData = ExperimentDataGenerator.generateData(size, REVOCATION_RATE);
//                CascadingBloomFilter cascadingBloomFilter = new CascadingBloomFilter();
//                ExperimentResults experimentResults = cascadingBloomFilter.generate(experimentData);
                CascadingBloomFilter cascadingBloomFilter = new CascadingBloomFilter();
                ExperimentResults experimentResults = cascadingBloomFilter.generate(experimentData);
                //, String.format("_%s_%s", size, REVOCATION_RATE), false);
                String resultString = String.format("%s, %s, %s", size, REVOCATION_RATE, experimentResults);
                System.out.println(resultString);
                writeResult(resultString);
                performSanityCheck(experimentData, cascadingBloomFilter);
            }
            size *= ExperimentSize.SUBSEQUENT_DATA_SIZE_MULTIPLIER;
        }
        bufferedWriter.close();
    }

    // 100: ExperimentResults[layerCount=2, bitSizes=[128, 64], falsePositives=[1], computationTime=19]
    // 1000: ExperimentResults[layerCount=3, bitSizes=[704, 256, 64], falsePositives=[30, 2], computationTime=48]
    // 10000: ExperimentResults[layerCount=4, bitSizes=[6976, 2048, 192, 64], falsePositives=[280, 19, 5], computationTime=126]
    // 100000: ExperimentResults[layerCount=6, bitSizes=[68992, 20288, 2240, 512, 64, 64], falsePositives=[2778, 299, 66, 7, 2], computationTime=341]
    // 800000: ExperimentResults[layerCount=7, bitSizes=[555008, 158976, 16704, 5056, 640, 128, 64], falsePositives=[21779, 2287, 690, 85, 14, 1], computationTime=757]
    private static void performSanityCheck(ExperimentData experimentData, CascadingFilter cascadingFilter)
            throws Exception {
        for (VerifiableCredential credential: experimentData.validCredentials()) {
            if (cascadingFilter.isRevoked(credential)) {
                throw new Exception("A valid credential is identified as revoked!");
            }
        }

        for (VerifiableCredential credential: experimentData.revokedCredentials()) {
            if (!cascadingFilter.isRevoked(credential)) {
                throw new Exception("A revoked credential is identified as valid!");
            }
        }
    }

    private static void writeResultHeader() throws Exception {
        String header = "Size,Revocation Rate,Layer Count,Bit Sizes,False Positive Rates,Computation Time";
        bufferedWriter.write(header);
        bufferedWriter.newLine();
    }
    private static void writeResult(String result) {
        try {
            bufferedWriter.append(result);
            bufferedWriter.newLine();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
