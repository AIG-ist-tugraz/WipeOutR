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
 * @author Viet-Man Le (vietman.le@ist.tugraz.at)
 */
@Slf4j
public class FMWipeOutFMModel extends CDRModel implements IChocoModel {

    @Getter
    private Model model;
    private FMKB fmkb;

    private final FeatureModel fm;

    /**
     * A constructor
     * On the basic of a given {@link FeatureModel}, it creates
     * corresponding variables and constraints for the model.
     *
     * @param fm a {@link FeatureModel}
     */
    public FMWipeOutFMModel(FeatureModel fm) {
        super(fm.getName());

        this.fm = fm;
        this.fmkb = new FMKB(fm, true);
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
        List<Constraint> C = new LinkedList<>(fmkb.getConstraintList());
        Collections.reverse(C);
        this.setPossiblyFaultyConstraints(C);

        // don't need the root constraint since WipeOutR_FM only checks isconsistent(CF - c1 U ~t1)

        // remove all Choco constraints
        model.unpost(model.getCstrs());

        LoggerUtils.outdent();
        log.debug("{}<<< Model {} initialized", LoggerUtils.tab(), getName());
    }

    public Object clone() throws CloneNotSupportedException {
        FMWipeOutFMModel clone = (FMWipeOutFMModel) super.clone();

        clone.fmkb = new FMKB(fm, true);
        clone.model = clone.fmkb.getModelKB();

        clone.initialize();

        return clone;
    }
}
