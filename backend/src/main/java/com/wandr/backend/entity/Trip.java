package com.wandr.backend.entity;

import lombok.Data;
import java.sql.Timestamp;
import java.time.Duration;

@Data
public class Trip {
    private Long tripId;
    private Integer travellerId;
    private String name;
    private Timestamp createdAt;
    private Timestamp updatedAt;
    private String status;
    private Long routeType;
    private Integer orderedTime;
    private Integer optimizedTime;
    private Integer orderedDistance;
    private Integer optimizedDistance;
    private Timestamp startTime;
    private Timestamp endTime;

}
