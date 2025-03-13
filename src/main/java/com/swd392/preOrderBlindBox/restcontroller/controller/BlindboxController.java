package com.swd392.preOrderBlindBox.restcontroller.controller;

import com.swd392.preOrderBlindBox.entity.BlindboxSeries;
import com.swd392.preOrderBlindBox.facade.facade.BlindboxFacade;
import com.swd392.preOrderBlindBox.restcontroller.request.CreateBlindboxSeriesRequest;
import com.swd392.preOrderBlindBox.restcontroller.response.BaseResponse;
import com.swd392.preOrderBlindBox.restcontroller.response.BlindboxSeriesDetailsResponse;
import com.swd392.preOrderBlindBox.restcontroller.response.BlindboxSeriesResponse;
import com.swd392.preOrderBlindBox.specification.BlindboxSeriesSpecification;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/${api.version}/blindbox")
@RequiredArgsConstructor
public class BlindboxController {
  private final BlindboxFacade blindboxFacade;

  @GetMapping("/{id}")
  @ResponseStatus(HttpStatus.OK)
  @Operation(
      summary =
          "Get blindbox series details by id, along with ongoing campaign (if any) and its tiers",
      tags = {"Blindbox Series APIs"})
  public BaseResponse<BlindboxSeriesDetailsResponse> getBlindboxSeriesById(@PathVariable Long id) {
    return this.blindboxFacade.getBlindboxSeriesWithDetailsById(id);
  }

  @GetMapping
  @Operation(
      summary =
          "Get all blindbox series (searching, paging, sorting, and filtering are applicable)",
      tags = {"Blindbox Series APIs"})
  public BaseResponse<Page<BlindboxSeriesResponse>> searchBlindboxSeries(
      @RequestParam(required = false) String seriesName,
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int size,
      @RequestParam(defaultValue = "id,asc") String[] sort) {

    Pageable pageable =
        PageRequest.of(
            page, size, Sort.by(Sort.Order.by(sort[0]).with(Sort.Direction.fromString(sort[1]))));
    Specification<BlindboxSeries> spec = Specification.where(null);

    if (seriesName != null) {
      spec = spec.and(BlindboxSeriesSpecification.hasSeriesName(seriesName));
    }

    Page<BlindboxSeriesResponse> blindboxSeriesPage =
        blindboxFacade.getBlindboxSeries(spec, pageable);

    return BaseResponse.build(blindboxSeriesPage, true);
  }

  @PostMapping()
  @ResponseStatus(HttpStatus.OK)
  @PreAuthorize("hasRole('ADMIN')")
  @SecurityRequirement(name = "Bearer Authentication")
  @Operation(
      summary = "Create blind box for Admin",
      tags = {"Blindbox Series APIs"})
  BaseResponse<Void> createBlindbox(@Valid @RequestBody CreateBlindboxSeriesRequest request) {
    return blindboxFacade.createBlindboxSeries(request);
  }
}
