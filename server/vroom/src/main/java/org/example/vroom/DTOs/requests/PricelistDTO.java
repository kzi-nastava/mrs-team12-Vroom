package org.example.vroom.DTOs.requests;


import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.validation.annotation.Validated;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Validated
public class PricelistDTO {

    @NotNull(message = "Price shouldn't be blank")
    @Min(value = 0, message = "Price needs to be above 0")
    private Double priceStandard;

    @NotNull(message = "Price shouldn't be blank")
    @Min(value = 0, message = "Price needs to be above 0")
    private Double priceLuxury;

    @NotNull(message = "Price shouldn't be blank")
    @Min(value = 0, message = "Price needs to be above 0")
    private Double priceMinivan;
}
