package com.learnforge.api.client.user;


import com.learnforge.api.client.user.fallback.UserClientFallback;
import com.learnforge.api.dto.user.LoginFormDTO;
import com.learnforge.api.dto.user.UserDTO;
import com.learnforge.common.domain.dto.LoginUserDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(value = "user-service", fallbackFactory = UserClientFallback.class)
public interface UserClient {

    /**
     * Query user id by phone number
     * @param phone phone number
     * @return user id
     */
    @GetMapping("/users/ids")
    Long exchangeUserIdWithPhone(@RequestParam("phone") String phone);

    /**
     * Login interface
     * @param loginDTO login information
     * @param isStaff whether the user is staff
     * @return user details
     */
    @PostMapping("/users/detail/{isStaff}")
    LoginUserDTO queryUserDetail(@RequestBody LoginFormDTO loginDTO, @PathVariable("isStaff") boolean isStaff);

    /**
     * Query user type
     * @param id user id
     * @return user type, 0 - regular learner, 1 - teacher, 2 - other staff
     */
    @GetMapping("/users/{id}/type")
    Integer queryUserType(@PathVariable("id") Long id);

    /**
     * <h1>Batch query user information by id</h1>
     * @param ids collection of user ids
     * @return user collection
     */
    @GetMapping("/users/list")
    List<UserDTO> queryUserByIds(@RequestParam("ids") Iterable<Long> ids);


    /**
     * Query single student information by id
     * @param id user id
     * @return student
     */
    @GetMapping("/users/{id}")
    UserDTO queryUserById(@PathVariable("id") Long id);
}
