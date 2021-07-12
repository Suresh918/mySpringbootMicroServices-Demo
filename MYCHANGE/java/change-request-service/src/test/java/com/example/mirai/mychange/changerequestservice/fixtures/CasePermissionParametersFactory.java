package com.example.mirai.projectname.changerequestservice.fixtures;

import org.junit.jupiter.params.provider.Arguments;

import java.io.*;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CasePermissionParametersFactory {

    public static Stream<Arguments> getArgumentsForUserHasCorrectCasePermissionOnRelatedEntityInStatus() throws Exception {
        return getArgumentsForUserHasCorrectCasePermissionOnEntityInStatus("expectations/myteam/casepermissions");
    }

    public static Stream<Arguments> getArgumentsForUserHasCorrectCasePermissionInStatus() throws Exception {
        return getArgumentsForUserHasCorrectCasePermissionOnEntityInStatus("changerequest/casepermissions");
    }

    private static Stream<Arguments> getArgumentsForUserHasCorrectCasePermissionOnEntityInStatus(String entityType) throws Exception {
        URI uri = CasePermissionParametersFactory.class.getResource("/" + entityType ).toURI();
        String path = Paths.get(uri).toString();
        return Files.walk(Paths.get(path))
                .filter(Files::isRegularFile)
                .map(file -> {
                    String[] pathElements = file.getParent().getFileName().toString().split(System.getProperty("line.separator"));
                    String user = pathElements[pathElements.length - 1];

                    pathElements = file.getFileName().toString().split(System.getProperty("line.separator"));
                    String[] status = pathElements[pathElements.length - 1].split("-");

                    String casePermission = getResourceContentFromAbsolutePath(file.toFile());

                    return Arguments.of(user, status[0], casePermission);

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
