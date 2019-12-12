package com.quiz.qa;

import com.quiz.qa.responseObjectModels.TriangleObject;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.List;

import static org.junit.Assert.*;


public class SmokeTest extends TestNGBaseTest {
    @Test
    public void smokeTest() throws IOException {
        TriangleObject triangle1 = postTriangle(4, 5, 6);

        assertEquals(Double.valueOf(4), triangle1.getFirstSide());
        assertEquals(Double.valueOf(5), triangle1.getSecondSide());
        assertEquals(Double.valueOf(6), triangle1.getThirdSide());

        TriangleObject triangle2 = postTriangle(3, 4, 5);

        TriangleObject storedTriangle = getTriangle(triangle1.getId());
        assertEquals(triangle1, storedTriangle);

        List<TriangleObject> trianglesList = getAllTriangles();
        assertEquals(2, trianglesList.size());
        assertTrue(trianglesList.contains(triangle1));
        assertTrue(trianglesList.contains(triangle2));

        deleteTriangle(triangle1.getId());
        trianglesList = getAllTriangles();
        assertEquals(1, trianglesList.size());
        assertFalse(trianglesList.contains(triangle1));
        assertTrue(trianglesList.contains(triangle2));

        double perimeter = getTrianglePerimeter(triangle2.getId());
        assertEquals(12.0, perimeter, 0.0);

        double area = getTriangleArea(triangle2.getId());
        assertEquals(6.0, area, 0.0);
    }
}
