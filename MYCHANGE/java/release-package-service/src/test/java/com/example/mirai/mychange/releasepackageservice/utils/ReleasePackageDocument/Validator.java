package com.example.mirai.projectname.releasepackageservice.utils.ReleasePackageDocument;

import com.example.mirai.libraries.document.model.Document;
import com.example.mirai.projectname.releasepackageservice.document.model.ReleasePackageDocument;
import com.example.mirai.projectname.releasepackageservice.json.ReleasePackageDocumentJson;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class Validator {

    public static void createReleasePackageDocumentIsSuccessful(Document document, ReleasePackageDocument releasePackageDocument, ReleasePackageDocumentJson releasePackageDocumentJson) {

        assertThat("status is not published for a document", releasePackageDocument.getStatus(), equalTo(1));
        assertThat("document tags are null for a document", releasePackageDocument.getTags(), is(notNullValue()));
        assertThat("document name is null for a document", releasePackageDocument.getName(), is(notNullValue()));
    }

    public static void releasePackagesDocumentAreSameWithoutComparingDescription(Document document1, Document document2) {
        assertThat("status are not same", document1.getStatus(), equalTo(document2.getStatus()));
    }
}
