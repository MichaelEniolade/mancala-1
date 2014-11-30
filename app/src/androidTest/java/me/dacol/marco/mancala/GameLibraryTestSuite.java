package me.dacol.marco.mancala;

import android.test.suitebuilder.TestSuiteBuilder;

import junit.framework.Test;
import junit.framework.TestSuite;



public class GameLibraryTestSuite {
    public static Test suite() {
        TestSuite testSuiteBuilder = new TestSuiteBuilder(GameLibraryTestSuite.class)
                .includeAllPackagesUnderHere()
                .build();
        return testSuiteBuilder;
    }

    public GameLibraryTestSuite() {
        super();
    }
}
