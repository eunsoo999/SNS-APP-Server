package com.example.demo.src.place;

import com.example.demo.config.BaseException;
import com.example.demo.src.place.model.GetSearchPlaceRes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

import static com.example.demo.config.BaseResponseStatus.DATABASE_ERROR;

@Service
@Transactional
public class PlaceProvider {
    final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final PlaceDao placeDao;

    @Autowired
    public PlaceProvider(PlaceDao placeDao) {
        this.placeDao = placeDao;
    }

    public List<GetSearchPlaceRes> getSearchPlaces(String search) throws BaseException {
        try{
            List<GetSearchPlaceRes> getSearchPlaceResList = placeDao.getSearchPlaces(search);
            return getSearchPlaceResList;
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }

    }

    public List<GetSearchPlaceRes> getPlacesByLocation(double lat, double lon) throws BaseException {
        try {
            List<GetSearchPlaceRes> getSearchPlaceResList = placeDao.getPlacesByLocation(lat, lon);
            return getSearchPlaceResList;
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }
}
