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
public class SideValuesCornerCasesTest<T> extends TestNGBaseTest {
    private TrianglePostPayload payload;

    public SideValuesCornerCasesTest(TrianglePostPayload payload) {
        this.payload = payload;
    }

    @Parameterized.Parameters(name = "{index}: Trying to post triangle: {0} ")
    public static List<TrianglePostPayload> dataForTest() {
        return Arrays.asList(
                new TrianglePostPayload(Double.MAX_VALUE, Double.MAX_VALUE, Double.MAX_VALUE),
                new TrianglePostPayload(Double.MIN_VALUE, Double.MIN_VALUE, Double.MIN_VALUE),
                new TrianglePostPayload(Long.MAX_VALUE, Long.MAX_VALUE, Long.MAX_VALUE),
                new TrianglePostPayload(Long.MIN_VALUE, Long.MIN_VALUE, Long.MIN_VALUE));
    }

    @Test
    public void sideValuesCornerCasesTest() throws IOException {
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
