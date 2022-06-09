/*
 * WipeOutR: Automated Redundancy Detection for Feature Models
 *
 * Copyright (c) 2022-2022 AIG team, Institute for Software Technology,
 * Graz University of Technology, Austria
 *
 * Contact: http://ase.ist.tugraz.at/ASE/
 */

package at.tugraz.ist.ase.wipeoutr.app.cli;

import at.tugraz.ist.ase.common.LoggerUtils;
import lombok.Getter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

/**
 * A class to manage the input configuration of WipeOutR applications.
 *
 * @author Viet-Man Le (vietman.le@ist.tugraz.at)
 */
@Getter
@ToString
@Slf4j
public final class ConfigManager {
    public static String defaultConfigFile_RuntimeForTestcasesV2 = "./conf/testcases_runtime.cfg";
    public static String defaultConfigFile_TestCasesSelector = "./conf/ts_select.cfg";
    public static String defaultConfigFile_TestCasesGenerator = "./conf/ts_gen.cfg";
    public static String defaultConfigFile_WipeOutFMEvaluationV2 = "./conf/wipeoutr_fm.cfg";
    public static String defaultConfigFile_SolverRuntimeEvaluation = "./conf/solver_runtime.cfg";
    public static String defaultConfigFile_WipeOutTEvaluationV2 = "./conf/wipeoutr_t.cfg";
    public static String defaultConfigFile_RedConstraintsGenerator = "./conf/rc_gen.cfg";

    private final String fullnameKB;
    private final String nameKB;
    private final String dataPath;
//    private final String fmPath;
    private final String outputPath;
    private final String scenarioPath;

    private final List<Integer> cardTC;
    private final List<Double> redRatios;
    private final int numIter;

    private final int maxFeaturesInTestCase;
    private final int maxCombinations;
    private final boolean randomlySearch;

    private final boolean hasNonRedundantConstraints;

    private static ConfigManager instance = null;

    public static ConfigManager getInstance(String configFile) {
        if (instance == null) {
            instance = new ConfigManager(configFile);
        }
        return instance;
    }

    private ConfigManager(String configFile) {
//        String appConfigPath = "./app.cfg";
        Properties appProps = new Properties();
        try {
            appProps.load(new FileInputStream(configFile));
        } catch (IOException e) {
            log.error("{}{}", LoggerUtils.tab(), e.getMessage());
        }

        dataPath = appProps.getProperty("dataPath", "./data/");
//        fmPath = appProps.getProperty("fm_path", "./data/fms/");
        outputPath = appProps.getProperty("outputPath", "./results/");
        scenarioPath = appProps.getProperty("scenarioPath", "./data/scenario/");

        fullnameKB = appProps.getProperty("nameKB", null);

        if (fullnameKB != null) {
            int index = fullnameKB.lastIndexOf(".");
            if (index != -1) {
                nameKB = fullnameKB.substring(0, index);
            } else {
                nameKB = fullnameKB;
            }
        } else {
            nameKB = null;
        }

        List<String> items = List.of(appProps.getProperty("cardTC", "5,10,25,50,100,250,500").split(","));
        cardTC = new LinkedList<>();
        for (String item : items) {
            cardTC.add(Integer.parseInt(item));
        }

        items = List.of(appProps.getProperty("redRatio", "0.0,0.2,0.5,0.7,0.9").split(","));
        redRatios = new LinkedList<>();
        for (String item : items) {
            redRatios.add(Double.parseDouble(item));
        }

        numIter = Integer.parseInt(appProps.getProperty("numIter", "3"));
        maxFeaturesInTestCase = Integer.parseInt(appProps.getProperty("maxFeaturesInTestCase", "5"));
        maxCombinations = Integer.parseInt(appProps.getProperty("maxCombinations", "3000"));
        randomlySearch = Boolean.parseBoolean(appProps.getProperty("randomlySearch", "false"));

        hasNonRedundantConstraints = Boolean.parseBoolean(appProps.getProperty("hasNonRedundantConstraints", "false"));

        log.trace("{}<<< Read configurations [dataPath={}, fullnameKB={}, resultPath={}]", LoggerUtils.tab(), dataPath, fullnameKB, outputPath);
    }

    public String getKBFilepath() {
        return dataPath + fullnameKB;
    }

    public String getFilepathToTestcaseRuntimeEvaluation() {
        File scenariosFolder = new File(scenarioPath);
        return outputPath + "runtime_" + nameKB + "_" + scenariosFolder.getName() + ".txt";
    }

    public String getScenariosPathInResults() {
        return outputPath + "scenarios";
    }

    public String getTestSuiteFilepath() {
        return getTestSuiteFolderPath() + nameKB + ".testsuite";
    }

    public String getTestSuiteFolderPath() {
        return dataPath + "testsuite/";
    }

    public String getTestSuiteOutputPath() {
        return outputPath + "testsuite/";
    }

    public String getRedConstraintsFilepath() {
        return outputPath + getNameKB() + "_redconstraints.xml";
    }

    public String getNonRedundantConstraintsFilepath() {
        return dataPath + nameKB + "_nonred.txt";
    }

    public String getNonRedundantConstraintsFilepathInResults() {
        return getOutputPath() + getNameKB() + "_nonred.txt";
    }

    public String getWipeOutFMResultsFilepath() {
        return outputPath + "wipeout_fm_" + nameKB + ".txt";
    }

    public String getWipeOutTResultsFilepath() {
        File scenariosFolder = new File(scenarioPath);
        return outputPath + "wipeout_t_" + nameKB + "_" + scenariosFolder.getName() + ".txt";
    }

    public String getSolverRuntimeResultsFilepath(boolean hasNonRedundantConstraints) {
        if (hasNonRedundantConstraints) {
            return outputPath + "solverruntime_" + nameKB + "_nonred.txt";
        }
        return outputPath + "solverruntime_" + nameKB + ".txt";
    }
}
