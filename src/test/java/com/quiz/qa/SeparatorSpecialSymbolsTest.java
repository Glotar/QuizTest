package com.quiz.qa;

import com.quiz.qa.responseObjectModels.TriangleObject;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class SeparatorSpecialSymbolsTest<T> extends TestNGBaseTest {

    @DataProvider(name = "testData")
    public static Object[] dataForTest() {
        String separatorCollectionString = "\',\",\\,\t,\b,\r,\f,\n";
        List<String> separatorCollection = Arrays.asList(separatorCollectionString.split(","));
        return separatorCollection.toArray();
    }

    @AfterMethod
    public void clean() throws IOException {
        deleteAllTriangles();
    }

    @Test(dataProvider = "testData")
    public void separatorTest(String separator) throws IOException {
        TriangleObject expectedTriangle = checkPostTriangle(
                7,7,7,
                separator);
        assertEquals(expectedTriangle, getTriangle(expectedTriangle.getId()));
        assertTrue(getAllTriangles().contains(expectedTriangle));

        checkPerimeter(expectedTriangle);
        checkArea(expectedTriangle);
    }
}
