import java.net.*;
import java.io.*;
import java.util.ArrayList;
class Partie {
    private String ip;
    private int port_multi ;
    private DatagramSocket dso;
    private int num ;
    private boolean en_cours = false;
    private Labyrinthe l;
    private ArrayList<Joueur> joueurs;
    private boolean isEnd = false;
    private int dep_f = 0;
    //private DatagramSocket dso;
    
    
    public Partie(Joueur j) {
        try{
            dso = new DatagramSocket();
        } catch(Exception e){
            e.printStackTrace();
        }
        num = Server.nbr_partie;
        port_multi = 4000 + num;
	joueurs = new ArrayList<Joueur>();
	joueurs.add(j);
	ip = "225.1.2."+num;
	l = new Labyrinthe();
        
    }

    Joueur gagnant(){
        int max = 0;
        Joueur j = null;
        for (Joueur jo : joueurs){
            if (jo.getScore() >= max){
                max = jo.getScore();
                j = jo;
            }
        }

        return j;
    }

    //envoie un message udp a toute la partie 
    void sendAllMessages(String mess,String idJ) {
        try{
            //DatagramSocket dso = new DatagramSocket();
            //port_multi =  dso.getLocalPort();
            byte[]data;
            String s= "MESA "+ idJ + " " + mess+"+++";
            data=s.getBytes();
            InetSocketAddress ia=new InetSocketAddress(ip,port_multi);
            DatagramPacket paquet=new 
                DatagramPacket(data,data.length,ia);
            dso.send(paquet);
            System.out.println("send " + ip + " " + port_multi);
            
        } catch(Exception e){
            e.printStackTrace();
        }
    
    }

    void sendMessage(String mess, String idJ, int port){
        try{
            byte[]data;
            String s= "MESP "+ idJ + " " + mess+"+++";
            data=s.getBytes();
            DatagramPacket paquet=new    
                DatagramPacket(data,data.length,
                               InetAddress.getByName("localhost"),port);
            dso.send(paquet);
        
        } catch(Exception e){
            e.printStackTrace();
        }
                
    }

     
    void sendScore(String idJ, int score, int x, int y) {
        try{
            
            byte[]data;
            String s= "SCOR "+ idJ + " " + score + " " +
                x + " " + y +"+++ \n";
            data=s.getBytes();
            InetSocketAddress ia=new InetSocketAddress(ip,port_multi);
            DatagramPacket paquet=new 
                DatagramPacket(data,data.length,ia);
            dso.send(paquet);
            System.out.println("send " + ip + " " + port_multi);
            
        } catch(Exception e){
            e.printStackTrace();
        }
    
    }

    void sendEndMessages(){
        try{
            byte[]data;
            String s= "END "+ this.gagnant().getId() + " " + this.gagnant().getScore() +"+++ \n"; 
            data=s.getBytes();
            InetSocketAddress ia=new InetSocketAddress(ip,port_multi);
            DatagramPacket paquet=new 
                DatagramPacket(data,data.length,ia);
            dso.send(paquet);

            isEnd = true;
        } catch(Exception e){
            e.printStackTrace();
        }
    
    }

    void sendFantMEssages(){
    	try{
    		for (Fantome f:this.getLab().getFantomes()){
    			byte[]data;
            	String s= "FANT "+ f.getX() + " " + f.getY()+"+++"; 
            	System.out.println(s);
            	data=s.getBytes();
            	InetSocketAddress ia=new InetSocketAddress(ip,port_multi);
            	DatagramPacket paquet=new 
                DatagramPacket(data,data.length,ia);
            	dso.send(paquet);
    		}

        } catch(Exception e){
            e.printStackTrace();
        }
    }
    
    
    ArrayList<Joueur> getJoueurs(){
    	return joueurs;
    }
    String getIp(){
        return ip;
    }

    int getNum(){
	return num;
    }
    
    void addJ(Joueur j){
        joueurs.add(j);
    }
    
    void rmJ(Joueur j){
    	joueurs.remove(j);
    }
    
    boolean getEnCours(){
    	return en_cours;
    }
    int getPortMulti(){
        return port_multi;
    }
    
    void setEnCours(){
    	en_cours = !en_cours;
    }

    boolean getIsEnd(){
        return isEnd;
    }
    
    Labyrinthe getLab(){
    	return l;
    }

    boolean isReady(){
        for (Joueur j: joueurs){
            if (!j.getIsReady())
                return false;
        }
        return true;
    }
    
    boolean treatUp(Joueur j, int d){
	int deplace = 0;

        int score = 0;
	int y = j.getY();
	int x = j.getX();
 	for(int i = 0; i<d;i++){
            if((x - 1)>=0){
                if(l.getLab()[x-1][y] != 1){
                    deplace++;
                    if(l.getLab()[x-1][y] == 2){
                        l.removeF(x-1,y);
                        score++;
                        this.sendScore(j.getId(), j.getScore() + score,x - 1, y);
                    }
                    x--;
                }else{
                    break;
                }
            }
            else{
                break;
            }
	}
        j.setX(j.getX() - deplace);
        j.setScore(score);
        if (this.getLab().getFantomes().size() == 0)
            sendEndMessages();
        dep_f++;
        if(dep_f == 3){
        	l.mouveF();
        	dep_f = 0;
        	sendFantMEssages();
        }
	if(score > 0)
	    return true;
	else
            return false;
    }
    
    boolean treatDown(Joueur j, int d){
	int deplace = 0;
        int score = 0;
	int y = j.getY();
	int x = j.getX();
 	for(int i = 0; i<d;i++){
            if((x + 1)<=l.getLab().length){
                if(l.getLab()[x+1][y] != 1){
                    deplace++;
                    if(l.getLab()[x+1][y] == 2){
                        l.removeF(x+1,y);
                        score++;
			this.sendScore(j.getId(), j.getScore() + score,x + 1, y);

                    }
                    x++;
                }else{
                    break;
                }
            }
            else{
                break;
            }
	}
        j.setX(j.getX() + deplace);
        j.setScore(score);
        if (this.getLab().getFantomes().size() == 0)
            sendEndMessages();
        dep_f++;
        if(dep_f == 3){
        	l.mouveF();
        	dep_f = 0;
        sendFantMEssages();

        }

	if(score > 0)
	    return true;
	else
            return false;
    }

    boolean treatLeft(Joueur j, int d){
	int deplace = 0;
        int score = 0;
	int y = j.getY();
	int x = j.getX();
 	for(int i = 0; i<d;i++){
            if((y - 1)>=0){
                if(l.getLab()[x][y-1] != 1){
                    deplace++;
                    if(l.getLab()[x][y-1] == 2){
                        l.removeF(x,y-1);
                        score++;
			this.sendScore(j.getId(), j.getScore() + score, x, y-1);

                    }
                    y--;
                }else{
                    break;
                }
            }
            else{
                break;
            }
	}
        j.setY(j.getY() - deplace);
        j.setScore(score);
        if (this.getLab().getFantomes().size() == 0)
            sendEndMessages();
        dep_f++;
        if(dep_f == 3){
        	l.mouveF();
        	dep_f = 0;
        	sendFantMEssages();

        }

	if(score > 0)
	    return true;
	else
            return false;
    }

    boolean treatRight(Joueur j, int d){
	int deplace = 0;
        int score = 0;
	int y = j.getY();
	int x = j.getX();
 	for(int i = 0; i<d;i++){
            if((y + 1)<=l.getLab()[0].length){
                if(l.getLab()[x][y+1] != 1){
                    deplace++;
                    if(l.getLab()[x][y+1] == 2){
                        l.removeF(x,y+1);
                        score++;
			this.sendScore(j.getId(), j.getScore() + score, x, y+1);

                    }
                    y++;
                }else{
                    break;
                }
            }
            else{
                break;
            }
	}
        j.setY(j.getY() + deplace);
        j.setScore(score);
        if (this.getLab().getFantomes().size() == 0)
            sendEndMessages();
        dep_f++;
        if(dep_f == 3){
        	l.mouveF();
        	dep_f = 0;
        sendFantMEssages();

        }
	if(score > 0)
	    return true;
	else
            return false;
    }
    
    //Test si un joueur existe, si non l'ajoute
    boolean exist (Joueur j){
	for (Joueur joueur: joueurs)
	    if (joueur.getId().equals(j.getId()) || joueur.getPortUdp() == j.getPortUdp())
		return true;
	return false;
    }



}
