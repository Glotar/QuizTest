package com.quiz.qa;

import com.google.gson.JsonObject;
import com.quiz.qa.responseObjectModels.TriangleObject;
import com.quiz.qa.responseObjectModels.ValueResponseObject;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.log4j.Logger;
import org.junit.After;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import static com.quiz.qa.CommonStrings.*;
import static com.quiz.qa.Config.getEnvUrl;
import static com.quiz.qa.RequestTypes.*;

public abstract class BaseTest {
    private static final Logger logger = Logger.getLogger(BaseTest.class);

    @After
    public void clearAllTriangles() throws IOException {
        deleteAllTriangles();
    }

    public static String getTriangleIdFromResponse(HttpResponse response) throws IOException {
        return RestHelpers.retrieveResourceFromResponse(response, TriangleObject.class).getId();
    }

    static List<String> getTriangleIdListFromResponse(HttpResponse response) throws IOException {
        return RestHelpers.retrieveResourceListFromResponse(response, TriangleObject.class)
                .stream()
                .map(TriangleObject::getId)
                .collect(Collectors.toList());
    }

    static HttpResponse getTriangleResponse(String triangleId) throws IOException {
        return RestHelpers.executeRequestAndGetResponse(
                GET,
                getEnvUrl() + TRIANGLE_REQUEST_PREFIX + triangleId);
    }

    static HttpResponse getAllTrianglesResponse() throws IOException {
        return RestHelpers.executeRequestAndGetResponse(
                GET,
                getEnvUrl() + TRIANGLE_All);
    }

    static HttpResponse getPublishTriangleResponse(String triangleRequestBody) throws IOException {
        return RestHelpers.executeRequestAndGetResponse(
                POST,
                getEnvUrl() + TRIANGLE_REQUEST_PREFIX,
                triangleRequestBody);
    }

    public static HttpResponse getTrianglePerimeterResponse(String triangleId) throws IOException {
        return RestHelpers.executeRequestAndGetResponse(
                GET,
                getEnvUrl() + TRIANGLE_REQUEST_PREFIX + triangleId + PERIMETER_POSTFIX);
    }


    public static HttpResponse getTriangleAreaResponse(String triangleId) throws IOException {
        return RestHelpers.executeRequestAndGetResponse(
                GET,
                getEnvUrl() + TRIANGLE_REQUEST_PREFIX + triangleId + AREA_POSTFIX);
    }

    static <T> TriangleObject postTriangle(T firstSide, T secondSide, T thirdSide) throws IOException {
        return postTriangle(firstSide, secondSide, thirdSide, null);
    }

    static <T> TriangleObject postTriangle(T firstSide, T secondSide, T thirdSide, String separator) throws IOException {
        HttpResponse response = getPublishTriangleResponse(firstSide, secondSide, thirdSide, separator);
        return RestHelpers.retrieveResourceFromResponse(response, TriangleObject.class);
    }

    static <T> HttpResponse getPublishTriangleResponse(T firstSide, T secondSide, T thirdSide, String separator) throws IOException {
        return getPublishTriangleResponse(
                getTriangleRequestBody(firstSide, secondSide, thirdSide, separator));
    }

    public static <T> String getTriangleRequestBody(T firstSide, T secondSide, T thirdSide) {
        return getTriangleRequestBody(firstSide, secondSide, thirdSide, null);
    }

    protected static <T> String getTriangleRequestBody(T firstSide, T secondSide, T thirdSide, String separator) {
        JsonObject triangleRequestJson = new JsonObject();
        if (separator != null) {
            triangleRequestJson.addProperty(SEPARATOR_FIELD_NAME, separator);
        } else separator = SEPARATOR_DEFAULT;
        String inputString = firstSide + separator + secondSide + separator + thirdSide;
        triangleRequestJson.addProperty(INPUT_FIELD_NAME, inputString);
        logger.info("Generated triangle request body: " + triangleRequestJson);
        return triangleRequestJson.toString();
    }

    static HttpResponse deleteTriangle(String triangleId) throws IOException {
        return RestHelpers.executeRequestAndGetResponse(DELETE, getEnvUrl() + TRIANGLE_REQUEST_PREFIX + triangleId);
    }

    static HttpResponse deleteTriangle(TriangleObject triangle) throws IOException {
        return deleteTriangle(triangle.getId());
    }

    static void deleteAllTriangles() throws IOException {
        List<TriangleObject> triangles = getAllTriangles();
        if (triangles.size() == 0) {
            logger.info("No triangle objects found for deletion.");
            return;
        }
        StringBuilder logMessage = new StringBuilder("Going to delete all triangle objects (" + triangles.size() + "): ");
        triangles.forEach(triangle -> {
            logMessage.append("\n").append(triangle.getId());
        });
        logger.info(logMessage.toString());
        triangles.forEach(triangle -> {
            try {
                deleteTriangle(triangle.getId());
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        logger.info("All triangle objects were deleted.");
    }

    public static List<TriangleObject> getAllTriangles() throws IOException {
        return RestHelpers.retrieveResourceListFromResponse(getAllTrianglesResponse(), TriangleObject.class);
    }

    public static TriangleObject getTriangle(String triangleId) throws IOException {
        HttpResponse response = getTriangleResponse(triangleId);
        if (response.getStatusLine().getStatusCode() == HttpStatus.SC_NOT_FOUND) {
            return null;
        } else
            return RestHelpers.retrieveResourceFromResponse(response, TriangleObject.class);
    }

    static Double getTrianglePerimeter(String triangleId) throws IOException {
        HttpResponse response = getTrianglePerimeterResponse(triangleId);
        return RestHelpers.retrieveResourceFromResponse(response, ValueResponseObject.class).getResult();
    }

    static Double getTriangleArea(String triangleId) throws IOException {
        HttpResponse response = getTriangleAreaResponse(triangleId);
        return RestHelpers.retrieveResourceFromResponse(response, ValueResponseObject.class).getResult();
    }
}
