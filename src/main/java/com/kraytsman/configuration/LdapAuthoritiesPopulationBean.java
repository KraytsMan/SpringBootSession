package com.kraytsman.configuration;

import com.kraytsman.domain.Authority;
import com.kraytsman.repository.UserRepository;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ldap.core.DirContextOperations;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.ldap.userdetails.LdapAuthoritiesPopulator;
import org.springframework.security.ldap.userdetails.LdapAuthority;
import org.springframework.stereotype.Component;

import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Component
public class LdapAuthoritiesPopulationBean implements LdapAuthoritiesPopulator {

    @Autowired
    private UserRepository userRepository;

    @Override
    public Collection<? extends GrantedAuthority> getGrantedAuthorities(DirContextOperations userData, String username) {
        return Stream.concat(getUserAuthorities(username), Stream.of("ROLE_USER"))
                .map(roleName -> createAuthority(roleName, userData))
                .collect(Collectors.toList());
    }

    private LDAPAuthority createAuthority(String roleName, DirContextOperations userData) {
        LDAPAuthority authority = new LDAPAuthority(roleName, "ou=People,ou=Users,dc=ldap,dc=sjua");
        if (userData != null) {
            authority.setFullName(getAttribute(userData, "cn"));
            authority.setEmail(getAttribute(userData, "mail"));
        }
        return authority;
    }

    private Stream<String> getUserAuthorities(String userName) {
        return Optional.ofNullable(userRepository.findByLdapId(userName))
                .flatMap(u -> Optional.ofNullable(u.getAuthorities()))
                .map(Collection::stream)
                .orElse(Stream.empty())
                .map(Authority::getAuthority);
    }

    private String getAttribute(DirContextOperations userData, String attrName) {
        try {
            Attributes attributes = userData != null ? userData.getAttributes("") : null;
            Attribute attribute = attributes != null ? attributes.get(attrName) : null;
            return (String) (attribute != null ? attribute.get(0) : null);
        } catch (NamingException e) {
            log.error("Can't retrieve attribute " + attrName + " for user " + userData);
            return null;
        }
    }


    @Data
    @EqualsAndHashCode(callSuper = true)
    private static class LDAPAuthority extends LdapAuthority {

        private String fullName;

        private String email;

        LDAPAuthority(String role, String dn) {
            super(role, dn);
        }
    }

}
