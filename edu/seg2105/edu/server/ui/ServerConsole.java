package edu.seg2105.edu.server.ui;

import java.io.*;
import java.util.Scanner;

import edu.seg2105.edu.server.backend.EchoServer;
import edu.seg2105.client.common.*;

public class ServerConsole implements ChatIF 
{

  final public static int DEFAULT_PORT = 5555;

  EchoServer server;
  
  /**
   * Scanner to read from the console
   */
  Scanner fromConsole; 

  public ServerConsole(int port) 
  {
    server = new EchoServer(port, this);
    // Create scanner object to read from console
    fromConsole = new Scanner(System.in); 
  }

  
  //Instance methods ************************************************
  
  /**
   * This method waits for input from the console.  Once it is 
   * received, it sends it to the client's message handler.
   */
  public void accept()
  {
    try
    {

      String message;

      while (true)
      {
        message = fromConsole.nextLine();
        
        if (message.startsWith("#")) {
          handleCommand(message);
        }
        else {
          display("SERVER MSG> " + message);
          server.sendToAllClients("SERVER MSG> " + message);
        }
      }
    }
    catch (Exception ex)
    {
      System.out.println
        ("Unexpected error while reading from console!");
    }
  }

  /**
   * This method handles commands from the server console.
   *
   * @param command The command to handle.
   */
  private void handleCommand(String command) {
	  
	  if (command.equals("#quit")) {
		  try {
			  server.close();
		  }
		  catch (IOException e) {
			  display("Error closing server before quitting.");
		  }
		  System.exit(0);
	  }
	  else if (command.equals("#stop")) {
		  server.stopListening();
	  }
	  else if (command.equals("#close")) {
          try {
        	  server.close();
          }
          catch(IOException e) {
        	  display("Error closing server");
          }
	  }
	  else if (command.startsWith("#setport")) {
		  if (server.isListening()) {
			 display("Server is not closed");
		  }
		  else {
			  String[] parts = command.split(" ", 2);
		        if (parts.length > 1) {
		            try {
		                int port = Integer.parseInt(parts[1]);
		                server.setPort(port);
		                display("Port set to " + port);
		            } 
		            catch (NumberFormatException e) {
		                display("Invalid port number.");
		            }
		        } 
		        else {
		            display("Usage: #setport <port>");
		        }
		  }
	  }
	  else if (command.equals("#start")) {
		  if (server.isListening()) {
			  display("Server is not closed");
		  }
		  else {
			  try {
			  server.listen();
			  }
			  catch(IOException e) {
				  display("Couldn't listen");
			  }
		  }
	  }
	  else if (command.equals("#getport")) {
		  display("Current port: " + server.getPort());
	  }
  }

  /**
   * This method overrides the method in the ChatIF interface.  It
   * displays a message onto the screen.
   *
   * @param message The string to be displayed.
   */
  public void display(String message) 
  {
    System.out.println(message);
  }

  
  //Class methods ***************************************************
  
  /**
   * This method is responsible for the creation of the Client UI.
   *
   * @param args[0] The host to connect to.
   */
  public static void main(String[] args) 
  {
    int port = 0;


    try
    {
    	port = Integer.parseInt(args[0]);
    }
    catch(ArrayIndexOutOfBoundsException e)
    {
    	port = DEFAULT_PORT;
    }
    catch(NumberFormatException ne) {
    	port = DEFAULT_PORT;
    }
    ServerConsole sc = new ServerConsole(port);
    try {
    	
        sc.server.listen();
        
    } catch (IOException e) {
    	
        sc.display("Could not listen for clients");
    }
    sc.accept();
    
  }
}
//End of ConsoleChat class
