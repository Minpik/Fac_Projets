import java.util.ArrayList;
import java.lang.Math;
class Labyrinthe {
    private int [][] laby;
    private int id;
    private int nbr;
    private ArrayList<Fantome> fantomes;
    public Labyrinthe(){
    	nbr=2;
	int[][] tab = new int[6][7];
        fantomes = new ArrayList<Fantome>();
	tab[0][0] = 1;
	tab[1][0] = 1;
	tab[2][0] = 1;
	tab[4][0] = 1;
	tab[5][0] = 1;
	tab[1][1] = 2;
	tab[5][1] = 1;
	tab[0][2] = 1;
	tab[1][2] = 1;
	tab[2][2] = 1;
	tab[3][2] = 1;
	tab[5][2] = 1;
	tab[0][3] = 1;
	tab[2][3] = 1;
	tab[5][3] = 1;
	tab[0][4] = 1;
	tab[5][4] = 1;
	tab[0][5] = 1;
	tab[1][5] = 2;
	tab[2][5] = 1;
	tab[3][5] = 1;
	tab[0][6] = 1;
	tab[2][6] = 1;
	tab[3][6] = 1;
	tab[4][6] = 1;
	tab[5][6] = 1;
	fantomes.add(new Fantome(1,1));
	fantomes.add(new Fantome(1,5));
	laby = tab;
    }
   

    void mouveF(){//Deplace Fantomes
	int nextX;
	int nextY;
	for(Fantome f: fantomes){
            while(true){
                nextX = (int)(Math.random() * (laby.length -1));
                nextY = (int)(Math.random() * (laby[0].length -1));
                if(laby[nextX][nextY]==0){
                    laby[f.getX()][f.getY()] = 0;
                    laby[nextX][nextY] = 2;
                    f.setPos(nextX,nextY);
                    break;
                }
            }
        }
    }
 
    int[][] getLab(){
    	return laby;
    }

    void aff(){
	for (int i = 0;i < laby.length;i++){
	    System.out.println();
	    for (int j = 0 ;j < laby[i].length; j++){
		if(laby[i][j] == 1) System.out.print("#");
		else if (laby[i][j] == 0) System.out.print("O");
		else System.out.print("F");
	    }
	}
	System.out.println();
    } 
    void removeF(int x, int y){
	for(Fantome f: fantomes){
	if(f.getX() == x && f.getY() == y){
	fantomes.remove(f);
	break;
	}
}
	laby[x][y] = 0;
    }

    int getNbr(){
    	return nbr;
    }

    ArrayList<Fantome> getFantomes(){
    	return fantomes;
    }

    public static void main(String[] args) {
	Labyrinthe l = new Labyrinthe();
	l.aff();
	l.removeF(1,1);
	l.mouveF();
	l.aff();
	l.mouveF();
	l.aff();
	l.mouveF();
	l.aff();
	
    }
}
    
