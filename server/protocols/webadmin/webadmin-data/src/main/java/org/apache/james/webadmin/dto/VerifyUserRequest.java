package org.apache.james.webadmin.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Preconditions;

public class VerifyUserRequest {
    private final char[] password;

    @JsonCreator
    public VerifyUserRequest(@JsonProperty("password") char[] password) {
        Preconditions.checkNotNull(password);
        this.password = password;
    }

    public char[] getPassword() {
        return password;
    }
}
