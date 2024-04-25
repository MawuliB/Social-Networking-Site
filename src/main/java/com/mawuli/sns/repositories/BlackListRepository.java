package com.mawuli.sns.repositories;

import com.mawuli.sns.domain.entities.BlackList;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

public interface BlackListRepository extends CrudRepository<BlackList, Integer>, PagingAndSortingRepository<BlackList, Integer> {
    public BlackList findByUser_IdAndContact_Id(Long userId, Long contactId);
    public List<BlackList> findByUser_Id(Long userId);
}