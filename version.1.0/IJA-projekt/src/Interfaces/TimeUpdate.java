package Interfaces;
import Sources.Coordinate;
import Sources.Stop;
import Sources.Street;

import java.time.LocalTime;
import java.util.List;

public interface TimeUpdate {
    Coordinate update(LocalTime time, List<Street> restriction_lvl_1, List<Street> restriction_lvl_2, String ClosedStreet, List<Stop> alt_stops_list );
}
