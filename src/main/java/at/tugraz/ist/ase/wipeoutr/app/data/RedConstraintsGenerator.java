/*
 * WipeOutR: Automated Redundancy Detection for Feature Models
 *
 * Copyright (c) 2022-2022 AIG team, Institute for Software Technology,
 * Graz University of Technology, Austria
 *
 * Contact: http://ase.ist.tugraz.at/ASE/
 */

package at.tugraz.ist.ase.wipeoutr.app.data;

import at.tugraz.ist.ase.fm.core.*;
import at.tugraz.ist.ase.fm.parser.FMFormat;
import at.tugraz.ist.ase.fm.parser.FeatureModelParser;
import at.tugraz.ist.ase.fm.parser.FeatureModelParserException;
import at.tugraz.ist.ase.fm.parser.factory.FMParserFactory;
import at.tugraz.ist.ase.wipeoutr.app.cli.CmdLineOptions;
import at.tugraz.ist.ase.wipeoutr.app.cli.ConfigManager;
import com.google.common.io.Files;
import lombok.Cleanup;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static at.tugraz.ist.ase.common.IOUtils.checkAndCreateFolder;
import static at.tugraz.ist.ase.wipeoutr.app.cli.ConfigManager.defaultConfigFile_TestCasesGenerator;

/**
 * This class generates redundant constraints for a feature model.
 * Generated redundant constraints includes:
 * - Excludes constraints between child features of alternative relationships
 * - Requires constraints from an optional feature to a mandatory feature
 * Output constraints are encoded using the FeatureIDE format.
 *
 * Configurations:
 * nameKB - name/filename of the knowledge base
 * dataPath - path to the data folder, where you store the knowledge base
 * outputPath - path to the folder, where the program will save the generated test suite
 *
 * @author Viet-Man Le (vietman.le@ist.tugraz.at)
 */
public class RedConstraintsGenerator {

    public static void main(String[] args) throws IOException, FeatureModelParserException {
        String programTitle = "Redundant Constraints Generator";
        String usage = "Usage: java -jar rc_gen.jar [options]]";

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

        String pathtoSave = cfg.getOutputPath();
        checkAndCreateFolder(pathtoSave);

        File fmFile = new File(cfg.getKBFilepath());
        String filename = fmFile.getName();

        // Generate redundant constraints
        RedConstraintsGenerator generator = new RedConstraintsGenerator(cfg);

        System.out.println("Generating redundant constraints for " + filename.toUpperCase() + "...");
        generator.generate();

        System.out.println("Done - " + fmFile.getName());
    }

    ConfigManager cfg;

    public RedConstraintsGenerator(ConfigManager cfg) {
        this.cfg = cfg;
    }

    private void generate() throws FeatureModelParserException, IOException {
        // Read feature model
        File fmFile = new File(cfg.getKBFilepath());
        String filename = fmFile.getName();

        FMFormat fmFormat = FMFormat.getFMFormat(Files.getFileExtension(filename));
        FeatureModelParser parser = FMParserFactory.getInstance().getParser(fmFormat);
        FeatureModel featureModel = parser.parse(fmFile);

        @Cleanup BufferedWriter writer = new BufferedWriter(new FileWriter(cfg.getRedConstraintsFilepath()));

        // Generate excludes constraints
        generateRedConstraintsForAlternative(featureModel, writer);
        // Generate requires constraints
        generateRedConstraintsForRequires(featureModel, writer);
    }

    /**
     * Generate excludes constraints between child features of alternative relationships
     *
     * Format of the generated constraints (FeatureIDE format):
     * <rule>
     * 		<disj>
     * 			<not>
     * 				<var>SMP</var>
     * 			</not>
     * 			<not>
     * 				<var>TINY_RCU</var>
     * 			</not>
     * 		</disj>
     * </rule>
     */
    private void generateRedConstraintsForAlternative(FeatureModel featureModel, BufferedWriter writer) {
        AtomicInteger count = new AtomicInteger();

        // for each alternative relationship
        featureModel.getRelationships().forEach(relationship -> {
            if (relationship.getType().equals(RelationshipType.ALTERNATIVE)) {

                List<Feature> subFeatures = ((BasicRelationship)relationship).getRightSide();

                // for each pair of child features of the alternative relationship
                for (int i = 0; i < subFeatures.size() - 1; i++) {
                    for (int j = i + 1; j < subFeatures.size(); j++) {
                        String child1 = subFeatures.get(i).getName();
                        String child2 = subFeatures.get(j).getName();

                        System.out.println(count.incrementAndGet());

                        String rule = "\t\t<rule>\n" +
                                "\t\t\t<disj>\n" +
                                "\t\t\t\t<not>\n" +
                                "\t\t\t\t\t<var>" + child1 + "</var>\n" +
                                "\t\t\t\t</not>\n" +
                                "\t\t\t\t<not>\n" +
                                "\t\t\t\t\t<var>" + child2 + "</var>\n" +
                                "\t\t\t\t</not>\n" +
                                "\t\t\t</disj>\n" +
                                "\t\t</rule>";

                        System.out.println(rule);

                        try {
                            writer.write(rule + "\n");
                            writer.flush();
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }
            }
        });
    }

    /**
     * Generate requires constraints from an optional feature to a mandatory feature
     *
     * Format of the generated constraints (FeatureIDE format):
     * <rule>
     * 		<disj>
     * 		    <var>PREEMPT</var>
     * 			<not>
     * 				<var>TREE_PREEMPT_RCU</var>
     * 			</not>
     * 		</disj>
     * 	</rule>
     */
    private void generateRedConstraintsForRequires(FeatureModel featureModel, BufferedWriter writer) {
        int count = 0;
        List<Feature> features = featureModel.getBfFeatures();

        for (Feature feature : features) {
            // all relationships which the feature participates in
            List<Relationship> relationships = featureModel.getRelationshipsWith(feature).stream()
                    .filter(r -> r.presentAtLeftSide(feature) || r.presentAtRightSide(feature)).toList();

            // get all sub features of the feature
            List<Feature> subFeatures = new LinkedList<>();
            for (Relationship relationship : relationships) {
                if (relationship.presentAtLeftSide(feature)) {
                    subFeatures.addAll(((BasicRelationship)relationship).getRightSide());
                } else {
                    subFeatures.add(((BasicRelationship)relationship).getLeftSide());
                }
            }

            // for each pair of sub features
            for (int i = 0; i < subFeatures.size(); i++) {
                Feature f1 = subFeatures.get(i);

                if (featureModel.isOptionalFeature(f1)) { // if the first sub feature is optional

                    for (int j = 0; j < subFeatures.size(); j++) {
                        if (i == j) continue;

                        Feature f2 = subFeatures.get(j);

                        if (featureModel.isMandatoryFeature(f2)) { // if the second sub feature is mandatory

                            System.out.println(++count);

                            String rule = "\t\t<rule>\n" +
                                    "\t\t\t<disj>\n" +
                                    "\t\t\t\t<var>" + f2.getName() + "</var>\n" +
                                    "\t\t\t\t<not>\n" +
                                    "\t\t\t\t\t<var>" + f1.getName() + "</var>\n" +
                                    "\t\t\t\t</not>\n" +
                                    "\t\t\t</disj>\n" +
                                    "\t\t</rule>";
                            System.out.println(rule);

                            try {
                                writer.write(rule + "\n");
                                writer.flush();
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                        }
                    }
                }
            }
        }
    }

    private static void printConf(ConfigManager config) {
        System.out.println("Configurations:");
        System.out.println("\tnameKB: " + config.getNameKB());
        System.out.println("\tdataPath: " + config.getDataPath());
        System.out.println("\toutputPath: " + config.getOutputPath());
    }
}
