package com.quiz.qa;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.IOException;

public class ImpossibleTriangleCreationFailTest<T> extends TestNGBaseTest {

    @AfterMethod
    public void clean() throws IOException {
        deleteAllTriangles();
    }

    @DataProvider(name = "testData")
    public static Object[] dataForTest() {
        return new Object[]{
                new TrianglePostPayload(1, 2, 10),
                new TrianglePostPayload(1, 16, 3),
                new TrianglePostPayload(45, 2, 2),
                new TrianglePostPayload(1.1, 2.1, 10.1)};
    }

    @Test(dataProvider = "testData")
    public void impossibleTriangleCreationFailTest(TrianglePostPayload payload) throws IOException {
        checkCannotProcessInputError(
                payload.firstSide,
                payload.secondSide,
                payload.thirdSide,
                payload.separator);
    }
}
