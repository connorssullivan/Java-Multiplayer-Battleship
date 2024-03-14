//Connor Sullivan

package Ships;

import java.io.Serializable;

public abstract class Ship implements Serializable {
    //Name of ship
    private String name;
    private int size;
    private int health;
    private String direction;
    private String imagePath;

    //Start row and Column of ship
    private int startRow, startColumn;
    private boolean shipPlaced;

    ////////////////////////////////////////////////////////////////
    //                     Constructors                           //
    ////////////////////////////////////////////////////////////////

    public Ship() {
        this.name = "";
        this.size = 0;
        this.health = 0;
        this.direction = "horizontal";
        this.startRow = 0;
        this.startColumn = 0;
        this.imagePath = "";
        this.shipPlaced = false;
    }

    public Ship(String name, int size, int health, String direction, String imagePath){
        this.name = name;
        this.size = size;
        this.health = size; // At the beginning, health equals size
        this.startRow = 0;
        this.startColumn = 0;
        this.direction = direction;
        this.imagePath = "";
        this.shipPlaced = false;
        this.shipPlaced = false;
    }

    ////////////////////////////////////////////////////////////////
    //                     Getters                                //
    ////////////////////////////////////////////////////////////////
    public String getName() {
        return name;
    }

    public int getSize() {
        return size;
    }

    public int getHealth() {
        return health;
    }

    public String getDirection() {
        return direction;
    }

    public int getStartRow() {
        return startRow;
    }

    public int getStartColumn() {
        return startColumn;
    }

    public boolean isShipPlaced() {return shipPlaced;}

    ////////////////////////////////////////////////////////////////
    //                     Setters                                //
    ////////////////////////////////////////////////////////////////

    public void setHealth(int health) {
        this.health = health;
    }

    public void setDirection(String direction) {
        this.direction = direction;
    }

    public void setStartRow(int startRow) {
        this.startRow = startRow;
    }


    public void setStartColumn(int startColumn) {
        this.startColumn = startColumn;
    }

    public void setShipPlaced(boolean shipPlaced) {this.shipPlaced = shipPlaced;}
}
