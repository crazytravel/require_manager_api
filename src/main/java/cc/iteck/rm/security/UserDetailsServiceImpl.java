package cc.iteck.rm.security;

import cc.iteck.rm.model.security.JwtUserDetails;
import cc.iteck.rm.service.UserService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserService userService;

    public UserDetailsServiceImpl(UserService userService) {
        this.userService = userService;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        var userDto = userService.findUserByUsername(username);
        return JwtUserDetails.builder()
                .userId(userDto.getId())
                .username(userDto.getUsername())
                .password(userDto.getPassword())
                .permissions(userDto.getPermissions())
                .build();
    }
}
