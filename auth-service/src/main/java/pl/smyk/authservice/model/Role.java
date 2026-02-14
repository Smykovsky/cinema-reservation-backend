package pl.smyk.authservice.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import static pl.smyk.authservice.model.Permission.*;

@RequiredArgsConstructor
@Getter
public enum Role {
    USER(Set.of(USER_READ)),
    OPERATOR(Set.of(OPERATOR_READ, OPERATOR_WRITE, OPERATOR_DELETE, USER_READ)),
    ADMIN(
            Set.of(
                    ADMIN_READ,
                    ADMIN_WRITE,
                    ADMIN_DELETE,
                    OPERATOR_READ,
                    OPERATOR_WRITE,
                    OPERATOR_DELETE,
                    USER_READ,
                    USER_WRITE,
                    USER_DELETE
            )
    );

    private final Set<Permission> permissions;

    public List<SimpleGrantedAuthority> getAuthorities() {
        var authorities = getPermissions()
                .stream()
                .map(permission -> new SimpleGrantedAuthority(permission.name()))
                .collect(Collectors.toList());
        authorities.add(new SimpleGrantedAuthority("ROLE_" + this.name()));
        return authorities;
    }
}
