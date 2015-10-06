package pt.isel.pdm.g04.se2_1;

import android.test.suitebuilder.annotation.SmallTest;

import org.junit.Test;

import pt.isel.pdm.g04.se2_1.helpers.HgDefs;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;
import static pt.isel.pdm.g04.se2_1.helpers.HgHttp.slashEnd;
import static pt.isel.pdm.g04.se2_1.helpers.HgHttp.unslashEnd;
import static pt.isel.pdm.g04.se2_1.helpers.HgUtil.generateRandom;

/**
 * <a href="http://d.android.com/tools/testing/testing_android.html">Testing Fundamentals</a>
 */
public class ApplicationTest {

    @Test
    public void testName() throws Exception {
        assertEquals("/", slashEnd(""));
        assertEquals("/", slashEnd("/"));
        assertEquals("test/", slashEnd("test"));
        assertEquals("test/", slashEnd("test/"));
    }

    @SmallTest
    public void generateRandomTest() {
        assertTrue(generateRandom() <= 1);
    }

    @SmallTest
    public void generateRandomRangeTest() {
        assertTrue(generateRandom(HgDefs.SEC) <= 1);
        assertTrue(generateRandom(2 * HgDefs.SEC) <= 2000);
    }

    @SmallTest
    public void slashEndTest() {
        assertEquals("/", slashEnd(""));
        assertEquals("/", slashEnd("/"));
        assertEquals("test", slashEnd("test/"));
        assertEquals("test/", slashEnd("test/"));
    }

    public void testUnslashEnd() {
        assertEquals("/", unslashEnd(""));
        assertEquals("/", unslashEnd("/"));
        assertEquals("est", unslashEnd("test/"));
        assertEquals("test/", unslashEnd("test/"));
    }
}