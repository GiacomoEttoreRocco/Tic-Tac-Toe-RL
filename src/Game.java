import java.io.IOException;
import java.io.*;
import java.util.Scanner;
public class Game {
    private Board board;
    private Agent player1;
    private Agent player2;

    public Game(){
        this.board = new Board();
    }
    public void reset(){
        this.board.reset();
        //System.out.println("Board reset. Game over check: " + this.gameOver());
        this.player1.reset();
        this.player2.reset();
    }

    public int gameOver(){
        if(this.board.isFull()){
            //System.out.println("The game is draw.");
            return 0;
        }else {
            int winner = this.board.checkWinner();
            if (winner != 0) {
                //System.out.println("Winner is: " + winner);
                return winner;
            }
            return -1;
        }
    }
    public void giveReward(){
        int result = this.gameOver();
        if(result == -1){
            System.out.println("Fatal error. Game not over.");
        }else{
            //System.out.println("Giving rewards...");
            if(result == 1){
                if(player1.type == "RL"){
                    this.player1.feedReward(1.0);
                }
                if(player2.type == "RL") {
                    this.player2.feedReward(0.0);
                }
            }else if(result == 2){
                if(player1.type == "RL") {
                    this.player1.feedReward(0.0);
                }
                if(player2.type == "RL") {
                    this.player2.feedReward(1.0);
                }
            }else{
                if(player1.type == "RL"){
                    this.player1.feedReward(0.1);
                }
                if(player2.type == "RL"){
                    this.player2.feedReward(0.5);
                }
            }
        }
    }
    public int[] trainPlay(String type1, String type2, int rounds) throws IOException {
        int[] statistics = new int[]{0,0,0};
        this.player1 = new Agent(type1, 1);
        this.player2 = new Agent(type2, 2);
        int winner = -1;
        for(int i = 0; i < rounds; i++){
            this.board.reset();
            while(this.gameOver() == -1){
                this.player1.selectAndDoMove(this.board, type1);
                int hashBoard1 = this.board.hashBoard();
                this.player1.addState(hashBoard1);
                winner = this.gameOver();
                if(winner != -1){
                    this.giveReward();
                    this.reset();
                    break;
                }else{
                    this.player2.selectAndDoMove(this.board, type2);
                    int hashBoard2 = this.board.hashBoard();
                    this.player2.addState(hashBoard2);
                    winner = this.gameOver();
                    if(winner != -1){
                        this.giveReward();
                        this.reset();
                        break;
                        }
                    }
                }
            if(i >= rounds*0.95) {
                statistics[winner]++;
                }
            }
        System.out.println("Number of states Player1: " + this.player1.statesValue.size());
        System.out.println("Number of states Player2: " + this.player2.statesValue.size());

        if(this.player1.type == "RL"){
            this.player1.savePolicy("policy1.txt");
        }
        if(this.player2.type == "RL"){
            this.player2.savePolicy("policy2.txt");
        }
        return statistics;
        }
    public void playAgainstHuman(int humanPos){
        Agent playerAI;
        if(humanPos == 1){
            playerAI = new Agent("RL", 2);
        }else{
            playerAI = new Agent("RL", 1);
        }
        int turn = 1;
        while(gameOver() == -1){
            if(playerAI.turnPlayer == turn){
                playerAI.selectAndDoMove(this.board, "RL", true);
            }else{
                this.board.printMovesAvailable();
                Scanner s = new Scanner(System.in);
                System.out.println("Insert the row of the move: ");
                int row = s.nextInt();
                System.out.println("Insert the col of the move: ");
                int col = s.nextInt();
                this.board.setPosition(row, col, humanPos);
            }
            if(gameOver() != -1){
                if(gameOver() == 0)
                    System.out.println("Game over! It's a draw.");
                else
                    System.out.println("Game over! Winner is "+ turn);
                }
            turn = (turn == 1) ? 2 : 1;
        }
    }
}



