/*
 * WipeOutR: Automated Redundancy Detection for Feature Models
 *
 * Copyright (c) 2021-2022 AIG team, Institute for Software Technology,
 * Graz University of Technology, Austria
 *
 * Contact: http://ase.ist.tugraz.at/ASE/
 */

package at.tugraz.ist.ase.wipeoutr.algorithm;

import at.tugraz.ist.ase.cacdr.checker.ChocoConsistencyChecker;
import at.tugraz.ist.ase.common.LoggerUtils;
import at.tugraz.ist.ase.eval.PerformanceEvaluator;
import at.tugraz.ist.ase.test.ITestCase;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.ListUtils;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import static at.tugraz.ist.ase.cacdr.eval.CAEvaluator.COUNTER_CONSISTENCY_CHECKS;
import static at.tugraz.ist.ase.eval.PerformanceEvaluator.stop;

/**
 * Implementation of the WipeOutR_T algorithm.
 *
 * // WipeOutR_T Algorithm
 * //--------------------
 * // T: test cases
 * //--------------------
 * // Func WipeOutR_T(T) :
 * // Tπ = T; TΔ = Φ
 * // while Tπ != Φ do
 * //   tα ← first(Tπ)
 * //   for all tγ ∈ T - T∆ − {tα} do
 * //      if inconsistent(tα ∧ ¬tγ) then
 * //         T∆ ← T∆ ∪ {tγ}
 * //         Tπ ← Tπ − {tγ}
 * //      end if
 * //   end for
 * //   Tπ ← Tπ − {tα}
 * // end while
 * // return(T − T∆)
 *
 * @author Viet-Man Le (vietman.le@ist.tugraz.at)
 */
@Slf4j
public class WipeOutR_T {

    // for evaluation
    public static final String TIMER_WIPEOUTR_T = "Timer for WipeOutR_T:";

    private final ChocoConsistencyChecker checker;

    public WipeOutR_T(@NonNull ChocoConsistencyChecker checker) {
        this.checker = checker;
    }

    /**
     * // Func WipeOutR_T(T) :
     * // Tπ = T; TΔ = Φ
     * // while Tπ != Φ do
     * //   tα ← first(Tπ)
     * //   for all tγ ∈ T - T∆ − {tα} do
     * //      if inconsistent(tα ∧ ¬tγ) then
     * //         T∆ ← T∆ ∪ {tγ}
     * //         Tπ ← Tπ − {tγ}
     * //      end if
     * //   end for
     * //   Tπ ← Tπ − {tα}
     * // end while
     * // return(T − T∆)
     */
    public List<ITestCase> run(@NonNull List<ITestCase> T) {
        log.debug("{}Detecting redundancies for [T={}] >>>", LoggerUtils.tab(), T);
        LoggerUtils.indent();

        // Tπ = T; TΔ = Φ
        List<ITestCase> T_pi = new LinkedList<>(T);
        List<ITestCase> T_alpha = new LinkedList<>();

        PerformanceEvaluator.start(TIMER_WIPEOUTR_T);
        // while Tπ != Φ do
        while (!T_pi.isEmpty()) {
            // tα ← first(Tπ)
            ITestCase t_alpha = T_pi.get(0);

            log.trace("{}[t_alpha = {}]", LoggerUtils.tab(), t_alpha);
            LoggerUtils.indent();

            // for all tγ ∈ T - T∆ − {tα} do
            List<ITestCase> newT = ListUtils.subtract(T, T_alpha);
            newT = ListUtils.subtract(newT, Collections.singletonList(t_alpha));

            newT.forEach(t_gamma -> {

                log.trace("{}[t_gamma = {}]", LoggerUtils.tab(), t_gamma);

                PerformanceEvaluator.incrementCounter(COUNTER_CONSISTENCY_CHECKS);
                // if inconsistent(tα ∧ ¬tγ) then
                if (!checker.isConsistent(t_alpha, t_gamma)) {
                    // T∆ ← T∆ ∪ {tγ}
                    T_alpha.add(t_gamma);
                    // Tπ ← Tπ − {tγ}
                    T_pi.remove(t_gamma);

                    log.trace("{}Redundancy detected: [t_alpha={}, t_gamma={}]", LoggerUtils.tab(), t_alpha, t_gamma);
                }
            });
            // Tπ ← Tπ − {tα}
            T_pi.remove(0);

            LoggerUtils.outdent();
        }
        stop(TIMER_WIPEOUTR_T);

        // return(T − T∆)
        List<ITestCase> non_redundant_T = ListUtils.subtract(T, T_alpha);

        LoggerUtils.outdent();
        log.debug("{}<<< Return non-redundant test cases [non_redundant_T={}]", LoggerUtils.tab(), non_redundant_T);

        return non_redundant_T;
    }
}
