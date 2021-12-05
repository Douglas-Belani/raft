package com.example.restapifiletransfer.model;

import java.util.Objects;

public class HomeFolder {

    private Long idFolder;
    private String username;
    private String folder;

    public Long getIdFolder() {
        return idFolder;
    }

    public void setIdFolder(Long idFolder) {
        this.idFolder = idFolder;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFolder() {
        return folder;
    }

    public void setFolder(String folder) {
        this.folder = folder;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        HomeFolder that = (HomeFolder) o;
        return Objects.equals(idFolder, that.idFolder) && Objects.equals(username, that.username) && Objects.equals(folder, that.folder);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idFolder, username, folder);
    }
}
