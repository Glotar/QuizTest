package com.quiz.qa;

import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;
import com.quiz.qa.responseObjectModels.ErrorResponseObject;
import com.quiz.qa.responseObjectModels.TriangleObject;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.log4j.Logger;
import org.junit.Assert;
import org.testng.ITestResult;
import org.testng.annotations.*;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import static com.quiz.qa.CommonStrings.TRIANGLE_REQUEST_PREFIX;
import static com.quiz.qa.Config.getEnvPropertyAsString;
import static com.quiz.qa.Config.inputStreamToString;
import static org.junit.Assert.assertEquals;

@Listeners({SuiteListener.class})
public abstract class TestNGBaseTest extends BaseTest {
    protected static final Logger logger = Logger.getLogger(TestNGBaseTest.class);
    protected Map<String, Object> vars = new HashMap<String, Object>();
    private static String CONFIG = System.getenv("CONFIG") == null || System.getenv("CONFIG").equals("")
            ? null : System.getenv("CONFIG");
    private static final String CONFIG_PATH = System.getenv("CONFIG_PATH") != null ? System.getenv("CONFIG_PATH") + "/" : "configs/";
    private static final String CONFIG_SECTION = "CONFIG_SECTION";

    static {
        try {
            System.out.println(System.getenv());
            if (CONFIG != null) {
                Config.load(CONFIG);
            } else {
                try {
                    CONFIG = CONFIG_PATH + getEnvPropertyAsString("CONFIG_FILE", "config_common.json");
                    logger.info("Retrieving config file from: " + CONFIG);
                    Config.load(inputStreamToString(TestNGBaseTest.class.getClassLoader().getResourceAsStream(CONFIG)));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            logger.info("Opened file " + CONFIG);
        } catch (IOException e) {
            throw new RuntimeException("Can't load config file '" + CONFIG + "'");
        } catch (RuntimeException e) {
            logger.error("Exception at static section of 'TestNGBaseTest'", e.getCause());
            e.printStackTrace();
            throw e;
        }
    }

    @BeforeMethod(groups = {"smoke", "regression", "regressionWeekly"})
    protected void test(Method method) {
        logger.info("");
        logger.info("Case started " + this.getClass().getName() + "." + method.getName() + ": " + method.getAnnotation(Test.class).description());
    }

    @AfterMethod(groups = {"smoke", "regression", "regressionWeekly"})
    protected void logStatusOfTestMethod(ITestResult testResult) {
        String status;
        String message = "";
        switch (testResult.getStatus()) {
            case ITestResult.SUCCESS:
                status = "SUCCESS";
                break;
            case ITestResult.FAILURE:
                status = "FAILURE";
                if (testResult.getThrowable() != null) {
                    message = testResult.getThrowable().getMessage();
                }
                break;
            case ITestResult.SKIP:
                status = "SKIP";
                if (testResult.getThrowable() != null) {
                    message = testResult.getThrowable().getMessage();
                }
                break;
            case ITestResult.CREATED:
                status = "CREATED (not run)";
                break;
            default:
                status = String.valueOf(testResult.getStatus());
        }
        logger.info("Case " + testResult.getName() + " finished with status " + status + " in " + getTestDuration(testResult) + " seconds.\r\n" + message);
    }

    private Long getTestDuration(ITestResult testResult) {
        return (testResult.getEndMillis() - testResult.getStartMillis()) / 1000;
    }

    @BeforeTest
    protected void initEnvironment() {
        logger.info("InitEnvironment...");

        String newConfigSection = java.util.Optional.ofNullable(System.getenv(CONFIG_SECTION)).orElse("default");
        logger.info("Using '" + newConfigSection + "' config section from environment variable");
        Config.setConfigSection(newConfigSection);
    }

    @BeforeClass
    public static void beforeTests() throws IOException {
        RestHelpers.disableAuthenticationOverride();
        deleteAllTriangles();
    }

    @AfterClass
    public static void afterTests() throws IOException {
        RestHelpers.disableAuthenticationOverride();
        deleteAllTriangles();
    }

    <T> void checkCannotProcessInputError(T firstSide, T secondSide, T thirdSide) throws IOException {
        checkCannotProcessInputError(firstSide, secondSide, thirdSide, null);
    }

    public <T> void checkCannotProcessInputError(T firstSide, T secondSide, T thirdSide, String separator) throws IOException {
        checkCannotProcessInputError(firstSide, secondSide, thirdSide, separator, null);
    }

    public void checkCannotProcessInputError(String body) throws IOException {
        checkCannotProcessInputError(null, null, null, null, body);
    }

    private <T> void checkCannotProcessInputError(T firstSide, T secondSide, T thirdSide, String separator, String requestBody) throws IOException {
        String expectedMessage = "Cannot process input";
        ErrorResponseObject unprocessableEntryError = checkPostTriangleError(firstSide, secondSide, thirdSide, separator, requestBody, expectedMessage);

        assertEquals(HttpStatus.SC_UNPROCESSABLE_ENTITY, unprocessableEntryError.getStatus());
        assertEquals("Unprocessable Entity", unprocessableEntryError.getError());
        assertEquals("com.natera.test.triangle.exception.UnprocessableDataException", unprocessableEntryError.getException());
        assertEquals(expectedMessage, unprocessableEntryError.getMessage());
        assertEquals(TRIANGLE_REQUEST_PREFIX, unprocessableEntryError.getPath());
    }

    protected <T> void checkLimitExceededError(T firstSide, T secondSide, T thirdSide) throws IOException {
        String expectedMessage = "Limit exceeded";
        ErrorResponseObject unprocessableEntryError = checkPostTriangleError(firstSide, secondSide, thirdSide, null, null, expectedMessage);

        assertEquals(HttpStatus.SC_UNPROCESSABLE_ENTITY, unprocessableEntryError.getStatus());
        assertEquals("Unprocessable Entity", unprocessableEntryError.getError());
        assertEquals("com.natera.test.triangle.exception.LimitExceededException", unprocessableEntryError.getException());
        assertEquals(expectedMessage, unprocessableEntryError.getMessage());
        assertEquals(TRIANGLE_REQUEST_PREFIX, unprocessableEntryError.getPath());
    }

    protected <T> ErrorResponseObject checkPostTriangleError(T firstSide, T secondSide, T thirdSide, String separator, String requestBody, String expectedMessage) throws IOException {
        requestBody = (requestBody == null) ? getTriangleRequestBody(firstSide, secondSide, thirdSide, separator) : requestBody;
        HttpResponse response = getPublishTriangleResponse(requestBody);
        try {
            return RestHelpers.retrieveResourceFromResponse(response, ErrorResponseObject.class);
        } catch (RestHelpers.ResponseObjectParsingException e) {
            parseTriangleAndFailIfSuccessful(e, requestBody, expectedMessage);
        }
        return null;
    }

    private void parseTriangleAndFailIfSuccessful(RestHelpers.ResponseObjectParsingException e, String requestBody, String expectedMessage) throws IOException {
        try {
            TriangleObject triangle = RestHelpers.retrieveResourceFromString(e.getJsonResponseString(), TriangleObject.class);
            String errorMessage = "An \"Unprocessable Entity - " + expectedMessage + "\" error message was expected for the following input:\n" +
                    requestBody +
                    "\nTriangle was created instead: \n" + triangle;
            deleteTriangle(triangle);
            Assert.fail(errorMessage);
        } catch (UnrecognizedPropertyException e2) {
            throw e.getPayloadException();
        }
    }

    protected <T> TriangleObject checkPostTriangle(T firstSide, T secondSide, T thirdSide) throws IOException {
        return checkPostTriangle(firstSide, secondSide, thirdSide, null);
    }

    protected <T> TriangleObject checkPostTriangle(T firstSide, T secondSide, T thirdSide, String separator) throws IOException {
        TriangleObject triangle = postTriangle(firstSide, secondSide, thirdSide, separator);

        assertEquals(getAbsDouble(firstSide), triangle.getFirstSide());
        assertEquals(getAbsDouble(secondSide), triangle.getSecondSide());
        assertEquals(getAbsDouble(thirdSide), triangle.getThirdSide());
        Assert.assertTrue(isUUID(triangle.getId()));

        logger.info("area= " + getTriangleArea(triangle.getId()));

        logger.info("Checked triangle object: " + triangle);

        return triangle;
    }

    private <T> Double getAbsDouble(T value) {
        String clazz = value.getClass().getName();
        switch (clazz) {
            case "java.lang.Double":
                return Math.abs((double) value);
            case "java.lang.Long":
                return Math.abs(Double.valueOf((Long) value));
            case "java.lang.Integer":
                return Math.abs(Double.valueOf((Integer) value));
            default:
                throw new IllegalArgumentException(value.getClass().getSimpleName());
        }
    }

    private boolean isUUID(String idString) {
        return idString.matches("[0-9a-f]{8}-[0-9a-f]{4}-[1-5][0-9a-f]{3}-[89ab][0-9a-f]{3}-[0-9a-f]{12}");
    }

    public void checkPerimeter(TriangleObject triangle) throws IOException {
        assertEquals(triangle.getPerimeter(), getTrianglePerimeter(triangle.getId()));
    }

    public void checkArea(TriangleObject triangle) throws IOException {
        assertEquals(triangle.getArea(), getTriangleArea(triangle.getId()));
    }

    public void checkNotFoundError(ErrorResponseObject notFoundError, String expectedPath) {
        assertEquals(HttpStatus.SC_NOT_FOUND, notFoundError.getStatus());
        assertEquals("Not Found", notFoundError.getError());
        assertEquals("com.natera.test.triangle.exception.NotFounException", notFoundError.getException());
        assertEquals("Not Found", notFoundError.getMessage());
        assertEquals(expectedPath, notFoundError.getPath());
    }

}
