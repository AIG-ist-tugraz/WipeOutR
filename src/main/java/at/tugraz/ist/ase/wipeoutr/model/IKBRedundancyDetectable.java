/*
 * WipeOutR: Automated Redundancy Detection for Feature Models
 *
 * Copyright (c) 2022-2022 AIG team, Institute for Software Technology,
 * Graz University of Technology, Austria
 *
 * Contact: http://ase.ist.tugraz.at/ASE/
 */

package at.tugraz.ist.ase.wipeoutr.model;

import at.tugraz.ist.ase.kb.core.Constraint;

import java.util.List;

/**
 * @author Viet-Man Le (vietman.le@ist.tugraz.at)
 * Migrated to CA-CDR-V2
 */
public interface IKBRedundancyDetectable {
    /**
     * Gets the set of non-redundant constraints.
     * @return the set of non-redundant constraints.
     */
    List<Constraint> getNonRedundantConstraints();
}
