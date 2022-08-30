/*
 * WipeOutR: Automated Redundancy Detection for Feature Models
 *
 * Copyright (c) 2022-2022 AIG team, Institute for Software Technology,
 * Graz University of Technology, Austria
 *
 * Contact: http://ase.ist.tugraz.at/ASE/
 */

package at.tugraz.ist.ase.wipeoutr.testmodel;

import at.tugraz.ist.ase.cdrmodel.CDRModel;
import at.tugraz.ist.ase.cdrmodel.IChocoModel;
import at.tugraz.ist.ase.cdrmodel.test.csp.CSPModels;
import at.tugraz.ist.ase.common.IOUtils;
import at.tugraz.ist.ase.common.LoggerUtils;
import at.tugraz.ist.ase.kb.core.Constraint;
import at.tugraz.ist.ase.wipeoutr.model.IKBRedundancyDetectable;
import lombok.Cleanup;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.chocosolver.solver.Model;
import org.chocosolver.solver.variables.IntVar;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import static at.tugraz.ist.ase.csp2choco.CSP2ChocoTranslator.loadConstraints;

/**
 * @author Viet-Man Le (vietman.le@ist.tugraz.at)
 * Migrated to CA-CDR-V2
 */
@Slf4j
public class WipeOutR_FM_Model2 extends CDRModel implements IChocoModel, IKBRedundancyDetectable {
    @Getter
    private Model model;

    @Getter
    private List<Constraint> nonRedundantConstraints = new LinkedList<>();

    public WipeOutR_FM_Model2() {
        super("csp3.mzn");
    }

    @Override
    public void initialize() throws IOException {
        log.trace("{}Initializing CDRModel for {} >>>", LoggerUtils.tab(), getName());
        LoggerUtils.indent();

        // create the model
        model = createModel();

        // sets possibly faulty constraints to super class
        List<Constraint> C = new ArrayList<>();
        for (org.chocosolver.solver.constraints.Constraint c: model.getCstrs()) {
            Constraint constraint = new Constraint(c.toString());
            constraint.addChocoConstraint(c);

            org.chocosolver.solver.constraints.Constraint opC = c.getOpposite(); // get negation of constraint
            model.post(opC);
            constraint.addNegChocoConstraint(opC);

            C.add(constraint);
        }
        this.setPossiblyFaultyConstraints(C);
        log.trace("{}Added constraints to the possibly faulty constraints [C={}]", LoggerUtils.tab(), C);

        // expected results
        nonRedundantConstraints.add(C.get(1));
        nonRedundantConstraints.add(C.get(3));

        model.unpost(model.getCstrs());

        LoggerUtils.outdent();
        log.debug("{}<<< Initialized CDRModel for {}", LoggerUtils.tab(), getName());
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

    public Object clone() throws CloneNotSupportedException {
        WipeOutR_FM_Model2 clone = (WipeOutR_FM_Model2) super.clone();

        clone.nonRedundantConstraints = new LinkedList<>();

        try {
            clone.initialize();
        } catch (IOException e) {
            throw new CloneNotSupportedException(e.getMessage());
        }

        return clone;
    }
}
