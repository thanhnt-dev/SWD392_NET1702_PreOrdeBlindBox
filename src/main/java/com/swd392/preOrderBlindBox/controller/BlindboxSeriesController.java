package com.swd392.preOrderBlindBox.controller;


import com.swd392.preOrderBlindBox.entity.BlindboxSeries;
import com.swd392.preOrderBlindBox.facade.BlindboxSeriesFacade;
import com.swd392.preOrderBlindBox.response.BaseResponse;
import com.swd392.preOrderBlindBox.response.BlindboxSeriesDetailsResponse;
import com.swd392.preOrderBlindBox.response.BlindboxSeriesResponse;
import com.swd392.preOrderBlindBox.service.BlindboxSeriesService;
import com.swd392.preOrderBlindBox.service.impl.BlindboxSeriesServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/blindbox-series")
@RequiredArgsConstructor
public class BlindboxSeriesController {
    private final BlindboxSeriesFacade blindboxSeriesFacade;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @Operation(
            summary = "Get all blindbox series",
            tags = {"Blindbox Series APIs"})
    public BaseResponse<List<BlindboxSeriesResponse>> getAllBlindboxSeries() {
        return this.blindboxSeriesFacade.getAllBlindboxSeries();
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(
            summary = "Get blindbox series details by id",
            tags = {"Blindbox Series APIs"})
    public BaseResponse<BlindboxSeriesDetailsResponse> getBlindboxSeriesById(@PathVariable Long id) {
        return this.blindboxSeriesFacade.getBlindboxSeriesWithDetailsById(id);
    }
}
