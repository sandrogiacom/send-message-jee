package com.identity.broker.message.dto;

import com.fluig.broker.domain.BrokerRequestBody;

public class SyncEventDTO implements BrokerRequestBody {
    private String type;
    private String activeDirectoryId;
    private Object message;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getActiveDirectoryId() {
        return activeDirectoryId;
    }

    public void setActiveDirectoryId(String activeDirectoryId) {
        this.activeDirectoryId = activeDirectoryId;
    }

    public Object getMessage() {
        return message;
    }

    public void setMessage(Object message) {
        this.message = message;
    }
}
