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
import at.tugraz.ist.ase.kb.core.Constraint;
import at.tugraz.ist.ase.kb.fm.FMKB;
import at.tugraz.ist.ase.wipeoutr.algorithm.WipeOutR_FM;
import at.tugraz.ist.ase.wipeoutr.app.cli.CmdLineOptions;
import at.tugraz.ist.ase.wipeoutr.app.cli.ConfigManager;
import at.tugraz.ist.ase.wipeoutr.model.WipeOutRFMModel;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

import static at.tugraz.ist.ase.wipeoutr.algorithm.WipeOutR_FM.TIMER_WIPEOUTR_FM;
import static at.tugraz.ist.ase.wipeoutr.app.cli.ConfigManager.defaultConfigFile_WipeOutFMEvaluationV2;
import static at.tugraz.ist.ase.wipeoutr.eval.WipeOutREvaluation.*;

/**
 * An evaluation for WipeOutR_FM algorithm
 * Using Linux feature model (linux-2.6.33.3.xml).
 * Results will be submitted to SPLC'22.
 *
 * Configurations:
 * nameKB - name of the knowledge base
 * dataPath - path to the data folder, where you store the knowledge base
 * outputPath - path to the output folder, where the program will save the results
 * numIter - number of iterations
 *
 * @author Viet-Man Le (vietman.le@ist.tugraz.at)
 */
public class WipeOutRFMEvaluationV2 {

    public static void main(String[] args) throws IOException, FeatureModelParserException {
        String programTitle = "WipeOutR_FM Evaluation - Feature Model";
        String usage = "Usage: java -jar wipeoutr_fm.jar [options]]";

        // Parse command line arguments
        CmdLineOptions cmdLineOptions = new CmdLineOptions(null, programTitle, null, usage);
        cmdLineOptions.parseArgument(args);

        if (cmdLineOptions.isHelp()) {
            cmdLineOptions.printUsage();
            System.exit(0);
        }

        cmdLineOptions.printWelcome();

        // Read configurations
        String confFile = cmdLineOptions.getConfFile() == null ? defaultConfigFile_WipeOutFMEvaluationV2 : cmdLineOptions.getConfFile();
        ConfigManager cfg = ConfigManager.getInstance(confFile);

        BufferedWriter writer = new BufferedWriter(new FileWriter(cfg.getWipeOutFMResultsFilepath()));

        printConf(cfg, writer);

        // Read feature model
        File fmFile = new File(cfg.getKBFilepath());
        FeatureIDEParser parser = new FeatureIDEParser();
        FeatureModel featureModel = parser.parse(fmFile);
        FMKB fmKB = new FMKB(featureModel, true); // generate CSP constraints and its negative constraints
        int numConstraintsOriginalModel = fmKB.getNumConstraints();
        int numChocoConstraintsOriginalModel = fmKB.getNumChocoConstraints();

        System.out.println("Evaluating WipeOutR_FM runtime on " + cfg.getNameKB() + "...");
        System.out.println("\t#constraints of the original model: " + numConstraintsOriginalModel);
        writer.write("\t#constraints of the original model: " + numConstraintsOriginalModel + "\n");
        System.out.println("\t#Choco constraints of the original model: " + numChocoConstraintsOriginalModel);
        writer.write("\t#Choco constraints of the original model: " + numChocoConstraintsOriginalModel + "\n");

        // FMWipeOutFMModel
        WipeOutRFMModel model = new WipeOutRFMModel(featureModel); // generate CSP constraints and its negative constraints
        model.initialize();
        // Checker
        ChocoConsistencyChecker checker = new ChocoConsistencyChecker(model);

        WipeOutR_FM wipeOutRFM = new WipeOutR_FM(checker);

        List<Constraint> newCstrs = null;
        double totalCC = 0;
        double totalRuntime = 0;
        for (int i = 0; i < cfg.getNumIter(); i++) {
            System.gc();

            System.out.println("\nITERATION " + (i + 1));
            writer.write("\t========================\n");
            System.out.println("\t#Choco constraints before evaluation: " + model.getModel().getNbCstrs());
            writer.write("\t#Choco constraints before evaluation: " + model.getModel().getNbCstrs() + "\n");

            System.out.println("\tWipeOutR_FM running...");
            // WipeOutR_FM evaluation
            reset();
            setCommonTimer(TIMER_WIPEOUTR_FM);
            newCstrs = wipeOutRFM.run(model.getAllConstraints().stream().toList());

            System.out.println("\t#Choco constraints after evaluation: " + model.getModel().getNbCstrs());
            writer.write("\t#Choco constraints after evaluation: " + model.getModel().getNbCstrs() + "\n");

            double cc = getCounter(COUNTER_CONSISTENCY_CHECKS).getValue();
            double t_wipeout_fm = (double)totalCommon(TIMER_WIPEOUTR_FM);

            System.out.println("\t#new constraints: " + newCstrs.size());
            writer.write("\t#new constraints: " + newCstrs.size() + "\n");
            System.out.println("\tConsistency checks: " + cc);
            writer.write("\tConsistency checks: " + cc + "\n");
            System.out.println("\tTime for solver running: " + (t_wipeout_fm / 1000000000.0));
            writer.write("\tTime for solver running: " + (t_wipeout_fm / 1000000000.0) + "\n");

            totalCC += cc;
            totalRuntime += t_wipeout_fm;

            writer.flush();
        }

        System.out.println("\nTotal consistency checks: " + totalCC);
        System.out.println("Total runtime: " + (totalRuntime / 1000000000.0) + " seconds");
        System.out.println("Number of iterations: " + cfg.getNumIter());

        assert newCstrs != null;
        Collections.reverse(newCstrs);
        // save non-redundant constraints
        saveConstraints(newCstrs, cfg.getNonRedundantConstraintsFilepathInResults());

        // Print results
        printResults(writer, numConstraintsOriginalModel, newCstrs.size(), totalCC / cfg.getNumIter(), totalRuntime / cfg.getNumIter());

        writer.close();

        System.out.println("DONE");
    }

    private static void printResults(BufferedWriter writer, int numConstraintsOriginalModel, int numNewCstrs, double totalCC, double totalRuntime) throws IOException {
        System.out.println("WipeOutR_FM evaluation results:");
        System.out.println("\t#constraints: " + numConstraintsOriginalModel);
        System.out.println("\t#new constraints: " + numNewCstrs);
        System.out.println("\t%redundant constraints: " + ((double)(numConstraintsOriginalModel - numNewCstrs) / numConstraintsOriginalModel) * 100);
        System.out.println("\tThe number of Consistency checks: " + (totalCC));
        System.out.println("\tTime for WipeOutFM running: " + (totalRuntime / 1000000000.0) + " seconds");

        writer.write("WipeOutR_FM evaluation results:");
        writer.write("\t#constraints: " + numConstraintsOriginalModel + "\n");
        writer.write("\t#new constraints: " + numNewCstrs + "\n");
        writer.write("\t%redundant constraints: " + (((double)(numConstraintsOriginalModel - numNewCstrs) / numConstraintsOriginalModel) * 100) + "\n");
        writer.write("\tThe number of Consistency checks: " + (totalCC) + "\n");
        writer.write("\tTime for WipeOutFM running: " + (totalRuntime / 1000000000.0) + " seconds\n");
    }

    private static void saveConstraints(List<Constraint> newCstrs, String nonRedundantConstraintsFilepath) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(nonRedundantConstraintsFilepath));

        writer.write(newCstrs.size() + "\n");
        for (Constraint cstr : newCstrs) {
            writer.write(cstr + "\n");
            writer.write("\t" + cstr.getChocoConstraints().size() + "\n");

            for (org.chocosolver.solver.constraints.Constraint chocoCstr : cstr.getChocoConstraints()) {
                writer.write("\t" + chocoCstr + "\n");
            }

            writer.flush();
        }
    }

    private static void printConf(ConfigManager config, BufferedWriter writer) throws IOException {
        writer.write("WipeOutFM Evaluation - Feature Model\n");
        System.out.println("Configurations:");
        writer.write("Configurations:\n");
        System.out.println("\tnameKB: " + config.getNameKB());
        writer.write("\tnameKB: " + config.getNameKB() + "\n");
        System.out.println("\tdataPath: " + config.getDataPath());
        writer.write("\tdataPath: " + config.getDataPath() + "\n");
        System.out.println("\toutputPath: " + config.getOutputPath());
        writer.write("\toutputPath: " + config.getOutputPath() + "\n");
        System.out.println("\tnumIter: " + config.getNumIter());
        writer.write("\tnumIter: " + config.getNumIter() + "\n");
    }
}
