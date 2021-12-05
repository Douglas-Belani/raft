package com.example.restapifiletransfer.persistence;

import com.example.restapifiletransfer.model.HomeFolder;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Optional;

@Repository
public class HomeFolderDAO {

    private static final String FIND_HOME_FOLDER_BY_EMAIL = "SELECT id_folder, username, folder " +
            "FROM TB_HOME_FOLDER " +
            "WHERE username = ?";

    private static final String CREATE_HOME_FOLDER = "INSERT INTO TB_HOME_FOLDER (id_folder, username, folder) " +
            "VALUES (NULL, ?, ?)";

    private static final String DELETE_HOME_FOLDER_BY_FOLDER_ID = "DELETE FROM TB_HOME_FOLDER " +
            "WHERE id_folder = ?";

    private final JdbcTemplate jdbcTemplate;

    public HomeFolderDAO(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Optional<HomeFolder> findHomeFolderByEmail(String username) {
        return Optional.ofNullable(jdbcTemplate.query(FIND_HOME_FOLDER_BY_EMAIL, (ResultSet rs) -> {
            HomeFolder homeFolder = null;

            if (rs.next()) {
                homeFolder = new HomeFolder();
                homeFolder.setIdFolder(rs.getLong("id_folder"));
                homeFolder.setUsername(rs.getString("username"));
                homeFolder.setFolder(rs.getString("folder"));
            }

            return homeFolder;
        }, username));
    }

    public HomeFolder createHomeFolder(HomeFolder homeFolder) {
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update((Connection con) -> {
            PreparedStatement preparedStatement = con.prepareStatement(CREATE_HOME_FOLDER, new String[]{"id_folder"});
            preparedStatement.setString(1, homeFolder.getUsername());
            preparedStatement.setString(2, homeFolder.getFolder());

            return preparedStatement;
        }, keyHolder);

        homeFolder.setIdFolder(keyHolder.getKey().longValue());

        return homeFolder;
    }

    public void deleteHomeFolder(Long folderId) {
        jdbcTemplate.update(DELETE_HOME_FOLDER_BY_FOLDER_ID, folderId);
    }
}
