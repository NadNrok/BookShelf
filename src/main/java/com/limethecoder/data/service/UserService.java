package com.limethecoder.data.service;


import com.limethecoder.data.domain.User;

import java.util.List;

public interface UserService extends Service<User, String> {
    byte[] loadImage(User user);
    List<String> getAllLogins();
}
