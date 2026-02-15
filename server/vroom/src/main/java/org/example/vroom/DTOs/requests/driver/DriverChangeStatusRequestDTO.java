package org.example.vroom.DTOs.requests.driver;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.vroom.enums.DriverStatus;
import org.jetbrains.annotations.NotNull;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DriverChangeStatusRequestDTO {
    @NotNull
    private DriverStatus status;
}
