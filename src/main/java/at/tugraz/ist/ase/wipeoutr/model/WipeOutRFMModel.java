/*
 * WipeOutR: Automated Redundancy Detection for Feature Models
 *
 * Copyright (c) 2022-2022 AIG team, Institute for Software Technology,
 * Graz University of Technology, Austria
 *
 * Contact: http://ase.ist.tugraz.at/ASE/
 */

package at.tugraz.ist.ase.wipeoutr.model;

import at.tugraz.ist.ase.cdrmodel.CDRModel;
import at.tugraz.ist.ase.cdrmodel.IChocoModel;
import at.tugraz.ist.ase.common.LoggerUtils;
import at.tugraz.ist.ase.fm.core.FeatureModel;
import at.tugraz.ist.ase.kb.core.Constraint;
import at.tugraz.ist.ase.kb.fm.FMKB;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.chocosolver.solver.Model;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Manages/Prepares the inputs (constraints/test cases) for the WipeOutR_FM algorithm.
 *
 * @author Viet-Man Le (vietman.le@ist.tugraz.at)
 */
@Slf4j
public class WipeOutRFMModel extends CDRModel implements IChocoModel {

    @Getter
    private Model model; // Choco model
    private FMKB fmkb; // store the feature model knowledge base (variables, domains, constraints)

    private final FeatureModel fm;

    /**
     * A constructor
     * On the basic of a given {@link FeatureModel}, it creates
     * corresponding variables and constraints for the model.
     *
     * @param fm a {@link FeatureModel}
     */
    public WipeOutRFMModel(FeatureModel fm) {
        super(fm.getName());

        this.fm = fm;
        this.fmkb = new FMKB(fm, true); // translate the feature model into variables and constraints
        this.model = fmkb.getModelKB();
    }

    /**
     * This function adds constraints to the set of possibly faulty constraints (C).
     */
    @Override
    public void initialize() {
        log.debug("{}Initializing FMWipeOutTModel for {} >>>", LoggerUtils.tab(), getName());
        LoggerUtils.indent();

        // sets possibly faulty constraints to super class
        log.trace("{}Adding possibly faulty constraints", LoggerUtils.tab());
        List<Constraint> C = new LinkedList<>(fmkb.getConstraintList());
        Collections.reverse(C);
        this.setPossiblyFaultyConstraints(C);

        // doesn't need the root constraint since WipeOutR_FM only checks isconsistent(CF - c1 U ~t1)

        // removes all Choco constraints
        model.unpost(model.getCstrs());

        LoggerUtils.outdent();
        log.debug("{}<<< Model {} initialized", LoggerUtils.tab(), getName());
    }

    public Object clone() throws CloneNotSupportedException {
        WipeOutRFMModel clone = (WipeOutRFMModel) super.clone();

        clone.fmkb = new FMKB(fm, true);
        clone.model = clone.fmkb.getModelKB();

        clone.initialize();

        return clone;
    }
}
