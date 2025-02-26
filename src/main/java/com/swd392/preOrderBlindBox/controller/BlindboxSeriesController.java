package com.swd392.preOrderBlindBox.controller;


import com.swd392.preOrderBlindBox.entity.BlindboxSeries;
import com.swd392.preOrderBlindBox.facade.BlindboxSeriesFacade;
import com.swd392.preOrderBlindBox.response.BaseResponse;
import com.swd392.preOrderBlindBox.response.BlindboxSeriesDetailsResponse;
import com.swd392.preOrderBlindBox.response.BlindboxSeriesResponse;
import com.swd392.preOrderBlindBox.service.BlindboxSeriesService;
import com.swd392.preOrderBlindBox.service.impl.BlindboxSeriesServiceImpl;
import com.swd392.preOrderBlindBox.specification.BlindboxSeriesSpecification;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/blindbox-series")
@RequiredArgsConstructor
public class BlindboxSeriesController {
    private final BlindboxSeriesFacade blindboxSeriesFacade;

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(
            summary = "Get blindbox series details by id",
            tags = {"Blindbox Series APIs"})
    public BaseResponse<BlindboxSeriesDetailsResponse> getBlindboxSeriesById(@PathVariable Long id) {
        return this.blindboxSeriesFacade.getBlindboxSeriesWithDetailsById(id);
    }

    @GetMapping
    @Operation(summary = "Get all blindbox series (searching, paging, sorting, and filtering are applicable)",
            tags = {"Blindbox Series APIs"})
    public BaseResponse<Page<BlindboxSeriesResponse>> searchBlindboxSeries(
            @RequestParam(required = false) String seriesName,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id,asc") String[] sort) {

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Order.by(sort[0]).with(Sort.Direction.fromString(sort[1]))));
        Specification<BlindboxSeries> spec = Specification.where(null);

        if (seriesName != null) {
            spec = spec.and(BlindboxSeriesSpecification.hasSeriesName(seriesName));
        }
        if (categoryId != null) {
            spec = spec.and(BlindboxSeriesSpecification.hasCategory(categoryId));
        }

        Page<BlindboxSeriesResponse> blindboxSeriesPage = blindboxSeriesFacade.searchBlindboxSeries(spec, pageable);

        return BaseResponse.build(blindboxSeriesPage, true);
    }
}
