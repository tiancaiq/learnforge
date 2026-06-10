package com.learnforge.api.client.user.fallback;

import com.learnforge.api.client.user.UserClient;
import com.learnforge.api.dto.user.LoginFormDTO;
import com.learnforge.api.dto.user.UserDTO;
import com.learnforge.common.domain.dto.LoginUserDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;

import java.util.Collections;
import java.util.List;

@Slf4j
public class UserClientFallback implements FallbackFactory<UserClient> {
    @Override
    public UserClient create(Throwable cause) {
        log.error("Query user service exception", cause);
        return new UserClient() {
            @Override
            public Long exchangeUserIdWithPhone(String phone) {
                return null;
            }

            @Override
            public LoginUserDTO queryUserDetail(LoginFormDTO loginDTO, boolean isStaff) {
                return null;
            }

            @Override
            public Integer queryUserType(Long id) {
                return null;
            }

            @Override
            public List<UserDTO> queryUserByIds(Iterable<Long> ids) {
                return Collections.emptyList();
            }

            @Override
            public UserDTO queryUserById(Long id) {
                return null;
            }
        };
    }
}
