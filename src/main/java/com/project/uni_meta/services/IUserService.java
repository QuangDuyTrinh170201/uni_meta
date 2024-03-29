package com.project.uni_meta.services;

import com.project.uni_meta.dtos.MailDTO;
import com.project.uni_meta.dtos.UpdateUserDTO;
import com.project.uni_meta.dtos.UserDTO;
import com.project.uni_meta.dtos.UserInforDTO;
import com.project.uni_meta.exceptions.DataNotFoundException;
import com.project.uni_meta.exceptions.PermissionDenyException;
import com.project.uni_meta.models.User;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface IUserService {
    User CreateUser(UserDTO userDTO) throws DataNotFoundException, PermissionDenyException;
    Map<String, Object> Login(String username, String password) throws DataNotFoundException;

    public List<User> getAllUsers() throws Exception;

    public Optional<User> getUserById(Long id) throws Exception;

    public User getUserDetailsFromToken(String token) throws Exception;

    public User updateUserInforByAdmin(Long userId, UpdateUserDTO updatedUserDTO) throws Exception;

    public boolean sendMailPassword(MailDTO mailDTO) throws DataNotFoundException;

    public User changePassword(Long id, UserInforDTO userInforDTO) throws DataNotFoundException;
}
