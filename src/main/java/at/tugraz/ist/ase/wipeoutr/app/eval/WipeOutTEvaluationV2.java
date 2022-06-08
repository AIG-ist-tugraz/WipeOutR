/*
 * WipeOut: Automated Redundancy Detection in Feature Models
 *
 * Copyright (c) 2021-2022
 *
 * @author: Viet-Man Le (vietman.le@ist.tugraz.at)
 */

package at.tugraz.ist.ase.wipeoutr.app.eval;

import at.tugraz.ist.ase.cacdr.checker.ChocoConsistencyChecker;
import at.tugraz.ist.ase.cdrmodel.IDebuggingModel;
import at.tugraz.ist.ase.fm.core.FeatureModel;
import at.tugraz.ist.ase.fm.parser.FeatureIDEParser;
import at.tugraz.ist.ase.fm.parser.FeatureModelParserException;
import at.tugraz.ist.ase.test.ITestCase;
import at.tugraz.ist.ase.test.TestSuite;
import at.tugraz.ist.ase.test.builder.ITestCaseBuildable;
import at.tugraz.ist.ase.test.builder.TestSuiteBuilder;
import at.tugraz.ist.ase.test.builder.fm.FMTestCaseBuilder;
import at.tugraz.ist.ase.wipeoutr.algorithm.WipeOutR_T;
import at.tugraz.ist.ase.wipeoutr.app.cli.CmdLineOptions;
import at.tugraz.ist.ase.wipeoutr.app.cli.ConfigManager;
import at.tugraz.ist.ase.wipeoutr.model.FMWipeOutTModel;
import lombok.Cleanup;

import java.io.*;
import java.util.List;
import java.util.Objects;

import static at.tugraz.ist.ase.cacdr.eval.CAEvaluator.COUNTER_CONSISTENCY_CHECKS;
import static at.tugraz.ist.ase.eval.PerformanceEvaluator.*;
import static at.tugraz.ist.ase.wipeoutr.algorithm.WipeOutR_T.TIMER_WIPEOUTR_T;
import static at.tugraz.ist.ase.wipeoutr.app.cli.ConfigManager.defaultConfigFile_WipeOutTEvaluationV2;

/**
 * An evaluation for WipeOutR_T algorithm
 * Using Linux feature model (linux-2.6.33.3.xml).
 * Results will be submitted to SPLC'22.
 *
 * Configurations:
 * nameKB - name/filename of the knowledge base
 * dataPath - path to the data folder, where you store the knowledge base file
 * scenarioPath - path to the folder, where you store test suites you want to measure the runtime
 * outputPath - path to the folder, where you store the results
 * numIter - number of iterations
 *
 * This program measures the runtime of WipeOutR_T' execution.
 * You can put as many test suites as you want in the scenarioPath folder.
 * The result is an average runtime.
 */
public class WipeOutTEvaluationV2 {

    public static void main(String[] args) throws IOException, FeatureModelParserException {

        String programTitle = "WipeOutR_T Evaluation - Feature Model";
        String usage = "Usage: java -jar wipeoutr_t.jar [options]]";

        CmdLineOptions cmdLineOptions = new CmdLineOptions(null, programTitle, null, usage);
        cmdLineOptions.parseArgument(args);

        if (cmdLineOptions.isHelp()) {
            cmdLineOptions.printUsage();
            System.exit(0);
        }

        cmdLineOptions.printWelcome();

        // Read configurations
        String confFile = cmdLineOptions.getConfFile() == null ? defaultConfigFile_WipeOutTEvaluationV2 : cmdLineOptions.getConfFile();
        ConfigManager cfg = ConfigManager.getInstance(confFile);

        BufferedWriter writer = new BufferedWriter(new FileWriter(cfg.getWipeOutTResultsFilepath()));

        printConf(cfg, writer);

        FeatureIDEParser parser = new FeatureIDEParser();
        File folder = new File(cfg.getScenarioPath());
        String fmPath = cfg.getKBFilepath();

        System.out.println("Evaluating WipeOutR_T runtime...");
        writer.write("Evaluating WipeOutR_T runtime...\n");
        int iterCounter = 0;
        double totalCC = 0.0;
        double totalRuntime = 0.0;
        for (int i = 0; i < cfg.getNumIter(); i++) {
            System.out.println("\nITERATION " + (i + 1));

            // Takes each file in the scenarioFolder
            for (final File file : Objects.requireNonNull(folder.listFiles())) {
                if (file.getName().endsWith(".testcases")) {
                    System.gc();
                    iterCounter++;

                    String filename = file.getName();
                    System.out.println();
                    System.out.println("\t========================");
                    System.out.println("\t" + filename.toUpperCase());
                    System.out.println();
                    writer.write("\t========================\n");
                    writer.write("\t" + filename.toUpperCase() + "\n");

                    // loads feature model
                    FeatureModel featureModel = parser.parse(new File(fmPath));
                    // TestSuite
                    File fileTS = new File(file.getPath());
                    TestSuiteBuilder factory = new TestSuiteBuilder();
                    ITestCaseBuildable testCaseFactory = new FMTestCaseBuilder();
                    @Cleanup InputStream is = new FileInputStream(fileTS);
                    TestSuite testSuite = factory.buildTestSuite(is, testCaseFactory);
                    int total = testSuite.size();
                    // WipeOutTModel
                    FMWipeOutTModel testCaseModel = new FMWipeOutTModel(featureModel, testSuite);
                    testCaseModel.initialize();

                    // Checker
                    ChocoConsistencyChecker checker = new ChocoConsistencyChecker(testCaseModel);
                    List<ITestCase> testCases = ((IDebuggingModel) testCaseModel).getTestcases().stream().toList();

                    WipeOutR_T wipeOut = new WipeOutR_T(checker);

                    reset();
                    setCommonTimer(TIMER_WIPEOUTR_T);
                    List<ITestCase> newT = wipeOut.run(testCases);

                    double cc = getCounter(COUNTER_CONSISTENCY_CHECKS).getValue();
                    double runtime = (double) totalCommon(TIMER_WIPEOUTR_T);

                    printResults(writer, total, newT.size(), cc, runtime);

                    saveNonRedundantTestcases(writer, newT);

                    totalCC += cc;
                    totalRuntime += runtime;

                    System.out.println("\tDone - " + file.getName());
                    writer.write("\tDone - " + file.getName() + "\n");

                    writer.flush();
                }
            }
        }

        System.out.println("\nTotal of consistency checks: " + totalCC);
        System.out.println("Total time: " + (totalRuntime / 1000000000.0));
        System.out.println("Num of iterations: " + iterCounter);

        totalCC /= iterCounter;
        totalRuntime /= iterCounter;

        System.out.println("Average number of Consistency checks: " + totalCC);
        System.out.println("Average time for running: " + (totalRuntime / 1000000000.0) + " seconds");
        writer.write("Average number of Consistency checks: " + totalCC + "\n");
        writer.write("Average time for running: " + (totalRuntime / 1000000000.0) + " seconds\n");

        System.out.println("DONE");
        writer.write("DONE");

        writer.close();
    }

    private static void printResults(BufferedWriter writer, int originalNumTestCases, int newNumTestCases, double cc, double runtime) throws IOException {
        System.out.println("\t#test cases: " + originalNumTestCases);
        writer.write("\t#test cases: " + originalNumTestCases + "\n");

        System.out.println("\t#new test cases: " + newNumTestCases);
        writer.write("\t#new test cases: " + newNumTestCases + "\n");

        System.out.println("\t%redundant test cases: " + ((double) (originalNumTestCases - newNumTestCases) / originalNumTestCases) * 100);
        writer.write("\t%redundant test cases: " + (((double) (originalNumTestCases - newNumTestCases) / originalNumTestCases) * 100) + "\n");

        System.out.println("\tThe number of Consistency checks: " + cc);
        writer.write("\tThe number of Consistency checks: " + cc + "\n");

        System.out.println("\tTime for running: " + (runtime / 1000000000.0));
        writer.write("\tTime for running: " + (runtime / 1000000000.0) + "\n");
    }

    private static void saveNonRedundantTestcases(BufferedWriter writer, List<ITestCase> newT) throws IOException {
        writer.write("\t\tNon-redundant test cases: " + newT.size() + "\n");
        for (ITestCase kbTestCase : newT) {
            writer.write("\t\t" + kbTestCase.toString() + "\n");
        }
    }

    private static void printConf(ConfigManager config, BufferedWriter writer) throws IOException {
        writer.write("WipeOutR_T Evaluation - Feature Model\n");
        System.out.println("Configurations:");
        writer.write("Configurations:\n");
        System.out.println("\tnameKB: " + config.getNameKB());
        writer.write("\tnameKB: " + config.getNameKB() + "\n");
        System.out.println("\tdataPath: " + config.getDataPath());
        writer.write("\tdataPath: " + config.getDataPath() + "\n");
        System.out.println("\tscenarioPath: " + config.getScenarioPath());
        writer.write("\tscenarioPath: " + config.getScenarioPath() + "\n");
        System.out.println("\toutputPath: " + config.getOutputPath());
        writer.write("\toutputPath: " + config.getOutputPath() + "\n");
        System.out.println("\tnumIter: " + config.getNumIter());
        writer.write("\tnumIter: " + config.getNumIter() + "\n");
    }
}
