package com.kraytsman.service;


import com.kraytsman.domain.User;

import java.util.List;

public interface LdapService {

    List<User> getAll();

    User getUser(String ldapId);

}
