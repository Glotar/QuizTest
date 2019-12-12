package com.quiz.qa;

import com.quiz.qa.responseObjectModels.ErrorResponseObject;
import com.quiz.qa.responseObjectModels.TriangleObject;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.entity.ContentType;
import org.testng.annotations.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.quiz.qa.CommonStrings.*;
import static org.junit.Assert.*;


public class AccessTest extends TestNGBaseTest {

    public static List<TriangleObject> initialTriangles = new ArrayList<>();

    @BeforeClass
    public void beforeTest() throws IOException {
        initialTriangles= new ArrayList<>();
        initialTriangles.add(postTriangle(1, 1, 1));
        initialTriangles.add(postTriangle(10, 10, 10));
        initialTriangles.add(postTriangle(3, 4, 5));
        logger.info("initialTriangles("+initialTriangles.size()+"):\n" + getAllTriangles());
    }

    @Test
    public void accessMainFlowTest() throws IOException {
        String triangleId = initialTriangles.get(0).getId();
        HttpResponse httpResponse = getTriangleResponse(triangleId);
        assertEquals(HttpStatus.SC_OK, httpResponse.getStatusLine().getStatusCode());

        String mimeType = ContentType.getOrDefault(httpResponse.getEntity()).getMimeType();
        assertEquals("application/json", mimeType);

        TriangleObject triangle = RestHelpers.retrieveResourceFromResponse(httpResponse, TriangleObject.class);
        assertEquals(triangle.getId(), triangleId);
    }

    @Test
    public void accessMainFlowGetAllTrianglesTest() throws IOException {
        List<String> initialTriangleIds = initialTriangles
                .stream()
                .map(TriangleObject::getId)
                .collect(Collectors.toList());
        HttpResponse httpResponse = getAllTrianglesResponse();
        assertEquals(HttpStatus.SC_OK, httpResponse.getStatusLine().getStatusCode());

        String mimeType = ContentType.getOrDefault(httpResponse.getEntity()).getMimeType();
        assertEquals("application/json", mimeType);

        List<String> triangleIds = getTriangleIdListFromResponse(httpResponse);
        assertEquals(initialTriangles.size(), triangleIds.size());
        assertTrue(initialTriangleIds
                .containsAll(triangleIds));
    }

    @Test
    public void deleteWithoutAuthenticationTest() throws IOException {
        String triangleId = postTriangle(1, 1, 1).getId();

        String expectedPath = TRIANGLE_REQUEST_PREFIX + triangleId;

        Function<String, HttpResponse> performDeleteRequest = (id) -> {
            try {
                return deleteTriangle(id);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        };

        checkWrongAuthHeaderName(performDeleteRequest, expectedPath, triangleId);
        checkWrongAuthToken(performDeleteRequest, expectedPath, triangleId);
        checkMissingAuthHeader(performDeleteRequest, expectedPath, triangleId);
    }

    @Test
    public void getTriangleWithoutAuthenticationTest() throws IOException {
        String triangleId = postTriangle(1, 1, 1).getId();

        String expectedPath = TRIANGLE_REQUEST_PREFIX + triangleId;

        Function<String, HttpResponse> performGetRequest = (id) -> {
            try {
                return getTriangleResponse(id);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        };

        checkWrongAuthHeaderName(performGetRequest, expectedPath, triangleId);
        checkWrongAuthToken(performGetRequest, expectedPath, triangleId);
        checkMissingAuthHeader(performGetRequest, expectedPath, triangleId);
    }

    @Test
    public void getAllWithoutAuthenticationTest() throws IOException {
        postTriangle(1, 1, 1);
        postTriangle(3, 4, 5);

        String expectedPath = TRIANGLE_All;

        Function<String, HttpResponse> performGetRequest = (id) -> {
            try {
                return getAllTrianglesResponse();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        };

        checkWrongAuthHeaderName(performGetRequest, expectedPath, null);
        checkWrongAuthToken(performGetRequest, expectedPath, null);
        checkMissingAuthHeader(performGetRequest, expectedPath, null);
    }

    @Test
    public void getPerimeterWithoutAuthenticationTest() throws IOException {
        String triangleId = postTriangle(1, 1, 1).getId();

        String expectedPath = TRIANGLE_REQUEST_PREFIX + triangleId + PERIMETER_POSTFIX;

        Function<String, HttpResponse> performGetRequest = (id) -> {
            try {
                return getTrianglePerimeterResponse(id);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        };

        checkWrongAuthHeaderName(performGetRequest, expectedPath, triangleId);
        checkWrongAuthToken(performGetRequest, expectedPath, triangleId);
        checkMissingAuthHeader(performGetRequest, expectedPath, triangleId);
    }

    @Test
    public void getAreaWithoutAuthenticationTest() throws IOException {
        String triangleId = postTriangle(1, 1, 1).getId();

        String expectedPath = TRIANGLE_REQUEST_PREFIX + triangleId + AREA_POSTFIX;

        Function<String, HttpResponse> performGetRequest = (id) -> {
            try {
                return getTriangleAreaResponse(id);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        };

        checkWrongAuthHeaderName(performGetRequest, expectedPath, triangleId);
        checkWrongAuthToken(performGetRequest, expectedPath, triangleId);
        checkMissingAuthHeader(performGetRequest, expectedPath, triangleId);
    }

    private void checkWrongAuthHeaderName(Function<String, HttpResponse> function, String expectedPath, String id) throws IOException {
        RestHelpers.setAuthenticationOverride("Y-User", null, true);
        checkAuthError(function.apply(id), expectedPath);
    }

    private void checkWrongAuthToken(Function<String, HttpResponse> function, String expectedPath, String id) throws IOException {
        RestHelpers.setAuthenticationOverride(null, "fake_token", true);
        checkAuthError(function.apply(id), expectedPath);
    }

    private void checkMissingAuthHeader(Function<String, HttpResponse> function, String expectedPath, String id) throws IOException {
        RestHelpers.setAuthenticationOverride(null, null, false);
        checkAuthError(function.apply(id), expectedPath);
    }

    private void checkAuthError(HttpResponse response, String expectedPath) throws IOException {
        ErrorResponseObject authError = RestHelpers.retrieveResourceFromResponse(response, ErrorResponseObject.class);
        assertEquals(HttpStatus.SC_UNAUTHORIZED, authError.getStatus());
        assertEquals("Unauthorized", authError.getError());
        assertNull(authError.getException());
        assertEquals("No message available", authError.getMessage());
        assertEquals(expectedPath, authError.getPath());
    }

}
