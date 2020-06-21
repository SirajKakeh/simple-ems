package com.task.ems.controller;

import com.task.ems.config.JwtTokenUtil;
import com.task.ems.exception.ResourceNotFoundException;
import com.task.ems.model.JwtRequest;
import com.task.ems.model.JwtResponse;
import com.task.ems.model.UserDTO;
import com.task.ems.model.UserEntity;
import com.task.ems.repository.DepartmentRepository;
import com.task.ems.repository.UserRepository;
import com.task.ems.service.JwtUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin
public class JwtAuthenticationController {

    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private JwtTokenUtil jwtTokenUtil;
    @Autowired
    private JwtUserDetailsService userDetailsService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private DepartmentRepository departmentRepository;

    public JwtAuthenticationController(BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    @RequestMapping(path = "/login", method = RequestMethod.POST, consumes = "application/json")
    public ResponseEntity<?> createAuthenticationToken(@RequestBody JwtRequest authenticationRequest) throws Exception {

        authenticate(authenticationRequest.getUsername(), authenticationRequest.getPassword());

        final UserDetails userDetails = userDetailsService
                .loadUserByUsername(authenticationRequest.getUsername());

        final String token = jwtTokenUtil.generateToken(userDetails);

        return ResponseEntity.ok(new JwtResponse(token));
    }

    @PostMapping(path = "/register")
    public UserEntity createUser(@RequestBody UserDTO userDTO) {
        Long departmentId = userDTO.getDepartmentId();
        if (departmentId == null) {
            departmentId = Long.valueOf(0);
        }
        Long finalDepartmentId = departmentId;
        return departmentRepository.findById(userDTO.getDepartmentId()).map(department -> {
            UserEntity user = new UserEntity();
            user.setDepartmentEntity(department);
            user.setUsername(userDTO.getUsername());
            user.setPassword(bCryptPasswordEncoder.encode(userDTO.getPassword()));
            return userRepository.save(user);
        }).orElseThrow(() -> new ResourceNotFoundException("departmentId " + finalDepartmentId + " not found"));
    }

    private void authenticate(String username, String password) throws Exception {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
        } catch (DisabledException e) {
            throw new Exception("USER_DISABLED", e);
        } catch (BadCredentialsException e) {
            throw new Exception("INVALID_CREDENTIALS", e);
        }
    }
}
