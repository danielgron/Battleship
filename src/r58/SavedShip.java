/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package r58;

import battleship.interfaces.Position;
import battleship.interfaces.Ship;

/**
 *
 * @author dennisschmock
 */
public class SavedShip {
    private Position pos;
    private Ship ship;
    private boolean  vert;
    
   public SavedShip(Position pos, Ship ship, boolean vert ){
       this.pos = pos;
       this.ship = ship;
       this.vert = vert;
   }

    /**
     * @return the pos
     */
    public Position getPos() {
        return pos;
    }

    /**
     * @param pos the pos to set
     */
    public void setPos(Position pos) {
        this.pos = pos;
    }

    /**
     * @return the ship
     */
    public Ship getShip() {
        return ship;
    }

    /**
     * @param ship the ship to set
     */
    public void setShip(Ship ship) {
        this.ship = ship;
    }

    /**
     * @return the vert
     */
    public boolean isVert() {
        return vert;
    }

    /**
     * @param vert the vert to set
     */
    public void setVert(boolean vert) {
        this.vert = vert;
    }

   
}
