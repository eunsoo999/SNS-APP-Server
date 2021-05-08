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
}
