/*
 * WipeOut: Automated Redundancy Detection in Feature Models
 *
 * Copyright (c) 2021-2022
 *
 * @author: Viet-Man Le (vietman.le@ist.tugraz.at)
 */

package at.tugraz.ist.ase.wipeoutr.app.data;

import at.tugraz.ist.ase.common.LoggerUtils;
import at.tugraz.ist.ase.common.RandomUtils;
import at.tugraz.ist.ase.test.Assignment;
import at.tugraz.ist.ase.test.ITestCase;
import at.tugraz.ist.ase.test.TestCase;
import at.tugraz.ist.ase.test.TestSuite;
import at.tugraz.ist.ase.test.builder.ITestCaseBuildable;
import at.tugraz.ist.ase.test.builder.ITestSuiteBuildable;
import at.tugraz.ist.ase.test.builder.fm.FMTestCaseBuilder;
import at.tugraz.ist.ase.wipeoutr.app.cli.CmdLineOptions;
import at.tugraz.ist.ase.wipeoutr.app.cli.ConfigManager;
import at.tugraz.ist.ase.wipeoutr.common.ArrayUtils;
import com.google.common.collect.Sets;
import lombok.Cleanup;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

import static at.tugraz.ist.ase.common.IOUtils.checkAndCreateFolder;
import static at.tugraz.ist.ase.wipeoutr.app.cli.ConfigManager.defaultConfigFile_TestCasesSelector;

/**
 * This class selects randomly test cases (scenarios) from a test suites.
 * Besides, it also automatically generates redundant test cases according to the redundancy ratio.
 *
 * Configurations:
 * nameKB - name/filename of the knowledge base
 * dataPath - path to the data folder, where you have a testsuite folder for test suites
 * outputPath - path to the folder, where you store the results
 * cardTC - list of numbers of test cases want to be selected
 * redRatio - list of redundancy ratios
 */
@Slf4j
public class TestCasesSelectorV2 {
    TestSuite testSuite;

    public static void main(String[] args) throws IOException {
        String programTitle = "Test Cases Selector V2";
        String usage = "Usage: java -jar ts_select.jar [options]]";

        CmdLineOptions cmdLineOptions = new CmdLineOptions(null, programTitle, null, usage);
        cmdLineOptions.parseArgument(args);

        if (cmdLineOptions.isHelp()) {
            cmdLineOptions.printUsage();
            System.exit(0);
        }

        cmdLineOptions.printWelcome();

        // Read configurations
        String confFile = cmdLineOptions.getConfFile() == null ? defaultConfigFile_TestCasesSelector : cmdLineOptions.getConfFile();
        ConfigManager cfg = ConfigManager.getInstance(confFile);

        printConf(cfg);

        checkAndCreateFolder(cfg.getScenariosPathInResults());
        TestCasesSelectorV2 selector = new TestCasesSelectorV2(cfg);
        selector.select();

        System.out.println("DONE");
    }

    ConfigManager cfg;

    public TestCasesSelectorV2(ConfigManager cfg) {
        this.cfg = cfg;
    }

    public void select() throws IOException {
        File fileTS = new File(cfg.getTestSuiteFilepath()); // testsuite
        String pathTestCases = cfg.getScenariosPathInResults(); // path to where stores test cases

        System.out.println("Testsuite: " + fileTS.getName());

        select(fileTS, pathTestCases);
    }

    private void select(File fileTS, String pathTestCases) throws IOException {
        // Read test suite
        System.out.println("\tLoading test suite...");
        readTestSuite(fileTS);

        System.out.println("\tSelecting...");

        List<ITestCase> testCasesWith5f = new ArrayList<>(testSuite.getTestCases().stream()
                .filter(tc -> tc.getAssignments().size() == 5).toList());
        Collections.shuffle(testCasesWith5f, new Random(RandomUtils.getSEED())); // random with a seed = 141982L

        List<ITestCase> otherTestCases = new ArrayList<>(testSuite.getTestCases().stream()
                .filter(tc -> tc.getAssignments().size() < 5).toList());
        Collections.shuffle(otherTestCases, new Random(RandomUtils.getSEED())); // random with a seed = 141982L

        // select test cases
        List<ITestCase> alltestcases = new LinkedList<>();
        for (int total : cfg.getCardTC()) {

            List<ITestCase> selectedTestCasesFrom5f = new LinkedList<>();
            int nonRedundantToGenRed = total / 30 + 1;
            while (selectedTestCasesFrom5f.size() < nonRedundantToGenRed) {
                selectedTestCasesFrom5f.add(testCasesWith5f.remove(0));
            }

            for (int i = cfg.getRedRatios().size() - 1; i >= 0; i--) {
                double redRatio = cfg.getRedRatios().get(i);

                int numRedundant = (int) Math.ceil(total * redRatio);
                int nonRedundant = total - numRedundant - nonRedundantToGenRed;

                System.out.println("\t\tCardinality: " + total);
                System.out.println("\t\tRedundancy ratio: " + redRatio);
                System.out.println("\t\tNumber of non-redundant test cases to generate: " + nonRedundantToGenRed);
                System.out.println("\t\tNumber of non-redundant test cases: " + nonRedundant);
                System.out.println("\t\tNumber of redundant test cases: " + numRedundant);

                alltestcases.addAll(selectedTestCasesFrom5f);

                System.out.println("Test cases selected from 5f:" + selectedTestCasesFrom5f.size());
                selectedTestCasesFrom5f.forEach(tc -> System.out.println("\t\t\t" + tc));

                List<ITestCase> testcases = generateRedundantTestCases(selectedTestCasesFrom5f, numRedundant);
                alltestcases.addAll(testcases);

                System.out.println("Test cases generated:" + testcases.size());
                testcases.forEach(tc -> System.out.println("\t\t\t" + tc));

                System.out.println("Test cases selected from other:");
                int j = 0;
                while (j < nonRedundant) {
                    // check and ignore redundant test cases
                    ITestCase tc = otherTestCases.remove(0);

                    if (alltestcases.stream().anyMatch(t -> new HashSet<>(t.getAssignments()).containsAll(tc.getAssignments()))) {
                        System.out.println("Trung: " + tc);
                        otherTestCases.add(tc);
                        continue;
                    }

                    alltestcases.add(tc);
                    System.out.println("\t\t\t" + tc);
                    j++;
                }

                System.out.println("Check:");
                alltestcases.forEach(tc -> System.out.println("\t\t\t" + tc));

                System.out.println("\t\t\t\t#Selected test cases: " + alltestcases.size());

                // save a test suite
                writeToFile(fileTS, pathTestCases, alltestcases, total, (int)(redRatio * 100), false);

                Collections.shuffle(alltestcases, new Random(RandomUtils.getSEED())); // random with a seed = 141982L
                writeToFile(fileTS, pathTestCases, alltestcases, total, (int)(redRatio * 100), true);

                alltestcases.clear();
            }
        }
    }

    private List<ITestCase> generateRedundantTestCases(List<ITestCase> testCases, int numRedundant) {
        if (numRedundant == 0) {
            return new LinkedList<>();
        }

        int numGenEachTestCase = (numRedundant / testCases.size()) + 1;

        List<ITestCase> redundantTestCases = new LinkedList<>();
        for (int i = 0; i < testCases.size() - 1; i++) {
            TestCase testCase = (TestCase) testCases.get(i);

            List<ITestCase> redTCs = generateRedundantTestCases(testCase, numGenEachTestCase);

            redundantTestCases.addAll(redTCs);
            numRedundant -= redTCs.size();
        }

        // last test case
        TestCase testCase = (TestCase) testCases.get(testCases.size() - 1);
        List<ITestCase> redTCs = generateRedundantTestCases(testCase, numRedundant);
        redundantTestCases.addAll(redTCs);

        return redundantTestCases;
    }

    private List<ITestCase> generateRedundantTestCases(TestCase testCase, int numRedundant) {
        List<ITestCase> redundantTestCases = new LinkedList<>();

        List<Set<Integer>> redundantIndexes = getRedundantTC_Indexes();
        for (int i = 0; i < numRedundant; i++) {
            Set<Integer> indexes = redundantIndexes.get(i);

            List<Assignment> assignments = new LinkedList<>();
            indexes.parallelStream().map(index -> {
                        try {
                            return (Assignment) testCase.getAssignments().get(index - 1).clone();
                        } catch (CloneNotSupportedException e) {
                            throw new RuntimeException(e);
                        }
                    })
                    .forEachOrdered(assignments::add);

            String testcase = getTestCaseString(assignments);

            TestCase redundantTestCase = TestCase.builder()
                    .testcase(testcase)
                    .assignments(assignments)
                    .build();

            if (!redundantTestCases.contains(redundantTestCase)) {
                redundantTestCases.add(redundantTestCase);
            }
        }
        return redundantTestCases;
    }

    private String getTestCaseString(List<Assignment> assignments) {
        return assignments.parallelStream().map(a -> a.getValue().equals("true") ? a.getVariable() : "~" + a.getVariable()).collect(Collectors.joining(" & "));
    }

    private List<Set<Integer>> getRedundantTC_Indexes() {
        Set<Integer> targetSet = Sets.newHashSet(ArrayUtils.createIndexesArray(5));
        List<Set<Integer>> combs = new LinkedList<>();
        combs.addAll(Sets.combinations(targetSet, 4));
        combs.addAll(Sets.combinations(targetSet, 3));
        combs.addAll(Sets.combinations(targetSet, 2));
        combs.addAll(Sets.combinations(targetSet, 1));
        Collections.shuffle(combs, new Random(RandomUtils.getSEED())); // random with a seed = 141982L

        return combs;
    }

    private static class TestSuiteBuilder implements ITestSuiteBuildable {

        @Override
        public TestSuite buildTestSuite(@NonNull InputStream is, @NonNull ITestCaseBuildable testCaseBuilder) throws IOException {
            log.trace("{}Building test suite from input stream >>>", LoggerUtils.tab());
            LoggerUtils.indent();

            @Cleanup BufferedReader br = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));

            List<ITestCase>  testCases;

            br.readLine(); // omit header lines
            br.readLine();
            br.readLine();
            br.readLine();
            br.readLine();
            br.readLine();

            // Read all test cases
            testCases = br.lines().map(testCaseBuilder::buildTestCase).collect(Collectors.toCollection(LinkedList::new));
        /*while ((line = br.readLine()) != null) {
            ITestCase testCase = testCaseBuilder.buildTestCase(line);
            testCases.add(testCase);
        }*/

            TestSuite testSuite = TestSuite.builder()
                    .testCases(testCases)
                    .build();

            LoggerUtils.outdent();
            log.debug("{}<<< Built test suite [testsuite={}]", LoggerUtils.tab(), testSuite);
            return testSuite;
        }
    }

    private void readTestSuite(File fileTS) throws IOException {
        // TestSuite
        TestSuiteBuilder factory = new TestSuiteBuilder();
        ITestCaseBuildable testCaseFactory = new FMTestCaseBuilder();
        @Cleanup InputStream is = new FileInputStream(fileTS);
        testSuite = factory.buildTestSuite(is, testCaseFactory);
    }

    private String createFileName(String pathTestCases, String name, int cardinality, int redRatio, boolean shuffled) {
        return pathTestCases + "/" + name + "_" + cardinality + "_" + redRatio + (shuffled ? "_s" : "") + ".testcases";
    }

    private void writeToFile(File fileTS, String pathTestCases, List<ITestCase> testcases,
                             int cardinality, int redRatio, boolean shuffled) throws IOException {
        String fileNameWithoutExtension = fileTS.getName().substring(0, fileTS.getName().length() - 13);
        String fileName = createFileName(pathTestCases, fileNameWithoutExtension, cardinality, redRatio, shuffled);

        System.out.println("\t\t\tSave to file... " + fileName);
        BufferedWriter writer = new BufferedWriter(new FileWriter(fileName));

        writer.write(testcases.size() + "\n");

        for (ITestCase tc: testcases) {
            writer.write(tc + "\n");
            writer.flush();
        }

        // close file
        writer.close();
    }

    private static void printConf(ConfigManager config) {
        System.out.println("Configurations:");
        System.out.println("\tnameKB: " + config.getNameKB());
        System.out.println("\tdataPath: " + config.getDataPath());
        System.out.println("\toutputPath: " + config.getOutputPath());
        System.out.println("\tcardTC: " + config.getCardTC());
        System.out.println("\tredRatio: " + config.getRedRatios());
    }
}