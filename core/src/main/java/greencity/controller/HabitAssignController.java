package greencity.controller;

import greencity.annotations.ApiLocale;
import greencity.annotations.CurrentUser;
import greencity.annotations.ValidLanguage;
import greencity.constant.HttpStatuses;
import greencity.dto.habit.*;
import greencity.dto.user.UserVO;
import greencity.service.HabitAssignService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import java.util.List;
import java.util.Locale;
import javax.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

@Validated
@AllArgsConstructor
@RestController
@RequestMapping("/habit/assign")
public class HabitAssignController {
    private final HabitAssignService habitAssignService;

    /**
     * Method which assigns habit for {@link UserVO} with default props.
     *
     * @param habitId {@link HabitVO} id.
     * @param userVO  {@link UserVO} instance.
     * @return {@link ResponseEntity}.
     */
    @ApiOperation(value = "Assign habit with default properties for current user.")
    @ApiResponses(value = {
        @ApiResponse(code = 201, message = HttpStatuses.CREATED, response = HabitAssignDto.class),
        @ApiResponse(code = 400, message = HttpStatuses.BAD_REQUEST),
        @ApiResponse(code = 403, message = HttpStatuses.FORBIDDEN),
        @ApiResponse(code = 404, message = HttpStatuses.NOT_FOUND)
    })
    @PostMapping("/{habitId}")
    public ResponseEntity<HabitAssignManagementDto> assignDefault(@PathVariable Long habitId,
        @ApiIgnore @CurrentUser UserVO userVO) {
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(habitAssignService.assignDefaultHabitForUser(habitId, userVO));
    }

    /**
     * Method which assigns habit for {@link UserVO} with custom props.
     *
     * @param habitId                  {@link HabitVO} id.
     * @param userVO                   {@link UserVO} instance.
     * @param habitAssignPropertiesDto {@link HabitAssignPropertiesDto} instance.
     * @return {@link ResponseEntity}.
     */
    @ApiOperation(value = "Assign habit with custom properties for current user.")
    @ApiResponses(value = {
        @ApiResponse(code = 201, message = HttpStatuses.CREATED, response = HabitAssignDto.class),
        @ApiResponse(code = 400, message = HttpStatuses.BAD_REQUEST),
        @ApiResponse(code = 403, message = HttpStatuses.FORBIDDEN),
        @ApiResponse(code = 404, message = HttpStatuses.NOT_FOUND)
    })
    @PostMapping("/{habitId}/custom")
    public ResponseEntity<HabitAssignManagementDto> assignCustom(@PathVariable Long habitId,
        @ApiIgnore @CurrentUser UserVO userVO,
        @Valid @RequestBody HabitAssignPropertiesDto habitAssignPropertiesDto) {
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(habitAssignService.assignCustomHabitForUser(habitId, userVO, habitAssignPropertiesDto));
    }

    /**
     * Method returns {@link HabitAssignDto} by it's id.
     *
     * @param id     {@link HabitAssignVO} id.
     * @param locale needed language code.
     * @return {@link HabitAssignDto}.
     */
    @ApiOperation(value = "Get habit assign.")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = HttpStatuses.OK, response = HabitAssignDto.class),
        @ApiResponse(code = 400, message = HttpStatuses.BAD_REQUEST),
        @ApiResponse(code = 404, message = HttpStatuses.NOT_FOUND)
    })
    @GetMapping("/{id}")
    public ResponseEntity<HabitAssignDto> getHabitAssign(@PathVariable Long id,
        @ApiIgnore @ValidLanguage Locale locale) {
        return ResponseEntity.status(HttpStatus.OK)
            .body(habitAssignService.getById(id, locale.getLanguage()));
    }

    /**
     * Method for finding all active {@link HabitAssignDto}'s for current user.
     *
     * @param userVO   {@link UserVO} instance.
     * @param acquired {@link Boolean} status.
     * @param locale   needed language code.
     * @return list of {@link HabitAssignDto}.
     */
    @ApiOperation(value = "Get active habit assigns for current user by acquired status.")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = HttpStatuses.OK, response = List.class),
        @ApiResponse(code = 400, message = HttpStatuses.BAD_REQUEST),
        @ApiResponse(code = 403, message = HttpStatuses.FORBIDDEN),
        @ApiResponse(code = 404, message = HttpStatuses.NOT_FOUND)
    })
    @ApiLocale
    @GetMapping("")
    public ResponseEntity<List<HabitAssignDto>> getCurrentUserHabitAssignsByIdAndAcquired(
        @ApiIgnore @CurrentUser UserVO userVO, @RequestParam Boolean acquired,
        @ApiIgnore @ValidLanguage Locale locale) {
        return ResponseEntity.status(HttpStatus.OK)
            .body(habitAssignService
                .getAllHabitAssignsByUserIdAndAcquiredStatus(userVO.getId(), acquired, locale.getLanguage()));
    }

    /**
     * Method to return all active {@link HabitAssignDto} by it's {@link HabitVO}
     * id.
     *
     * @param habitId  {@link HabitVO} id.
     * @param acquired {@link Boolean} status.
     * @param locale   needed language code.
     * @return {@link List} of {@link HabitAssignDto}.
     */
    @ApiOperation(value = "Get all active assigns by certain habit.")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = HttpStatuses.OK, response = List.class),
        @ApiResponse(code = 400, message = HttpStatuses.BAD_REQUEST),
        @ApiResponse(code = 404, message = HttpStatuses.NOT_FOUND)
    })
    @ApiLocale
    @GetMapping("/{habitId}/all")
    public ResponseEntity<List<HabitAssignDto>> getAllHabitAssignsByHabitIdAndAcquired(@PathVariable Long habitId,
        @RequestParam Boolean acquired,
        @ApiIgnore @ValidLanguage Locale locale) {
        return ResponseEntity.status(HttpStatus.OK)
            .body(habitAssignService.getAllHabitAssignsByHabitIdAndAcquiredStatus(habitId,
                acquired, locale.getLanguage()));
    }

    /**
     * Method to return {@link HabitAssignVO} by it's {@link HabitVO} id.
     *
     * @param habitId {@link HabitVO} id.
     * @param userVO  {@link UserVO} user.
     * @param locale  needed language code.
     * @return {@link HabitAssignDto} instance.
     */
    @ApiOperation(value = "Get active assign by habit id for current user.")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = HttpStatuses.OK, response = HabitAssignDto.class),
        @ApiResponse(code = 400, message = HttpStatuses.BAD_REQUEST),
        @ApiResponse(code = 403, message = HttpStatuses.FORBIDDEN),
        @ApiResponse(code = 404, message = HttpStatuses.NOT_FOUND)
    })
    @ApiLocale
    @GetMapping("/{habitId}/active")
    public ResponseEntity<HabitAssignDto> getHabitAssignByHabitId(
        @ApiIgnore @CurrentUser UserVO userVO,
        @PathVariable Long habitId,
        @ApiIgnore @ValidLanguage Locale locale) {
        return ResponseEntity.status(HttpStatus.OK)
            .body(habitAssignService
                .findActiveHabitAssignByUserIdAndHabitId(userVO.getId(), habitId, locale.getLanguage()));
    }

    /**
     * Method to update active {@link HabitAssignVO} for it's {@link HabitVO} id and
     * current user.
     *
     * @param userVO             {@link UserVO} instance.
     * @param habitId            {@link HabitVO} id.
     * @param habitAssignStatDto {@link HabitAssignStatDto} instance.
     * @return {@link HabitAssignManagementDto}.
     */
    @ApiOperation(value = "Update active user habit assign acquired or suspended status.")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = HttpStatuses.OK, response = HabitAssignDto.class),
        @ApiResponse(code = 400, message = HttpStatuses.BAD_REQUEST),
        @ApiResponse(code = 403, message = HttpStatuses.FORBIDDEN),
        @ApiResponse(code = 404, message = HttpStatuses.NOT_FOUND)
    })
    @PatchMapping("/{habitId}")
    public ResponseEntity<HabitAssignManagementDto> updateAssignByHabitId(
        @ApiIgnore @CurrentUser UserVO userVO,
        @PathVariable Long habitId, @Valid @RequestBody HabitAssignStatDto habitAssignStatDto) {
        return ResponseEntity.status(HttpStatus.OK).body(habitAssignService
            .updateStatusByHabitIdAndUserId(habitId, userVO.getId(), habitAssignStatDto));
    }
}