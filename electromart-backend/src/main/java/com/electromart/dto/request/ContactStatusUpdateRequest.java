package com.electromart.dto.request;

import com.electromart.enums.ContactStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class ContactStatusUpdateRequest {

    @NotNull(message = "Status is required")
    private ContactStatus status;
}