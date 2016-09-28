package com.maxi.chatdemo.entity;

import java.io.Serializable;

public class ChatBean implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = -5262942344177152943L;

    public enum SendState {
        SENDING,
        COMPLETED,
        SENDERROR
    }

    private String UserId;
    private String UserHeadIcon;
    private String UserName;
    private String UserContent;
    private String time;
    private int type;
    private int messagetype;
    private float UserVoiceTime;
    private String UserVoicePath;
    private String UserVoiceUrl;
    private SendState sendState;
    private String imageUrl;
    private String imageIconUrl;
    private String imageLocal;

    public String getUserVoiceUrl() {
        return UserVoiceUrl;
    }

    public void setUserVoiceUrl(String userVoiceUrl) {
        UserVoiceUrl = userVoiceUrl;
    }

    public String getImageLocal() {
        return imageLocal;
    }

    public void setImageLocal(String imageLocal) {
        this.imageLocal = imageLocal;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getImageIconUrl() {
        return imageIconUrl;
    }

    public void setImageIconUrl(String imageIconUrl) {
        this.imageIconUrl = imageIconUrl;
    }

    public SendState getSendState() {
        return sendState;
    }

    public void setSendState(SendState sendState) {
        this.sendState = sendState;
    }

    public float getUserVoiceTime() {
        return UserVoiceTime;
    }

    public void setUserVoiceTime(float userVoiceTime) {
        UserVoiceTime = userVoiceTime;
    }

    public String getUserVoicePath() {
        return UserVoicePath;
    }

    public void setUserVoicePath(String userVoicePath) {
        UserVoicePath = userVoicePath;
    }

    public int getMessagetype() {
        return messagetype;
    }

    public void setMessagetype(int messagetype) {
        this.messagetype = messagetype;
    }

    public String getUserName() {
        return UserName;
    }

    public void setUserName(String userName) {
        UserName = userName;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getUserId() {
        return UserId;
    }

    public void setUserId(String userId) {
        UserId = userId;
    }

    public String getUserHeadIcon() {
        return UserHeadIcon;
    }

    public void setUserHeadIcon(String userHeadIcon) {
        UserHeadIcon = userHeadIcon;
    }

    public String getUserContent() {
        return UserContent;
    }

    public void setUserContent(String userContent) {
        UserContent = userContent;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    @Override
    public String toString() {
        // TODO Auto-generated method stub
        return super.toString();
    }


}
