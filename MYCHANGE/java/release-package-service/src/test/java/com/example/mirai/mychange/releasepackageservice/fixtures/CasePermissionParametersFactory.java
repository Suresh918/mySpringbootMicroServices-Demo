package com.example.mirai.projectname.releasepackageservice.fixtures;

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
        return getArgumentsForUserHasCorrectCasePermissionOnEntityInStatus("myteam");
    }

    public static Stream<Arguments> getArgumentsForUserHasCorrectCasePermissionInStatus() throws Exception {
        return getArgumentsForUserHasCorrectCasePermissionOnEntityInStatus("releasepackage");
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
                    String[] status = pathElements[pathElements.length - 1].split("-");

                    String casePermission = getResourceContentFromAbsolutePath(file.toFile());
                    if (user.equalsIgnoreCase("ReleasePackage.change-control-board-member") && entityType.equalsIgnoreCase("myteam")) {
                        return Arguments.of(user, status[1], status[2], casePermission);
                    } else {
                        return Arguments.of(user, status[1], "false", casePermission);
                    }
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
