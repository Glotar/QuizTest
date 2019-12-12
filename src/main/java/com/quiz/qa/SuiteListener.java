package com.quiz.qa;

import org.testng.ISuite;
import org.testng.ISuiteListener;

import java.text.SimpleDateFormat;
import java.util.Date;

public class SuiteListener implements ISuiteListener {

        @Override
        public void onStart(ISuite iSuite) {
            System.out.println("\nSuite " + iSuite.getName() + " started in " + iSuite.getXmlSuite().getThreadCount() + " threads. " + iSuite.getAllMethods().size() + " tests to be executed.\n");

            String req_components = System.getenv("REQUIRED_COMPONENTS") != null ? System.getenv("REQUIRED_COMPONENTS") : "API";

            //String display_name = String.join(", ", components) + (reportFilePostFix != null ? "_" + reportFilePostFix : "");
            String display_name = iSuite.getName();

            String date = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
            String fullName = System.getenv("TESTLINK_BUILD_FULLNAME") != null ? System.getenv("TESTLINK_BUILD_FULLNAME") : "Local";
            String buildName = System.getenv("TESTLINK_BUILD_NAME") != null ? date + "|" + display_name + "|Jenkins job " + System.getenv("TESTLINK_BUILD_NAME") : fullName;
            String planName = System.getenv("TESTLINK_PLAN_NAME") != null ? System.getenv("TESTLINK_PLAN_NAME") : "Reltio API regression test plan " + new SimpleDateFormat("yyyyMMdd").format(new Date());

            iSuite.setAttribute("build", buildName);
            iSuite.setAttribute("plan", planName);
        }

        @Override
        public void onFinish(ISuite iSuite) {
            System.out.println("\nSuite " + iSuite.getName() + " finished");
        }



}
