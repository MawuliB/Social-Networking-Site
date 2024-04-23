package com.mawuli.sns.services;

import com.mawuli.sns.repositories.BlackListRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class BlackListService {

    private final BlackListRepository blackListRepository;
}
