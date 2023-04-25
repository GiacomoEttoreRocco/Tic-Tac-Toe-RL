import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        double total;
        Game game = new Game();
        boolean train = false;
        if(train) {
            int[] stats = game.trainPlay("RL", "RL", 300000);
            total = stats[0] + stats[1] + stats[2];
            System.out.println("Draws: " + String.format("  %.2f", stats[0] / total * 100) + "%\n"
                    + "Player1: " + String.format("%.2f", stats[1] / total * 100) + "%\n"
                    + "Player2: " + String.format("%.2f", stats[2] / total * 100) + "%\n");
        }
        game.playAgainstHuman(2);
    }
}
