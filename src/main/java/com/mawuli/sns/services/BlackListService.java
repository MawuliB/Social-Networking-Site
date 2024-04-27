package com.mawuli.sns.services;

import com.mawuli.sns.domain.dto.mappers.BlackListMapper;
import com.mawuli.sns.domain.dto.request.BlackListDto;
import com.mawuli.sns.domain.entities.BlackList;
import com.mawuli.sns.exceptionhandler.graphql.EntityNotFoundException;
import com.mawuli.sns.repositories.BlackListRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@RequiredArgsConstructor
@Service
public class BlackListService {

    private final BlackListRepository blackListRepository;
    private final BlackListMapper blackListMapper;

    public BlackList addContactToBlackList(BlackListDto blackListDto) {
        if(blackListDto == null) throw new IllegalArgumentException("BlackListDto cannot be null");
        var blackList = blackListMapper.mapToBlackList(blackListDto);

        // check if a contact to blacklisted contact already exists
        Optional<BlackList> existingBlackList = Optional.ofNullable(blackListRepository.findByUser_IdAndContact_Id(blackList.getUser().getId(), blackList.getContact().getId()));
        if(existingBlackList.isPresent()) {
            throw new IllegalArgumentException("Contact already exists in blacklist");
        }

        return blackListRepository.save(blackList);
    }


    public void removeContactFromBlackList(Integer blackListId) {
        BlackList blackListEntity = blackListRepository.findById(blackListId)
                .orElseThrow(() -> new EntityNotFoundException("Blacklist not found"));
        blackListRepository.delete(blackListEntity);
    }

    public List<BlackList> getBlackListByContactId(Long contactId) {
        return blackListRepository.findByUser_Id(contactId);
    }
}
