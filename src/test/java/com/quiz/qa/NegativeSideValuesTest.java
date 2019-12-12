package com.quiz.qa;

import com.quiz.qa.responseObjectModels.TriangleObject;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class NegativeSideValuesTest<T> extends TestNGBaseTest {

    @AfterMethod
    public void clean() throws IOException {
        deleteAllTriangles();
    }

    @DataProvider(name = "testData")
    public static Object[] dataForTest() {
        return new Object[]{
                new TrianglePostPayload(-5, -6, -7),
                new TrianglePostPayload(-2.1, -3.2, -4.3),
                new TrianglePostPayload(-2, 3, 4),
                new TrianglePostPayload(3, -4, 5),
                new TrianglePostPayload(3, 4, -5),
                new TrianglePostPayload(-2.1, 3.2, 4.3),
                new TrianglePostPayload(2.1, -3.2, 4.3),
                new TrianglePostPayload(2.1, 3.2, -4.3),
                new TrianglePostPayload(-2, -3, 4),
                new TrianglePostPayload(3, -4, -5),
                new TrianglePostPayload(-3, 4, -5),
                new TrianglePostPayload(-2.1, -3.2, 4.3),
                new TrianglePostPayload(2.1, -3.2, -4.3),
                new TrianglePostPayload(-2.1, 3.2, -4.3)};
    }

    @Test(dataProvider = "testData")
    public void negativeSideValuesTest(TrianglePostPayload payload) throws IOException {
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
