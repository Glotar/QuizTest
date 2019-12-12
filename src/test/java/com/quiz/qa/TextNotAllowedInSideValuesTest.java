package com.quiz.qa;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.IOException;

public class TextNotAllowedInSideValuesTest<T> extends TestNGBaseTest {

    @AfterMethod
    public void clean() throws IOException {
        deleteAllTriangles();
    }

    @DataProvider(name = "testData")
    public static Object[] dataForTest() {
        return new Object[]{
                new TrianglePostPayload("text", "text", "text"),
                new TrianglePostPayload("0b10", "0b10", "0b10"),
                new TrianglePostPayload("0xff", "0xff", "0xff"),
                new TrianglePostPayload("1L", "1L", "1L"),
                new TrianglePostPayload("1f", "1f", "1f"),
                new TrianglePostPayload("1f", "1", "1"),
                new TrianglePostPayload("1", "1f", "1"),
                new TrianglePostPayload("1", "1", "1f"),
                new TrianglePostPayload("1d", "1d", "1d"),
                new TrianglePostPayload("1d", "1", "1"),
                new TrianglePostPayload("1", "1d", "1"),
                new TrianglePostPayload("1", "1", "1d"),
                new TrianglePostPayload(Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY),
                new TrianglePostPayload(Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY)};
    }

    @Test(dataProvider = "testData")
    public void textNotAllowedInSideValuesTest(TrianglePostPayload payload) throws IOException {
        checkCannotProcessInputError(
                payload.requestBody);
    }
}
