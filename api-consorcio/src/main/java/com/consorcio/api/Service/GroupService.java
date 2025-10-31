package com.consorcio.api.Service;

import com.consorcio.api.Model.GroupModel;
import com.consorcio.api.Model.UserModel;
import com.consorcio.api.Repository.GroupRepository;
import com.consorcio.api.Repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class GroupService {
    @Autowired
    private GroupRepository groupRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PrizeService prizeService;

    @Autowired
    private PaymentService paymentService;

    @Transactional
    public ResponseEntity<?> create(Long userId, GroupModel group) {
        try {
            UserModel user = userRepository.findById(userId)
                    .orElseThrow(() -> new EntityNotFoundException("User with id " + userId + " not found!"));

            group.setCreatedBy(userId);
            user.getGroups().add(group);

            groupRepository.save(group);
            userRepository.save(user);

            prizeService.createPrizesByGroup(group);
            paymentService.createPaymentsByGroup(user, group);

            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("message", "Group created successfully!");
            return new ResponseEntity<>(errorResponse, HttpStatus.OK);
        }
        catch (Exception e)
        {
            // Handle other exceptions generically
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", 500);
            errorResponse.put("message", "An error occurred while creating the group.");
            return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Transactional
    public ResponseEntity<Object> joinGroup(Long userId, Long groupId)
    {
        try
        {
            UserModel user = userRepository.findById(userId)
                    .orElseThrow(() -> new EntityNotFoundException("User with id " + userId + " not found!"));

            GroupModel group = groupRepository.findById(groupId)
                    .orElseThrow(() -> new EntityNotFoundException("Group with id " + groupId + " not found!"));

            // Check if the group is full
            if (group.getUsers().size() >= group.getQuantidadePessoas())
            {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("error", 409); // Conflict
                errorResponse.put("message", "Group is full!");
                return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
            }

            // Check if user is already in the group
            if (user.getGroups().contains(group))
            {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("error", 409); // Conflict
                errorResponse.put("message", "User already joined this group!");
                return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
            }

            user.getGroups().add(group);
            userRepository.save(user);

            paymentService.createPaymentsByGroup(user, group);

            Map<String, Object> successResponse = new HashMap<>();
            successResponse.put("message", "User successfully joined the group!");
            return new ResponseEntity<>(successResponse, HttpStatus.OK);
        }
        catch (EntityNotFoundException e)
        {
            // Handle EntityNotFoundExceptions specifically
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", 404);
            errorResponse.put("message", e.getMessage());
            return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
        }
        catch (Exception e)
        {
            // Handle other exceptions generically
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", 500); // Internal Server Error
            errorResponse.put("message", "An error occurred while joining the group.");
            return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Transactional
    public ResponseEntity<Object> leaveGroup(Long userId, Long groupId)
    {
        try
        {
            // Find user and group
            UserModel user = userRepository.findById(userId)
                    .orElseThrow(() -> new EntityNotFoundException("User with id " + userId + " not found!"));

            GroupModel group = groupRepository.findById(groupId)
                    .orElseThrow(() -> new EntityNotFoundException("Group with id " + groupId + " not found!"));

            // Check if user is already in the group (optional)
            if (!user.getGroups().contains(group)) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("error", 404);
                errorResponse.put("message", "User is not currently a member of this group!");
                return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
            }

            // Check if user is the group creator (optional)
            if (userId.equals(group.getCreatedBy()))
            {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("error", 403); // Forbidden
                errorResponse.put("message", "Group creator cannot leave the group!");
                return new ResponseEntity<>(errorResponse, HttpStatus.FORBIDDEN);
            }

            // Remove the group from the user's groups
            user.getGroups().remove(group);
            userRepository.save(user);

            // Leave successful, return success message
            Map<String, Object> successResponse = new HashMap<>();
            successResponse.put("message", "User successfully left the group!");
            return new ResponseEntity<>(successResponse, HttpStatus.OK);
        }
        catch (EntityNotFoundException e)
        {
            // Handle EntityNotFoundExceptions specifically
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", 404);
            errorResponse.put("message", e.getMessage());
            return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
        }
        catch (Exception e)
        {
            // Handle other exceptions generically
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", 500); // Internal Server Error
            errorResponse.put("message", "An error occurred while leaving the group.");
            return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public List<GroupModel> readGroup() throws Exception
    {
        return groupRepository.findAll();
    }

    public ResponseEntity<Object> readById(long id)
    {
        Optional<GroupModel> grupoPesquisado = groupRepository.findById(id);

        if (grupoPesquisado.isEmpty())
        {
            // Group not found, return error message
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", 404);
            errorResponse.put("message", "Group not found!");
            return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
        }

        // Group found, return the group object (modify as needed)
        return new ResponseEntity<>(grupoPesquisado.get(), HttpStatus.OK);
    }

    @Transactional
    public ResponseEntity<Object> delete(Long userId, Long groupId)
    {
        try
        {
            // Find user and group
            UserModel user = userRepository.findById(userId)
                    .orElseThrow(() -> new EntityNotFoundException("User with id " + userId + " not found!"));

            GroupModel group = groupRepository.findById(groupId)
                    .orElseThrow(() -> new EntityNotFoundException("Group with id " + groupId + " not found!"));

            boolean groupAdmin = userId.equals(group.getCreatedBy());

            // Check if user is or not the group creator
            if (!groupAdmin)
            {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("error", 403); // Forbidden
                errorResponse.put("message", "Only group creator can delete the group!");
                return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
            }
            else
            {
                if (group.getUsers().size() > 1) {
                    Map<String, Object> errorResponse = new HashMap<>();
                    errorResponse.put("error", 403); // Forbidden
                    errorResponse.put("message", "Group creator cannot delete the group while other users are in the group!");
                    return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
                }
            }

            // Remove the group from the user's groups
            user.getGroups().remove(group);
            userRepository.save(user);

            groupRepository.deleteById(groupId);

            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("message", "Group deleted successfully!");
            return new ResponseEntity<>(errorResponse, HttpStatus.OK);
        }
        catch (EntityNotFoundException e)
        {
            // Handle EntityNotFoundExceptions specifically
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", 404);
            errorResponse.put("message", e.getMessage());
            return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
        }
    }
}
