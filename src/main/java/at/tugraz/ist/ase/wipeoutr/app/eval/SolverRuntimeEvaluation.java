/*
 * WipeOutR: Automated Redundancy Detection for Feature Models
 *
 * Copyright (c) 2022-2022 AIG team, Institute for Software Technology,
 * Graz University of Technology, Austria
 *
 * Contact: http://ase.ist.tugraz.at/ASE/
 */

package at.tugraz.ist.ase.wipeoutr.app.eval;

import at.tugraz.ist.ase.fm.core.FeatureModel;
import at.tugraz.ist.ase.fm.parser.FeatureIDEParser;
import at.tugraz.ist.ase.fm.parser.FeatureModelParserException;
import at.tugraz.ist.ase.kb.fm.FMKB;
import at.tugraz.ist.ase.wipeoutr.app.cli.CmdLineOptions;
import at.tugraz.ist.ase.wipeoutr.app.cli.ConfigManager;
import org.chocosolver.solver.Model;
import org.chocosolver.solver.constraints.Constraint;

import java.io.*;
import java.util.LinkedList;
import java.util.List;

import static at.tugraz.ist.ase.eval.PerformanceEvaluator.*;
import static at.tugraz.ist.ase.wipeoutr.app.cli.ConfigManager.defaultConfigFile_SolverRuntimeEvaluation;
import static at.tugraz.ist.ase.wipeoutr.eval.WipeOutREvaluation.TIMER_SOLUTION;

/**
 * An evaluation for solver runtime to find a configuration for a given feature model.
 * Using Linux feature model (linux-2.6.33.3.xml).
 * Results submitted to SPLC'22.
 *
 * Configurations:
 * nameKB - name of the knowledge base
 * dataPath - path to the data folder, containing the knowledge base
 * outputPath - path to the output folder, containing the results
 * hasNonRedundantConstraints - true if want to evaluate redundancy-free model. The program will load non-redundant constraints
 *                              from a file with the same name as the knowledge base, but with the suffix "_nonred",
 *                              stored in the data folder.
 * numIter - number of iterations
 *
 * @author Viet-Man Le (vietman.le@ist.tugraz.at)
 */
public class SolverRuntimeEvaluation {

    public static void main(String[] args) throws IOException, FeatureModelParserException {
        String programTitle = "Solver Runtime Evaluation - Feature Model";
        String usage = "Usage: java -jar solver_runtime.jar [options]]";

        // Parse command line arguments
        CmdLineOptions cmdLineOptions = new CmdLineOptions(null, programTitle, null, usage);
        cmdLineOptions.parseArgument(args);

        if (cmdLineOptions.isHelp()) {
            cmdLineOptions.printUsage();
            System.exit(0);
        }

        cmdLineOptions.printWelcome();

        // Read configurations
        String confFile = cmdLineOptions.getConfFile() == null ? defaultConfigFile_SolverRuntimeEvaluation : cmdLineOptions.getConfFile();
        ConfigManager cfg = ConfigManager.getInstance(confFile);

        BufferedWriter writer = new BufferedWriter(new FileWriter(cfg.getSolverRuntimeResultsFilepath(cfg.isHasNonRedundantConstraints())));

        printConf(cfg, writer);

        // Read feature model
        File fmFile = new File(cfg.getDataPath() + cfg.getFullnameKB());
        FeatureIDEParser parser = new FeatureIDEParser();
        FeatureModel featureModel = parser.parse(fmFile);
        FMKB fmKB = new FMKB(featureModel, false); // generate CSP constraints and its negative constraints

        if (cfg.isHasNonRedundantConstraints()) {
            System.out.println("Evaluating Solver runtime on the redundancy-free model...");
            writer.write("Evaluating Solver runtime on the redundancy-free model...\n");
        } else {
            System.out.println("Evaluating Solver runtime on the original model...");
            writer.write("Evaluating Solver runtime on the original model...\n");
        }
        System.out.println("\t#constraints of the original model: " + fmKB.getNumConstraints());
        writer.write("\t#constraints of the original model: " + fmKB.getNumConstraints() + "\n");
        System.out.println("\t#Choco constraints of the original model: " + fmKB.getModelKB().getNbCstrs());
        writer.write("\t#Choco constraints of the original model: " + fmKB.getModelKB().getNbCstrs() + "\n");

        // if there are non-redundant constraints
        if (cfg.isHasNonRedundantConstraints()) {
            // load non-redundant constraints from file
            List<String> nonRedundantConstraints = loadNonRedundantConstraints(cfg);

            // remove redundant constraints from the model
            System.out.println("\tRemoving redundant constraints...");
            removeRedundantConstraints(fmKB, nonRedundantConstraints);
        }

        if (cfg.isHasNonRedundantConstraints()) {
            System.out.println("\t#Choco constraints of the redundancy-free model: " + fmKB.getModelKB().getNbCstrs());
            writer.write("\t#Choco constraints of the redundancy-free model: " + fmKB.getModelKB().getNbCstrs() + "\n");
        }

        double totalRuntime = 0;
        for (int i = 0; i < cfg.getNumIter(); i++) {
            System.out.println("\nITERATION " + (i + 1));
            writer.write("\t========================\n");

            reset(); // reset timers and counters
            setCommonTimer(TIMER_SOLUTION);
            start(TIMER_SOLUTION);
            fmKB.getModelKB().getSolver().solve();
            stop(TIMER_SOLUTION);
            fmKB.getModelKB().getSolver().reset(); // reset solver

            double runtime = (double) totalCommon(TIMER_SOLUTION);

            System.out.println("\tTime for solver running: " + (runtime / 1000000000.0));
            writer.write("\tTime for solver running: " + (runtime / 1000000000.0) + "\n");

            totalRuntime += runtime;

            writer.flush();
        }

        // Print results
        System.out.println("\nTotal runtime: " + (totalRuntime / 1000000000.0) + " seconds");
        System.out.println("Number of iterations: " + cfg.getNumIter());

        printResults(writer, cfg, totalRuntime / cfg.getNumIter());

        writer.close();

        System.out.println("DONE");
    }

    private static void removeRedundantConstraints(FMKB fmKB, List<String> nonRedundantConstraints) {
        Model model = fmKB.getModelKB();
        for (Constraint c : model.getCstrs()) {
            if (!nonRedundantConstraints.contains(c.toString())) {
                model.unpost(c);
            }
        }
    }

    private static void printResults(BufferedWriter writer, ConfigManager cfg, double ts) throws IOException {
        System.out.println("Solver Runtime Evaluation results:");
        writer.write("Solver Runtime Evaluation results:\n");

        String mess = "the original model";
        if (cfg.isHasNonRedundantConstraints()) {
            mess = "the redundancy-free model";
        }
        System.out.println("\tTime for solver on " + mess + ": " + (ts / 1000000000.0));
        writer.write("\tTime for solver on " + mess + ": " + (ts / 1000000000.0) + "\n");
    }

    private static List<String> loadNonRedundantConstraints(ConfigManager cfg) {
        System.out.println("\tLoading non-redundant constraints...");

        File file = new File(cfg.getNonRedundantConstraintsFilepath());
        List<String> nonRedundantConstraints = new LinkedList<>();

        BufferedReader reader;
        String line;
        try {

            // Open file
            reader = new BufferedReader(new FileReader(file));

            // Read the total number of constraints
            line = reader.readLine();
            int totalConstraints = Integer.parseInt(line);

            // Read all constraints
            for (int i = 0; i < totalConstraints; i++) {
                reader.readLine(); // read the constraints

                // read the total number of Choco constraints
                line = reader.readLine().trim();
                int totalChocoConstraints = Integer.parseInt(line);

                // read all Choco constraints
                for (int j = 0; j < totalChocoConstraints; j++) {
                    line = reader.readLine().trim();

                    nonRedundantConstraints.add(line);
                }
            }

            // Close file
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return nonRedundantConstraints;
    }

    private static void printConf(ConfigManager config, BufferedWriter writer) throws IOException {
        writer.write("Solver Runtime Evaluation - Feature Model\n");
        System.out.println("Configurations:");
        writer.write("Configurations:\n");
        System.out.println("\tnameKB: " + config.getNameKB());
        writer.write("\tnameKB: " + config.getNameKB() + "\n");
        System.out.println("\tdataPath: " + config.getDataPath());
        writer.write("\tdataPath: " + config.getDataPath() + "\n");
        System.out.println("\toutputPath: " + config.getOutputPath());
        writer.write("\toutputPath: " + config.getOutputPath() + "\n");
        System.out.println("\thasNonRedundantConstraints: " + config.isHasNonRedundantConstraints());
        writer.write("\thasNonRedundantConstraints: " + config.isHasNonRedundantConstraints() + "\n");
        System.out.println("\tnumIter: " + config.getNumIter());
        writer.write("\tnumIter: " + config.getNumIter() + "\n");
    }
}
