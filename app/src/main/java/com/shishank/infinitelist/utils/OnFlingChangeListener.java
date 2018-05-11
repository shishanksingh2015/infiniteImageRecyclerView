package com.shishank.infinitelist.utils;

/**
 * An interface that LayoutManagers that should snap to grid should implement.
 */
public interface OnFlingChangeListener {

    /**
     * @param velocityY
     * @return  position from a fling of the given velocity.
     */
    int getPositionForVelocity(int velocityY);

    /**
     * @return the position this list must scroll to to fix a state where the 
     * views are not snapped to grid.
     */
    int getFixScrollPos();        

}