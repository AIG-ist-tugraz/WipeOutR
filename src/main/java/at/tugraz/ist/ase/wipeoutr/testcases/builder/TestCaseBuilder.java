/*
 * WipeOut: Automated Redundancy Detection in Feature Models
 *
 * Copyright (c) 2022-2022
 *
 * @author: Viet-Man Le (vietman.le@ist.tugraz.at)
 */

package at.tugraz.ist.ase.wipeoutr.testcases.builder;

import at.tugraz.ist.ase.common.LoggerUtils;
import at.tugraz.ist.ase.test.Assignment;
import at.tugraz.ist.ase.test.TestCase;
import at.tugraz.ist.ase.test.builder.ITestCaseBuildable;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import java.util.LinkedList;
import java.util.List;

/***
 * TestCaseBuilder for PC or Renault
 * Maybe don't use this class anymore
 */
@Slf4j
public class TestCaseBuilder implements ITestCaseBuildable {
    public TestCase buildTestCase(@NonNull String testcase) {
        log.trace("{}Building test case [testcase={}] >>>", LoggerUtils.tab(), testcase);
        LoggerUtils.indent();

        List<Assignment> assignments = splitTestCase(testcase);

        TestCase testCase = TestCase.builder()
                .testcase(testcase)
                .assignments(assignments)
                .build();

        LoggerUtils.outdent();
        log.debug("{}<<< Built test case [testcase={}]", LoggerUtils.tab(), testCase);

        return testCase;
    }

    private List<Assignment> splitTestCase(String testcase) {
        List<Assignment> assignments = new LinkedList<>();
        String[] clauses = testcase.split(" & ");

        for (String clause: clauses) {
            String variable;
            String value;

            String[] items = clause.split("=");

            variable = items[0];
            value = items[1];

            Assignment assignment = Assignment.builder()
                    .variable(variable)
                    .value(value)
                    .build();

            assignments.add(assignment);

            log.trace("{}Parsed assignment [clause={}, assignment={}]", LoggerUtils.tab(), clause, assignment);
        }
        return assignments;
    }
}
