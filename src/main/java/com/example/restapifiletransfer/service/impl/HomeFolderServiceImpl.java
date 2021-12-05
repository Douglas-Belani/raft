package com.example.restapifiletransfer.service.impl;

import com.example.restapifiletransfer.model.HomeFolder;
import com.example.restapifiletransfer.persistence.HomeFolderDAO;
import com.example.restapifiletransfer.service.HomeFolderService;
import org.apache.commons.text.RandomStringGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class HomeFolderServiceImpl implements HomeFolderService {

    private static final Logger log = LoggerFactory.getLogger(HomeFolderServiceImpl.class);

    @Value("${rest.api.file.transfer.random-name-length}")
    private Integer folderNameLength;

    private final HomeFolderDAO homeFolderDAO;
    private final RandomStringGenerator randomStringGenerator;

    public HomeFolderServiceImpl(HomeFolderDAO homeFolderDAO, RandomStringGenerator randomStringGenerator) {
        this.homeFolderDAO = homeFolderDAO;
        this.randomStringGenerator = randomStringGenerator;
    }

    @Override
    public HomeFolder findHomeFolder(String username) {
        return homeFolderDAO
                .findHomeFolderByEmail(username)
                .orElseGet(() -> {
                    log.info("No home folder found for username {}. Creating a new one", username);
                    HomeFolder homeFolder = new HomeFolder();
                    homeFolder.setUsername(username);
                    homeFolder.setFolder(randomStringGenerator.generate(folderNameLength) + "-" + username.hashCode());

                    return homeFolderDAO.createHomeFolder(homeFolder);
                });
    }

}
