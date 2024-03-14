//Connor Sullivan
// Client that reads and displays information sent from a Server.

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

public class Client extends JFrame {
   private JTextField enterField; // enters information from user
   private JTextArea displayArea; // display information to user
   private ObjectOutputStream output; // output stream to server
   private ObjectInputStream input; // input stream from server
   private String chatServer; // host server for this application
   private Socket client; // socket to communicate with server
   private GameWindow gameWindow; // Reference to the existing GameWindow instance

   public Client(String host, GameWindow gameWindow) {
      super("Client");
      this.gameWindow = gameWindow;
      chatServer = host;

      enterField = new JTextField();
      enterField.setEditable(false);
      enterField.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent event) {
            sendData(event.getActionCommand());
            enterField.setText("");
         }
      });
      add(enterField, BorderLayout.NORTH);

      displayArea = new JTextArea();
      add(new JScrollPane(displayArea), BorderLayout.CENTER);

      setSize(300, 150);
      setVisible(true);
   }

   public void runClient() {
      try {
         connectToServer();
         getStreams();
         processConnection();
      } catch (EOFException eofException) {
         displayMessage("\nClient terminated connection");
      } catch (IOException ioException) {
         ioException.printStackTrace();
      } finally {
         closeConnection();
      }
   }

   private void connectToServer() throws IOException {
      displayMessage("Attempting connection\n");
      client = new Socket(InetAddress.getByName(chatServer), 12345);
      displayMessage("Connected to: " + client.getInetAddress().getHostName());
      SwingUtilities.invokeLater(() -> gameWindow.setVisibility(true));
   }

   private void getStreams() throws IOException {
      output = new ObjectOutputStream(client.getOutputStream());
      output.flush();
      input = new ObjectInputStream(client.getInputStream());
      displayMessage("\nGot I/O streams\n");
   }

   private void processConnection() throws IOException {
      setTextFieldEditable(true);
      Object receivedObject = null; // Initialize receivedObject
      do {
         try {
            receivedObject = input.readObject();
            if (receivedObject instanceof BattleshipGrid) {
               BattleshipGrid receivedGrid = (BattleshipGrid) receivedObject;
               gameWindow.setOpponentsBattleshipGrid(receivedGrid);
               // Set the user's turn to true
               gameWindow.getUsersBattleshipGrid().setIsTurn(true);
               // Update the opponent's shots fired on the user's grid
               gameWindow.getUsersBattleshipGrid().setOpponentShotsFired(receivedGrid.getUserShotsFired());
               gameWindow.usersBattleshipGrid.updatePanels(gameWindow.usersBattleshipGrid.getUserShips());
               gameWindow.updateHighlightedCells();
               gameWindow.updateLabelVisibility();
               // Display the received grid
               displayMessage("\nReceived Grid: " + receivedGrid);
            } else if (receivedObject instanceof String) {
               String receivedMessage = (String) receivedObject;
               displayMessage("\n" + receivedMessage);
            } else {
               displayMessage("\nUnknown object type received");
            }
         } catch (ClassNotFoundException classNotFoundException) {
            displayMessage("\nUnknown object type received");
         }
      } while (receivedObject == null || !receivedObject.equals("SERVER>>> TERMINATE"));
   }

   private void closeConnection() {
      displayMessage("\nClosing connection");
      setTextFieldEditable(false);
      try {
         output.close();
         input.close();
         client.close();
      } catch (IOException ioException) {
         ioException.printStackTrace();
      }
   }

   private void sendData(String message) {
      try {
         output.writeObject("CLIENT>>> " + message);
         output.flush();
         displayMessage("\nCLIENT>>> " + message);
      } catch (IOException ioException) {
         displayArea.append("\nError writing object");
      }
   }

   private void displayMessage(final String messageToDisplay) {
      SwingUtilities.invokeLater(() -> {
         displayArea.append(messageToDisplay);
      });
   }

   public void sendSClientBoard(BattleshipGrid updatedGrid) {
      try {
         System.out.println("Attemptig to send: "+ updatedGrid );
         //updatedGrid.printUsersShots();
         output.writeObject(updatedGrid);
         System.out.println("\n\nSending these Shots: ");
         updatedGrid.printUsersShots();
         output.reset();
         output.flush();
         displayMessage("\nClient>>> Sent game state to client");
      } catch (IOException ioException) {
         displayArea.append("\nError writing object");
         System.out.println("Error " + ioException);
      }
   }


   private void setTextFieldEditable(final boolean editable) {
      SwingUtilities.invokeLater(() -> {
         enterField.setEditable(editable);
      });
   }
}
