package com.example.mirai.projectname.releasepackageservice.tests.releasepackage;

import com.example.mirai.libraries.core.exception.InternalAssertionException;
import com.example.mirai.projectname.releasepackageservice.zecn.model.MySqlEcr;
import com.example.mirai.projectname.releasepackageservice.zecn.service.ZecnServiceInterface;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.stereotype.Service;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import java.io.StringWriter;

@Service("releasePackageZecnService")
@Data
@Slf4j
@ConditionalOnExpression("T(org.springframework.util.StringUtils).isEmpty('${mirai.libraries.jms.url:}')")
public class ReleasePackageZecnTestService implements ZecnServiceInterface {

    @Override
    public void processAndSendMessage(String ecnId, String title, String state) {

    }

    @Override
    public void sendMessage(String message) {

    }
}
