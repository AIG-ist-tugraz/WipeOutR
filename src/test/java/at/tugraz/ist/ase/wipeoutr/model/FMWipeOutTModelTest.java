/*
 * WipeOut: Automated Redundancy Detection in Feature Models
 *
 * Copyright (c) 2022-2022
 *
 * @author: Viet-Man Le (vietman.le@ist.tugraz.at)
 */

package at.tugraz.ist.ase.wipeoutr.model;

import at.tugraz.ist.ase.cacdr.checker.ChocoConsistencyChecker;
import at.tugraz.ist.ase.fm.core.FeatureModel;
import at.tugraz.ist.ase.fm.parser.FeatureIDEParser;
import at.tugraz.ist.ase.fm.parser.FeatureModelParserException;
import at.tugraz.ist.ase.test.ITestCase;
import at.tugraz.ist.ase.test.TestCase;
import at.tugraz.ist.ase.test.TestSuite;
import at.tugraz.ist.ase.test.builder.ITestCaseBuildable;
import at.tugraz.ist.ase.test.builder.TestSuiteBuilder;
import at.tugraz.ist.ase.test.builder.fm.FMTestCaseBuilder;
import at.tugraz.ist.ase.wipeoutr.algorithm.WipeOutR_T;
import at.tugraz.ist.ase.wipeoutr.testmodel.WipeOutR_T_Model1;
import at.tugraz.ist.ase.wipeoutr.testmodel.WipeOutR_T_Model2;
import com.google.common.collect.Iterators;
import lombok.Cleanup;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;

import static at.tugraz.ist.ase.eval.PerformanceEvaluator.reset;
import static org.junit.jupiter.api.Assertions.*;

class FMWipeOutTModelTest {
    @Test
    void shouldCloneable() throws FeatureModelParserException, IOException, CloneNotSupportedException {
        FeatureIDEParser parser = new FeatureIDEParser();
        String fmFile = "src/test/resources/linux-2.6.33.3.xml";
        String testcaseFile = "src/test/resources/linux-2.6.3_10_0.testcases";

        // loads feature model
        FeatureModel featureModel = parser.parse(new File(fmFile));
        // TestSuite
        File fileTS = new File(testcaseFile);
        TestSuiteBuilder factory = new TestSuiteBuilder();
        ITestCaseBuildable testCaseFactory = new FMTestCaseBuilder();
        @Cleanup InputStream is = new FileInputStream(fileTS);
        TestSuite testSuite = factory.buildTestSuite(is, testCaseFactory);
        // FMWipeOutTModelTest
        FMWipeOutTModel testCaseModel = new FMWipeOutTModel(featureModel, testSuite);
        testCaseModel.initialize();

        FMWipeOutTModel clone = (FMWipeOutTModel) testCaseModel.clone();

        assertNotSame(testCaseModel.getModel(), clone.getModel());
        for (int i = 0; i < testCaseModel.getTestcases().size(); i++) {
            assertNotSame(Iterators.get(testCaseModel.getTestcases().iterator(), i), Iterators.get(clone.getTestcases().iterator(), i));
        }
        for (int i = 0; i < testCaseModel.getPossiblyFaultyConstraints().size(); i++) {
            assertNotSame(Iterators.get(testCaseModel.getPossiblyFaultyConstraints().iterator(), i), Iterators.get(clone.getPossiblyFaultyConstraints().iterator(), i));
        }
    }

    @Test
    void shouldSupportClone1() throws IOException, CloneNotSupportedException {
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

        WipeOutR_T wipeOut = new WipeOutR_T(checker);

        reset();
        List<ITestCase> newT = wipeOut.run(T);

        assertEquals(newT, testCaseModel.getNonRedundantTestcases());

        System.out.println("Result test cases:");
        newT.forEach(System.out::println);

        // in a parallel scenario, T doesn't change, whereas the model changes
        // this test should be work to show that the algorithm works well in parallel scenarios
        WipeOutR_T_Model1 testCaseModel_clone = (WipeOutR_T_Model1) testCaseModel.clone();
        ChocoConsistencyChecker checker_new = new ChocoConsistencyChecker(testCaseModel_clone);
        WipeOutR_T wipeOut_new = new WipeOutR_T(checker_new);

        // rerun WipeOutR_T with the original test cases
        reset();
        newT = wipeOut_new.run(T);

        assertEquals(newT, testCaseModel.getNonRedundantTestcases());

        System.out.println("Result test cases:");
        newT.forEach(System.out::println);
    }

    @Test
    void shouldSupportClone2() throws IOException, CloneNotSupportedException {
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

        // in a parallel scenario, T doesn't change, whereas the model changes
        // this test should be work to show that the algorithm works well in parallel scenarios
        WipeOutR_T_Model2 testCaseModel_clone = (WipeOutR_T_Model2) testCaseModel.clone();
        ChocoConsistencyChecker checker_new = new ChocoConsistencyChecker(testCaseModel_clone);
        WipeOutR_T wipeOut_new = new WipeOutR_T(checker_new);

        // rerun WipeOutR_T with the original test cases
        reset();
        newT = wipeOut_new.run(T);

        assertEquals(newT, testCaseModel.getNonRedundantTestcases());

        System.out.println("Result test cases:");
        newT.forEach(System.out::println);
    }

    @Test
    void shouldSupportClone3() throws CloneNotSupportedException {
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

        List<ITestCase> finalNewT = newT;
        assertAll( () -> assertFalse(finalNewT.contains(t1)),
                () -> assertTrue(finalNewT.contains(t2)),
                () -> assertTrue(finalNewT.contains(t3)),
                () -> assertFalse(finalNewT.contains(t4)),
                () -> assertTrue(finalNewT.contains(t5))
        );

        // in a parallel scenario, T doesn't change, whereas the model changes
        // this test should be work to show that the algorithm works well in parallel scenarios
        FMWipeOutTModel testCaseModel_clone = (FMWipeOutTModel) testCaseModel.clone();
        ChocoConsistencyChecker checker_new = new ChocoConsistencyChecker(testCaseModel_clone);
        WipeOutR_T wipeOut_new = new WipeOutR_T(checker_new);

        // rerun WipeOutR_T with the original test cases
        reset();
        newT = wipeOut_new.run(testCases);

        System.out.println("Result test cases:");
        newT.forEach(System.out::println);

        List<ITestCase> finalNewT1 = newT;
        assertAll( () -> assertFalse(finalNewT1.contains(t1)),
                () -> assertTrue(finalNewT1.contains(t2)),
                () -> assertTrue(finalNewT1.contains(t3)),
                () -> assertFalse(finalNewT1.contains(t4)),
                () -> assertTrue(finalNewT1.contains(t5))
        );
    }
}