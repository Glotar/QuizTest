package com.quiz.qa;

import com.quiz.qa.responseObjectModels.TriangleObject;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class SeparatorAlphabetTest<T> extends TestNGBaseTest {

    @DataProvider(name = "testData")
    public static Object[] dataForTest() {
        String separatorCollectionString = "a,b,c,d,e,f,g,h,i,j,k,l,m,n,o,p,q,r,s,t,u,v,w,x,y,z";
        List<String> separatorCollection = Arrays.asList(separatorCollectionString.split(","));

        return separatorCollection.toArray();
    }

    @AfterMethod
    public void clean() throws IOException {
        deleteAllTriangles();
    }

    @Test(dataProvider = "testData")
    public void separatorAlphabetTest(String separator) throws IOException {
        TriangleObject expectedTriangle = checkPostTriangle(
                7,7,7,
                separator);
        assertEquals(expectedTriangle, getTriangle(expectedTriangle.getId()));
        assertTrue(getAllTriangles().contains(expectedTriangle));

        checkPerimeter(expectedTriangle);
        checkArea(expectedTriangle);
    }
}
