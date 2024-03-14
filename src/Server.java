//Connor Sullivan
// Fig. 24.5: Server.java
// Set up a Server that will receive a connection from a client, send 
// a string to the client, and close the connection.
import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

public class Server extends JFrame
{
   private JTextField enterField; // inputs message from user
   private JTextArea displayArea; // display information to user
   private ObjectOutputStream output; // output stream to client
   private ObjectInputStream input; // input stream from client
   private ServerSocket server; // server socket
   private Socket connection; // connection to client
   private int counter = 1; // counter of number of connections
   private GameWindow gameWindow; // Reference to the existing GameWindow instance

   // set up GUI
   public Server(GameWindow gameWindow)
   {
      super( "Server" );
      this.gameWindow = gameWindow;

      enterField = new JTextField(); // create enterField
      enterField.setEditable( false );
      enterField.addActionListener(
              new ActionListener()
              {
                 // send message to client
                 public void actionPerformed( ActionEvent event )
                 {
                    sendData( event.getActionCommand() );
                    enterField.setText( "" );
                 } // end method actionPerformed
              } // end anonymous inner class
      ); // end call to addActionListener

      add( enterField, BorderLayout.NORTH );

      displayArea = new JTextArea(); // create displayArea
      add( new JScrollPane( displayArea ), BorderLayout.CENTER );

      setSize( 300, 150 ); // set size of window
      setVisible( true ); // show window
   } // end Server constructor

   // set up and run server
   public void runServer()
   {
      try // set up server to receive connections; process connections
      {
         server = new ServerSocket( 12345, 100 ); // create ServerSocket

         while ( true )
         {
            try
            {
               waitForConnection(); // wait for a connection
               getStreams(); // get input & output streams
               processConnection(); // process connection
            } // end try
            catch ( EOFException eofException )
            {
               displayMessage( "\nServer terminated connection" );
            } // end catch
            finally
            {
               closeConnection(); //  close connection
               counter++;
            } // end finally
         } // end while
      } // end try
      catch ( IOException ioException )
      {
         ioException.printStackTrace();
      } // end catch
   } // end method runServer

   public void sendServerBoard(BattleshipGrid updatedGrid) {
      try {
         //System.out.println("Attemptig to send: "+ updatedGrid );
         output.writeObject(updatedGrid);
         output.flush();
         output.reset();
         displayMessage("\nClient>>> Sent game state to client");
      } catch (IOException ioException) {
         displayArea.append("\nError writing object");
         System.out.println("Error " + ioException);
      }
   }


   // wait for connection to arrive, then display connection info
   private void waitForConnection() throws IOException
   {
      displayMessage( "Waiting for connection\n" );
      connection = server.accept(); // allow server to accept connection
      displayMessage( "Connection " + counter + " received from: " +
              connection.getInetAddress().getHostName() );
      SwingUtilities.invokeLater(() -> gameWindow.setVisibility(true));
   } // end method waitForConnection

   // get streams to send and receive data
   private void getStreams() throws IOException
   {
      // set up output stream for objects
      output = new ObjectOutputStream( connection.getOutputStream() );
      output.flush(); // flush output buffer to send header information

      // set up input stream for objects
      input = new ObjectInputStream( connection.getInputStream() );

      displayMessage( "\nGot I/O streams\n" );
   } // end method getStreams

   // process connection with client
   private void processConnection() throws IOException {
      setTextFieldEditable(true);
      Object receivedObject = null;
      try {
         while ((receivedObject = input.readObject()) != null) {
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
               // Process other types of messages if needed
               String receivedMessage = (String) receivedObject;
               displayMessage("\n" + receivedMessage);
            } else {
               displayMessage("\nUnknown object type received");
            }
         }
      } catch (ClassNotFoundException classNotFoundException) {
         displayMessage("\nUnknown object type received");
      }
   }


   // close streams and socket
   private void closeConnection()
   {
      displayMessage( "\nTerminating connection\n" );
      setTextFieldEditable( false ); // disable enterField

      try
      {
         output.close(); // close output stream
         input.close(); // close input stream
         connection.close(); // close socket
      } // end try
      catch ( IOException ioException )
      {
         ioException.printStackTrace();
      } // end catch
   } // end method closeConnection

   // send message to client
   private void sendData( String message )
   {
      try // send object to client
      {
         output.writeObject( "SERVER>>> " + message );
         output.flush(); // flush output to client
         displayMessage( "\nSERVER>>> " + message );
      } // end try
      catch ( IOException ioException )
      {
         displayArea.append( "\nError writing object" );
      } // end catch
   } // end method sendData

   // manipulates displayArea in the event-dispatch thread
   private void displayMessage( final String messageToDisplay )
   {
      SwingUtilities.invokeLater(
              new Runnable()
              {
                 public void run() // updates displayArea
                 {
                    displayArea.append( messageToDisplay ); // append message
                 } // end method run
              } // end anonymous inner class
      ); // end call to SwingUtilities.invokeLater
   } // end method displayMessage

   // manipulates enterField in the event-dispatch thread
   private void setTextFieldEditable( final boolean editable )
   {
      SwingUtilities.invokeLater(
              new Runnable()
              {
                 public void run() // sets enterField's editability
                 {
                    enterField.setEditable( editable );
                 } // end method run
              }  // end inner class
      ); // end call to SwingUtilities.invokeLater
   } // end method setTextFieldEditable
} // end class Server


/**************************************************************************
 * (C) Copyright 1992-2005 by Deitel & Associates, Inc. and               *
 * Pearson Education, Inc. All Rights Reserved.                           *
 *                                                                        *
 * DISCLAIMER: The authors and publisher of this book have used their     *
 * best efforts in preparing the book. These efforts include the          *
 * development, research, and testing of the theories and programs        *
 * to determine their effectiveness. The authors and publisher make       *
 * no warranty of any kind, expressed or implied, with regard to these    *
 * programs or to the documentation contained in these books. The authors *
 * and publisher shall not be liable in any event for incidental or       *
 * consequential damages in connection with, or arising out of, the       *
 * furnishing, performance, or use of these programs.                     *
 *************************************************************************/