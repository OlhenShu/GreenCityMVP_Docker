package greencity.service;

import greencity.ModelUtils;
import greencity.client.RestClient;
import greencity.dto.PageableAdvancedDto;
import greencity.dto.event.AddEventDtoRequest;
import greencity.dto.event.AddEventDtoResponse;
import greencity.dto.event.EventDto;
import greencity.dto.tag.TagVO;
import greencity.entity.Event;
import greencity.entity.Tag;
import greencity.entity.User;
import greencity.enums.TagType;
import greencity.exception.exceptions.BadRequestException;
import greencity.repository.EventRepo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.multipart.MultipartFile;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
class EventServiceImplTest {
    @Mock
    ModelMapper modelMapper;

    @Mock
    EventRepo eventRepo;

    @Mock
    RestClient restClient;

    @Mock
    TagsService tagService;

    @Mock
    private FileService fileService;

    @InjectMocks
    EventServiceImpl eventService;

    @Test
    void save() {
        EventDto eventDto = ModelUtils.getEventDto();
        AddEventDtoRequest addEventDtoRequest = ModelUtils.addEventDtoRequest;
        Event event = ModelUtils.getEvent();
        List<Tag> tags = ModelUtils.getEventTags();

        when(modelMapper.map(addEventDtoRequest, Event.class)).thenReturn(event);
        when(restClient.findByEmail(anyString())).thenReturn(ModelUtils.TEST_USER_VO);
        when(modelMapper.map(ModelUtils.TEST_USER_VO, User.class)).thenReturn(ModelUtils.getUser());
        when(eventRepo.save(event)).thenReturn(event);
        when(modelMapper.map(event, EventDto.class)).thenReturn(eventDto);
        List<TagVO> tagVOList = Collections.singletonList(ModelUtils.getTagVO());
        when(tagService.findTagsByNamesAndType(anyList(), eq(TagType.ECO_NEWS))).thenReturn(tagVOList);
        when(modelMapper.map(tagVOList, new TypeToken<List<Tag>>() {
        }.getType())).thenReturn(tags);

        assertEquals(eventDto, eventService.save(addEventDtoRequest, ModelUtils.getUser().getEmail(), null));

        MultipartFile multipartFile = ModelUtils.getMultipartFile();
        when(fileService.upload(multipartFile)).thenReturn("/url1");
        assertEquals(eventDto,
            eventService.save(addEventDtoRequest, ModelUtils.getUser().getEmail(),
                new MultipartFile[] {multipartFile}));

        MultipartFile[] multipartFiles = ModelUtils.getMultipartFiles();
        when(fileService.upload(multipartFiles[0])).thenReturn("/url1");
        when(fileService.upload(multipartFiles[1])).thenReturn("/url2");
        assertEquals(eventDto,
            eventService.save(addEventDtoRequest, ModelUtils.getUser().getEmail(), multipartFiles));
    }

    @Test
    void delete() {
        Event event = ModelUtils.getEvent();
        when(modelMapper.map(restClient.findByEmail(ModelUtils.getUserVO().getEmail()), User.class))
            .thenReturn(ModelUtils.getUser());
        when(eventRepo.getOne(any())).thenReturn(event);

        eventService.delete(event.getId(), ModelUtils.getUserVO().getEmail());

        verify(eventRepo).delete(event);
    }

    @Test
    void deleteWithException() throws BadRequestException {
        Event event = ModelUtils.getEvent();
        User user = ModelUtils.getUser();
        user.setId(2L);
        when(modelMapper.map(restClient.findByEmail(ModelUtils.getUserVO().getEmail()), User.class)).thenReturn(user);
        when(eventRepo.getOne(any())).thenReturn(event);
        String userEmail = ModelUtils.getUserVO().getEmail();
        Long eventId = event.getId();

        try {
            eventService.delete(eventId, userEmail);
            fail();
        } catch (RuntimeException re) {
        }

    }

    @Test
    void getEvent() {
        Event event = ModelUtils.getEvent();
        EventDto eventDto = ModelUtils.getEventDto();
        when(eventRepo.getOne(anyLong())).thenReturn(event);
        when(modelMapper.map(event, EventDto.class)).thenReturn(eventDto);
        EventDto actual = eventService.getEvent(1L);
        assertEquals(eventDto.getId(), actual.getId());
        assertEquals(eventDto.getAdditionalImages(), actual.getAdditionalImages());
        assertEquals(eventDto.getTitleImage(), actual.getTitleImage());
    }

    @Test
    void addAttender() {
        Event event = ModelUtils.getEvent();
        Set<User> userSet = new HashSet();
        User organizer = ModelUtils.getUser();
        User user = ModelUtils.getUser();
        user.setId(22L);
        userSet.add(user);
        event.setAttenders(userSet);
        when(eventRepo.getOne(any())).thenReturn(event);
        when(modelMapper.map(restClient.findByEmail(ModelUtils.getUserVO().getEmail()), User.class))
            .thenReturn(organizer);

        eventService.addAttender(event.getId(), user.getEmail());

        verify(eventRepo).save(event);
    }

    @Test
    void addAttenderThrows() throws BadRequestException {
        Event event = ModelUtils.getEvent();
        Set<User> userSet = new HashSet();
        userSet.add(ModelUtils.getUser());
        event.setAttenders(userSet);
        User user = ModelUtils.getUser();
        when(eventRepo.getOne(any())).thenReturn(event);
        when(modelMapper.map(restClient.findByEmail(ModelUtils.getUserVO().getEmail()), User.class)).thenReturn(user);
        Long eventId = event.getId();
        String userEmail = user.getEmail();
        try {
            eventService.addAttender(eventId, userEmail);
            fail();
        } catch (RuntimeException re) {
        }

    }

    @Test
    void removeAttender() {
        Event event = ModelUtils.getEvent();
        Set<User> userSet = new HashSet();
        User user = ModelUtils.getUser();
        user.setId(22L);
        userSet.add(user);
        event.setAttenders(userSet);
        when(eventRepo.getOne(any())).thenReturn(event);
        when(modelMapper.map(restClient.findByEmail(ModelUtils.getUserVO().getEmail()), User.class)).thenReturn(user);

        eventService.removeAttender(event.getId(), user.getEmail());

        verify(eventRepo).save(event);
    }

    @Test
    void getAll() {
        List<Event> events = List.of(ModelUtils.getEvent());
        EventDto expected = ModelUtils.getEventDto();

        PageRequest pageRequest = PageRequest.of(0, 1);

        when(eventRepo.getAll(pageRequest)).thenReturn(new PageImpl<>(events, pageRequest, events.size()));
        when(modelMapper.map(events.get(0), EventDto.class)).thenReturn(expected);

        PageableAdvancedDto<EventDto> eventDtoPageableAdvancedDto = eventService.getAll(pageRequest);
        EventDto actual = eventDtoPageableAdvancedDto.getPage().get(0);

        assertEquals(expected.getId(), actual.getId());
        assertEquals(expected.getDescription(), actual.getDescription());
    }
}