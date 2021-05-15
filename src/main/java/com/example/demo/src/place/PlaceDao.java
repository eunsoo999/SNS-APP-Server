package com.example.demo.src.place;

import com.example.demo.src.place.model.GetSearchPlaceRes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.List;

@Repository
public class PlaceDao {
    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource){
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }


    public List<GetSearchPlaceRes> getSearchPlaces(String search) {
        String getSearchPlacesQuery = "SELECT Place.idx AS 'placeIdx', title, address " +
                "FROM Place " +
                "WHERE title LIKE ?";
        String searchParam = "%" + search + "%";

        return this.jdbcTemplate.query(getSearchPlacesQuery,
                (rs,rowNum) -> new GetSearchPlaceRes(rs.getInt("placeIdx"),
                        rs.getString("title"),
                        rs.getString("address")), searchParam);
    }

    public List<GetSearchPlaceRes> getPlacesByLocation(double lat, double lon) {
        String getPlacesByLocationQuery = "SELECT idx AS 'placeIdx', title, address, " +
                "(6371*acos(cos(radians(?1))*cos(radians(latitude))*cos(radians(longitude) " +
                "- radians(?2))+sin(radians(?1))*sin(radians(latitude)))) AS distance " +
                "FROM Place HAVING " +
                "distance <= 2 " +
                "ORDER BY distance";

        return this.jdbcTemplate.query(getPlacesByLocationQuery,
                (rs,rowNum) -> new GetSearchPlaceRes(
                        rs.getInt("placeIdx"),
                        rs.getString("title"),
                        rs.getString("address")), lat, lon, lat);
    }
}
