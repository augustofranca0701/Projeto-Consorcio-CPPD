package com.consorcio.api.Controller;

import com.consorcio.api.Model.GroupModel;
import com.consorcio.api.Service.GroupService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("groups")
public class GroupController
{
    @Autowired
    private GroupService groupService;

    @PostMapping("{userId}/create")
    public ResponseEntity<?> create(@PathVariable("userId") Long userId, @RequestBody @Valid GroupModel group)
    {
        return groupService.create(userId, group);
    }

    @PostMapping("join/{userId}/{groupId}")
    public ResponseEntity<Object> joinGroup(@PathVariable("userId") Long userId, @PathVariable("groupId") Long groupId)
    {
        return groupService.joinGroup(userId, groupId);
    }

    @PostMapping("leave/{userId}/{groupId}")
    public ResponseEntity<Object> leaveGroup(@PathVariable("userId") Long userId, @PathVariable("groupId") Long groupId)
    {
        return groupService.leaveGroup(userId, groupId);
    }

    @GetMapping("")
    public List<GroupModel> readGroup() throws Exception{

        return groupService.readGroup();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> readGroupById(@PathVariable("id") Long id) throws Exception{

        return groupService.readById(id);
    }

    @DeleteMapping("delete/{userId}/{groupId}")
    public ResponseEntity<Object> deleteGroup(@PathVariable("userId") Long userId, @PathVariable("groupId") Long groupId)
    {
        return groupService.delete(userId, groupId);
    }
}
