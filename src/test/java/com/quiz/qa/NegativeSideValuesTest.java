package com.quiz.qa;

import com.quiz.qa.responseObjectModels.TriangleObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(Parameterized.class)
public class NegativeSideValuesTest<T> extends TestNGBaseTest {
    private TrianglePostPayload payload;

    public NegativeSideValuesTest(TrianglePostPayload payload) {
        this.payload = payload;
    }

    @Parameterized.Parameters(name = "{index}: Trying to post triangle: {0} ")
    public static List<TrianglePostPayload> dataForTest() {
        return Arrays.asList(
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
                new TrianglePostPayload(-2.1, 3.2, -4.3));
    }

    @Test
    public void negativeSideValuesTest() throws IOException {
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
