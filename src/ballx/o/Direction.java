package ballx.o;

/**
 *
 * @author Subhankar
 */
public class Direction {
    
    public static final String FORWARD  = "forward";
    public static final String BACKWARD = "backward";
    public static final String UPWARD   = "upward";
    public static final String DOWNWARD = "downward";
    
    private boolean isDirectionChanged;
    
    public String horizontalDirection = FORWARD, verticalDirection = DOWNWARD,
                  lastHorizontalDirection = FORWARD, lastVerticalDirection = DOWNWARD;
    
    public Direction() {
        
    }
    
    public Direction(String horizontalDirection, String verticalDirection) {
        this.horizontalDirection = horizontalDirection;
        this.verticalDirection = verticalDirection;
    }

    public void setHorizontalDirection(String horizontalDirection) {
        this.lastHorizontalDirection = this.horizontalDirection;
        this.horizontalDirection = horizontalDirection;
        this.isDirectionChanged = true;
    }

    public void setVerticalDirection(String verticalDirection) {
        this.lastVerticalDirection = this.verticalDirection;
        this.verticalDirection = verticalDirection;
        this.isDirectionChanged = true;
    }

    public boolean isDirectionChanged() {
        return isDirectionChanged;
    }

    public void setDirectionChanged(boolean isDirectionChanged) {
        this.isDirectionChanged = isDirectionChanged;
    }
}
