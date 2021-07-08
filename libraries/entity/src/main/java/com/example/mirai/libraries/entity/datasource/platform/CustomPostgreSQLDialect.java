package com.example.mirai.libraries.entity.datasource.platform;

import org.hibernate.dialect.PostgreSQL82Dialect;
import org.hibernate.dialect.function.SQLFunctionTemplate;
import org.hibernate.type.StandardBasicTypes;

public class CustomPostgreSQLDialect extends PostgreSQL82Dialect {

   public CustomPostgreSQLDialect() {
      super();
      registerFunction("regexp", new SQLFunctionTemplate(StandardBasicTypes.BOOLEAN, "?1 ~ ?2"));
   }
}
