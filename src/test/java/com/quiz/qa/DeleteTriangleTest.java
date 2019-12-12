package com.quiz.qa;

import com.quiz.qa.responseObjectModels.ErrorResponseObject;
import com.quiz.qa.responseObjectModels.TriangleObject;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.entity.ContentType;
import org.junit.Assert;
import org.testng.annotations.Test;

import java.io.IOException;

import static com.quiz.qa.CommonStrings.TRIANGLE_REQUEST_PREFIX;
import static org.junit.Assert.*;

public class DeleteTriangleTest extends TestNGBaseTest {
    @Test
    public void deleteTrianglePositiveTest() throws IOException {
        TriangleObject triangle = postTriangle(1, 1, 1);
        String triangleId = triangle.getId();
        TriangleObject storedTriangle = getTriangle(triangleId);
        Assert.assertEquals(triangle, storedTriangle);

        HttpResponse deleteResponse = deleteTriangle(triangleId);
        assertEquals(HttpStatus.SC_OK, deleteResponse.getStatusLine().getStatusCode());

        String mimeType = ContentType.getOrDefault(deleteResponse.getEntity()).getMimeType();
        assertEquals("text/plain", mimeType);

        assertNull(getTriangle(triangleId));
        assertFalse(getAllTriangles().contains(triangle));
    }

    @Test
    public void deleteNonExistentTriangle() throws IOException {
        String triangleId = "fakeId";
        HttpResponse deleteResponse = deleteTriangle(triangleId);
        assertEquals(HttpStatus.SC_NOT_FOUND, deleteResponse.getStatusLine().getStatusCode());
        ErrorResponseObject notFoundError = RestHelpers.retrieveResourceFromResponse(deleteResponse, ErrorResponseObject.class);
        checkNotFoundError(notFoundError, TRIANGLE_REQUEST_PREFIX + triangleId);
    }

    @Test
    public void deleteTriangleTwice() throws IOException {
        TriangleObject triangle = postTriangle(1, 1, 1);
        String triangleId = triangle.getId();
        HttpResponse deleteResponse = deleteTriangle(triangleId);
        assertEquals(HttpStatus.SC_OK, deleteResponse.getStatusLine().getStatusCode());
        assertNull(getTriangle(triangleId));
        deleteResponse = deleteTriangle(triangleId);
        assertEquals(HttpStatus.SC_NOT_FOUND, deleteResponse.getStatusLine().getStatusCode());
        ErrorResponseObject notFoundError = RestHelpers.retrieveResourceFromResponse(deleteResponse, ErrorResponseObject.class);
        checkNotFoundError(notFoundError, TRIANGLE_REQUEST_PREFIX + triangleId);
    }
}
