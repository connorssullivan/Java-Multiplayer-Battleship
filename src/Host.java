//Connor Sullivan

import javax.swing.*;

public class Host {
    public static void main(String[] args) {
        GameWindow hostWidow = new GameWindow(true);
        hostWidow.setIsServer(true);
        Server application = new Server(hostWidow); // create server
        hostWidow.setServer(application);
        application.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        application.runServer();
    }
}