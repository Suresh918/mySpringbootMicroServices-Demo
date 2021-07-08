package com.example.mirai.libraries.notification.error;

import com.example.mirai.libraries.core.model.Event;
import com.example.mirai.libraries.notification.engine.processor.BaseRole;
import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.TypeDefs;

import javax.jms.Message;
import javax.persistence.*;
import javax.validation.constraints.Size;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@TypeDefs({ @TypeDef(name = "jsonb", typeClass = JsonBinaryType.class) })
public class NotificationError {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Size(min = 3, max = 20)
    private String userId;
    @Type(type = "jsonb")
    @Column(columnDefinition = "jsonb")
    private BaseRole role;

    private String messageType;
    private String messageEntity;
    @Column(columnDefinition = "TEXT")
    private String message;
    @Type(type = "jsonb")
    @Column(columnDefinition = "jsonb")
    private Event event;

    @Column(columnDefinition = "TEXT")
    private String exception;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Temporal(TemporalType.TIMESTAMP)
    private Date timestamp;
}
