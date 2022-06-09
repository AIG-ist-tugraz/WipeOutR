/*
 * WipeOutR: Automated Redundancy Detection for Feature Models
 *
 * Copyright (c) 2022-2022 AIG team, Institute for Software Technology,
 * Graz University of Technology, Austria
 *
 * Contact: http://ase.ist.tugraz.at/ASE/
 */

package at.tugraz.ist.ase.wipeoutr.common;

import lombok.experimental.UtilityClass;

/**
 * Some utility methods for array manipulation.
 *
 * @author Viet-Man Le (vietman.le@ist.tugraz.at)
 */
@UtilityClass
public class ArrayUtils {
    /**
     * Creates an indexes array, in which each element is (the index of the element + 1).
     * Ex: with size = 4, [1, 2, 3, 4] is returned.
     */
    public Integer[] createIndexesArray(int size) {
        Integer[] indexesArr = new Integer[size];
        for (int i = 0; i < size; i++) {
            indexesArr[i] = i + 1;
        }
        return indexesArr;
    }
}
