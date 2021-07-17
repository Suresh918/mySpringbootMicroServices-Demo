package com.example.mirai.projectname.reviewservice.fixtures;

import org.junit.jupiter.params.provider.Arguments;

import java.io.*;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CasePermissionParametersFactory {

    public static Stream<Arguments> userHasCorrectCasePermissionOnReviewInStatus() throws Exception {
        return getArgumentsForUserHasCorrectCasePermissionOnEntityInStatus("review");
    }

    public static Stream<Arguments> userHasCorrectCasePermissionOnReviewTaskInStatus() throws Exception {
        return getArgumentsForUserHasCorrectCasePermissionOnEntityInStatus("reviewtask");
    }

    public static Stream<Arguments> userHasCorrectCasePermissionOnReviewEntryInStatus() throws Exception {
        return getArgumentsForUserHasCorrectCasePermissionOnEntityInStatus("reviewentry");
    }

    private static Stream<Arguments> getArgumentsForUserHasCorrectCasePermissionOnEntityInStatus(String entityType) throws Exception {
        URI uri = CasePermissionParametersFactory.class.getResource("/expectations/" + entityType + "/casepermissions").toURI();
        String path = Paths.get(uri).toString();
        return Files.walk(Paths.get(path))
                .filter(Files::isRegularFile)
                .map(file -> {
                    String[] pathElements = file.getParent().getFileName().toString().split(System.getProperty("line.separator"));
                    String user = pathElements[pathElements.length - 1];

                    pathElements = file.getFileName().toString().split(System.getProperty("line.separator"));
                    Object statusElement = pathElements[pathElements.length - 1];
                    String relatedEntityStatus = null;
                    String entityStatus = null;
                    String[] statusElementValue = ((String) statusElement).split("-");
                    if (statusElementValue.length > 1) {
                        relatedEntityStatus = statusElementValue[1];
                        entityStatus = statusElementValue[3];
                    } else {
                        entityStatus = statusElementValue[0];
                    }
                    String casePermission = getResourceContentFromAbsolutePath(file.toFile());
                    if (Objects.nonNull(relatedEntityStatus)) {
                        return Arguments.of(user, relatedEntityStatus, entityStatus, casePermission);
                    }
                    return Arguments.of(user, entityStatus, casePermission);
                }).sequential();
    }

    private static String getResourceContentFromAbsolutePath(File file) {
        InputStream inputStream = null;
        try {
            inputStream = new FileInputStream(file);
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
        return bufferedReader.lines().collect(Collectors.joining(System.lineSeparator()));
    }

}
