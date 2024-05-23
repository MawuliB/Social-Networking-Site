package com.mawuli.sns.contact;

import com.mawuli.sns.domain.dto.mappers.ContactMapper;
import com.mawuli.sns.domain.dto.request.ContactDto;
import com.mawuli.sns.domain.entities.Contact;
import com.mawuli.sns.repositories.ContactRepository;
import com.mawuli.sns.repositories.UserAccessRepository;
import com.mawuli.sns.security.domain.entities.User;
import com.mawuli.sns.services.ContactService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ContactServiceTest {

    @Mock
    private ContactRepository contactRepository;

    @Mock
    private UserAccessRepository userAccessRepository;

    @Mock
    private ContactMapper contactMapper;

    @InjectMocks
    private ContactService contactService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testAddContactToBlackList() {
        Contact contact = new Contact();
        contact.setId(1L);
        contact.setIsBlacklisted(false);

        when(contactRepository.findById(1)).thenReturn(Optional.of(contact));
        when(contactRepository.save(contact)).thenReturn(contact);

        Contact result = contactService.addContactToBlackList(1);

        assertTrue(result.getIsBlacklisted());
        verify(contactRepository, times(1)).findById(1);
        verify(contactRepository, times(1)).save(contact);
    }

    @Test
    public void testRemoveContactFromBlackList() {
        Contact contact = new Contact();
        contact.setId(1L);
        contact.setIsBlacklisted(true);

        when(contactRepository.findById(1)).thenReturn(Optional.of(contact));
        when(contactRepository.save(contact)).thenReturn(contact);

        Contact result = contactService.removeContactFromBlackList(1);

        assertFalse(result.getIsBlacklisted());
        verify(contactRepository, times(1)).findById(1);
        verify(contactRepository, times(1)).save(contact);
    }

    @Test
    public void testGetInvitationsByUserId() {
        Long userId = 1L;
        User user = new User();
        user.setId(userId);

        Contact contact1 = new Contact();
        contact1.setId(1L);
        contact1.setIsAccepted(false);

        Contact contact2 = new Contact();
        contact2.setId(2L);
        contact2.setIsAccepted(false);

        List<Contact> contacts = Arrays.asList(contact1, contact2);

        when(userAccessRepository.findById(userId)).thenReturn(Optional.of(user));
        when(contactRepository.findAllByContactAndIsAcceptedFalse(user)).thenReturn(contacts);

        List<Contact> result = contactService.getInvitationsByUserId(userId);

        assertEquals(2, result.size());
        verify(userAccessRepository, times(1)).findById(userId);
        verify(contactRepository, times(1)).findAllByContactAndIsAcceptedFalse(user);
    }

    @Test
    public void testGetAllContactByContactId() {
        Long userId = 1L;
        User user = new User();
        user.setId(userId);

        Contact contact1 = new Contact();
        contact1.setId(1L);

        Contact contact2 = new Contact();
        contact2.setId(2L);

        List<Contact> contacts = Arrays.asList(contact1, contact2);

        when(userAccessRepository.findById(userId)).thenReturn(Optional.of(user));
        when(contactRepository.findAllByUserAndIsAcceptedTrueAndIsBlacklistedFalse(user)).thenReturn(contacts);

        List<Contact> result = contactService.getAllContactByContactId(userId);

        assertEquals(2, result.size());
        verify(userAccessRepository, times(1)).findById(userId);
        verify(contactRepository, times(1)).findAllByUserAndIsAcceptedTrueAndIsBlacklistedFalse(user);
    }

    @Test
    public void testAcceptContactInvitation() {
        Integer contactId = 1;

        Contact contact = new Contact();
        contact.setId(1L);
        contact.setIsAccepted(false);

        Contact newContact = new Contact();
        newContact.setId(2L);
        newContact.setIsAccepted(false);

        when(contactRepository.findById(contactId)).thenReturn(Optional.of(contact));
        when(contactRepository.save(any(Contact.class))).thenAnswer(invocation -> {
            Contact savedContact = invocation.getArgument(0);
            if (savedContact.getId() == null) {
                savedContact.setId(2L); // This is the newContact
                newContact.setIsAccepted(savedContact.getIsAccepted());
            }
            return savedContact;
        });

        contactService.acceptContactInvitation(contactId);

        assertTrue(contact.getIsAccepted());
        assertTrue(newContact.getIsAccepted());
        verify(contactRepository, times(1)).findById(contactId);
        verify(contactRepository, times(2)).save(any(Contact.class));
    }

    @Test
    public void testRemoveContactFromContactList() {
        Integer contactId = 1;

        Contact contact = new Contact();
        contact.setId(1L);
        contact.setIsBlacklisted(false);

        when(contactRepository.findById(contactId)).thenReturn(Optional.of(contact));

        contactService.removeContactFromContactList(contactId);

        verify(contactRepository, times(1)).findById(contactId);
        verify(contactRepository, times(1)).delete(contact);
    }

    @Test
    public void testAddContactToContactList() {
        ContactDto contactDto = new ContactDto(1L, 2L, false, false);

        User user = new User();
        user.setId(1L);

        User contactUser = new User();
        contactUser.setId(2L);

        Contact contactEntity = new Contact();
        contactEntity.setUser(user);
        contactEntity.setContact(contactUser);
        contactEntity.setIsAccepted(false);

        when(contactMapper.mapToContact(contactDto)).thenReturn(contactEntity);
        when(contactRepository.findByUserAndContact(user, contactUser)).thenReturn(Optional.empty());
        when(contactRepository.findByUserAndContact(contactUser, user)).thenReturn(Optional.empty());
        when(contactRepository.save(contactEntity)).thenReturn(contactEntity);

        Contact result = contactService.addContactToContactList(contactDto);

        assertEquals(contactEntity, result);
        verify(contactMapper, times(1)).mapToContact(contactDto);
        verify(contactRepository, times(1)).findByUserAndContact(user, contactUser);
        verify(contactRepository, times(1)).findByUserAndContact(contactUser, user);
        verify(contactRepository, times(1)).save(contactEntity);
    }
}