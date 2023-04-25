import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.nio.file.Files.exists;

public class Agent {
    String type;
    List<Integer> states;
    HashMap<Integer, Double> statesValue;
    double explorationRate;
    double learningRate;
    double decayGamma;
    int turnPlayer;
    public Agent(String type, int turnPlayer){
        System.out.println("New agent" + turnPlayer + " created.");
        this.type = type;
        this.turnPlayer = turnPlayer;
        this.states = new ArrayList<Integer>(); // record all positions taken
        this.learningRate = 0.9;
        this.explorationRate = 0.99;
        this.decayGamma = 0.9;
        this.statesValue = new HashMap<Integer, Double>(){};
        if(turnPlayer == 1 && exists(Path.of("policy1.txt"))) {
            this.loadPolicy("policy1.txt");
        }else if(turnPlayer == 2 && exists(Path.of("policy2.txt"))){
            this.loadPolicy("policy2.txt");
        }
    }
    public void reset(){
        this.states = new ArrayList<Integer>(); // record all positions taken
        //this.statesValue = new HashMap<Integer, Double>(){};   // state -> value
    }
    public void addState(Integer hashBoard){
        states.add(hashBoard);
    }
    public void printStatesValues(){
        System.out.println("States value of player " + this.type + ": ");
        for (Map.Entry<Integer, Double> entry : this.statesValue.entrySet()) {
            System.out.println(entry.getKey() + " -> " + entry.getValue());
        }
    }
    public void savePolicy(String filePath){
        File file = new File(filePath);
        BufferedWriter bf = null;
        try {
            // create new BufferedWriter for the output file
            bf = new BufferedWriter(new FileWriter(file));

            // iterate map entries
            for (Map.Entry<Integer, Double> entry : this.statesValue.entrySet()) {
                bf.write(entry.getKey()+":"+ entry.getValue());
                bf.newLine();
            }
            bf.flush();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            try {
                bf.close();
            }
            catch (Exception e) {
            }
        }
    }
    public void loadPolicy(String filePath){
        BufferedReader br = null;
        //HashMap<Integer, Double>
        try {
            File file = new File(filePath);
            // create BufferedReader object from the File
            br = new BufferedReader(new FileReader(file));
            String line = null;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(":");
                // first part is name, second is number
                Integer state = Integer.parseInt(parts[0].trim());
                Double value = Double.parseDouble(parts[1].trim());
                this.statesValue.put(state, value);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            if (br != null) {
                try {
                    br.close();
                }
                catch (Exception e) {
                };
            }
        }
        System.out.println("Policy correctly loaded.");
    }
    public boolean makeAction(int posRow, int posCol, Board board){
        if(!board.isEmptySpace(posRow, posCol)){
            System.out.println("- Fatal error: Place already occupied: bad position indexes. - ");
            return false;
        }
        else{
            if(this.turnPlayer != 1 && this.turnPlayer != 2){
                System.out.println(" - Fatal error: Bad player index. - ");
                return false;
            }else{
                board.setPosition(posRow, posCol, this.turnPlayer);
                return true;
            }
        }
    }

    public void selectAndDoMove(Board board, String type){
        int[] move = moveSelection(board, type);
        this.makeAction(move[0], move[1], board);
    }

    public void selectAndDoMove(Board board, String type, boolean debug){
        int[] move = moveSelection(board, type);
        this.makeAction(move[0], move[1], board);
        if(debug){
            System.out.println("Correctly executed move by Agent " + this.turnPlayer + ". Type: " + this.type + ". Move: [" + move[0] + ", " + move[1] + "]. Value: " + this.statesValue.get(board.hashBoard()));
        }
    }
    public int[] exploitationMove(Board board){
        int[] bestAction = new int[]{-1,-1};
        double maxValue = -999;
        for(int[] move: board.movesAvailable){
            Board tempBoard = new Board(board);
            this.makeAction(move[0], move[1], tempBoard);
            int tempHash = tempBoard.hashBoard();
            double value;
            value = this.statesValue.getOrDefault(tempHash, 0.0);
            if(value > maxValue){
                maxValue = value;
                bestAction[0] = move[0];
                bestAction[1] = move[1];
            }
        }
        return bestAction;
    }

    public int[] exploratoryMove(Board board){
        int[] bestAction = new int[]{-1,-1};
        for(int[] move: board.movesAvailable){
            Board tempBoard = new Board(board);
            this.makeAction(move[0], move[1], tempBoard);
            int tempHash = tempBoard.hashBoard();
            if(!this.statesValue.containsKey(tempHash)){
                bestAction[0] = move[0];
                bestAction[1] = move[1];
                return bestAction;
            }
        }
        return exploitationMove(board);
    }
    private int[] moveSelection(Board board, String type){
        if(board.isFull()){
            System.out.println("The board is full, no available moves.");
            return new int[]{-1, -1};
        }
        if(type.equals("random")){
            return board.randomAvailablePosition();
        }else{
            double uniform = Math.random();
            if(uniform <= this.explorationRate){
                return this.exploratoryMove(board);
            }else{
                return this.exploitationMove(board);
                }
            }
        }
    public void feedReward(double reward){
        int key;
        //System.out.println("Feedforward on player "+ turnPlayer + "...");
        for(int i = this.states.size()-1; i >= 0; i--){
            key = this.states.get(i);
            if(!this.statesValue.containsKey(key)){
                this.statesValue.put(key, 0.0);
            }
            double value = this.statesValue.get(key) + this.learningRate * (this.decayGamma*reward - this.statesValue.get(key));
            this.statesValue.put(key, value);
        }
    }
}



