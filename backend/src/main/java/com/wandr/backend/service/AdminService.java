package com.wandr.backend.service;

import com.wandr.backend.dto.*;

public interface AdminService {

    ApiResponse<UserDetailsDTO> loginAdmin(UserLoginDTO request);

    public String getSalt(String email);

    ApiResponse<Void> updateAdminJwt (String jwt, Long adminId);

}
