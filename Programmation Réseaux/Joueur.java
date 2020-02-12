import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.net.*;
import java.io.*;
import java.util.ArrayList;
import java.lang.*;

class Joueur {
    private PrintWriter pw;
    private String id;
    private int port_udp;
    private String ip;
    private boolean isReady = false;
    private int partie_num=-1;
    private int score = 0;
    private int x = 4;
    private int y = 1;

    Joueur(String id, int port_udp , String ip, PrintWriter pw){
    this.port_udp = port_udp;
    this.pw = pw;
    this.id = id;
    this.ip = ip;
    }


    void setPartieNum(int i){
        partie_num = i;
    }
    int getPartieNum(){
        return partie_num;
    }

    String getId(){
	return id;
    }

    String getIp(){
	return ip;
    }

    int getPortUdp(){
	return port_udp;
    }

    boolean getIsReady(){
        return isReady;
    }
    PrintWriter getPw(){
        return pw;
    }

    int getX(){
	return x;
    }

    int getY(){
	return y;
    }
    int getScore(){
	return score;
    }

    void setX(int x){
	this.x = x;
    }

    void setY(int y){
	this.y = y;
    }
  
    void setScore(int score){
	this.score += score;
}

    void setIsReady(){
        isReady = !isReady;
    }
   

   void affiche(){
	System.out.println("Joueur : "+id+" Pos X : " + x + " Pos Y : " + y + " Score : " +score);
}
}
