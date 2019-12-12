package com.quiz.qa;

public class TrianglePostPayload<T> {
    public T firstSide;
    public T secondSide;
    public T thirdSide;
    public String separator;
    public String requestBody;

    TrianglePostPayload(T firstSide, T secondSide, T thirdSide, String separator) {
        this.firstSide = firstSide;
        this.secondSide = secondSide;
        this.thirdSide = thirdSide;
        this.separator = separator;
        this.requestBody = BaseTest.getTriangleRequestBody(firstSide, secondSide, thirdSide, separator);
    }

    public TrianglePostPayload(T firstSide, T secondSide, T thirdSide) {
        this.firstSide = firstSide;
        this.secondSide = secondSide;
        this.thirdSide = thirdSide;
        this.separator = null;
        this.requestBody = BaseTest.getTriangleRequestBody(firstSide, secondSide, thirdSide, null);
    }

    TrianglePostPayload(String requestBody) {
        this.requestBody = requestBody;
    }

    @Override
    public String toString() {
        return requestBody;
    }
}
