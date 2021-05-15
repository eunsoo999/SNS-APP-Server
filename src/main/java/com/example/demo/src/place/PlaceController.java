package com.example.demo.src.place;

import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponse;
import com.example.demo.src.place.model.GetSearchPlaceRes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.example.demo.config.BaseResponseStatus.SEARCH_EMPTY_KEYWORD;

@RestController
@RequestMapping("/places")
public class PlaceController {
    final Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    private final PlaceProvider placeProvider;
    @Autowired
    private final PlaceService placeService;

    public PlaceController(PlaceProvider placeProvider, PlaceService placeService) {
        this.placeProvider = placeProvider;
        this.placeService = placeService;
    }

    /**
     * 장소 조회(검색)
     * [GET] /places?search=
     * @return BaseResponse<List<GetSearchPlaceRes>>
     */
    @ResponseBody
    @GetMapping("")
    public BaseResponse<List<GetSearchPlaceRes>> getSearchPlaces(@RequestParam(required = false) String search) {
        if (search.isEmpty()) {
            return new BaseResponse<>(SEARCH_EMPTY_KEYWORD);
        }
        try {
            List<GetSearchPlaceRes> getSearchPlaceResList = placeProvider.getSearchPlaces(search);
            return new BaseResponse<>(getSearchPlaceResList);
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }

    /**
     * 사용자 위치 기반 근처 장소 조회 (위도, 경도)
     * [GET] /places/location?lat=33.23&lon=
     * @return BaseResponse<GetSearchPlaceRes>
     */
    @ResponseBody
    @GetMapping("/location")
    public BaseResponse<List<GetSearchPlaceRes>> getPlacesBylocation(@RequestParam double lat, @RequestParam double lon) {
        try {
            List<GetSearchPlaceRes> getSearchPlaceResList = placeProvider.getPlacesByLocation(lat, lon);
            return new BaseResponse<>(getSearchPlaceResList);
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }
}
