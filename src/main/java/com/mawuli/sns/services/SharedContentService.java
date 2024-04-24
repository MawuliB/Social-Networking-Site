package com.mawuli.sns.services;

import com.mawuli.sns.repositories.SharedContentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SharedContentService {

        private final SharedContentRepository sharedContentRepository;
}
