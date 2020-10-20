package greencity.dto.habitstatuscalendar;

import java.time.LocalDate;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Builder
public class HabitStatusCalendarVO {
    private Long id;
    private LocalDate enrollDate;
}
