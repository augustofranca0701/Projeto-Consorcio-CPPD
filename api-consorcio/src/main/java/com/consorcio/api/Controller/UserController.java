package com.consorcio.api.Controller;

import com.consorcio.api.DTO.UserDTO.UserLoginDTO;
import com.consorcio.api.DTO.UserDTO.UserLoginUpdateDTO;
import com.consorcio.api.DTO.UserDTO.UserUpdateDTO;
import com.consorcio.api.Model.UserModel;
import com.consorcio.api.Service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("users")
public class UserController
{
    @Autowired
    private UserService userService;

    @PostMapping("signup")
    public ResponseEntity<Object> signup(@RequestBody @Valid UserModel user)
    {
        return userService.create(user);
    }

    @PostMapping("login")
    public ResponseEntity<Object> login(@RequestBody @Valid UserLoginDTO user)
    {
        return userService.login(user);
    }

    @GetMapping("")
    public List<UserModel> readUsers() throws Exception
    {
        return userService.readUsers();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> readUserById(@PathVariable("id") Long id) throws Exception
    {
        return userService.readById(id);
    }

    @GetMapping("/{userId}/groups")
    public ResponseEntity<Object> getUserGroups(@PathVariable("userId") Long userId) throws Exception
    {
        return userService.getUserGroups(userId);
    }

    @PutMapping("/{id}/update")
    public ResponseEntity<Object> update(@PathVariable("id") Long id, @RequestBody @Valid UserUpdateDTO user) throws Exception
    {
        return userService.update(user, id);
    }

    @PutMapping("/{id}/updatelogin")
    public ResponseEntity<Object> updateLogin(@PathVariable("id") Long id, @RequestBody @Valid UserLoginUpdateDTO user) throws Exception
    {
        return userService.updateLogin(user, id);
    }

    @DeleteMapping("/{id}/delete")
    public ResponseEntity<Object> delete(@PathVariable("id") Long id) throws Exception
    {
        return userService.delete(id);
    }
}
