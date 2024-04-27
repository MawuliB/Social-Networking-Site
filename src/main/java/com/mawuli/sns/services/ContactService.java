package com.mawuli.sns.services;

import com.mawuli.sns.domain.dto.mappers.ContactMapper;
import com.mawuli.sns.domain.dto.request.BlackListDto;
import com.mawuli.sns.domain.dto.request.ContactDto;
import com.mawuli.sns.domain.entities.Contact;
import com.mawuli.sns.repositories.ContactRepository;
import com.mawuli.sns.repositories.UserAccessRepository;
import com.mawuli.sns.security.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ContactService {

    private final ContactRepository contactRepository;
    private final UserAccessRepository userAccessRepository;
    private final ContactMapper contactMapper;


    public Contact addContactToContact(ContactDto contactDto) {
        if(contactDto ==null) throw new IllegalArgumentException("Contact cannot be null");
        var contactEntity = ContactMapper.mapToContact(contactDto);

        return null;

    }

    public void removeContactFromContact(Integer contactId) {

    }

    public List<Contact> getContactByContactId(Long aLong) {
        return null;
    }
}
