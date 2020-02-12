import java.net.*;
import java.io.*;
import java.util.ArrayList;
import java.lang.*;

class ServerTCP implements Runnable{
    private Socket socket;
    private Joueur j;


    public ServerTCP(Socket socket){
	this.socket = socket;

    }

    public boolean pEnd(){
    	if (this.j == null)
            return false;
    	else{
            for(Partie p : Server.parties){
                if (j.getPartieNum() == p.getNum()){
                    return p.getIsEnd();
                }
            }
            return false;
    	}
    	
    }

    public void treatNewPartie(PrintWriter pw, String mess){

    	String[] s = mess.split(" ");
    	Joueur  j = new Joueur(s[1], Integer.parseInt(s[2].substring(0,s[2].length()-3)), socket.getInetAddress().getHostAddress(),pw);
    	this.j = j;
        Partie p = new Partie(j);
        this.j.setPartieNum(p.getNum());
        Server.nbr_partie++;
        j.setPartieNum(p.getNum());
        Server.parties.add(p);
        pw.print("REGOK "+p.getNum()+"***");
        pw.flush();
    }
    
    public void treatRegPartie(PrintWriter pw, String mess){
        String[] s = mess.split(" ");
    	
        for (Partie partie: Server.parties){
            if(partie.getNum() == Integer.parseInt(s[3].substring(0,s[3].length()-3)) ){
	    	for (Joueur jo : partie.getJoueurs()){
                    if(jo.getId().equals(s[1])){
                        pw.print("REGNO***");
                        pw.flush();
                        return;
                    }
	    	}
	    	Joueur  j = new Joueur(s[1], Integer.parseInt(s[2]), socket.getInetAddress().getHostAddress(),pw);
    		this.j = j;
    		this.j.setPartieNum(partie.getNum());
                partie.addJ(j);
                pw.print("REGOK " + partie.getNum() + "***");
                pw.flush();
                return;
            }
		
	}
	pw.print("REGNO***");
	pw.flush();
    }

    public void treatUnregPartie(PrintWriter pw, String mess){
        for (Partie partie: Server.parties){
            if(partie.getNum() == j.getPartieNum()){
                partie.rmJ(j);
                j.setPartieNum(-1);
                pw.print("UNREGOK " + partie.getNum()+"***");
                pw.flush();
                return;
            }
        }
        pw.print("DUNNO***");
        pw.flush();
        return;

    }
    //gere que ceux dans une partie
    public void treatStartPartie(PrintWriter pw){
        j.setIsReady();
        for (Partie partie: Server.parties){
            if(j.getPartieNum() == partie.getNum()){

                if(partie.isReady()){
                    for(Joueur jo: partie.getJoueurs()){
                        jo.getPw().print("WELCOME "+partie.getNum()+" "+
                                         partie.getLab().getLab().length + " " + partie.getLab().getLab()[0].length
                                         + " " + partie.getLab().getNbr() + " " +partie.getIp() + " " +
                                         partie.getPortMulti()+"***");
                        jo.getPw().flush();
						
                    }
                    partie.setEnCours();
						
                }
                return;
            }
	   		
        }	
    }

    public void treatSizePartie(PrintWriter pw, String mess){
        String[] m = mess.split(" ");
        for (Partie partie: Server.parties){
            if(partie.getNum() == Integer.parseInt(m[1].substring(0,m[1].length() - 3))){
                pw.print("SIZE! " + partie.getJoueurs().size() + " " + 
                         partie.getLab().getLab().length + " " + partie.getLab().getLab()[0].length + "***" );
                pw.flush();
                return;	
            }
				
        }
        pw.print("DUNNO***");
        pw.flush();
    }

    public void treatListPartie(PrintWriter pw, String mess){
    	String[] m = mess.split(" ");
        for (Partie partie: Server.parties){
            if(partie.getNum() == Integer.parseInt(m[1].substring(0,m[1].length() - 3))){
                pw.print("LIST! " +partie.getNum() + " "+ partie.getJoueurs().size()+"***");
                for (int i = 0; i<partie.getJoueurs().size(); i++){
                    pw.print("PLAYER "+partie.getJoueurs().get(i).getId() + "***");
                }
                pw.flush();
                return;
            }

        }
        pw.print("DUNNO***");
        pw.flush();
    }

    public void treatGamesPartie(PrintWriter pw){
        int tmp = 0;
        for(Partie partie : Server.parties){
            if (!partie.isReady())
                tmp ++;
        }
        pw.print("GAMES "+tmp+"***");
        for (int i = 0; i<tmp; i++){
            if(!Server.parties.get(i).getEnCours()){
                pw.print("GAME " +Server.parties.get(i).getNum()+ " " +Server.parties.get(i).getJoueurs().size()+ "***");
			
            }
        }
        pw.flush();
    }
    //********************************* MEssages ***************************************
    public void treatAllMessages(PrintWriter pw, String mess){

    	String[] mesall = mess.split(" ");
    	mesall[1] = mesall[1].substring(0,mesall[1].length() - 3);
    	for (Partie partie: Server.parties){
            if(partie.exist(j)){
                partie.sendAllMessages(mesall[1],j.getId());
                pw.print("ALL!***");
                pw.flush();
                return;
            }
    	}

    }
    public void treatSendMessages(PrintWriter pw, String mess){

        String[] mes = mess.split(" ");

        mes[2] = mes[2].substring(0,mes[2].length() - 3);
        for (Partie partie: Server.parties){
            if(partie.exist(j)){
                for(Joueur jo : partie.getJoueurs()){
                    if(jo.getId().equals(mes[1])){
                        partie.sendMessage(mes[2],j.getId(), jo.getPortUdp());
                        pw.print("SEND!***");
                        pw.flush();
                        return;
                    }
						
                }
					
            }
            pw.print("NOSEND***");
            pw.flush();
            return;
        }

    }


    //********************************** Dans partie ************************************
    public void treatGlistPartie(PrintWriter pw){
        for(Partie partie: Server.parties){
            if(partie.getNum() == j.getPartieNum()){
                pw.print("GLIST! "+partie.getJoueurs().size()+"***");
                for(Joueur joueur: partie.getJoueurs()){
                    pw.print("GPLAYER "+joueur.getId()+" "+
                             joueur.getX()+" "+joueur.getY()+" "+ joueur.getScore()+"***");
                }
                pw.flush();
                break;
            }
        }
    }


    public void treatUpPartie(PrintWriter pw, String mess){
        String[] m = mess.split(" ");
        for (Partie partie: Server.parties){
            if(partie.getNum() == j.getPartieNum()){
                if(partie.treatUp(j,Integer.parseInt(m[1].substring(0,m[1].length() - 3)))){
                    pw.print("MOF "+j.getX() +" "+j.getY() +" "+j.getScore()+"***");
                    //mettre les x,y des fantomes pasdes j			
                }
                else
                    pw.print("MOV "+j.getX() +" "+j.getY()+"***");
                pw.flush();
            }
        }

    }
    public void treatDownPartie(PrintWriter pw, String mess){
        String[] m = mess.split(" ");
        for (Partie partie: Server.parties){
            if(partie.getNum() == j.getPartieNum()){
                if(partie.treatDown(j,Integer.parseInt(m[1].substring(0,m[1].length() - 3)))){
                    pw.print("MOF "+j.getX() +" "+j.getY() +" "+j.getScore()+"***");
                    //mettre les x,y des fantomes pasdes j
                }

                else
                    pw.print("MOV "+j.getX() +" "+j.getY()+"***");
                pw.flush();
            }
        }

    }
    public void treatLeftPartie(PrintWriter pw, String mess){
        String[] m = mess.split(" ");
        for (Partie partie: Server.parties){
            if(partie.getNum() == j.getPartieNum()){
                if(partie.treatLeft(j,Integer.parseInt(m[1].substring(0,m[1].length() - 3)))){
                    pw.print("MOF "+j.getX() +" "+j.getY() +" "+j.getScore()+"***");
                    //mettre les x,y des fantomes pasdes j
			
                }
                else
                    pw.print("MOV "+j.getX() +" "+j.getY()+"***");
                pw.flush();
            }
        }

    }
    public void treatRightPartie(PrintWriter pw, String mess){
        String[] m = mess.split(" ");
        for (Partie partie: Server.parties){
            if(partie.getNum() == j.getPartieNum()){
                if(partie.treatRight(j,Integer.parseInt(m[1].substring(0,m[1].length() - 3)))){
                    pw.print("MOF "+j.getX() +" "+j.getY() +" "+j.getScore()+"***");
                    //mettre les x,y des fantomes pasdes j			
                }			
                else
                    pw.print("MOV "+j.getX() +" "+j.getY()+"***");
                pw.flush();
            }
        }
    }

    public String readL(BufferedReader br){
	try{

	    String res = "";
	    int c = -1;
	    int etoile = 0;
	    while(etoile < 3){
		c = br.read();
		if ((char)c == '*')
		    etoile++;
		else
		    etoile = 0;
		res += (char)c;
	    }
            while(br.read() != 10);
	    return res;
	}
	catch(Exception e){
	    System.out.println(e);
	    e.printStackTrace();
	    return e.toString();
	}
    }

    
    public void run(){
	try{
	    boolean fin = true;
	    while(true){
		BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		PrintWriter pw = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
		treatGamesPartie(pw);
		String mess;
		while(fin){

                    mess=readL(br);
                    System.out.println(mess);
			

		    if(mess.substring(0,3).equals("NEW") && !pEnd()){
                        treatNewPartie(pw, mess);

		    }
		    else if (mess.substring(0,3).equals("REG")&& !pEnd()){
			treatRegPartie(pw, mess);
		    }
		    else if (mess.substring(0,5).equals("START")&& !pEnd()){
                        treatStartPartie(pw);
                    }
	 	    else if (mess.substring(0,5).equals("UNREG")&& !pEnd()){
	 	    	treatUnregPartie(pw,mess);
                    }
		    else if (mess.substring(0,5).equals("SIZE?")&& !pEnd()){
	 	    	treatSizePartie(pw,mess);
                    }
		    else if (mess.substring(0,5).equals("LIST?")&& !pEnd()){
	 	    	treatListPartie(pw,mess);
		    }
		    else if (mess.substring(0,6).equals("GAMES?")&& !pEnd()){
	 	    	treatGamesPartie(pw);
		    }
		    //******************* MESSAGE ****************************
                    else if (mess.substring(0,4).equals("ALL?")&& !pEnd()){
                        treatAllMessages(pw,mess);
		    }
		    else if (mess.substring(0,5).equals("SEND?")&& !pEnd()){
                        treatSendMessages(pw,mess);
		    }
		    
		    //*******************Dans une game ***********************

                    else if (mess.substring(0,6).equals("GLIST?")&& !pEnd()){
                        treatGlistPartie(pw);
                    }
	            else if (mess.substring(0,2).equals("UP")&& !pEnd()){
	 	    	treatUpPartie(pw,mess);
                    }
	            else if (mess.substring(0,4).equals("DOWN")&& !pEnd()){
	 	    	treatDownPartie(pw,mess);
                    }
	            else if (mess.substring(0,4).equals("LEFT")&& !pEnd()){
	 	    	treatLeftPartie(pw,mess);
                    }
	            else if (mess.substring(0,5).equals("RIGHT")&& !pEnd()){
	 	    	treatRightPartie(pw,mess);
                    }
	            else if (mess.substring(0,4).equals("QUIT")) {
											if(j!= null){
                        for (Partie partie: Server.parties){
                            if(partie.getNum() == j.getPartieNum()){
                                partie.rmJ(j);
                                break;
                            }
                        }
                        }
                        pw.print("BYE***");
                        pw.flush();
                        br.close();
                        pw.close();
                        socket.close();
                        break;
                    }
                    else if (pEnd()){
                        pw.print("BYE***");
                        pw.flush();
                        br.close();
                        pw.close();
                        socket.close();
                        break;
                    }
                    else{
                        pw.print("REGNO***");
                        pw.flush();
                    }
                }

                return;
	    }
	}
	catch(Exception e){
	    System.out.println(e);
	    e.printStackTrace();
	}
	return;

    }
}
