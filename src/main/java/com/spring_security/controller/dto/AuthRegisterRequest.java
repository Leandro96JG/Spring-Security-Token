package com.spring_security.controller.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;

public record AuthRegisterRequest(@NotBlank String username,
                                  @NotBlank String password,
                                  @Valid AuthCreateRoleRequest roleRequest) {
}
