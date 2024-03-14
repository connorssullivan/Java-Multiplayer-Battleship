//Connor Sullivan

import Ships.*;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

public class BattleshipGrid implements Serializable {
        //W = water
        //H = hit
        //S = ship
        private char[][] gridPanels = new char[10][10];//Grid Panels

        //Shots Fired
        private char[][] opponentShotsFired = new char[10][10];//Opponents shots fired
        private char[][] userShotsFired = new char[10][10];//Users Shots Fired


        //Set up ship arrays
        private ArrayList<Ship> userShips = new ArrayList<Ship>();
        private ArrayList<Ship> opponentShips = new ArrayList<Ship>();

        private boolean isTurn;//Keeps track if its the players turn



    ////////////////////////////////////////////////////////////////
    //                     Default constructor                    //
    ////////////////////////////////////////////////////////////////
        public BattleshipGrid() {
            //Initalize opponents shots
            for(int i = 0; i < gridPanels.length;i++) {
                for (int j = 0; j < gridPanels[i].length; j++) {
                    gridPanels[i][j] = 'W';
                    opponentShotsFired[i][j] = 'O';
                    userShotsFired[i][j] = 'O';
                }
            }

            //Set up user ships
            userShips.add(new Aircraft());

            userShips.add(new Battleship());

            userShips.add(new Cruiser());

            userShips.add(new Submarine());

            userShips.add(new Destroyer());

            //Set is turn to false
        }

    ////////////////////////////////////////////////////////////////
    //                     Print Grid to console                 //
    ////////////////////////////////////////////////////////////////
        public void printGrid(){
            for(int i = 0; i < gridPanels.length; i++){
                System.out.println();
                for(int j = 0; j < gridPanels[i].length; j++){
                    System.out.print(gridPanels[i][j]);
                }
            }
        }

    ////////////////////////////////////////////////////////////////
    //                     Print Users Shots Fired to console     //
    ////////////////////////////////////////////////////////////////
    public void printUsersShots(){
        for(int i = 0; i < gridPanels.length; i++){
            System.out.println();
            for(int j = 0; j < gridPanels[i].length; j++){
                System.out.print(userShotsFired[i][j]);
            }
        }
    }

    ////////////////////////////////////////////////////////////////
    //                     Update Panels                          //
    ////////////////////////////////////////////////////////////////
        public void updatePanels(ArrayList<Ship> ships) {

            for (Ship ship : ships) {
                int startRow = ship.getStartRow();
                int startColumn = ship.getStartColumn();
                int size = ship.getSize();
                String direction = ship.getDirection();

                if(ship.isShipPlaced()) {
                    //go through sideways ships
                    if (direction.equalsIgnoreCase("horizontal")) {
                        for (int i = startColumn; i < startColumn + size; i++) {
                            gridPanels[startRow][i] = 'S';
                        }
                    } else { //Assuming "vertical" direction by default
                        for (int i = startRow; i < startRow + size; i++) {
                            gridPanels[i][startColumn] = 'S';
                        }
                    }
                }
                //Update userShots
                for(int i = 0; i < 10; i++){
                    for(int j = 0; j< 10; j++){
                        if(gridPanels[i][j] == 'S' && opponentShotsFired[i][j] == 'X')
                            gridPanels[i][j] = 'H';
                    }
                }
            }
            setShipsHealthBasedOnHits();
        }


    //!!!!!!!!!!!!!!!This is a function useful for testing!!!!!!!!!!
    ////////////////////////////////////////////////////////////////
    //                     Move Ships                             //
    ////////////////////////////////////////////////////////////////
    public void moveShip(Ship currentShip) {
        Scanner scanner = new Scanner(System.in);

        boolean invalid = true;//Check for invalid coord
        while(invalid) {
            printGrid();
            //Stop the loop unlesss determined Invalid
            invalid = false;
            int dir = -1;
            while(dir < 0 || dir > 1){
                System.out.println("Enter a direction \n0: Verticle\n1: Horizontal\n enter");
                dir = scanner.nextInt();
                if(dir == 1)
                    currentShip.setDirection("horizontal");
                else currentShip.setDirection("vertical");
            }
            int x = -1;
            //Get X coordinate
            while (x < 0 || x > 9) {
                System.out.println("Enter a valid x for your " + currentShip.getName());
                x = scanner.nextInt();
                currentShip.setStartColumn(x);
            }
            int y = -1;
            //Get Y coordinate
            while (y < 0 || y > 9) {
                System.out.println("Enter a valid y for your " + currentShip.getName());
                y = scanner.nextInt();
                currentShip.setStartRow(y);
            }

            //Check  if any of the current spaces are invalid
            //If the direction is horizontal check down
            if (currentShip.getDirection().equalsIgnoreCase( "horizontal")) {
                for (int i =0, j=x; i< currentShip.getSize();i++,j++){
                    //If x is grater then 9, then it's out of ranger
                    if(j > 9){
                        invalid = true;
                        break;
                    }
                    if(gridPanels[j][y] != 'W') {
                        invalid = true;
                        break;
                    }

                }
            }
            //Check Verticle
            else{
                for(int i = 0,j = y; i < currentShip.getSize();i++, j++){
                    if(j > 9){
                        invalid =true;
                        break;
                    }

                    if(gridPanels[x][j] != 'W') {
                        invalid = true;
                        break;
                    }

                }
            }

        }

    }

    ////////////////////////////////////////////////////////////////
    //                     Move Ships                             //
    ////////////////////////////////////////////////////////////////
    public boolean moveShip(Ship currentShip,int x, int y, String direction) {
        if(currentShip.isShipPlaced()){
            System.out.println("Ship already placed");
            return false;
        }
        //Set the ship sirection and coordinates
        currentShip.setDirection(direction);
        currentShip.setStartRow(x);
        currentShip.setStartColumn(y);
        //If the ship is already placed

        boolean invalid = false;//Check for invalid coord

        //Check  if any of the current spaces are invalid
        //If the direction is horizontal Sizeways
        if (currentShip.getDirection().equalsIgnoreCase("horizontal")) {
            for (int i = 0, j = y; i < currentShip.getSize(); i++, j++) {
                // If j is out of range, it's invalid
                if (j >= 10) {
                    invalid = true;
                    break;
                }
                // If the grid panel is not water, it's invalid
                if (gridPanels[x][j] != 'W') {
                    invalid = true;
                    break;
                }
            }
        }
        //Check Verticle
        else {
            for (int i = 0, j = x; i < currentShip.getSize(); i++, j++) {
                // If j is out of range, it's invalid
                if (j >= 10) {
                    invalid = true;
                    break;
                }
                // If the grid panel is not water, it's invalid
                if (gridPanels[j][y] != 'W') {
                    invalid = true;
                    break;
                }
            }
        }

        //If coordinate is invalis
        if(invalid) {
            currentShip.setStartRow(1);
            currentShip.setStartColumn(1);
            return false;
        }

        //Coordinate is valid
        else {
            currentShip.setShipPlaced(true);//Set ship placed to true
            return true;
        }
    }


    ////////////////////////////////////////////////////////////////
    //                     Checks if all ships are placed         //
    ////////////////////////////////////////////////////////////////
    public boolean allShipsPlaced() {
            for(Ship s : userShips)
                if(!s.isShipPlaced())
                    return false;
            return true;
    }

    ////////////////////////////////////////////////////////////////
    //          Randomly Place Remaining Ships on the Grid        //
    ////////////////////////////////////////////////////////////////
    public void randomlyPlaceRemainingShips() {
        Random random = new Random();

        // Iterate through the user's ships
        for (Ship ship : userShips) {
            // Check if the ship is already placed
            if (!ship.isShipPlaced()) {
                boolean placed = false;

                // Continue trying to place the ship until it is successfully placed
                while (!placed) {
                    // Generate random coordinates and direction
                    int x = random.nextInt(10);
                    int y = random.nextInt(10);
                    boolean isHorizontal = random.nextBoolean();

                    //Try to place the ship
                    placed = moveShip(ship, x, y, isHorizontal ? "horizontal" : "vertical");

                    //Update Panels
                    updatePanels(userShips);
                }
            }
        }
    }
    ////////////////////////////////////////////////////////////////
    //          Set ships health                                  //
    ////////////////////////////////////////////////////////////////
    public void setShipsHealthBasedOnHits() {
        for (Ship ship : userShips) {
            //Get ships start coordinates and direction
            int startRow = ship.getStartRow();
            int startColumn = ship.getStartColumn();
            String direction = ship.getDirection();

            //Initialize jits
            int hits = 0;

            //Check hits around the ships location based on its direction
            if (direction.equalsIgnoreCase("horizontal")) {
                for (int i = startColumn; i < startColumn + ship.getSize(); i++) {
                    if (i < 10 && gridPanels[startRow][i] == 'H') {
                        hits++;
                    }
                }
            } else {
                for (int i = startRow; i < startRow + ship.getSize(); i++) {
                    if (i < 10 && gridPanels[i][startColumn] == 'H') {
                        hits++;
                    }
                }
            }

            //Subtract hits from ship's health
            ship.setHealth(ship.getSize() - hits);
        }
    }

    public boolean isOutOfShips(){
            for (int i = 0; i < userShips.size(); i++) {
                if(userShips.get(i).getHealth() > 0)
                    return false;
            }

            return true;
    }

    ////////////////////////////////////////////////////////////////
    //                     Fire Shot                              //
    ////////////////////////////////////////////////////////////////
    void fireShot(int x, int y){
        userShotsFired[x][y] = 'X';
    }

    ////////////////////////////////////////////////////////////////
    //                    Get Grid                                //
    ////////////////////////////////////////////////////////////////
    public char getGridPanel(int x, int y) {
        return gridPanels[x][y];
    }

    ////////////////////////////////////////////////////////////////
    //                     Set Opponents Shot                     //
    ////////////////////////////////////////////////////////////////
    public void setOpponentShot(int x, int y){
        opponentShotsFired[x][y] = 'X';
    }

    ////////////////////////////////////////////////////////////////
    //                     set Opponents Shots                 //
    ////////////////////////////////////////////////////////////////
    public void setOpponentShotsFired(char[][] arr){
        opponentShotsFired = arr;
    }

    ////////////////////////////////////////////////////////////////
    //                     Get Users Shots Fired                  //
    ////////////////////////////////////////////////////////////////
    public char[][] getUserShotsFired() {
        return userShotsFired;
    }

    public char[][] getGridPanels() {
        return gridPanels;
    }

    public char[][] getOpponentShotsFired() {
        return opponentShotsFired;
    }

    public ArrayList<Ship> getOpponentShips() {
        return opponentShips;
    }

    ////////////////////////////////////////////////////////////////
    //                     Set Users Shots Fired                  //
    ////////////////////////////////////////////////////////////////
    public void setUserShotsFired(char[][] userShotsFired) {
        this.userShotsFired = userShotsFired;
    }

    ////////////////////////////////////////////////////////////////
    //                     Get Is Turn                           //
    ////////////////////////////////////////////////////////////////
    public boolean isTurn() {
        return isTurn;
    }

    ////////////////////////////////////////////////////////////////
    //                     Set Is Turn                           //
    ////////////////////////////////////////////////////////////////
    public void setIsTurn(boolean turn) {
        isTurn = turn;
    }

    public ArrayList<Ship> getUserShips() {
        return userShips;
    }

    public void setUserShips(ArrayList<Ship> userShips) {
        this.userShips = userShips;
    }

}


