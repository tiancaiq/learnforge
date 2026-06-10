package com.learnforge.message.controller;


import com.learnforge.common.domain.dto.PageDTO;
import com.learnforge.message.domain.dto.UserInboxDTO;
import com.learnforge.message.domain.dto.UserInboxFormDTO;
import com.learnforge.message.domain.query.UserInboxQuery;
import com.learnforge.message.service.IUserInboxService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 * User notification record frontend controller
 * </p>
 *
 * @author LearnForge contributors
 * @since 2022-08-19
 */
@Api(tags = "User inbox interface")
@RestController
@RequestMapping("/inboxes")
@RequiredArgsConstructor
public class UserInboxController {

    private final IUserInboxService inboxService;

    @PostMapping
    @ApiOperation("Send private message")
    public Long sentMessageToUser(@RequestBody UserInboxFormDTO userInboxFormDTO){
        return inboxService.sentMessageToUser(userInboxFormDTO);
    }

    @ApiOperation("Page query inbox")
    @GetMapping
    public PageDTO<UserInboxDTO> queryUserInBoxesPage(UserInboxQuery query){
        return inboxService.queryUserInBoxesPage(query);
    }
}
