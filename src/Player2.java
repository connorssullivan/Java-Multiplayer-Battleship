//Connor Sullivan

import javax.swing.*;

public class Player2 {
    public static void main( String args[] ) throws ClassNotFoundException {
        GameWindow clientGameWindow = new GameWindow(false);
        Client application; //declare client application

        //if no command line args
        if ( args.length == 0 )
            application = new Client( "127.0.0.1",clientGameWindow ); // connect to localhost
        else
            application = new Client( args[ 0 ] ,clientGameWindow); // use args to connect

        application.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        clientGameWindow.setIsServer(false);
        clientGameWindow.setClient(application);
        application.runClient(); // run client application
    } // end main
}
