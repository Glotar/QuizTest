package com.quiz.qa;

import com.quiz.qa.responseObjectModels.ErrorResponseObject;
import org.apache.http.HttpResponse;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterTest;
import org.testng.annotations.Test;

import java.io.IOException;

import static com.quiz.qa.CommonStrings.*;
import static org.junit.Assert.assertEquals;

public class PostTriangleSingleTests extends TestNGBaseTest {

    @AfterMethod
    public void clean() throws IOException {
        deleteAllTriangles();
    }

    @Test
    public void emptySeparatorTest() throws IOException {
        checkCannotProcessInputError(1, 1, 1, "");
    }

    @Test
    public void dotSeparatorTest() throws IOException {
        checkCannotProcessInputError(1, 1, 1, ".");
    }

    @Test
    public void moreThanThreeValuesTest() throws IOException {
        checkCannotProcessInputError("{\"input\":\"5;6;7;8\"}");
    }

    @Test
    public void maxTrianglesStorageTest() throws IOException {
        int storageLimit = 10;
        for (int i = 0; i < storageLimit; i++) {
            postTriangle(4, 5, 6);
        }
        assertEquals(storageLimit, getAllTriangles().size());
        checkLimitExceededError(9, 8, 7);
        assertEquals(storageLimit, getAllTriangles().size());
        checkLimitExceededError(6, 5, 4);
        assertEquals(storageLimit, getAllTriangles().size());
    }

    @Test
    public void getAreaFromNonExistentTriangleTest() throws IOException {
        String fakeTriangleId = "fakeId";
        HttpResponse response = getTriangleAreaResponse(fakeTriangleId);
        ErrorResponseObject notFoundError = RestHelpers.retrieveResourceFromResponse(response, ErrorResponseObject.class);
        checkNotFoundError(notFoundError, TRIANGLE_REQUEST_PREFIX + fakeTriangleId + AREA_POSTFIX);
    }

    @Test
    public void getAreaFromDeletedTriangleTest() throws IOException {
        String deletedTriangleId = postTriangle(9, 8, 7).getId();
        deleteTriangle(deletedTriangleId);
        HttpResponse response = getTriangleAreaResponse(deletedTriangleId);
        ErrorResponseObject notFoundError = RestHelpers.retrieveResourceFromResponse(response, ErrorResponseObject.class);
        checkNotFoundError(notFoundError, TRIANGLE_REQUEST_PREFIX + deletedTriangleId + AREA_POSTFIX);
    }

    @Test
    public void getPerimeterFromNonExistentTriangleTest() throws IOException {
        String fakeTriangleId = "fakeId";
        HttpResponse response = getTrianglePerimeterResponse(fakeTriangleId);
        ErrorResponseObject notFoundError = RestHelpers.retrieveResourceFromResponse(response, ErrorResponseObject.class);
        checkNotFoundError(notFoundError, TRIANGLE_REQUEST_PREFIX + fakeTriangleId + PERIMETER_POSTFIX);
    }

    @Test
    public void getPerimeterFromDeletedTriangleTest() throws IOException {
        String deletedTriangleId = postTriangle(9, 8, 7).getId();
        deleteTriangle(deletedTriangleId);
        HttpResponse response = getTrianglePerimeterResponse(deletedTriangleId);
        ErrorResponseObject notFoundError = RestHelpers.retrieveResourceFromResponse(response, ErrorResponseObject.class);
        checkNotFoundError(notFoundError, TRIANGLE_REQUEST_PREFIX + deletedTriangleId + PERIMETER_POSTFIX);
    }
}
