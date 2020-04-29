package Interfaces;
import sources.Coordinate;

import java.time.LocalTime;

public interface TimeUpdate {
    Coordinate update(LocalTime time );
}
