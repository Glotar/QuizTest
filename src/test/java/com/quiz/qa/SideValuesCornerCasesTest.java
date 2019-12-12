package com.quiz.qa;

import com.quiz.qa.responseObjectModels.TriangleObject;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class SideValuesCornerCasesTest<T> extends TestNGBaseTest {

    @AfterMethod
    public void clean() throws IOException {
        deleteAllTriangles();
    }

    @DataProvider(name = "testData")
    public static Object[] dataForTest() {
        return new Object[]{
                new TrianglePostPayload(Double.MAX_VALUE, Double.MAX_VALUE, Double.MAX_VALUE),
                new TrianglePostPayload(Double.MIN_VALUE, Double.MIN_VALUE, Double.MIN_VALUE),
                new TrianglePostPayload(Long.MAX_VALUE, Long.MAX_VALUE, Long.MAX_VALUE),
                new TrianglePostPayload(Long.MIN_VALUE, Long.MIN_VALUE, Long.MIN_VALUE)};
    }

    @Test(dataProvider = "testData")
    public void sideValuesCornerCasesTest(TrianglePostPayload payload) throws IOException {
        TriangleObject expectedTriangle = checkPostTriangle(
                payload.firstSide,
                payload.secondSide,
                payload.thirdSide,
                payload.separator);
        assertEquals(expectedTriangle, getTriangle(expectedTriangle.getId()));
        assertTrue(getAllTriangles().contains(expectedTriangle));

        checkPerimeter(expectedTriangle);
        checkArea(expectedTriangle);
    }
}
