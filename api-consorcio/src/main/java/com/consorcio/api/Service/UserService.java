package com.consorcio.api.service;

import com.consorcio.api.dto.UserDTO.UserLoginDTO;
import com.consorcio.api.dto.UserDTO.UserLoginUpdateDTO;
import com.consorcio.api.dto.UserDTO.UserUpdateDTO;
import com.consorcio.api.model.GroupModel;
import com.consorcio.api.model.UserModel;
import com.consorcio.api.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.http.ResponseEntity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Transactional
    public ResponseEntity<Object> create(UserModel user) {
        try {
            userRepository.save(user);
            return new ResponseEntity<>(user, HttpStatus.OK);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", 500);
            errorResponse.put("message", e.getMessage());
            return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Transactional
    public ResponseEntity<Object> login(UserLoginDTO user) {
        try {
            Optional<UserModel> opt = userRepository.findByEmail(user.getEmail());
            if (opt.isEmpty()) {
                return generateErrorResponse(401, "User or password is incorrect.");
            }
            UserModel existingUser = opt.get();

            if (existingUser.getPassword() == null || !existingUser.getPassword().equals(user.getPassword())) {
                return generateErrorResponse(401, "User or password is incorrect.");
            }

            return new ResponseEntity<>(existingUser, HttpStatus.OK);
        } catch (Exception e) {
            return generateErrorResponse(500, e.getMessage());
        }
    }

    private ResponseEntity<Object> generateErrorResponse(int errorCode, String message) {
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("error", errorCode);
        errorResponse.put("message", message);
        return new ResponseEntity<>(errorResponse, HttpStatus.valueOf(errorCode));
    }

    public List<UserModel> readUsers() throws Exception {
        return userRepository.findAll();
    }

    public ResponseEntity<Object> readById(Long id) {
        Optional<UserModel> userOptional = userRepository.findById(id);

        if (userOptional.isEmpty()) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", 404);
            errorResponse.put("message", "User not found!");
            return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(userOptional.get(), HttpStatus.OK);
    }

    public ResponseEntity<Object> getUserGroups(Long userId) {
        Optional<UserModel> userOptional = userRepository.findById(userId);

        if (userOptional.isEmpty()) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", 404);
            errorResponse.put("message", "User not found!");
            return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
        }

        List<GroupModel> groups = userOptional.get().getGroups();

        return new ResponseEntity<>(groups, HttpStatus.OK);
    }

    @Transactional
    public ResponseEntity<Object> update(UserUpdateDTO user, Long id) {
        try {
            Optional<UserModel> userOptional = userRepository.findById(id);
            if (userOptional.isEmpty()) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("error", 404);
                errorResponse.put("message", "User not found with id " + id);
                return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
            }

            UserModel userToUpdate = userOptional.get();
            userToUpdate.setName(user.getName());
            userToUpdate.setPhone(user.getPhone());
            userToUpdate.setAddress(user.getAddress());
            userToUpdate.setComplement(user.getComplement());
            userToUpdate.setState(user.getState());
            userToUpdate.setCity(user.getCity());

            userRepository.save(userToUpdate);

            Map<String, Object> successResponse = new HashMap<>();
            successResponse.put("message", "User updated successfully!");
            return new ResponseEntity<>(successResponse, HttpStatus.OK);

        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", 500);
            errorResponse.put("message", e.getMessage());
            return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Transactional
    public ResponseEntity<Object> updateLogin(UserLoginUpdateDTO user, Long id) {
        try {
            Optional<UserModel> userOptional = userRepository.findById(id);
            if (userOptional.isEmpty()) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("error", 404);
                errorResponse.put("message", "User not found with id " + id);
                return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
            }

            UserModel userToUpdate = userOptional.get();
            userToUpdate.setEmail(user.getEmail());
            userToUpdate.setPassword(user.getPassword());

            userRepository.save(userToUpdate);

            Map<String, Object> successResponse = new HashMap<>();
            successResponse.put("message", "User updated successfully!");
            return new ResponseEntity<>(successResponse, HttpStatus.OK);

        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", 500);
            errorResponse.put("message", e.getMessage());
            return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Transactional
    public ResponseEntity<Object> delete(Long id) {
        try {
            Optional<UserModel> userOptional = userRepository.findById(id);

            if (userOptional.isEmpty()) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("error", 404);
                errorResponse.put("message", "User not found with id " + id);
                return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
            }

            UserModel user = userOptional.get();
            boolean hasGroups = !user.getGroups().isEmpty();

            if (hasGroups) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("error", 409);
                errorResponse.put("message", "User cannot be deleted as it belongs to groups!");
                return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
            }

            userRepository.deleteById(id);
            return new ResponseEntity<>(HttpStatus.OK);

        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", 500);
            errorResponse.put("message", "An error occurred while deleting the user.");
            return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
