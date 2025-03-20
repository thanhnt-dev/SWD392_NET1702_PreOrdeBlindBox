package com.swd392.preOrderBlindBox.restcontroller.controller;

import com.swd392.preOrderBlindBox.restcontroller.request.PreorderStatusUpdateRequest;
import com.swd392.preOrderBlindBox.restcontroller.response.*;
import com.swd392.preOrderBlindBox.service.service.PreorderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
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

    @GetMapping("/all")
    @PreAuthorize("hasRole('STAFF')")
    @SecurityRequirement(name = "Bearer Authentication")
    @ResponseStatus(HttpStatus.OK)
    @Operation(
            summary = "Get all preorders",
            tags = {"Preorder APIs"})
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "Preorders retrieved"),
                    @ApiResponse(
                            responseCode = "403",
                            description = "Unauthorized access",
                            content = @Content(schema = @Schema(implementation = ExceptionResponse.class))),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Internal server error",
                            content = @Content(schema = @Schema(implementation = ExceptionResponse.class)))
            })
    public BaseResponse<List<PreordersHistoryResponse>> getPreorders() {
        return BaseResponse.build(preorderService.getAllPreorders(), true);
    }

    @GetMapping("/management/{id}")
    @PreAuthorize("hasRole('STAFF')")
    @SecurityRequirement(name = "Bearer Authentication")
    @ResponseStatus(HttpStatus.OK)
    @Operation(
            summary = "Get preorder details for management",
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
    public BaseResponse<PreorderDetailsManagementResponse> getPreorderDetailsManagement(@PathVariable @NotNull Long id) {
        return BaseResponse.build(preorderService.getPreorderDetailsManagement(id), true);
    }

    @PutMapping
    @PreAuthorize("hasRole('STAFF')")
    @SecurityRequirement(name = "Bearer Authentication")
    @ResponseStatus(HttpStatus.OK)
    @Operation(
            summary = "Update preorder status",
            description = "Updates the status of a preorder. Valid statuses include: PENDING, DEPOSIT_PAID, FULLY_PAID, " +
                    "PENDING_FOR_DELIVERY, IN_DELIVERY, DELIVERY_SUCCESS, DELIVERY_FAILED, COMPLETED, CANCELED. " +
                    "Transitions are validated based on current status (e.g., PENDING can only transition to " +
                    "DEPOSIT_PAID or CANCELED).",
            tags = {"Preorder APIs"})
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "Preorder updated"),
                    @ApiResponse(
                            responseCode = "403",
                            description = "Unauthorized access",
                            content = @Content(schema = @Schema(implementation = ExceptionResponse.class))),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Internal server error",
                            content = @Content(schema = @Schema(implementation = ExceptionResponse.class)))
            })
    public BaseResponse<PreorderDetailsManagementResponse> updatePreorderStatus(@Valid @RequestBody PreorderStatusUpdateRequest request) {
        return BaseResponse.build(preorderService.updatePreorderStatus(request.getId(), request.getStatus()), true);
    }

}
