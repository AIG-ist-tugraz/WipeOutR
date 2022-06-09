/*
 * WipeOutR: Automated Redundancy Detection for Feature Models
 *
 * Copyright (c) 2021-2022 AIG team, Institute for Software Technology,
 * Graz University of Technology, Austria
 *
 * Contact: http://ase.ist.tugraz.at/ASE/
 */

package at.tugraz.ist.ase.wipeoutr.app.eval;

import at.tugraz.ist.ase.cacdr.checker.ChocoConsistencyChecker;
import at.tugraz.ist.ase.fm.core.FeatureModel;
import at.tugraz.ist.ase.fm.parser.FeatureIDEParser;
import at.tugraz.ist.ase.fm.parser.FeatureModelParserException;
import at.tugraz.ist.ase.test.ITestCase;
import at.tugraz.ist.ase.test.TestSuite;
import at.tugraz.ist.ase.test.builder.ITestCaseBuildable;
import at.tugraz.ist.ase.test.builder.TestSuiteBuilder;
import at.tugraz.ist.ase.test.builder.fm.FMTestCaseBuilder;
import at.tugraz.ist.ase.wipeoutr.app.cli.CmdLineOptions;
import at.tugraz.ist.ase.wipeoutr.app.cli.ConfigManager;
import at.tugraz.ist.ase.wipeoutr.model.WipeOutRTModel;
import lombok.Cleanup;

import java.io.*;
import java.util.Objects;

import static at.tugraz.ist.ase.wipeoutr.app.cli.ConfigManager.defaultConfigFile_RuntimeForTestcasesV2;
import static at.tugraz.ist.ase.wipeoutr.eval.WipeOutREvaluation.*;

/**
 * An evaluation for test case execution.
 * Using Linux feature model (linux-2.6.33.3.xml).
 * Results submitted to SPLC'22.
 *
 * Configurations:
 * nameKB - name/filename of the knowledge base
 * dataPath - path to the data folder, where you store the knowledge base file
 * scenarioPath - path to the folder, where you store test suites you want to measure the runtime
 * outputPath - path to the folder, where you store the results
 * numIter - number of iterations
 *
 * This program measures the runtime of test suites' execution.
 * You can put as many test suites as you want in the scenarioPath folder.
 * The result is an average runtime.
 *
 * @author Viet-Man Le (vietman.le@ist.tugraz.at)
 */
public class RuntimeForTestcasesV2 {

    public static void main(String[] args) throws IOException, FeatureModelParserException {

        String programTitle = "Test case runtime evaluation - WipeOutR_T";
        String usage = "Usage: java -jar ts_runtime.jar [options]]";

        // Parse command line arguments
        CmdLineOptions cmdLineOptions = new CmdLineOptions(null, programTitle, null, usage);
        cmdLineOptions.parseArgument(args);

        if (cmdLineOptions.isHelp()) {
            cmdLineOptions.printUsage();
            System.exit(0);
        }

        cmdLineOptions.printWelcome();

        // Read configurations
        String confFile = cmdLineOptions.getConfFile() == null ? defaultConfigFile_RuntimeForTestcasesV2 : cmdLineOptions.getConfFile();
        ConfigManager cfg = ConfigManager.getInstance(confFile);

        BufferedWriter writer = new BufferedWriter(new FileWriter(cfg.getFilepathToTestcaseRuntimeEvaluation()));

        printConf(cfg, writer);

        // loads feature model
        FeatureIDEParser parser = new FeatureIDEParser(); // uses FeatureIDEParser to parse the feature model
        FeatureModel featureModel = parser.parse(new File(cfg.getKBFilepath()));
        File scenariosFolder = new File(cfg.getScenarioPath());

        System.out.println("Evaluating...");
        writer.write("Evaluating...\n");
        int iterCounter = 0;
        double totalRuntime = 0;
        // iterations - calculate the average runtime
        for (int i = 0; i < cfg.getNumIter(); i++) {
            System.out.println("\nITERATION " + (i + 1));

            // Takes each file in the scenariosFolder
            for (final File file : Objects.requireNonNull(scenariosFolder.listFiles())) {
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

                    // TestSuite
                    File fileTS = new File(file.getPath());
                    TestSuiteBuilder factory = new TestSuiteBuilder();
                    ITestCaseBuildable testCaseFactory = new FMTestCaseBuilder();
                    @Cleanup InputStream is = new FileInputStream(fileTS);
                    TestSuite testSuite = factory.buildTestSuite(is, testCaseFactory);

                    // WipeOutTModel
                    WipeOutRTModel testCaseModel = new WipeOutRTModel(featureModel, testSuite);
                    testCaseModel.initialize();

                    // Checker
                    ChocoConsistencyChecker checker = new ChocoConsistencyChecker(testCaseModel);

                    reset();
                    setCommonTimer(TIMER_RUNNING);
                    int tcCounter = 0;
                    for (ITestCase tc : testSuite.getTestCases()) { // for each test case
                        // check
                        start(TIMER_RUNNING);
                        boolean consistent = checker.isConsistent(testCaseModel.getAllConstraints(), tc);
                        stop(TIMER_RUNNING);

                        if (consistent) {
                            System.out.println("\t\t" + ++tcCounter + ". Consistent - " + tc);
                        } else {
                            System.out.println("\t\t" + ++tcCounter + ". Inconsistent - " + tc);
                        }
                    }

                    double runtime = (double) totalCommon(TIMER_RUNNING);

                    System.out.println("\tTime for test running: " + (runtime / 1000000000.0));
                    writer.write("\tTime for test running: " + (runtime / 1000000000.0) + "\n");

                    totalRuntime += runtime;

                    System.out.println("\tDone - " + file.getName());

                    writer.flush();
                }
            }
        }

        System.out.println("\nTotal time: " + (totalRuntime / 1000000000.0));
        System.out.println("Num of iterations: " + iterCounter);

        totalRuntime /= iterCounter;

        System.out.println("Average time for test running: " + (totalRuntime / 1000000000.0));
        writer.write("Average time for test running: " + (totalRuntime / 1000000000.0) + "\n");

        System.out.println("DONE");
        writer.write("DONE");

        writer.close();
    }

    private static void printConf(ConfigManager config, BufferedWriter writer) throws IOException {
        writer.write("Test case runtime evaluation - WipeOutR_T\n");
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
        System.out.println("\tnumOfIter: " + config.getNumIter());
        writer.write("\tnumOfIter: " + config.getNumIter() + "\n");
    }
}
