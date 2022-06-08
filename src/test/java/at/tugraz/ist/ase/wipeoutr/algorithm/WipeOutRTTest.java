/*
 * WipeOut: Automated Redundancy Detection in Feature Models
 *
 * Copyright (c) 2022-2022
 *
 * @author: Viet-Man Le (vietman.le@ist.tugraz.at)
 */

package at.tugraz.ist.ase.wipeoutr.algorithm;

import at.tugraz.ist.ase.cacdr.checker.ChocoConsistencyChecker;
import at.tugraz.ist.ase.fm.core.FeatureModel;
import at.tugraz.ist.ase.test.ITestCase;
import at.tugraz.ist.ase.test.TestCase;
import at.tugraz.ist.ase.test.TestSuite;
import at.tugraz.ist.ase.test.builder.fm.FMTestCaseBuilder;
import at.tugraz.ist.ase.wipeoutr.model.FMWipeOutTModel;
import at.tugraz.ist.ase.wipeoutr.testmodel.WipeOutR_T_Model1;
import at.tugraz.ist.ase.wipeoutr.testmodel.WipeOutR_T_Model2;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import static at.tugraz.ist.ase.eval.PerformanceEvaluator.reset;
import static org.junit.jupiter.api.Assertions.*;

class WipeOutRTTest {
    @Test
    void test1() throws IOException {
        WipeOutR_T_Model1 testCaseModel = new WipeOutR_T_Model1();
        testCaseModel.initialize();

        System.out.println("=========================================");
        System.out.println("Test cases translated from the text file:");
        for (ITestCase testCase : testCaseModel.getTestcases()) {
            System.out.println(testCase.toString());
        }
        System.out.println("=========================================");

        ChocoConsistencyChecker checker = new ChocoConsistencyChecker(testCaseModel);

        List<ITestCase> T = new LinkedList<>(testCaseModel.getTestcases());

        System.out.println("Try");

        WipeOutR_T wipeOut = new WipeOutR_T(checker);

        reset();
        List<ITestCase> newT = wipeOut.run(T);

        assertEquals(newT, testCaseModel.getNonRedundantTestcases());

        System.out.println("Result test cases:");
        newT.forEach(System.out::println);
    }

    @Test
    void test2() throws IOException {
        WipeOutR_T_Model2 testCaseModel = new WipeOutR_T_Model2();
        testCaseModel.initialize();

        System.out.println("=========================================");
        System.out.println("Test cases translated from the text file:");
        for (ITestCase testCase : testCaseModel.getTestcases()) {
            System.out.println(testCase.toString());
        }
        System.out.println("=========================================");

        ChocoConsistencyChecker checker = new ChocoConsistencyChecker(testCaseModel);

        List<ITestCase> T = new LinkedList<>(testCaseModel.getTestcases());

        System.out.println("Try");

        WipeOutR_T wipeOut = new WipeOutR_T(checker);

        reset();
        List<ITestCase> newT = wipeOut.run(T);

        assertEquals(newT, testCaseModel.getNonRedundantTestcases());

        System.out.println("Result test cases:");
        newT.forEach(System.out::println);
    }

    @Test
    void test3() {
        FeatureModel fm = new FeatureModel();
        fm.addFeature("CBRec", "CBRec");
        fm.addFeature("RecEng", "RecEng");
        fm.addFeature("Stat", "Stat");
        fm.addFeature("QType", "QType");
        fm.addFeature("ImgAnaTask", "ImgAnaTask");

        FMTestCaseBuilder builder = new FMTestCaseBuilder();
        List<ITestCase> testCaseList = new LinkedList<>();
        // t1: CBRec = true
        TestCase t1 = builder.buildTestCase("CBRec");
        testCaseList.add(t1);
        // t2: RecEng = false /\ Stat = false
        TestCase t2 = builder.buildTestCase("~RecEng & ~Stat");
        testCaseList.add(t2);
        // t3: QType = false
        TestCase t3 = builder.buildTestCase("~QType");
        testCaseList.add(t3);
        // t4: ImgAnaTask = false
        TestCase t4 = builder.buildTestCase("~ImgAnaTask");
        testCaseList.add(t4);
        // t5: CBRec = true /\ ImgAnaTask = false
        TestCase t5 = builder.buildTestCase("CBRec & ~ImgAnaTask");
        testCaseList.add(t5);
        // t6: Stat = true
        TestSuite testSuite = TestSuite.builder().testCases(testCaseList).build();

        FMWipeOutTModel testCaseModel = new FMWipeOutTModel(fm, testSuite);
        testCaseModel.initialize();

        System.out.println("=========================================");
        System.out.println("Test cases translated from the text file:");
        for (ITestCase testCase : testCaseModel.getTestcases()) {
            System.out.println(testCase.toString());
        }
        System.out.println("=========================================");

        ChocoConsistencyChecker checker = new ChocoConsistencyChecker(testCaseModel);

        List<ITestCase> testCases = new LinkedList<>(testCaseModel.getTestcases());

        System.out.println("Try");

        WipeOutR_T wipeOut = new WipeOutR_T(checker);

        reset();
        List<ITestCase> newT = wipeOut.run(testCases);

        System.out.println("Result test cases:");
        newT.forEach(System.out::println);

        assertAll( () -> assertFalse(newT.contains(t1)),
                () -> assertTrue(newT.contains(t2)),
                () -> assertTrue(newT.contains(t3)),
                () -> assertFalse(newT.contains(t4)),
                () -> assertTrue(newT.contains(t5))
        );
    }
}