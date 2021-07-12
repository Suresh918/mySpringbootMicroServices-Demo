package com.example.mirai.projectname.changerequestservice.utils.ChangeRequestDocument;

import com.example.mirai.libraries.document.model.Document;
import com.example.mirai.projectname.changerequestservice.document.model.ChangeRequestDocument;
import com.example.mirai.projectname.changerequestservice.json.ChangeRequestDocumentJson;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class Validator {

    public static void createChangeRequestDocumentIsSuccessful(Document document, ChangeRequestDocument changeRequestDocument, ChangeRequestDocumentJson changeRequestDocumentJson) {

        assertThat("status is not published for a document", changeRequestDocument.getStatus(), equalTo(1));
        assertThat("document tags are null for a document", changeRequestDocument.getTags(), is(notNullValue()));
        assertThat("document name is null for a document", changeRequestDocument.getName(), is(notNullValue()));
    }

    public static void changeRequestsDocumentAreSameWithoutComparingDescription(Document document1, Document document2) {
        assertThat("status are not same", document1.getStatus(), equalTo(document2.getStatus()));
    }
}
