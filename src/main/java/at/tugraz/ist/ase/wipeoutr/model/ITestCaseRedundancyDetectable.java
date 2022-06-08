/*
 * WipeOut: Automated Redundancy Detection in Feature Models
 *
 * Copyright (c) 2022-2022
 *
 * @author: Viet-Man Le (vietman.le@ist.tugraz.at)
 */

package at.tugraz.ist.ase.wipeoutr.model;

import at.tugraz.ist.ase.test.ITestCase;

import java.util.List;

public interface ITestCaseRedundancyDetectable {

    /**
     * Gets the set of non-redundant test cases.
     * @return the set of non-redundant test cases.
     */
    List<ITestCase> getNonRedundantTestcases();
}
