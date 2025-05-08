package com.elitedesk.model;

import java.time.LocalDateTime;

public class Reservation {
    private long id;
    private long spaceId;
    private String userId;
    private String userName;
    private String spaceName;
    private String spaceLocation;
    private String spaceType;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String title;
    private String description;
    private String status;

    public Reservation() {
    }

    public Reservation(long id, long spaceId, String userId, LocalDateTime startTime,
            LocalDateTime endTime, String title, String description, String status) {
        this.id = id;
        this.spaceId = spaceId;
        this.userId = userId;
        this.startTime = startTime;
        this.endTime = endTime;
        this.title = title;
        this.description = description;
        this.status = status;
    }

    public Reservation(long id, long spaceId, String userId, String userName, String spaceName,
            String spaceLocation, String spaceType, LocalDateTime startTime, LocalDateTime endTime, String status) {
        this.id = id;
        this.spaceId = spaceId;
        this.userId = userId;
        this.userName = userName;
        this.spaceName = spaceName;
        this.spaceLocation = spaceLocation;
        this.spaceType = spaceType;
        this.startTime = startTime;
        this.endTime = endTime;
        this.status = status;
    }

    // Getters and Setters
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getSpaceId() {
        return spaceId;
    }

    public void setSpaceId(long spaceId) {
        this.spaceId = spaceId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getSpaceName() {
        return spaceName;
    }

    public void setSpaceName(String spaceName) {
        this.spaceName = spaceName;
    }

    public String getSpaceLocation() {
        return spaceLocation;
    }

    public void setSpaceLocation(String spaceLocation) {
        this.spaceLocation = spaceLocation;
    }

    public String getSpaceType() {
        return spaceType;
    }

    public void setSpaceType(String spaceType) {
        this.spaceType = spaceType;
    }
}