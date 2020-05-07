package Interfaces;
import sources.Coordinate;
import sources.Street;

import java.time.LocalTime;
import java.util.List;

public interface TimeUpdate {
    Coordinate update(LocalTime time, List<Street> restriction_lvl_1, List<Street> restriction_lvl_2, String ClosedStreet );
}
