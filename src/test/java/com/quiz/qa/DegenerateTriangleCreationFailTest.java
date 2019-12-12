package com.quiz.qa;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@RunWith(Parameterized.class)
public class DegenerateTriangleCreationFailTest<T> extends TestNGBaseTest {
    private TrianglePostPayload payload;

    public DegenerateTriangleCreationFailTest(TrianglePostPayload payload) {
        this.payload = payload;
    }

    @Parameterized.Parameters(name = "{index}: Trying to post triangle: {0} ")
    public static List<TrianglePostPayload> dataForTest() {
        return Arrays.asList(
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
                new TrianglePostPayload(-0.0, 7, 7));
    }

    @Test
    public void degenerateTriangleCreationFailTest() throws IOException {
        checkCannotProcessInputError(
                payload.requestBody);
    }


}
