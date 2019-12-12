package com.quiz.qa;

import com.quiz.qa.TestNGBaseTest;
import com.quiz.qa.TrianglePostPayload;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@RunWith(Parameterized.class)
public class TextNotAllowedInSideValuesTest<T> extends TestNGBaseTest {
    private TrianglePostPayload payload;

    public TextNotAllowedInSideValuesTest(TrianglePostPayload payload){
        this.payload=payload;
    }

    @Parameterized.Parameters(name = "{index}: Trying to post triangle: {0} ")
    public static List<TrianglePostPayload> dataForTest() {
        return Arrays.asList(
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
                new TrianglePostPayload(Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY));
    }

    @Test
    public void textNotAllowedInSideValuesTest() throws IOException {
        checkCannotProcessInputError(
                payload.requestBody);
    }
}
