/*
 * WipeOutR: Automated Redundancy Detection for Feature Models
 *
 * Copyright (c) 2021-2022 AIG team, Institute for Software Technology,
 * Graz University of Technology, Austria
 *
 * Contact: http://ase.ist.tugraz.at/ASE/
 */

package at.tugraz.ist.ase.wipeoutr.model;

import at.tugraz.ist.ase.cdrmodel.CDRModel;
import at.tugraz.ist.ase.cdrmodel.IChocoModel;
import at.tugraz.ist.ase.cdrmodel.IDebuggingModel;
import at.tugraz.ist.ase.common.LoggerUtils;
import at.tugraz.ist.ase.fm.core.FeatureModel;
import at.tugraz.ist.ase.kb.fm.FMKB;
import at.tugraz.ist.ase.test.Assignment;
import at.tugraz.ist.ase.test.ITestCase;
import at.tugraz.ist.ase.test.TestCase;
import at.tugraz.ist.ase.test.TestSuite;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.chocosolver.solver.Model;
import org.chocosolver.solver.constraints.Constraint;
import org.chocosolver.solver.constraints.nary.cnf.LogOp;
import org.chocosolver.solver.variables.BoolVar;

import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import static at.tugraz.ist.ase.common.ChocoSolverUtils.getVariable;

/**
 * @author Viet-Man Le (vietman.le@ist.tugraz.at)
 */
@Slf4j
public class FMWipeOutTModel extends CDRModel implements IChocoModel, IDebuggingModel {

    @Getter
    private Model model;

    private final FeatureModel fm;
    private FMKB fmkb;
    private TestSuite testSuite;

    /**
     * The set of test cases.
     */
    @Getter
    private Set<ITestCase> testcases = new LinkedHashSet<>();

    /**
     * A constructor
     * On the basic of a given {@link FeatureModel}, it creates
     * corresponding variables and constraints for the model.
     *
     * @param fm a {@link FeatureModel}
     * @param testSuite a {@link TestSuite}
     */
    public FMWipeOutTModel(FeatureModel fm, TestSuite testSuite) {
        super(fm.getName());

        this.fm = fm;
        this.fmkb = new FMKB(fm, false);
        this.testSuite = testSuite;
        this.model = fmkb.getModelKB();
    }

    /**
     * This function creates a Choco models, variables, constraints
     * for a corresponding feature models. Besides, test cases are
     * also translated to Choco constraints.
     */
    @Override
    public void initialize() {
        log.debug("{}Initializing FMWipeOutTModel for {} >>>", LoggerUtils.tab(), getName());
        LoggerUtils.indent();

        // sets possibly faulty constraints to super class
        log.trace("{}Adding possibly faulty constraints", LoggerUtils.tab());
        List<at.tugraz.ist.ase.kb.core.Constraint> C = new LinkedList<>(fmkb.getConstraintList());
//        Collections.reverse(C);
        this.setPossiblyFaultyConstraints(C);

        // don't need the root constraint since WipeOutR_T only checks isconsistent(t1 & ~t2)

        // translates test cases to Choco constraints
        log.trace("{}Translating test cases to Choco constraints", LoggerUtils.tab());
        if (testSuite != null) {
            createTestCases();

            // sets the translated constraints
            testcases.addAll(testSuite.getTestCases());
        }

        // remove all Choco constraints, cause we just need variables and test cases
        model.unpost(model.getCstrs());

        LoggerUtils.outdent();
        log.debug("{}<<< Model {} initialized", LoggerUtils.tab(), getName());
    }

    /**
     * Gets a corresponding {@link ITestCase} object of a textual testcase.
     * @param testcase a textual testcase.
     * @return a corresponding {@link ITestCase} object.
     */
    public ITestCase getTestCase(String testcase) {
        return testSuite.getTestCase(testcase);
    }

    /**
     * Translates test cases to Choco constraints.
     */
    private void createTestCases() {
        for (ITestCase itc: testSuite.getTestCases()) { // for each test case
            TestCase tc = (TestCase) itc;

            int startIdx = model.getNbCstrs();

            LogOp logOp = LogOp.and(); // creates a AND LogOp
            for (Assignment assignment: tc.getAssignments()) { // get each clause
                BoolVar v = (BoolVar) getVariable(model, assignment.getVariable()); // get the corresponding variable
                if (assignment.getValue().equals("true")) { // true
                    logOp.addChild(v);
                } else { // false
                    logOp.addChild(v.not());
                }
            }
            model.addClauses(logOp); // add the translated constraints to the Choco model
            int lastCstrIdx = model.getNbCstrs();

            // add the translated constraints to the TestCase object
            setConstraintsToTestCase(tc, startIdx, lastCstrIdx - 1, false);

            // Negative test cases
            LogOp negLogOp = LogOp.nand(logOp);
            startIdx = model.getNbCstrs();
            model.addClauses(negLogOp);
            lastCstrIdx = model.getNbCstrs();
            setConstraintsToTestCase(tc, startIdx, lastCstrIdx - 1, true);
        }
    }

    /**
     * Sets translated Choco constraints to the {@link TestCase} object.
     */
    private void setConstraintsToTestCase(TestCase testCase, int startIdx, int endIdx, boolean negative) {
        Constraint[] constraints = model.getCstrs();
        int index = startIdx;
        while (index <= endIdx) {
            if (!negative) {
                testCase.addChocoConstraint(constraints[index]);
            } else {
                testCase.addNegChocoConstraint(constraints[index]);
            }
            index++;
        }
    }

    public Object clone() throws CloneNotSupportedException {
        FMWipeOutTModel clone = (FMWipeOutTModel) super.clone();

        clone.fmkb = new FMKB(fm, false);
        clone.testSuite = (TestSuite) testSuite.clone();
        clone.model = clone.fmkb.getModelKB();
        clone.testcases = new LinkedHashSet<>();

        clone.initialize();

        return clone;
    }
}
