package com.portifolio.api.service;

import com.portifolio.api.dto.request.ProfileRequest;
import com.portifolio.api.dto.response.ProfileResponse;

public interface ProfileService {

    ProfileResponse get();

    ProfileResponse update(ProfileRequest request);
}
