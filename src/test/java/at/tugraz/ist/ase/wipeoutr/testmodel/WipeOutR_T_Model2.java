/*
 * WipeOut: Automated Redundancy Detection in Feature Models
 *
 * Copyright (c) 2021-2022
 *
 * @author: Viet-Man Le (vietman.le@ist.tugraz.at)
 */

package at.tugraz.ist.ase.wipeoutr.testmodel;

import at.tugraz.ist.ase.cdrmodel.CDRModel;
import at.tugraz.ist.ase.cdrmodel.IChocoModel;
import at.tugraz.ist.ase.cdrmodel.IDebuggingModel;
import at.tugraz.ist.ase.cdrmodel.test.csp.CSPModels;
import at.tugraz.ist.ase.common.IOUtils;
import at.tugraz.ist.ase.common.LoggerUtils;
import at.tugraz.ist.ase.test.ITestCase;
import at.tugraz.ist.ase.test.TestCase;
import at.tugraz.ist.ase.wipeoutr.model.ITestCaseRedundancyDetectable;
import lombok.Cleanup;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.IteratorUtils;
import org.chocosolver.solver.Model;
import org.chocosolver.solver.constraints.Constraint;
import org.chocosolver.solver.variables.IntVar;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

import static at.tugraz.ist.ase.csp2choco.CSP2ChocoTranslator.loadConstraints;

@Slf4j
public class WipeOutR_T_Model2 extends CDRModel implements IChocoModel, IDebuggingModel, ITestCaseRedundancyDetectable {

    @Getter
    private Model model;

    /**
     * The set of test cases.
     */
    @Getter
    private Set<ITestCase> testcases = new LinkedHashSet<>();

    @Getter
    private List<ITestCase> nonRedundantTestcases = new LinkedList<>();

    public WipeOutR_T_Model2() {
        super("csp3.mzn");
    }

    @Override
    public void initialize() throws IOException {
        log.trace("{}Initializing CDRModel for {} >>>", LoggerUtils.tab(), getName());
        LoggerUtils.indent();

        // add tat ca nhung test case vao
        model = createModel();

        // translates test cases to Choco constraints
        log.trace("{}Translating test cases to Choco constraints", LoggerUtils.tab());
        createTestCases();

        identifyExpectedResults();

        model.unpost(model.getCstrs());

        LoggerUtils.outdent();
        log.debug("{}<<< Initialized CDRModel for {}", LoggerUtils.tab(), getName());
    }

    /**
     * Translates test cases to Choco constraints.
     */
    private void createTestCases() {
        for (Constraint c: model.getCstrs()) {
            Constraint opC = c.getOpposite(); // get negation of constraint
            model.post(opC);

            TestCase tc = TestCase.builder()
                    .testcase(c.toString())
                    .assignments(Collections.emptyList())
                    .chocoConstraints(Collections.singletonList(c))
                    .negChocoConstraints(Collections.singletonList(opC))
                    .build();

            testcases.add(tc);
        }
    }

    private Model createModel() throws IOException {
        // create a model
        Model model = new Model("csp3.mzn");

        // Decision variables
        IntVar x = model.intVar("x", -10, 10);
        IntVar y = model.intVar("y", -10, 10);

        ClassLoader classLoader = CSPModels.class.getClassLoader();
        @Cleanup InputStream inputStream = IOUtils.getInputStream(classLoader, getName());

        loadConstraints(inputStream, model);
        log.trace("{}Created constraints", LoggerUtils.tab());

        return model;
    }

    public void identifyExpectedResults() {
        // Expected non redundant test cases
        nonRedundantTestcases.add(IteratorUtils.get(testcases.iterator(), 0));
        nonRedundantTestcases.add(IteratorUtils.get(testcases.iterator(), 1));
        nonRedundantTestcases.add(IteratorUtils.get(testcases.iterator(), 3));
    }

    @Override
    public ITestCase getTestCase(String s) {
        return testcases.stream().filter(itc -> {
            TestCase tc = (TestCase) itc;
            return tc.getTestcase().equals(s);
        }).findFirst().orElse(null);
    }

    public Object clone() throws CloneNotSupportedException {
        WipeOutR_T_Model2 clone = (WipeOutR_T_Model2) super.clone();

        clone.testcases = new LinkedHashSet<>();
        clone.nonRedundantTestcases = new LinkedList<>();

        try {
            clone.initialize();
        } catch (IOException e) {
            throw new CloneNotSupportedException(e.getMessage());
        }

        return clone;
    }
}
