import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Board{
    public int[][] grid;
    public List<int[]> movesAvailable;
    public Board() {
        System.out.println("Board initialization.");
        this.grid = new int[3][3];
        this.movesAvailable = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                this.grid[i][j] = 0;
                movesAvailable.add(new int[]{i, j});
                //System.out.println("Moves added: " + i + " " + j);
            }
        }
    }

    public Board(Board b){
        this.grid = new int[3][3];
        this.movesAvailable = new ArrayList<>();
        for(int i = 0; i < 3; i++){
            for(int j = 0; j < 3; j++) {
                this.grid[i][j] = b.grid[i][j];
            }
        }
        for(int[] pos: b.movesAvailable){
            this.movesAvailable.add(new int[]{pos[0], pos[1]});
        }
    }

    public void printMovesAvailable(){
        for(int i = 0; i < this.movesAvailable.size(); i++){
            System.out.println(movesAvailable.get(i)[0] + ", " + movesAvailable.get(i)[1]);
        }
    }
    public boolean isEmptySpace(int posRow, int posCol){
        if(this.grid[posRow][posCol] != 0){
            System.out.println("Place already occupied.");
            return false;
        }
        return true;
    }

    public void setPosition(int posRow, int posCol, int turnPlayer){
        this.grid[posRow][posCol] = turnPlayer;
        int[] moveToMake = new int[]{posRow, posCol};
        for(int i = 0; i < this.movesAvailable.size(); i++){
            int[] toRemove = this.movesAvailable.get(i);
            if((toRemove[0] == moveToMake[0]) && (toRemove[1] == moveToMake[1])){
                this.movesAvailable.remove(i);
                break;
            }
        }
    }
    public void reset(){
        this.grid = new int[3][3];
        this.movesAvailable = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                this.grid[i][j] = 0;
                this.movesAvailable.add(new int[]{i, j});
            }
        }
    }
    public int hashBoard(){
        int hashValue = 0;
        for(int i = 0; i < 3; i++){
            for(int j = 0; j < 3; j++){
                hashValue += Math.pow(3, 3*i+j) * this.grid[i][j];
            }
        }
        //System.out.println(hashValue);
        return hashValue;
    }
    public int[] randomAvailablePosition(){
        Random rand = new Random();
        int upperBound = this.movesAvailable.size();
        int randomInt = rand.nextInt(upperBound);
        return this.movesAvailable.get(randomInt);
    }
    public int checkWinner(){
        // rows
        for(int row = 0; row < 3; row++) {
            if ((this.grid[row][0] != 0) && (this.grid[row][0] == this.grid[row][1]) && (this.grid[row][0] == this.grid[row][2])){
                return this.grid[row][0];
            }
            if ((this.grid[0][row] != 0) && (this.grid[0][row] == this.grid[1][row]) && (this.grid[0][row] == this.grid[2][row])){
                return this.grid[0][row];
            }
        }
        if((this.grid[0][0] == this.grid[1][1]) && (this.grid[0][0] == this.grid[2][2]) && (this.grid[0][0] != 0)){
            return this.grid[0][0];
        }
        if((this.grid[0][2] == this.grid[1][1]) && (this.grid[0][2] == this.grid[2][0]) && (this.grid[0][2] != 0)){
            return this.grid[0][2];
        }
        return 0;
    }
    public boolean isFull(){
        for(int i = 0; i < 3; i++){
            for(int j = 0; j < 3; j++){
                if(this.grid[i][j] == 0){
                    return false;
                }
            }
        }
        return true;
    }
    @Override
    public String toString() {
        String s = "";
        for(int i = 0; i<3; i++){
            for(int j = 0; j<3; j++){
                s += this.grid[i][j];
            }
        }
        return s;
    }
}
