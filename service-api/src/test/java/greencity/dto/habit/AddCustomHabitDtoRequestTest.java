package greencity.dto.habit;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.Set;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AddCustomHabitDtoRequestTest {

    @SneakyThrows
    @ParameterizedTest
    @MethodSource("provideFieldsAndValidValues")
    void validComplexityInAddCustomHabitDtoRequestTest(Integer complexity) {
        var dto = AddCustomHabitDtoRequest.builder()
            .complexity(complexity)
            .build();

        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        final Validator validator = factory.getValidator();

        Set<ConstraintViolation<AddCustomHabitDtoRequest>> constraintViolations =
            validator.validate(dto);

        assertTrue(constraintViolations.isEmpty());
    }

    @SneakyThrows
    @ParameterizedTest
    @MethodSource("provideFieldsAndInvalidValues")
    void invalidComplexityInAddCustomHabitDtoRequestTest(Integer complexity) {
        var dto = AddCustomHabitDtoRequest.builder()
            .complexity(complexity)
            .build();

        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        final Validator validator = factory.getValidator();

        Set<ConstraintViolation<AddCustomHabitDtoRequest>> constraintViolations =
            validator.validate(dto);

        assertEquals(1, constraintViolations.size());
    }

    @Test
    void invalidComplexityIsNullInAddCustomHabitDtoRequestTest() {
        var dto = AddCustomHabitDtoRequest.builder()
            .complexity(null)
            .build();

        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        final Validator validator = factory.getValidator();

        Set<ConstraintViolation<AddCustomHabitDtoRequest>> constraintViolations =
            validator.validate(dto);

        assertEquals(1, constraintViolations.size());
    }

    private static Stream<Arguments> provideFieldsAndValidValues() {
        return Stream.of(
            Arguments.of(1),
            Arguments.of(2),
            Arguments.of(3));
    }

    private static Stream<Arguments> provideFieldsAndInvalidValues() {
        return Stream.of(
            Arguments.of(4),
            Arguments.of(5),
            Arguments.of(-6),
            Arguments.of(-1),
            Arguments.of(0));
    }
}