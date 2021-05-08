package com.example.demo.src.searchHistory;

import com.example.demo.src.searchHistory.model.GetSearchHistoryRes;
import com.example.demo.src.searchHistory.model.PostSearchHistoryReq;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.List;

@Repository
public class SearchHistoryDao {
    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource){
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public int createSearchHistory(PostSearchHistoryReq postSearchHistoryReq, int loginIdx) {
        String createSearchHistory = "INSERT INTO SearchHistory (searchType, targetIdx, userIdx) VALUES (?, ?, ?)";
        this.jdbcTemplate.update(createSearchHistory, postSearchHistoryReq.getSearchType(), postSearchHistoryReq.getTargetIdx(), loginIdx);

        String lastInsertIdQuery = "select last_insert_id()";
        return this.jdbcTemplate.queryForObject(lastInsertIdQuery,int.class);
    }


    public int updateSearchHistory(int searchHistoryIdx) {
        String updateSearchHistoryQuery = "UPDATE SearchHistory SET updatedAt = NOW() WHERE idx = ? ";
        this.jdbcTemplate.update(updateSearchHistoryQuery, searchHistoryIdx);

        String lastInsertIdQuery = "select last_insert_id()";
        return this.jdbcTemplate.queryForObject(lastInsertIdQuery,int.class);
    }

    //계정 최근검색어
    public List<GetSearchHistoryRes> getSearchHistorysOfUser(int loginIdx) {
        String getSearchHistoryQuery = "SELECT SearchHistory.idx AS 'searchHistoryIdx', " +
                "SearchHistory.searchType, " +
                "SearchHistory.targetIdx, " +
                "User.profileUrl AS 'imageUrl', " +
                "User.userId AS 'mainTitle', " +
                "User.name AS 'subTitle' " +
                "FROM SearchHistory INNER JOIN User ON SearchHistory.targetIdx = User.idx " +
                "WHERE SearchHistory.searchType = 'USER' AND SearchHistory.userIdx = ? AND User.status != 'N' AND SearchHistory.status = 'Y'" +
                "ORDER BY SearchHistory.updatedAt DESC";

        return this.jdbcTemplate.query(getSearchHistoryQuery, (rs, rowNum) -> new GetSearchHistoryRes(
                rs.getInt("searchHistoryIdx"),
                rs.getString("searchType"),
                rs.getInt("targetIdx"),
                rs.getString("imageUrl"),
                rs.getString("mainTitle"),
                rs.getString("subTitle")), loginIdx);
    }

    //태그 최근검색어
    public List<GetSearchHistoryRes> getSearchHistorysOfTag(int loginIdx) {
        String getSearchHistoryQuery = "SELECT SearchHistory.idx AS 'searchHistoryIdx', SearchHistory.searchType, " +
                "SearchHistory.targetIdx, " +
                "null AS 'imageUrl', " +
                "Tag.tagName AS 'mainTitle', " +
                "CASE WHEN (SELECT count(*) FROM PostTag JOIN Post ON Post.idx = PostTag.postIdx WHERE PostTag.tagIdx = SearchHistory.targetIdx AND PostTag.status = 'Y') < 100 " +
                "THEN '100개 미만' " +
                "WHEN (SELECT count(*) FROM PostTag JOIN Post ON Post.idx = PostTag.postIdx WHERE PostTag.tagIdx = SearchHistory.targetIdx AND PostTag.status = 'Y') < 500 " +
                "THEN '100+' " +
                "WHEN (SELECT count(*) FROM PostTag JOIN Post ON Post.idx = PostTag.postIdx WHERE PostTag.tagIdx = SearchHistory.targetIdx AND PostTag.status = 'Y') < 1000 " +
                "THEN '500+' " +
                "WHEN (SELECT count(*) FROM PostTag JOIN Post ON Post.idx = PostTag.postIdx WHERE PostTag.tagIdx = SearchHistory.targetIdx AND PostTag.status = 'Y') < 10000 " +
                "THEN '1000+' " +
                "WHEN (SELECT count(*) FROM PostTag JOIN Post ON Post.idx = PostTag.postIdx WHERE PostTag.tagIdx = SearchHistory.targetIdx AND PostTag.status = 'Y') <= 10000 " +
                "THEN (SELECT TRUNCATE(count(*) / 10000, 1) FROM PostTag JOIN Post ON Post.idx = PostTag.postIdx WHERE PostTag.tagIdx = SearchHistory.targetIdx AND PostTag.status = 'Y') " +
                "END AS subTitle " +
                "FROM SearchHistory INNER JOIN Tag ON SearchHistory.targetIdx = Tag.idx " +
                "WHERE SearchHistory.searchType = 'TAG' AND SearchHistory.userIdx = ? AND SearchHistory.status = 'Y'" +
                "ORDER BY SearchHistory.updatedAt DESC";

        return this.jdbcTemplate.query(getSearchHistoryQuery, (rs, rowNum) -> new GetSearchHistoryRes(
                rs.getInt("searchHistoryIdx"),
                rs.getString("searchType"),
                rs.getInt("targetIdx"),
                rs.getString("imageUrl"),
                rs.getString("mainTitle"),
                rs.getString("subTitle")), loginIdx);
    }

    //장소 최근검색어
    public List<GetSearchHistoryRes> getSearchHistorysOfPlace(int loginIdx) {
        String getSearchHistoryPlaceQuery = "SELECT SearchHistory.idx AS 'searchHistoryIdx', " +
                "SearchHistory.searchType, " +
                "SearchHistory.targetIdx, " +
                "null AS 'imageUrl', " +
                "Place.title AS 'mainTitle', " +
                "null AS 'imageUrl', " +
                "Place.address AS 'subTitle' " +
                "FROM SearchHistory INNER JOIN Place ON SearchHistory.targetIdx = Place.idx " +
                "WHERE SearchHistory.searchType = 'PLACE' AND SearchHistory.userIdx = ? AND SearchHistory.status = 'Y'";

        return this.jdbcTemplate.query(getSearchHistoryPlaceQuery, (rs, rowNum) -> new GetSearchHistoryRes(
                rs.getInt("searchHistoryIdx"),
                rs.getString("searchType"),
                rs.getInt("targetIdx"),
                rs.getString("imageUrl"),
                rs.getString("mainTitle"),
                rs.getString("subTitle")), loginIdx);
    }

    //(인기) 최근검색어
    public List<GetSearchHistoryRes> getSearchHistorys(int loginIdx) {
        String getSearchHistoryPlaceQuery = "SELECT SearchHistory.idx AS 'searchHistoryIdx', " +
                "SearchHistory.searchType, " +
                "SearchHistory.targetIdx, " +
                "null AS 'imageUrl', " +
                "Place.title AS 'mainTitle', " +
                "null AS 'imageUrl', " +
                "Place.address AS 'subTitle' " +
                "FROM SearchHistory INNER JOIN Place ON SearchHistory.targetIdx = Place.idx " +
                "WHERE SearchHistory.userIdx = ? AND SearchHistory.status = 'Y' " +
                "ORDER BY SearchHistory.updatedAt DESC";

        return this.jdbcTemplate.query(getSearchHistoryPlaceQuery, (rs, rowNum) -> new GetSearchHistoryRes(
                rs.getInt("searchHistoryIdx"),
                rs.getString("searchType"),
                rs.getInt("targetIdx"),
                rs.getString("imageUrl"),
                rs.getString("mainTitle"),
                rs.getString("subTitle")), loginIdx);
    }


    public int deleteSearchHistory(int searchHistoryIdx) {
        String deleteSearchHistoryQuery = "UPDATE SearchHistory SET status = 'N' WHERE idx = ? ";
        return this.jdbcTemplate.update(deleteSearchHistoryQuery, searchHistoryIdx);
    }

    public int checkSearchHistoryIdx(int searchHistoryIdx) {
        String checkSearchHistoryIdxQuery = "select exists(select idx from SearchHistory where idx = ? and status != 'N')";
        int searchHistoryIdxParams = searchHistoryIdx;
        return this.jdbcTemplate.queryForObject(checkSearchHistoryIdxQuery, int.class, searchHistoryIdxParams);
    }
}
