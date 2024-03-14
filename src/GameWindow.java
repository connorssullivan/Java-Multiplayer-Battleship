//Connor Sullivan

import Ships.Ship;

import javax.imageio.ImageIO;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.*;
import javax.swing.event.MouseInputAdapter;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;

////////////////////////////////////////////////////////////////
//                     Main Window for Game                   //
////////////////////////////////////////////////////////////////
public class GameWindow implements Serializable {
    BattleshipGrid usersBattleshipGrid = new BattleshipGrid();//Bship grid
    BattleshipGrid opponentsBattleshipGrid = new BattleshipGrid();
    //Size of board
    int rows = 10;
    int cols = 10;

    //Frames
    private JFrame frame = new JFrame();
    private JLabel[][] userGridLables;
    private JPanel userGridPanel = new JPanel(new GridLayout(rows, cols));
    //Opponents grid
    private JLabel[][] opponentGridLables;
    private JPanel opponentGridPanel = new JPanel(new GridLayout(rows, cols));
    JLabel textField = new JLabel();

    //Main frame Panels
    BorderLayout layout = new BorderLayout();

    //Panels for main Frame
    JPanel northPanel = new JPanel();
    JPanel southPanel = new JPanel();
    JPanel westPanel = new JPanel();
    JPanel centerPanel = new JPanel();

    //Ship Markers
    private int currentShipIndex;
    private Ship currentShip;
    boolean allShipsPlaced;

    //Rotation
    private JButton rotateShipButton = new JButton();
    private String shipCurrentRotation = "horizontal";


    //Fields for Networking
    private boolean isServer;
    private Server server;
    private Client client;

    //Ship Lables
    private JLabel AircraftLabel;
    private JLabel BattleshipLabel;
    private JLabel CruiserLabel;
    private JLabel SubmarineLabel;
    private JLabel DestroyLabel;

    //Opponents Ships
    JPanel opponentsImagePanel;
    private JLabel OpponentAircraftLabel;
    private JLabel OpponentBattleshipLabel;
    private JLabel OpponentCruiserLabel;
    private JLabel OpponentSubmarineLabel;
    private JLabel OpponentDestroyLabel;

    //Ship placment
    // Store the coordinates where the user wants to place the ship
    private int shipPlacementX = -1;
    private int shipPlacementY = -1;

    // Button to confirm ship placement
    private JButton confirmPlacementButton = new JButton("Confirm Placement");

    //Game over Screen
    JFrame gameOverScreen = new JFrame();
    JLabel gameOverScreenLabel = new JLabel();
    private Clip shotSound;

    ////////////////////////////////////////////////////////////////
    //                     Main Constructor                       //
    ////////////////////////////////////////////////////////////////
    public GameWindow(boolean isServer) {
        this.isServer = isServer;
        initialize();
        initializeShotSound();
    }

    ////////////////////////////////////////////////////////////////
    //                     Initalize the Window                   //
    ////////////////////////////////////////////////////////////////
    public void initialize(){
        //Host gets first turn
        if(isServer) {
            usersBattleshipGrid.setIsTurn(true);//Set is turn to false
        }else{ usersBattleshipGrid.setIsTurn(false);}

        System.out.println("Is Turn Set to " + getUsersBattleshipGrid().isTurn());

        //Initalize Ship Buttons

        //Add button Listners
        addButtonListeners();

        //Set basic main frame properties
        this.frame.setTitle("BattleShip");
        this.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.frame.setSize(1000,1000);
        this.frame.setBackground(Color.LIGHT_GRAY);
        gameOverScreen.setTitle("Game Over");
        gameOverScreen.setSize(500,500);
        gameOverScreen.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        gameOverScreen.setVisible(false);

        //Set Layouts properies
        layout.setHgap(10);
        layout.setVgap(10);
        frame.setLayout(layout);//Set border layout

        this.frame.setLocationRelativeTo(null);
        this.frame.setResizable(true);
        this.frame.setVisible(false);//Iitially set the main frame to not visable

        //North Panel
        northPanel.setLayout(new BorderLayout());
        northPanel.setPreferredSize(new Dimension(1000,200));
        northPanel.setBackground(Color.DARK_GRAY);
        textField.setHorizontalAlignment(JLabel.CENTER);
        textField.setText("Choose A Ship");

        textField.setForeground(Color.WHITE);
        textField.setSize(new Dimension(1000,200));
        northPanel.add(textField, BorderLayout.NORTH);

        //Create a nested panel for the images with FlowLayout
        JPanel imagePanel = new JPanel(new FlowLayout());
        imagePanel.setBackground(Color.DARK_GRAY);

        //Load and scale the aircraft image
        AircraftLabel = loadImageAndScale("Ships/aircraft.png", 100, 100);
        imagePanel.add(AircraftLabel);

        AircraftLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                currentShipIndex = 0; // Update currentShipIndex for Aircraft
                currentShip = usersBattleshipGrid.getUserShips().get(currentShipIndex);
                textField.setText("Place " + currentShip.getName() + " (" + shipCurrentRotation + ")");
                usersBattleshipGrid.updatePanels(usersBattleshipGrid.getUserShips());


                //Check if the ship is already placed
                if (currentShip.isShipPlaced()) {
                    //If the ship is placed, add a semi-transparent overlay panel
                    addOverlay(AircraftLabel);
                }

            }
        });

        //load and scale the battleship image
        BattleshipLabel = loadImageAndScale("Ships/battleship.png", 100, 100);
        imagePanel.add(BattleshipLabel);
        //Create a mouse listener for the BattleshipLabel
        BattleshipLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                currentShipIndex = 1; // Update currentShipIndex for Battleship
                currentShip = usersBattleshipGrid.getUserShips().get(currentShipIndex);
                textField.setText("Place " + currentShip.getName() + " (" + shipCurrentRotation + ")");
                usersBattleshipGrid.updatePanels(usersBattleshipGrid.getUserShips());

                //Check if the ship is already placed
                if (currentShip.isShipPlaced()) {
                    //If the ship is placed, add a semi-transparent overlay panel
                    addOverlay(BattleshipLabel);
                }
            }
        });


        // Load the Cruiser and scale Image
        CruiserLabel = loadImageAndScale("Ships/cruiser.png", 100, 100);
        imagePanel.add(CruiserLabel);

        // Create a mouse listener for the CruiserLabel
        CruiserLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                currentShipIndex = 2; // Update currentShipIndex for Battleship
                currentShip = usersBattleshipGrid.getUserShips().get(currentShipIndex);
                textField.setText("Place " + currentShip.getName() + " (" + shipCurrentRotation + ")");
                usersBattleshipGrid.updatePanels(usersBattleshipGrid.getUserShips());

                //Check if the ship is already placed
                if (currentShip.isShipPlaced()) {
                    //If the ship is placed, add a semi-transparent overlay panel
                    addOverlay(CruiserLabel);
                }
            }
        });

        //Load the Submarine and scale image
        SubmarineLabel = loadImageAndScale("Ships/submarine.png", 100, 100);
        imagePanel.add(SubmarineLabel);

        SubmarineLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                currentShipIndex = 3; // Update currentShipIndex for Battleship
                currentShip = usersBattleshipGrid.getUserShips().get(currentShipIndex);
                textField.setText("Place " + currentShip.getName() + " (" + shipCurrentRotation + ")");
                usersBattleshipGrid.updatePanels(usersBattleshipGrid.getUserShips());

                //Check if the ship is already placed
                if (currentShip.isShipPlaced()) {
                    //If the ship is placed, add a semi-transparent overlay panel
                    addOverlay(SubmarineLabel);
                }

            }
        });

        //Load the Destroyer and scale Image
        DestroyLabel = loadImageAndScale("Ships/destroyer.png", 100, 100);
        imagePanel.add(DestroyLabel);

        DestroyLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                currentShipIndex = 4; // Update currentShipIndex for Battleship
                currentShip = usersBattleshipGrid.getUserShips().get(currentShipIndex);
                textField.setText("Place " + currentShip.getName() + " (" + shipCurrentRotation + ")");
                usersBattleshipGrid.updatePanels(usersBattleshipGrid.getUserShips());

                //Check if the ship is already placed
                if (currentShip.isShipPlaced()) {
                    //If the ship is placed, add a semi-transparent overlay panel
                    addOverlay(DestroyLabel);
                }
            }
        });


        //Add the image panel to the north panel
        northPanel.add(imagePanel, BorderLayout.CENTER);

        frame.add(northPanel, BorderLayout.NORTH);

        //South Panel
        southPanel.setPreferredSize(new Dimension(1000,200));
        southPanel.setBackground(Color.DARK_GRAY);
        rotateShipButton.setText("Rotate Ship");
        southPanel.add(rotateShipButton);
        frame.add(southPanel, BorderLayout.SOUTH);
        southPanel.add(confirmPlacementButton);


        //Add a new button for random placement
        JButton randomPlacementButton = new JButton("Random Placement");
        randomPlacementButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                usersBattleshipGrid.randomlyPlaceRemainingShips();
                usersBattleshipGrid.updatePanels(usersBattleshipGrid.getUserShips());
                updateHighlightedCells();
            }
        });
        southPanel.add(randomPlacementButton);

        //Center Panel
        centerPanel.setPreferredSize(new Dimension(50,50));
        centerPanel.setBackground(Color.DARK_GRAY);
        frame.add(centerPanel, BorderLayout.CENTER);

        //Set up opponents ship lables
        opponentsImagePanel = new JPanel();
        opponentsImagePanel.setLayout(new BoxLayout(opponentsImagePanel, BoxLayout.Y_AXIS));

        OpponentAircraftLabel = loadImageAndScale("Ships/aircraft.png", 50, 50);
        OpponentAircraftLabel.setBackground(Color.DARK_GRAY);
        opponentsImagePanel.add(OpponentAircraftLabel);

        OpponentBattleshipLabel = loadImageAndScale("Ships/battleship.png", 50, 50);
        opponentsImagePanel.add(OpponentBattleshipLabel);

        OpponentCruiserLabel = loadImageAndScale("Ships/cruiser.png", 50, 50);
        opponentsImagePanel.add(OpponentCruiserLabel);

        OpponentSubmarineLabel = loadImageAndScale("Ships/submarine.png", 50, 50);
        opponentsImagePanel.add(OpponentSubmarineLabel);

        OpponentDestroyLabel = loadImageAndScale("Ships/destroyer.png", 50, 50);
        opponentsImagePanel.add(OpponentDestroyLabel);

        centerPanel.add(opponentsImagePanel);



        //Add grids to the frame Grid
        frame.add(userGridPanel, BorderLayout.WEST);
        frame.add(opponentGridPanel, BorderLayout.EAST);

        userGridLables = new JLabel[rows][cols];
        opponentGridLables = new JLabel[rows][cols];


        //Set up grids
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                //Add users grid
                userGridLables[row][col] = new JLabel(); // Create a new JLabel
                userGridLables[row][col].setPreferredSize(new Dimension(40, 40));
                userGridLables[row][col].setBorder(BorderFactory.createLineBorder(Color.BLACK)); // Add border for visualization
                userGridLables[row][col].setBackground(Color.blue);
                userGridLables[row][col].setOpaque(true);
                userGridLables[row][col].addMouseListener(new panelMouseListener());
                userGridPanel.add(userGridLables[row][col]); // Add JLabel to the grid panel

                //Add opponents grid
                opponentGridLables[row][col] = new JLabel(); // Create a new JLabel
                opponentGridLables[row][col].setPreferredSize(new Dimension(40, 40));
                opponentGridLables[row][col].setBorder(BorderFactory.createLineBorder(Color.BLACK)); // Add border for visualization
                opponentGridLables[row][col].setBackground(Color.blue);
                opponentGridLables[row][col].setOpaque(true);
                opponentGridLables[row][col].addMouseListener(new panelMouseListener());
                opponentGridPanel.add(opponentGridLables[row][col]); // Add JLabel to the grid panel
            }
        }

        //Set some stuff
        currentShipIndex = 0;
        currentShip = usersBattleshipGrid.getUserShips().get(currentShipIndex);
        allShipsPlaced =false;

        addShipListners();
    }

    ////////////////////////////////////////////////////////////////
    //                     Add an overlay to a panel              //
    ////////////////////////////////////////////////////////////////
    private void addOverlay(JLabel label) {
        // Create a semi-transparent overlay panel
        JPanel overlayPanel = new JPanel();
        overlayPanel.setBackground(new Color(0, 0, 0, 128)); // Semi-transparent black color
        overlayPanel.setBounds(0, 0, label.getWidth(), label.getHeight()); // Set bounds to cover the label
        overlayPanel.setOpaque(false); // Make the panel transparent

        // Add the overlay panel to the label
        label.add(overlayPanel);
        label.revalidate();
    }


    ////////////////////////////////////////////////////////////////
    //                     Load Images                            //
    ////////////////////////////////////////////////////////////////
    public static JLabel loadImageAndScale(String filePath, int width, int height) {
        //Load the image file
        File file = new File(filePath);
        BufferedImage bufferedImage = null;
        try {
            bufferedImage = ImageIO.read(file);
        } catch (IOException e) {
            e.printStackTrace();
            return null; // Return null if image loading fails
        }

        //Scale the image
        Image scaledImage = bufferedImage.getScaledInstance(width, height, Image.SCALE_SMOOTH);

        //Create an ImageIcon from the scaled image
        ImageIcon imageIcon = new ImageIcon(scaledImage);

        //Create a jlabel to hold the scaled image
        JLabel imageLabel = new JLabel(imageIcon);

        return imageLabel;
    }

    ////////////////////////////////////////////////////////////
    //               Clear Highlighted Cells                //
    ////////////////////////////////////////////////////////////
    public void updateHighlightedCells() {
        // Reset the background color of all cells to blue
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if(usersBattleshipGrid.getGridPanel(i,j) != 'S' && usersBattleshipGrid.getGridPanel(i,j) != 'H')
                    userGridLables[i][j].setBackground(Color.blue);
                else{
                    if(usersBattleshipGrid.getGridPanel(i,j) == 'H'){//If Its a hit make the cell red
                        userGridLables[i][j].setBackground(Color.RED);
                    }else {//If it not hit stay greem
                        userGridLables[i][j].setBackground(Color.GREEN);
                    }
                }
            }
        }
    }

    ////////////////////////////////////////////////////////////////
    //                     Set Button Lisners                     //
    ////////////////////////////////////////////////////////////////
    private void addButtonListeners() {
        rotateShipButton.addActionListener(e -> {
            //Toggle ship rotation
            if (shipCurrentRotation.equals("horizontal")) {
                shipCurrentRotation = "vertical";
            } else {
                shipCurrentRotation = "horizontal";
            }

            //Update ship rotation text
            textField.setText("Place " + usersBattleshipGrid.getUserShips().get(currentShipIndex).getName() + " (" + shipCurrentRotation + ")");
        });


        //Action listners for ships
        confirmPlacementButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //Check if the ship placement is valid
                if (shipPlacementX != -1 && shipPlacementY != -1) {
                    //Attempt to place the ship
                    boolean placed = usersBattleshipGrid.moveShip(currentShip, shipPlacementX, shipPlacementY, shipCurrentRotation);

                    if (placed) {
                        //Update the UI
                        updateUIAfterPlacement(currentShip, shipPlacementX, shipPlacementY);
                    } else {
                        // Inform the user of invalid placement
                        textField.setText("Invalid Ship Placement");
                    }

                    //Reset coords
                    shipPlacementX = -1;
                    shipPlacementY = -1;
                } else {
                    textField.setText("Select a Cell to Place Ship First");
                }
            }
        });
    }

    ////////////////////////////////////////////////////////////////
    //            Update UI After Successful Ship Placement       //
    ////////////////////////////////////////////////////////////////
    private void updateUIAfterPlacement(Ship ship, int x, int y) {
        //Make tiles light up where ship is placed
        if (ship.getDirection().equals("horizontal")) {
            for (int i = 0; i < ship.getSize(); i++) {
                userGridLables[x][y + i].setBackground(Color.GREEN);
            }
        } else {//If ship is vertical
            for (int i = 0; i < ship.getSize(); i++) {
                userGridLables[x + i][y].setBackground(Color.GREEN);
            }
        }

        // Check if all ships are placed
        if (currentShipIndex >= usersBattleshipGrid.getUserShips().size() - 1) {
            System.out.println("All Ships Placed");
            textField.setText("Battleship");
            return;
        }

        // Print out ship placed
        System.out.println("Ship Placed");
        usersBattleshipGrid.updatePanels(usersBattleshipGrid.getUserShips());
        textField.setText("Place " + usersBattleshipGrid.getUserShips().get(currentShipIndex).getName());

        //Send users board across
        if(isServer){
            server.sendServerBoard(usersBattleshipGrid);
        }else{
            client.sendSClientBoard(usersBattleshipGrid);
        }
    }

    ////////////////////////////////////////////////////////////////
    //                     Mouse Listner Class For Panels         //
    ////////////////////////////////////////////////////////////////
    private class panelMouseListener extends MouseAdapter {

        ////////////////////////////////////////////////////////////////
        //                     mouseClicked                            //
        ////////////////////////////////////////////////////////////////
        @Override
        public void mouseClicked(MouseEvent e) {
            if(opponentsBattleshipGrid.isOutOfShips()){
                frame.setVisible(false);
                gameOverScreen.setVisible(true);
                gameOverScreenLabel.setText("You Win");
                gameOverScreen.add(gameOverScreenLabel);
            }

            if(usersBattleshipGrid.isOutOfShips()){
                frame.setVisible(false);
                gameOverScreen.setVisible(true);
                gameOverScreenLabel.setText("Lose");
                gameOverScreen.add(gameOverScreenLabel);
            }

            usersBattleshipGrid.setOpponentShotsFired(opponentsBattleshipGrid.getUserShotsFired());
            usersBattleshipGrid.updatePanels(usersBattleshipGrid.getUserShips());

            //Update the highlighted cells
            updateHighlightedCells();

            //Get the label clicked
            JLabel label = (JLabel) e.getSource();

            //If all the ships arnt placed get the ship desired
            if(!usersBattleshipGrid.allShipsPlaced())
                currentShip = usersBattleshipGrid.getUserShips().get(currentShipIndex);

            //Go through and find the clicked panel
            int x = 0;
            int y = 0;
            for (int i = 0; i < rows; i++) {
                for (int j = 0; j < cols; j++) {
                    if (label == userGridLables[i][j]) {//If label is in the gridLables
                        //Place the peice
                        x = i;
                        y = j;
                        if(!allShipsPlaced)
                            handleUserGrid(x, y);
                    } else if (label == opponentGridLables[i][j]) {
                        x = i;
                        y = j;
                        if(usersBattleshipGrid.isTurn() && usersBattleshipGrid.allShipsPlaced()){
                            usersBattleshipGrid.fireShot(x,y);//Fire shot
                            playShotSound();
                        }else{System.out.println("Not executing Shot fired");}
                        handleOpponentGridClick(x, y);
                    }
                }
            }
            //Update the opponents board
            if(isServer){
                server.sendServerBoard(usersBattleshipGrid);
            }else{
                client.sendSClientBoard(usersBattleshipGrid);
            }
            //Update the grid Panels
            usersBattleshipGrid.updatePanels(usersBattleshipGrid.getUserShips());
            opponentsBattleshipGrid.updatePanels(opponentsBattleshipGrid.getOpponentShips());
        }

        ////////////////////////////////////////////////////////////////
        //                     Handle User Grid                       //
        ////////////////////////////////////////////////////////////////
        private void handleUserGrid(int x, int y) {
            if (usersBattleshipGrid.allShipsPlaced()) {
                System.out.println("All Ships Placed ATTACK!");
                return;
            }

            //Check if the ship has already been placed
            if (currentShip.isShipPlaced()) {
                textField.setText(usersBattleshipGrid.getUserShips().get(currentShipIndex).getName() + " already placed");
                return;
            }

            updateHighlightedCells();
            usersBattleshipGrid.updatePanels(usersBattleshipGrid.getUserShips());

            // Store the coordinates where the user wants to place the ship
            shipPlacementX = x;
            shipPlacementY = y;

            //Change the background color of the selected cell to indicate selection
            int count = 0;
            if("horizontal".equals(shipCurrentRotation.toLowerCase())) {
                while (y < 10 && count < usersBattleshipGrid.getUserShips().get(currentShipIndex).getSize()) {
                    userGridLables[x][y].setBackground(Color.YELLOW);
                    y++;
                    count++;
                }
            } else {
                while (x < 10 && count < usersBattleshipGrid.getUserShips().get(currentShipIndex).getSize()) {
                    userGridLables[x][y].setBackground(Color.YELLOW);
                    x++;
                    count++;
                }
            }

        }


        ////////////////////////////////////////////////////////////////
        //                     Handle Clicking right grid             //
        ////////////////////////////////////////////////////////////////
        private void handleOpponentGridClick(int x, int y) {
            if(usersBattleshipGrid.isTurn() && usersBattleshipGrid.allShipsPlaced()) {
                // Set the clicked cell to red
                if(opponentsBattleshipGrid.getGridPanel(x,y) == 'S')
                    opponentGridLables[x][y].setBackground(Color.RED);
                else{
                    opponentGridLables[x][y].setBackground(Color.WHITE);
                }
                //Send the users board to the client
                if(isServer){
                    server.sendServerBoard(usersBattleshipGrid);
                    usersBattleshipGrid.setIsTurn(false);
                }else{
                    client.sendSClientBoard(usersBattleshipGrid);
                    usersBattleshipGrid.setIsTurn(false);
                }

            }else{//If it's not the users turn
                textField.setText("Not yout turn");
            }
            updateHighlightedCells();
        }
    }

    ////////////////////////////////////////////////////////////////
    //                     Update Label Visibility                //
    ////////////////////////////////////////////////////////////////
    public void updateLabelVisibility() {
        //Go through user Ships
        for(Ship ship : opponentsBattleshipGrid.getUserShips()) {
            if (ship.getHealth() <= 0) {
                //If the ship is out of health, remove the label from the panel
                if(ship.getName().equalsIgnoreCase("Aircraft"))
                    opponentsImagePanel.remove(OpponentAircraftLabel);
                if(ship.getName().equalsIgnoreCase("Battleship"))
                    opponentsImagePanel.remove(OpponentBattleshipLabel);
                if(ship.getName().equalsIgnoreCase("Submarine"))
                    opponentsImagePanel.remove(OpponentSubmarineLabel);
                if(ship.getName().equalsIgnoreCase("Cruiser"))
                    opponentsImagePanel.remove(OpponentCruiserLabel);
                if(ship.getName().equalsIgnoreCase("Destroyer"))
                    opponentsImagePanel.remove(OpponentDestroyLabel);

                opponentsImagePanel.revalidate();
                opponentsImagePanel.repaint();
            }
        }
    }

    private void initializeShotSound() {
        try {
            // Load the sound file
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(new File("Ships/pew.wav"));
            shotSound = AudioSystem.getClip();
            shotSound.open(audioInputStream);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void playShotSound() {
        if (shotSound != null) {
            shotSound.setFramePosition(0);
            shotSound.start();
        }
    }


    ////////////////////////////////////////////////////////////////
    //                     Get user BattleshipGrid                //
    ////////////////////////////////////////////////////////////////
    public BattleshipGrid getUsersBattleshipGrid() {
        return usersBattleshipGrid;
    }

    ////////////////////////////////////////////////////////////////
    //                     Set Opponets Grid                      //
    ////////////////////////////////////////////////////////////////
    public void setOpponentsBattleshipGrid(BattleshipGrid opponentsBattleshipGrid) {
        this.opponentsBattleshipGrid = opponentsBattleshipGrid;
    }

    ////////////////////////////////////////////////////////////////
    //                     Set If game is server                  //
    ////////////////////////////////////////////////////////////////
    public void setIsServer(boolean server) {
        isServer = server;
    }

    ////////////////////////////////////////////////////////////////
    //                     Set The Server for the game            //
    ////////////////////////////////////////////////////////////////
    public void setServer(Server server) {
        this.server = server;
    }

    ////////////////////////////////////////////////////////////////
    //                     Set The Client                         //
    ////////////////////////////////////////////////////////////////
    public void setClient(Client client) {
        this.client = client;
    }

    ////////////////////////////////////////////////////////////////
    //                     Set visibility of the frame            //
    ////////////////////////////////////////////////////////////////
    public void setVisibility(boolean isVisible) {
        frame.setVisible(isVisible);
    }

    private class PanelMouseListner extends MouseInputAdapter{
        @Override
        public void mousePressed(MouseEvent e) {
            //Get the label
            JLabel clickedLabel = (JLabel) e.getSource();

            clickedLabel.getParent().setComponentZOrder(clickedLabel, 0);
        }

        @Override
        public void mouseReleased(MouseEvent e) {
        }
    }

    private void addShipListners() {
        AircraftLabel.addMouseListener(new PanelMouseListner());

        BattleshipLabel.addMouseListener(new PanelMouseListner());

        CruiserLabel.addMouseListener(new PanelMouseListner());

        SubmarineLabel.addMouseListener(new PanelMouseListner());

        DestroyLabel.addMouseListener(new PanelMouseListner());
    }
}


