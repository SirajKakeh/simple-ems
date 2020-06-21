package com.task.ems.controller;

import com.task.ems.exception.ResourceNotFoundException;
import com.task.ems.model.UserDTO;
import com.task.ems.model.UserEntity;
import com.task.ems.repository.DepartmentRepository;
import com.task.ems.repository.UserRepository;
import com.task.ems.service.AWSS3Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;

@RestController
@RequestMapping(path = "/employees", produces = MediaType.APPLICATION_JSON_VALUE)
public class EmployeeController {
    String[] allowedMimeTypes = {
            MediaType.APPLICATION_PDF_VALUE,
            "application/vnd.openxmlformats-officedocument.wordprocessingml.document"
    };

    @Autowired
    private AWSS3Service service;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private DepartmentRepository departmentRepository;

    @GetMapping()
    public @ResponseBody
    Iterable<UserEntity> getAllEmployees() {
        return userRepository.findAll();
    }

    @GetMapping(path = "search")
    public Iterable<UserEntity> getEmployeeByDepartmentOrUsername(@RequestParam(name = "username", required = false) String username, @RequestParam(name = "departmentId", required = false) Long departmentId) {
        String usernameParam = null;
        if (username != null) {
            usernameParam = "%" + username + "%";
        }
        Iterable<UserEntity> employees = userRepository.findByDepartmentIdOrUserName(departmentId, usernameParam);
        return employees;
    }

    @PostMapping(value = "/{userId}/cv")
    public ResponseEntity<String> postCV(@PathVariable(name = "userId") final String userId, @RequestPart(value = "cv") final MultipartFile multipartFile) {
        String mimeType = multipartFile.getContentType();
        if (!Arrays.asList(allowedMimeTypes).contains(mimeType.toLowerCase())) {
            return new ResponseEntity("{ \"error\": \"Invalid file type\" }", HttpStatus.BAD_REQUEST);
        }
        service.uploadFile(userId, multipartFile);
        final String response = "{ \"status\": \"Success\" }";
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PutMapping("/employee/{employeeId}")
    public UserEntity updateComment(@PathVariable(value = "employeeId") Long employeeId,
                                    @RequestBody UserDTO userDTO) {

        if (!departmentRepository.existsById(userDTO.getDepartmentId())) {
            throw new ResourceNotFoundException("department " + userDTO.getDepartmentId() + " not found");
        }

        return userRepository.findById(Math.toIntExact(employeeId)).map(user -> {
            user.setUsername(userDTO.getUsername());
            return userRepository.save(user);
        }).orElseThrow(() -> new ResourceNotFoundException("employeeId " + employeeId + "not found"));
    }

    @DeleteMapping("/employee/{employeeId}")
    public ResponseEntity<?> deletePost(@PathVariable(value = "employeeId") Long employeeId) {
        return userRepository.findById(Math.toIntExact(employeeId)).map(user -> {
            userRepository.delete(user);
            return ResponseEntity.ok().build();
        }).orElseThrow(() -> new ResourceNotFoundException("employeeId " + employeeId + " not found"));
    }
}
