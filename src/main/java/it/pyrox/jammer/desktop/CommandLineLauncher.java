package it.pyrox.jammer.desktop;

// This class is needed i order to launch the application from command line.
// Launching the main from the class extending Application does not work.
public class CommandLineLauncher {
	
	public static void main(String[] args) {
        MainApplication.main(args);
    }
}
