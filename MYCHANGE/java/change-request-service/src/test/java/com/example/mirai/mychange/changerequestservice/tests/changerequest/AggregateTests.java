package com.example.mirai.projectname.changerequestservice.tests.changerequest;

import com.example.mirai.projectname.changerequestservice.tests.BaseTest;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

public class AggregateTests extends BaseTest {

    private static String reviewAggregateExpectedContent;

    static {
        InputStream inputStream = FilterTests.class.getResourceAsStream("/expectations/review/aggregate/ReviewAggregate.txt");
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
        reviewAggregateExpectedContent = bufferedReader.lines().collect(Collectors.joining(System.lineSeparator()));
    }
}
