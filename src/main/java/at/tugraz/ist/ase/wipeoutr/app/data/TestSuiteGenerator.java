/*
 * WipeOutR: Automated Redundancy Detection for Feature Models
 *
 * Copyright (c) 2021-2022 AIG team, Institute for Software Technology,
 * Graz University of Technology, Austria
 *
 * Contact: http://ase.ist.tugraz.at/ASE/
 */

package at.tugraz.ist.ase.wipeoutr.app.data;

import at.tugraz.ist.ase.common.RandomUtils;
import at.tugraz.ist.ase.fm.core.Feature;
import at.tugraz.ist.ase.fm.core.FeatureModel;
import at.tugraz.ist.ase.fm.core.FeatureModelException;
import at.tugraz.ist.ase.fm.parser.FMFormat;
import at.tugraz.ist.ase.fm.parser.FeatureModelParser;
import at.tugraz.ist.ase.fm.parser.FeatureModelParserException;
import at.tugraz.ist.ase.fm.parser.factory.FMParserFactory;
import at.tugraz.ist.ase.wipeoutr.app.cli.CmdLineOptions;
import at.tugraz.ist.ase.wipeoutr.app.cli.ConfigManager;
import at.tugraz.ist.ase.wipeoutr.common.ArrayUtils;
import com.google.common.collect.Sets;
import com.google.common.io.Files;
import org.apache.commons.collections4.IteratorUtils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.RecursiveTask;

import static at.tugraz.ist.ase.common.IOUtils.checkAndCreateFolder;
import static at.tugraz.ist.ase.wipeoutr.app.cli.ConfigManager.defaultConfigFile_TestCasesGenerator;
import static java.util.concurrent.ForkJoinTask.invokeAll;

/**
 * This class generates a testsuite for a feature model.
 * Generated test cases includes dead feature test cases, full mandatory test cases,
 * false optional test cases, false mandatory test cases, and partial configuration test cases.
 *
 * Configurations:
 * nameKB - name/filename of the knowledge base
 * dataPath - path to the data folder, where you store the knowledge base
 * outputPath - path to the folder, where the program will save the generated test suite
 *
 * Three configurations used in the generation of partial configuration test cases:
 * maxCombinations - maximum number of combinations to generate for each cardinality of features
 * randomlySearch - randomly select combinations from a list of combinations
 * maxFeaturesInTestCase - maximum number of features in a test case
 *                         e.g. if maxFeaturesInTestCase = 4, then the program will generate test cases with 2, 3, 4 features
 *
 * @author Viet-Man Le (vietman.le@ist.tugraz.at)
 */
public class TestSuiteGenerator {
    private static FeatureModel featureModel = null;
    private final String filename;

    private static final ConcurrentLinkedQueue<String> testsuite = new ConcurrentLinkedQueue<>();
    private int numDeadFeatures;
    private int numFalseOptional;
    private int numFullMandatory;
    private int numFalseMandatory;
    private int numPartialConfiguration;

    public static void main(String[] args) throws IOException, FeatureModelException, FeatureModelParserException {
        String programTitle = "Test Suite Generator";
        String usage = "Usage: java -jar ts_gen.jar [options]";

        // Parse command line arguments
        CmdLineOptions cmdLineOptions = new CmdLineOptions(null, programTitle, null, usage);
        cmdLineOptions.parseArgument(args);

        if (cmdLineOptions.isHelp()) {
            cmdLineOptions.printUsage();
            System.exit(0);
        }

        cmdLineOptions.printWelcome();

        // Read configurations
        String confFile = cmdLineOptions.getConfFile() == null ? defaultConfigFile_TestCasesGenerator : cmdLineOptions.getConfFile();
        ConfigManager cfg = ConfigManager.getInstance(confFile);

        printConf(cfg);

        String pathtoSave = cfg.getTestSuiteOutputPath();
        checkAndCreateFolder(pathtoSave);

        // Read feature model
        File fmFile = new File(cfg.getKBFilepath());
        String filename = fmFile.getName();

        FMFormat fmFormat = FMFormat.getFMFormat(Files.getFileExtension(filename));
        FeatureModelParser parser = FMParserFactory.getInstance().getParser(fmFormat);
        FeatureModel featureModel = parser.parse(fmFile);

        // Generate test suite
        TestSuiteGenerator generator = new TestSuiteGenerator(featureModel, filename);

        System.out.println("Generating test suite for " + filename.toUpperCase() + "...");
        generator.generate(pathtoSave, cfg.getMaxCombinations(), cfg.isRandomlySearch(), cfg.getMaxFeaturesInTestCase());

        System.out.println("Done - " + fmFile.getName());
    }

    public TestSuiteGenerator(FeatureModel featureModel, String filename) {
        TestSuiteGenerator.featureModel = featureModel;
        this.filename = filename;
    }

    public void generate(String pathtoSave, int maxCombinations, boolean randomlySearch, int maxFeaturesInTestCase) throws IOException, FeatureModelException {
        int oldSize;
        // generate test cases for dead feature property
        System.out.println("\tGenerating test cases for dead feature property");
        generateDeadFeatureTestCases();
        numDeadFeatures = testsuite.size();

        // generate test cases for false optional property
        System.out.println("\tGenerating test cases for false optional property");
        oldSize = testsuite.size();
        generateFalseOptionalTestCases();
        numFalseOptional = testsuite.size() - oldSize;

        // generate test cases for full mandatory property
        System.out.println("\tGenerating test cases for full mandatory property");
        oldSize = testsuite.size();
        generateFullMandatoryTestCases();
        numFullMandatory = testsuite.size() - oldSize;

        // generate test cases for false mandatory property
        System.out.println("\tGenerating test cases for false mandatory property");
        oldSize = testsuite.size();
        generateFalseMandatoryTestCases();
        numFalseMandatory = testsuite.size() - oldSize;

        // generate test cases for partial configuration
        System.out.println("\tGenerating test cases for partial configuration");
        oldSize = testsuite.size();
        generatePartialConfigurationTestCases(maxCombinations, randomlySearch, maxFeaturesInTestCase);
        numPartialConfiguration = testsuite.size() - oldSize;

        int sum = numDeadFeatures + numFalseOptional + numFullMandatory + numFalseMandatory + numPartialConfiguration;
        System.out.println("\tNumber of generated test cases: " + sum);
        System.out.println("\tNumber of generated test cases for dead features: " + numDeadFeatures);
        System.out.println("\tNumber of generated test cases for false optional: " + numFalseOptional);
        System.out.println("\tNumber of generated test cases for full mandatory: " + numFullMandatory);
        System.out.println("\tNumber of generated test cases for false mandatory: " + numFalseMandatory);
        System.out.println("\tNumber of generated test cases for partial configuration: " + numPartialConfiguration);

        // write to file
        System.out.println("\tWriting to file...");
        writeToFile(pathtoSave);
    }

    private void generateDeadFeatureTestCases() {
        // create dead feature test cases
        Feature root = featureModel.getFeature(0);
        featureModel.getBfFeatures().parallelStream()
                .filter(f -> !f.equals(root) && !testsuite.contains(f.getName()))
                .forEachOrdered(feature -> testsuite.add(feature.getName()));
        /*for (int i = 1; i < featureModel.getNumOfFeatures(); i++) {
            Feature feature = featureModel.getFeature(i);
            // if feature is not the root feature
            // and doesn't exist in testsuite
            if (!testsuite.contains(feature.getName())) {
                testsuite.add(feature.getName()); // f_i = true
            }
        }*/
    }

    // Conditionally Dead is not an error
//    private static void generateConditionallyDeadTestCases(FeatureModel featureModel) throws IOException {
//        // create file name
//        String fileName = createFileName("conditionallydead", featureModel.getName());
//        // open file
//        BufferedWriter writer = new BufferedWriter(new FileWriter(fileName));
//
//        // create conditionally dead test cases
////        for (int i = 1; i < featureModel.getNumOfFeatures(); i++) {
////            Feature fi = featureModel.getFeature(i);
////
////            // a feature is not DEAD and have to be optional
////            if (featureModel.isOptionalFeature(fi)) {
////
////                for (int j = 1; j < featureModel.getNumOfFeatures(); j++) {
////                    if (j != i) {
////                        Feature fj = featureModel.getFeature(j);
////
////                        // add {fi = true}
////                        model.addClauseTrue(vi);
////                        // add {fj = true}
////                        model.addClauseTrue(vj);
////
//////                        System.out.println("Before the checking----------------");
//////                        System.out.println(model.getNbCstrs());
//////                        printConstraints(model);
////
////                        model.getSolver().reset();
////                        if (!model.getSolver().solve()) {
//////                            System.out.println("------------> inConsistent: " + fi.toString());
////
////                            if (!isExistInArrayList(conditionallydeads, fi.toString())) { // neu chua moi them vao
////                                conditionallydeads.add(fi.toString());
////                                fi.setAnomalyType(CONDITIONALLYDEAD);
////
////                                List<Set<Constraint>> allDiag = calculateAllDiagnoses();
////
//////                                System.out.println("All diagnoses:---------------------------");
//////                                printAllDiagnoses(allDiag);
////
////                                explanations.put(fi.getName(), new ArrayList<>());
////                                createExplanations(allDiag, fi.getName(), explanations);
////                            }
////                        }
////                    }
////                }
////            }
////        }
//
//        for (Feature f: featureModel.getFeatures()) {
//            writer.write(f.getName() + "\n");
//        }
//
//        // close file
//        writer.close();
//    }

    private void generateFullMandatoryTestCases() {
        // create full mandatory test cases
        Feature root = featureModel.getFeature(0);
        featureModel.getBfFeatures().parallelStream()
                .filter(f -> !f.equals(root) && featureModel.isOptionalFeature(f)) // check whether the feature is optional
                .map(feature -> "~" + feature.getName()) // add {f_opt = false}
                .filter(testcase -> !testsuite.contains(testcase))
                .forEachOrdered(testsuite::add);
        /*for (int i = 1; i < featureModel.getNumOfFeatures(); i++) {
            Feature feature = featureModel.getFeature(i);

            // check whether the feature is optional
            if (featureModel.isOptionalFeature(feature)) {
                // add {f_opt = false}
                String testcase = "~" + feature.getName();
                if (!testsuite.contains(testcase)) {
                    testsuite.add(testcase);
                }
            }
        }*/
    }

    private void generateFalseOptionalTestCases() throws FeatureModelException {
        // create false optional test cases
        // f_p = true /\ f_opt = false
        Feature root = featureModel.getFeature(0);
        List<Feature> optionalFeatures = featureModel.getBfFeatures().parallelStream()
                .filter(f -> !f.equals(root) && featureModel.isOptionalFeature(f)).toList();

        for (Feature f: optionalFeatures) {
            String tc = "~" + f.getName(); // add {f_opt = false}

            featureModel.getMandatoryParents(f).parallelStream() // and in the right part of a requires constraint
                    .filter(parent -> featureModel.isMandatoryFeature(parent))
                    .map(parent -> tc + " & " + parent.getName())
                    .filter(testcase -> !testsuite.contains(testcase)) // add {f_p = true}
                    .forEachOrdered(testsuite::add);
        }
        /*for (int i = 1; i < featureModel.getNumOfFeatures(); i++) {
            Feature feature = featureModel.getFeature(i);

            // check whether the feature is optional
            if (featureModel.isOptionalFeature(feature)) {

                List<Feature> parents = featureModel.getMandatoryParents(feature);

                if (parents.size() > 0) { // and in the right part of a requires constraint

                    // add {f_opt = false}
                    String tc = "~" + feature.getName();

                    for (Feature parent : parents) {

                        if (featureModel.isMandatoryFeature(parent)) { // trong mot so truong hop no co the bat cau
                            // add {f_p = true}
                            String testcase = tc + " & " + parent.getName();

                            if (!testsuite.contains(testcase)) {
                                testsuite.add(testcase);
                            }
                        }
                    }
                }
            }
        }*/
    }

    private void generateFalseMandatoryTestCases() {
        // create false mandatory test cases
        Feature root = featureModel.getFeature(0);
        featureModel.getBfFeatures().parallelStream()
                .filter(f -> !f.equals(root) && featureModel.isMandatoryFeature(f)) // check whether the feature is mandatory
                .map(feature -> "~" + feature.getName()) // add {f_opt = false}
                .filter(testcase -> !testsuite.contains(testcase))
                .forEachOrdered(testsuite::add);
        /*for (int i = 1; i < featureModel.getNumOfFeatures(); i++) {
            Feature feature = featureModel.getFeature(i);

            // check whether the feature is mandatory
            if (featureModel.isMandatoryFeature(feature)) {
                String testcase = "~" + feature.getName();

                if (!testsuite.contains(testcase)) {
                    testsuite.add(testcase);
                }
            }
        }*/
    }

    private void generatePartialConfigurationTestCases(int maxCombinations, boolean randomlySearch, int maxFeaturesInTestCase) {
        int numFeatures = featureModel.getNumOfFeatures() - 1; // ignore f_0

        Set<Integer> targetSet = Sets.newHashSet(ArrayUtils.createIndexesArray(numFeatures));

        int maxSelectedFeatures = Math.min(numFeatures, maxFeaturesInTestCase);

        for (int numSelectedFeatures = 2; numSelectedFeatures <= maxSelectedFeatures; numSelectedFeatures++) {
            System.gc();
            System.out.println("\tGenerating test cases for partial configuration with cardinality: " + numSelectedFeatures);

            Set<Set<Integer>> combinations = Sets.combinations(targetSet, numSelectedFeatures);

            System.out.println("\t\tNumber of Combinations: " + combinations.size());

            List<PCTestCasesGenerationWorker> workers = new LinkedList<>();

            if (combinations.size() > maxCombinations) {

                if (randomlySearch) {
                    Set<Integer> indexes = getRandomlyIndexes(maxCombinations, combinations.size());

                    for (int i = 0; i < indexes.size(); i++) {
                        Integer index = IteratorUtils.get(indexes.iterator(), i);

                        Set<Integer> selected = IteratorUtils.get(combinations.iterator(), index);

//                        numTC = generatePartialConfigurationTestCases(numTC, selected);
                        PCTestCasesGenerationWorker worker = new PCTestCasesGenerationWorker(selected);
                        workers.add(worker);
                    }
                } else {
                    for (int i = 0; i < maxCombinations; i++) {
                        Set<Integer> selected = IteratorUtils.get(combinations.iterator(), i);

//                        numTC = generatePartialConfigurationTestCases(numTC, selected);
                        PCTestCasesGenerationWorker worker = new PCTestCasesGenerationWorker(selected);
                        workers.add(worker);
                    }
                }
            } else {
                for (Set<Integer> indexes: combinations) {
//                    numTC = generatePartialConfigurationTestCases(numTC, indexes);
                    PCTestCasesGenerationWorker worker = new PCTestCasesGenerationWorker(indexes);
                    workers.add(worker);
                }
            }

            invokeAll(workers);

            int numTC = 0;
            for (PCTestCasesGenerationWorker task: workers) {
                List<String> testcases = task.join();

                int oldTestCases = testsuite.size();
                testcases.parallelStream().filter(tc -> !testsuite.contains(tc)).forEachOrdered(testsuite::add);
                numTC += testsuite.size() - oldTestCases;
            }

            System.out.println("\t\tNumber of added test cases: " + numTC);
        }
    }

    private static class PCTestCasesGenerationWorker extends RecursiveTask<List<String>> {

        Set<Integer> selected;

        public PCTestCasesGenerationWorker(Set<Integer> selected) {
            this.selected = selected;
        }

        @Override
        protected List<String> compute() {
            List<String> tcs = new LinkedList<>();
            tcs.add("");
            for (Integer id : selected) {
                Feature f = featureModel.getFeature(id);

                tcs = generateTestCases(tcs, f.getName());
            }

            return tcs;
        }
    }

    private int generatePartialConfigurationTestCases(int numTC, Set<Integer> selected) {
        List<String> tcs = new LinkedList<>();
        tcs.add("");
        for (Integer id : selected) {
            Feature f = featureModel.getFeature(id);

            tcs = generateTestCases(tcs, f.getName());
        }

        for (String tc : tcs) {
            if (!testsuite.contains(tc)) {
                numTC = numTC + 1;
                testsuite.add(tc);
            }
        }
        return numTC;
    }

    private Set<Integer> getRandomlyIndexes(int num, int size) {
        Set<Integer> indexes = new LinkedHashSet<>();

        int id = RandomUtils.getRandomInt(size);
        int maxStep = Math.max((size / num) - 1, 1);

        int count = 0;
        while (count < num) {
            if (!indexes.contains(id)) {
                indexes.add(id);
                count++;
            }

            int step = maxStep == 1 ? 1 : (RandomUtils.getRandomInt(maxStep) + 1);

            if ((size - id) > step) {
                id = id + step;
            } else {
                id = step - (size - id);
            }
        }

        return indexes;
    }

    private static List<String> generateTestCases(List<String> tcs, String fName) {
        List<String> newTCS = new LinkedList<>();

        for (String tc: tcs) {
            if (tc.isEmpty()) {
                newTCS.add(fName); // true checking
                newTCS.add("~" + fName); // false checking
            } else {
                newTCS.add(tc + " & " + fName); // true checking
                newTCS.add(tc + " & " + "~" + fName); // false checking
            }
        }

        return newTCS;
    }

    private String createFileName(String pathtoSave) {
        return pathtoSave + "/" + filename.substring(0, filename.length() - 4) + ".testsuite";
    }

    private void writeToFile(String pathtoSave) throws IOException {
        String fileName = createFileName(pathtoSave);
        BufferedWriter writer = new BufferedWriter(new FileWriter(fileName));

        int sum = numDeadFeatures + numFalseOptional + numFullMandatory + numFalseMandatory + numPartialConfiguration;

        writer.write(sum + "\n");
        writer.write(numDeadFeatures + "\n");
        writer.write(numFalseOptional + "\n");
        writer.write(numFullMandatory + "\n");
        writer.write(numFalseMandatory + "\n");
        writer.write(numPartialConfiguration + "\n");

        for (String st: testsuite) {
            writer.write(st + "\n");
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
        System.out.println("\tmaxCombinations: " + config.getMaxCombinations());
        System.out.println("\trandomlySearch: " + config.isRandomlySearch());
        System.out.println("\tmaxFeaturesInTestCase: " + config.getMaxFeaturesInTestCase());
    }
}
