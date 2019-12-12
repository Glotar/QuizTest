package com.quiz.qa.responseObjectModels;

import com.google.gson.JsonObject;

import java.lang.reflect.Field;

public class TriangleObject {
    private String id;
    private Double firstSide;
    private Double secondSide;
    private Double thirdSide;

    public String getId() {
        return id;
    }

    public Double getFirstSide() {
        return firstSide;
    }

    public Double getSecondSide() {
        return secondSide;
    }

    public Double getThirdSide() {
        return thirdSide;
    }
    // standard getters and setters


    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof TriangleObject)) return false;
        TriangleObject anotherTriangle = (TriangleObject) obj;
        if (!this.id.equals(anotherTriangle.getId())) return false;
        if (!this.firstSide.equals(anotherTriangle.getFirstSide())) return false;
        if (!this.secondSide.equals(anotherTriangle.getSecondSide())) return false;
        return this.thirdSide.equals(anotherTriangle.getThirdSide());
    }

    @Override
    public String toString(){
        JsonObject triangleJson = new JsonObject();
        for(Field f : TriangleObject.class.getDeclaredFields()) {
            try {
                triangleJson.addProperty(f.getName(), f.get(this).toString());
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return triangleJson.toString();
    }

    public Double getPerimeter(){
        return firstSide+secondSide+thirdSide;
    }

    public Double getArea(){
        Double halfPerimeter = getPerimeter()/2.0;
        return Math.sqrt(halfPerimeter*(halfPerimeter-firstSide)*(halfPerimeter-secondSide)*(halfPerimeter-thirdSide));
    }
}
