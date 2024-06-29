package com.wandr.backend.service;

import com.wandr.backend.dto.*;

public interface AdminService {

    ApiResponse<UserDetailsDTO> loginAdmin(UserLoginDTO request);

    ApiResponse<Void> updateAdminJwt (String jwt, Long travellerId);

}
