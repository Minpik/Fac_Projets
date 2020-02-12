import java.net.*;
import java.io.*;
import java.util.ArrayList;
import java.lang.*;
class Server{
    static ArrayList<Partie> parties = new ArrayList<Partie>();
    static int nbr_partie = 1;
    

    public static void main(String[] args){

	try{
	    ServerSocket server=new ServerSocket(4242);
	    while(true){
	    Socket socket=server.accept();
	    Thread t = new Thread(new ServerTCP(socket));
	    t.start();
	    }

	}
	catch(Exception e){
	    System.out.println(e);
	    e.printStackTrace();
	}
    }
}

