package com.limethecoder.data.service.impl;


import com.limethecoder.data.domain.Like;
import com.limethecoder.data.domain.User;
import com.limethecoder.data.repository.*;
import com.limethecoder.data.service.CacheService;
import com.limethecoder.data.service.UserService;
import com.limethecoder.util.FileUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

@Service
@Transactional
public class UserServiceImpl extends AbstractJPAService<User, String>
        implements UserService {

    private final static String ICON_PREFIX = "_icon";
    private final static String DEFAULT_ICON = "default_user.png";

    private UserRepository userRepository;
    private RoleRepository roleRepository;
    private BookRepository bookRepository;
    private LikeRepository likeRepository;
    private CacheService cacheService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    public UserServiceImpl(UserRepository userRepository,
                           RoleRepository roleRepository,
                           BookRepository bookRepository,
                           LikeRepository likeRepository,
                           CacheService cacheService) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.bookRepository = bookRepository;
        this.likeRepository = likeRepository;
        this.cacheService = cacheService;
    }

    @Override
    public User add(User user) {
        if(userRepository.exists(user.getLogin())) {
            return null;
        }

        savePhoto(user);

        user.setPassword(passwordEncoder.encode(user.getPassword()));

        if(user.getRoles() == null || user.getRoles().isEmpty()) {
            user.setRoles(Arrays.asList(roleRepository.findOne("USER")));
        }

        return userRepository.saveAndFlush(user);
    }

    @Override
    public User findOne(String id) {
        return getRepository().findOne(id);
    }

    @Override
    public User update(User user) {
        if(cacheService.userExists(user.getLogin())) {
            cacheService.invalidateUserKeys(user.getLogin());
        }

        savePhoto(user);
        return userRepository.saveAndFlush(user);
    }

    @Override
    public void delete(String login) {
        User user = userRepository.findOne(login);
        FileUtil.removeFileIfExists(user.getPhotoUrl());

        bookRepository.deleteReviewsByUser(user);

        List<Like> likes = likeRepository.findByUserId(login);

        if(likes != null && !likes.isEmpty()) {
            likeRepository.delete(likes);
        }

        if(cacheService.userExists(user.getLogin())) {
            cacheService.invalidateUserKeys(user.getLogin());
        }
        userRepository.delete(login);
    }

    @Override
    protected JpaRepository<User, String> getRepository() {
        return userRepository;
    }

    private void savePhoto(User user) {
        if(user.getPhoto() != null && !user.getPhoto().isEmpty()) {
            String[] parts = user.getPhoto().getOriginalFilename()
                    .split("\\.");

            String fileExtension = parts[parts.length - 1];
            String filename = user.getLogin() + ICON_PREFIX +
                    "." + fileExtension;

            FileUtil.saveFile(user.getPhoto(), filename);
            user.setPhotoUrl(filename);
        }
    }

    @Override
    public byte[] loadImage(User user) {
        if(FileUtil.isExists(user.getPhotoUrl())) {
            return FileUtil.loadImage(user.getPhotoUrl());
        }
        return FileUtil.loadImage(DEFAULT_ICON);
    }

    @Override
    public List<String> getAllLogins() {
        return userRepository.getAllLogins();
    }
}
