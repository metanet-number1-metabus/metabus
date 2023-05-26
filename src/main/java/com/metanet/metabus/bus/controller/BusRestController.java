package com.metanet.metabus.bus.controller;

import com.metanet.metabus.bus.dto.BusRouteResponseDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class BusRestController {

    @GetMapping("/bus/route")
    public ResponseEntity<BusRouteResponseDto> search(
            @RequestParam("departurehome") String departureHome,
            @RequestParam("destinationhome") String destinationHome,
            @RequestParam("departuredate") String departureDate,
            @RequestParam(value = "roundtrip", defaultValue = "off") String roundTrip
    ) {
        // 매개변수 처리 로직
        // 예를 들어, 매개변수를 사용하여 필요한 데이터를 검색하고 가공하는 등의 작업을 수행할 수 있습니다.

        // 최종 response 생성
        BusRouteResponseDto busRouteResponseDto = new BusRouteResponseDto(departureHome, destinationHome, departureDate, roundTrip);

        return new ResponseEntity<>(busRouteResponseDto, HttpStatus.OK);
    }
}
