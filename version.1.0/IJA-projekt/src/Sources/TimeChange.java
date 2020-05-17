/*
 * Authors: Terézia Sochova(xsocho14), Adrián Piaček(xpiace00)
 * Source code TimeChange.java contains methods used for determination of Time change and its subsequent methods used for
 * specific assignment functionality.
 */
package Sources;
import java.util.Objects;

public class TimeChange {
        private boolean time_changed;
        public TimeChange(boolean time_changed){
            this.time_changed = time_changed;
        }
    public boolean getValue() {
       return this.time_changed;
    }

    public void setTrue() {
        this.time_changed = true;
    }
    public void setFalse() {
        this.time_changed = false;
    }

}

