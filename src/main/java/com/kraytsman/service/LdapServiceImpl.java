package com.kraytsman.service;

import com.kraytsman.domain.User;
import com.kraytsman.util.UserAttributesMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

@Service
public class LdapServiceImpl implements LdapService {

    @Value("${ldap.usersBase}")
    private String ldapUsersBase;

    @Autowired
    private LdapTemplate ldapTemplate;

    @Override
    public List<User> getAll() {
        List<User> result = ldapTemplate.search(ldapUsersBase, "(&(mail=*)(objectClass=person))", new UserAttributesMapper());
        result.sort(Comparator.comparing(User::getFullName));
        return result;
    }

    @Override
    public User getUser(String ldapId) {
        List<User> result = ldapTemplate.search(ldapUsersBase, "(uid=" + ldapId + ")", new UserAttributesMapper());
        return result.stream().findFirst().orElse(null);
    }
}
