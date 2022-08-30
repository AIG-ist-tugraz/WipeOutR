/*
 * WipeOutR: Automated Redundancy Detection for Feature Models
 *
 * Copyright (c) 2022-2022 AIG team, Institute for Software Technology,
 * Graz University of Technology, Austria
 *
 * Contact: http://ase.ist.tugraz.at/ASE/
 */

package at.tugraz.ist.ase.wipeoutr.algorithm;

import at.tugraz.ist.ase.cacdr.checker.ChocoConsistencyChecker;
import at.tugraz.ist.ase.fm.core.FeatureModel;
import at.tugraz.ist.ase.fm.core.FeatureModelException;
import at.tugraz.ist.ase.fm.core.RelationshipType;
import at.tugraz.ist.ase.kb.core.Constraint;
import at.tugraz.ist.ase.wipeoutr.model.WipeOutRFMModel;
import at.tugraz.ist.ase.wipeoutr.testmodel.WipeOutR_FM_Model1;
import at.tugraz.ist.ase.wipeoutr.testmodel.WipeOutR_FM_Model2;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import static at.tugraz.ist.ase.common.ConstraintUtils.convertToString;
import static at.tugraz.ist.ase.eval.PerformanceEvaluator.reset;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author Viet-Man Le (vietman.le@ist.tugraz.at)
 * Migrated to CA-CDR-V2
 */
class WipeOutRFMTest {
    @Test
    void test1() throws IOException {
        WipeOutR_FM_Model1 testCaseModel = new WipeOutR_FM_Model1();
        testCaseModel.initialize();

        System.out.println("=========================================");
        System.out.println("Constraints translated from the text file:");
        System.out.println(convertToString(testCaseModel.getPossiblyFaultyConstraints()));
        System.out.println("=========================================");

        ChocoConsistencyChecker checker = new ChocoConsistencyChecker(testCaseModel);

        List<Constraint> CF = new LinkedList<>(testCaseModel.getPossiblyFaultyConstraints());

        WipeOutR_FM wipeOut = new WipeOutR_FM(checker);

        reset();
        List<Constraint> newCF = wipeOut.run(CF);

        assertEquals(newCF, testCaseModel.getNonRedundantConstraints());

        System.out.println("Result constraints:");
        newCF.forEach(System.out::println);
    }

    @Test
    void test2() throws IOException {
        WipeOutR_FM_Model2 testCaseModel = new WipeOutR_FM_Model2();
        testCaseModel.initialize();

        System.out.println("=========================================");
        System.out.println("Constraints translated from the text file:");
        System.out.println(convertToString(testCaseModel.getPossiblyFaultyConstraints()));
        System.out.println("=========================================");

        ChocoConsistencyChecker checker = new ChocoConsistencyChecker(testCaseModel);

        List<Constraint> CF = new LinkedList<>(testCaseModel.getPossiblyFaultyConstraints());

        WipeOutR_FM wipeOut = new WipeOutR_FM(checker);

        reset();
        List<Constraint> newCF = wipeOut.run(CF);

        assertEquals(newCF, testCaseModel.getNonRedundantConstraints());

        System.out.println("Result constraints:");
        newCF.forEach(System.out::println);
    }

    @Test
    void test3() throws FeatureModelException {
        FeatureModel fm = new FeatureModel();
        fm.addFeature("survey", "survey");
        fm.addFeature("pay", "pay");
        fm.addFeature("ABtesting", "ABtesting");
        fm.addFeature("statistics", "statistics");
        fm.addFeature("qa", "qa");
        fm.addFeature("license", "license");
        fm.addFeature("nonlicense", "nonlicense");
        fm.addFeature("multiplechoice", "multiplechoice");
        fm.addFeature("singlechoice", "singlechoice");
        fm.addRelationship(RelationshipType.MANDATORY, fm.getFeature("survey"), Collections.singletonList(fm.getFeature("pay")));
        fm.addRelationship(RelationshipType.OPTIONAL, fm.getFeature("ABtesting"), Collections.singletonList(fm.getFeature("survey")));
        fm.addRelationship(RelationshipType.MANDATORY, fm.getFeature("survey"), Collections.singletonList(fm.getFeature("statistics")));
        fm.addRelationship(RelationshipType.MANDATORY, fm.getFeature("survey"), Collections.singletonList(fm.getFeature("qa")));
        fm.addRelationship(RelationshipType.ALTERNATIVE, fm.getFeature("pay"), List.of(fm.getFeature("license"), fm.getFeature("nonlicense")));
        fm.addRelationship(RelationshipType.OR, fm.getFeature("qa"), List.of(fm.getFeature("multiplechoice"), fm.getFeature("singlechoice")));
        fm.addConstraint(RelationshipType.REQUIRES, fm.getFeature("ABtesting"), Collections.singletonList(fm.getFeature("statistics")));
        fm.addConstraint(RelationshipType.EXCLUDES, fm.getFeature("ABtesting"), Collections.singletonList(fm.getFeature("nonlicense")));

        WipeOutRFMModel testCaseModel = new WipeOutRFMModel(fm);
        testCaseModel.initialize();

        System.out.println("=========================================");
        System.out.println("Constraints translated from the text file:");
        System.out.println(convertToString(testCaseModel.getPossiblyFaultyConstraints()));
//        for (Constraint c : testCaseModel.getPossiblyFaultyConstraints()) {
//            System.out.println(c);
//            c.getChocoConstraints().forEach(System.out::println);
//        }
        System.out.println("=========================================");

        ChocoConsistencyChecker checker = new ChocoConsistencyChecker(testCaseModel);

        List<Constraint> CF = new LinkedList<>(testCaseModel.getPossiblyFaultyConstraints());

        WipeOutR_FM wipeOut = new WipeOutR_FM(checker);

        reset();
        List<Constraint> newCF = wipeOut.run(CF);

        // test
        CF.remove(1); // remove the constraint "ABtesting -> statistics"
        assertEquals(newCF, CF);

        System.out.println("Result constraints:");
        newCF.forEach(System.out::println);
    }
}