package com.quiz.qa;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;
import org.testng.annotations.DataProvider;

import java.io.IOException;

public class DegenerateTriangleCreationFailTest<T> extends TestNGBaseTest {

    @AfterMethod
    public void clean() throws IOException {
        deleteAllTriangles();
    }

    @DataProvider(name="testData")
    public static Object[] dataForTest() {
        return new Object[]{
                new TrianglePostPayload(1, 2, 3),
                new TrianglePostPayload(1, 2, 3),
                new TrianglePostPayload(1.1, 2.2, 3.3),
                new TrianglePostPayload(0, 0, 0),
                new TrianglePostPayload(0.0, 0.0, 0.0),
                new TrianglePostPayload(-0.0, -0.0, -0.0),
                new TrianglePostPayload(7, 7, 0),
                new TrianglePostPayload(7, 0, 7),
                new TrianglePostPayload(0, 7, 7),
                new TrianglePostPayload(7, 7, 0.0),
                new TrianglePostPayload(7, 0.0, 7),
                new TrianglePostPayload(0.0, 7, 7),
                new TrianglePostPayload(7, 7, -0.0),
                new TrianglePostPayload(7, -0.0, 7),
                new TrianglePostPayload(-0.0, 7, 7)};
    }

    @Test(dataProvider = "testData")
    public void degenerateTriangleCreationFailTest(TrianglePostPayload payload) throws IOException {
        checkCannotProcessInputError(
                payload.requestBody);
    }


}
