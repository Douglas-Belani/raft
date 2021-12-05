package com.example.restapifiletransfer.service;

import com.example.restapifiletransfer.model.HomeFolder;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.OAuth2AuthenticatedPrincipal;

public interface HomeFolderService {

    public abstract HomeFolder findHomeFolder(String email);

}
