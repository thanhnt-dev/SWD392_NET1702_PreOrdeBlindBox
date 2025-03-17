package com.swd392.preOrderBlindBox.restcontroller.controller;

import com.swd392.preOrderBlindBox.restcontroller.response.BaseResponse;
import com.swd392.preOrderBlindBox.restcontroller.response.ExceptionResponse;
import com.swd392.preOrderBlindBox.restcontroller.response.PreorderDetailsResponse;
import com.swd392.preOrderBlindBox.restcontroller.response.PreordersHistoryResponse;
import com.swd392.preOrderBlindBox.service.service.PreorderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/${api.version}/preorders")
@RequiredArgsConstructor
public class PreorderController {
    private final PreorderService preorderService;

    @GetMapping
    @PreAuthorize("hasRole('USER')")
    @SecurityRequirement(name = "Bearer Authentication")
    @ResponseStatus(HttpStatus.OK)
    @Operation(
            summary = "Get preorders history",
            tags = {"Preorder APIs"})
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "Preorders history retrieved"),
                    @ApiResponse(
                            responseCode = "403",
                            description = "Unauthorized access",
                            content = @Content(schema = @Schema(implementation = ExceptionResponse.class))),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Internal server error",
                            content = @Content(schema = @Schema(implementation = ExceptionResponse.class)))
            })
    public BaseResponse<List<PreordersHistoryResponse>> getPreordersHistory() {
        return BaseResponse.build(preorderService.getPreordersOfUser(), true);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('USER')")
    @SecurityRequirement(name = "Bearer Authentication")
    @ResponseStatus(HttpStatus.OK)
    @Operation(
            summary = "Get preorder details",
            tags = {"Preorder APIs"})
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "Preorder details retrieved"),
                    @ApiResponse(
                            responseCode = "403",
                            description = "Unauthorized access",
                            content = @Content(schema = @Schema(implementation = ExceptionResponse.class))),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Internal server error",
                            content = @Content(schema = @Schema(implementation = ExceptionResponse.class)))
            })
    public BaseResponse<PreorderDetailsResponse> getPreorderDetails(@PathVariable @NotNull Long id) {
        return BaseResponse.build(preorderService.getPreorderDetails(id), true);
    }

}
