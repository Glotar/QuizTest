package com.quiz.qa;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static com.quiz.qa.CommonStrings.AUTH_HEADER_NAME;
import static com.quiz.qa.Config.getUserToken;

public class RestHelpers {
    private static final Logger logger = LogManager.getLogger(RestHelpers.class);

    public static String OVERRIDE_AUTH_HEADER_NAME = null;
    public static String OVERRIDE_AUTH_TOKEN = null;
    public static boolean ADD_AUTH_HEADER = true;

    public static void setAuthenticationOverride(String headerName, String authToken, boolean addAuthHeader) {
        OVERRIDE_AUTH_HEADER_NAME = headerName;
        OVERRIDE_AUTH_TOKEN = authToken;
        ADD_AUTH_HEADER = addAuthHeader;
    }

    public static void disableAuthenticationOverride() {
        OVERRIDE_AUTH_HEADER_NAME = OVERRIDE_AUTH_TOKEN = null;
        ADD_AUTH_HEADER = true;
    }

    public static <T> T retrieveResourceFromResponse(HttpResponse response, Class<T> clazz) throws IOException {
        String jsonFromResponse = EntityUtils.toString(response.getEntity());
        return retrieveResourceFromString(jsonFromResponse, clazz);
    }

    public static <T> T retrieveResourceFromString(String jsonFromResponse, Class<T> clazz) throws IOException {
        try {
            ObjectMapper mapper = new ObjectMapper()
                    .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, true);
            return mapper.readValue(jsonFromResponse, clazz);
        } catch (UnrecognizedPropertyException e) {
            logger.error("Failed to parse '" + clazz.getSimpleName() + "' object from response!");
            throw new ResponseObjectParsingException(jsonFromResponse, e, clazz);
        } catch (MismatchedInputException e){
            if (e.getMessage().contains("No content to map due to end-of-input")){
                throw new RuntimeException("Got empty response instead of " + clazz.getSimpleName());
            }
            throw e;
        }
    }

    public static class ResponseObjectParsingException extends RuntimeException {
        private String jsonResponseString;
        private UnrecognizedPropertyException payloadException;
        private Class failedClazz;
        public ResponseObjectParsingException (String jsonResponseString, UnrecognizedPropertyException e, Class failedClazz){
            super("Failed to parse '" + failedClazz + "' object from the following json:\n"+jsonResponseString);
            this.jsonResponseString = jsonResponseString;
            this.payloadException=e;
            this.failedClazz=failedClazz;
        }

        public String getJsonResponseString(){
            return this.jsonResponseString;
        }

        public UnrecognizedPropertyException getPayloadException(){
            return this.payloadException;
        }

        public Class getFailedClazz(){
            return this.failedClazz;
        }
    }

    public static <T> List<T> retrieveResourceListFromResponse(HttpResponse response, Class<T> clazz) throws IOException {
        List<T> resultList = new ArrayList<>();
        String jsonFromResponse = EntityUtils.toString(response.getEntity());
        ObjectMapper mapper = new ObjectMapper()
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, true);
        Iterator<T> it = mapper.readerFor(clazz).readValues(jsonFromResponse);
        while (it.hasNext()) {
            resultList.add(it.next());
        }
        return resultList;
    }

    public static HttpUriRequest getHttpUriRequestByType(RequestTypes requestType, String uri, String body) throws UnsupportedEncodingException {
        switch (requestType) {
            case GET:
                return new HttpGet(uri);
            case POST:
                HttpUriRequest request = new HttpPost(uri);
                if (body != null) {
                    request.setHeader("Content-Type", "application/json");
                    StringEntity entity = new StringEntity(body);
                    entity.setContentType("application/json");
                    ((HttpPost) request).setEntity(entity);
                }
                return request;
            case DELETE:
                return new HttpDelete(uri);
            default:
                throw new RuntimeException("requestType " + requestType + "is not supported by BaseTest!");
        }
    }

    static HttpResponse executeRequestAndGetResponse(RequestTypes requestType, String uri) throws IOException {
        return executeRequestAndGetResponse(requestType, uri, null);
    }

    static HttpResponse executeRequestAndGetResponse(RequestTypes requestType, String uri, String body) throws IOException {
        logger.info("Performing " + requestType + " request to '" + uri + "' with body: " + body);
        HttpUriRequest request = getHttpUriRequestByType(requestType, uri, body);
        setAuthHeader(request);
        return HttpClientBuilder.create().build().execute(request);
    }

    private static void setAuthHeader(HttpUriRequest request) {
        if (!ADD_AUTH_HEADER) return;
        String headerName = (OVERRIDE_AUTH_HEADER_NAME != null) ? OVERRIDE_AUTH_HEADER_NAME : AUTH_HEADER_NAME;
        String token = (OVERRIDE_AUTH_TOKEN != null) ? OVERRIDE_AUTH_TOKEN : getUserToken();
        request.setHeader(headerName, token);
        logger.info("header: " + headerName + " token: " + token);
    }
}
