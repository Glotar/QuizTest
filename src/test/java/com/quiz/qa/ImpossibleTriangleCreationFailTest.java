package com.quiz.qa;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@RunWith(Parameterized.class)
public class ImpossibleTriangleCreationFailTest<T> extends TestNGBaseTest {
    private TrianglePostPayload payload;

    public ImpossibleTriangleCreationFailTest(TrianglePostPayload payload) {
        this.payload = payload;
    }

    @Parameterized.Parameters(name = "{index}: Trying to post triangle: {0} ")
    public static List<TrianglePostPayload> dataForTest() {
        return Arrays.asList(
                new TrianglePostPayload(1, 2, 10),
                new TrianglePostPayload(1, 16, 3),
                new TrianglePostPayload(45, 2, 2),
                new TrianglePostPayload(1.1, 2.1, 10.1));
    }

    @Test
    public void impossibleTriangleCreationFailTest() throws IOException {
        checkCannotProcessInputError(
                payload.firstSide,
                payload.secondSide,
                payload.thirdSide,
                payload.separator);
    }
}
