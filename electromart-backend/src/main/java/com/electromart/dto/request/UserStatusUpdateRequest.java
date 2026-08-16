package com.electromart.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class UserStatusUpdateRequest {

    @NotNull(message = "Enabled flag is required")
    private Boolean enabled;
}