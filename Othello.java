
// The following code implements an intelligent game-playing agent for Othello 
// which uses k-step lool-ahead and MiniMax algorithm to select its moves


import java.io.*;
import java.util.*;

public class Othello {
    int turn;
    int winner;
    int board[][];
    // add required class variables here

    public Othello(String filename) throws Exception {
        File file = new File(filename);
        Scanner sc = new Scanner(file);
        turn = sc.nextInt();
        board = new int[8][8];
        for (int i = 0; i < 8; ++i) {
            for (int j = 0; j < 8; ++j) {
                board[i][j] = sc.nextInt();
            }
        }
        winner = -1;
        sc.close();
        // Student can choose to add preprocessing here
    }

    // add required helper functions here
    private boolean isValidMove(int i, int j) {
        if (board[i][j] != -1) {
            return false;
        }
        int[][] directions = { { 0, 1 }, { 0, -1 }, { 1, 0 }, { -1, 0 }, { 1, 1 }, { -1, -1 }, { 1, -1 }, { -1, 1 } };
        for (int[] dir : directions) {
            int row = i + dir[0];
            int col = j + dir[1];
            boolean flag = false;
            while (row >= 0 && row < 8 && col >= 0 && col < 8) {
                if (board[row][col] == -1) {
                    //no continuous opposite color tile
                    break;
                }
                if (board[row][col] == 1 - turn) {
                    //found opposite color tile
                    flag = true;
                } else if (flag && board[row][col] == turn) {
                    //found tile of same color after opposite color tile
                    return true;
                } else {
                    break;
                }
                //keep moving in that direction
                row += dir[0];
                col += dir[1];
            }
        }
        return false;
    }

    private ArrayList<Integer> validMoves() {
        ArrayList<Integer> moves = new ArrayList<Integer>();
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (isValidMove(i, j)) {
                    int move = 8*i+j;
                    moves.add(move);
                    // System.out.println(move);
                }
            }
        }
        // System.out.println(turn);
        // System.out.println(moves);
        return moves;
    }

    private void makeMove(int i, int j) {
        board[i][j] = turn;
        int[][] directions = { { 0, 1 }, { 0, -1 }, { 1, 0 }, { -1, 0 }, { 1, 1 }, { -1, -1 }, { 1, -1 }, { -1, 1 } };
        for (int[] dir : directions) {
            int row = i + dir[0];
            int col = j + dir[1];
            boolean flag = false;
            while (row >= 0 && row < 8 && col >= 0 && col < 8) {
                if (board[row][col] == -1) {
                    break;
                }
                if (board[row][col] == 1 - turn) {
                    flag = true;
                } else if (flag && board[row][col] == turn) {
                    int r = i + dir[0];
                    int c = j + dir[1];
                    while (r != row || c != col) {
                        board[r][c] = turn;
                        r += dir[0];
                        c += dir[1];
                    }
                    break;
                } else {
                    break;
                }
                row += dir[0];
                col += dir[1];
            }
        }
    }

    private void undoMove(int[][] previousBoardState) {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                board[i][j] = previousBoardState[i][j];
            }
        }
    }

    public int boardScore() {
        /* return num_black_tiles - num_white_tiles if turn = 0, 
         * and num_white_tiles-num_black_tiles otherwise. 
        */
        int black_tiles = 0;
        int white_tiles = 0;
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (board[i][j] == 0) {
                    black_tiles++;
                } 
                else if (board[i][j] == 1) {
                    white_tiles++;
                }
            }
        }
        if (turn == 0) {
        return black_tiles - white_tiles;
        }
        else {
        return white_tiles - black_tiles;
        }
    }
    public int bestMove(int k) {
        /* build a Minimax tree of depth k (current board being at depth 0),
         * for the current player (siginified by the variable turn), and propagate scores upward to find
         * the best move. If the best move (move with max score at depth 0) is i,j; return i*8+j
         * In case of ties, return the smallest integer value representing the tile with best score.
        */
        int bestScore = Integer.MIN_VALUE;
        int bestPos = -1;
        int a = Integer.MIN_VALUE;
        int b = Integer.MAX_VALUE;
        ArrayList<Integer> moves = validMoves();
        for (int i = 0; i < moves.size(); i++) {
            int pos = moves.get(i);
            int x = pos / 8;
            int y = pos % 8;
            int[][] copy = getBoardCopy();
            makeMove(x, y);
            int score = minimax(k-1, a, b, false);
            if (score > bestScore) {
                bestScore = score;
                bestPos = pos;
            } else if (score == bestScore && pos < bestPos) {
                bestPos = pos;
            }
            undoMove(copy);
        }

        return bestPos;
    }
    
    private int minimax(int depth, int a, int b, boolean maxi) {
        if (depth == 0) {
            return boardScore();
        }
        ArrayList<Integer> moves = validMoves();
        if (maxi) {
            int maxScore = Integer.MIN_VALUE;
            for (int i = 0; i < moves.size(); i++) {
                int pos = moves.get(i);
                int x = pos / 8;
                int y = pos % 8;
                int[][] copy = getBoardCopy();
                makeMove(x, y);
                int score = minimax(depth-1, a, b, false);
                maxScore = Math.max(maxScore, score);
                a = Math.max(a, score);
                undoMove(copy);
                if (b <= a) {
                    break;
                }
            }
            return maxScore;
        } else {
            int minScore = Integer.MAX_VALUE;
            for (int i = 0; i < moves.size(); i++) {
                int pos = moves.get(i);
                int x = pos / 8;
                int y = pos % 8;
                int[][] copy = getBoardCopy();
                makeMove(x, y);
                int score = minimax(depth-1, a, b, true);
                minScore = Math.min(minScore, score);
                b = Math.min(b, score);
                undoMove(copy);
                if (b <= a) {
                    break;
                }
            }
            return minScore;
        }
    }
    private int determineWinner() {
        int blackTiles = 0;
        int whiteTiles = 0;

        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (board[i][j] == 0) {
                    blackTiles++;
                } else if (board[i][j] == 1) {
                    whiteTiles++;
                }
            }
        }

        if (blackTiles > whiteTiles) {
            return 0;
        } else if (whiteTiles > blackTiles) {
            return 1;
        } else {
            return -1; // Draw
        }
    }

    public ArrayList<Integer> fullGame(int k) {
        /* Function to compute and execute the best move for each player starting from
         * the current turn using k-step look-ahead. Accordingly modify the board and the turn
         * at each step. In the end, modify the winner variable as required.
         */
        ArrayList<Integer> moves = new ArrayList<Integer>();
        Boolean flag = true;
        while (true) {
            int bestMove = bestMove(k);
            // System.out.println(bestMove);
            if (bestMove == -1) {
                if(flag){
                    turn = 1-turn;
                    flag = false;
                    continue;
                }
                else
                    break;
            }
            int x = bestMove / 8;
            int y = bestMove % 8;
            makeMove(x, y);
            moves.add(bestMove);
            // printBoard();
            turn = 1 - turn;
        }
        winner = determineWinner();
        return moves;
    }   
    
    
    public int[][] getBoardCopy() {
        int copy[][] = new int[8][8];
        for(int i = 0; i < 8; ++i)
            System.arraycopy(board[i], 0, copy[i], 0, 8);
        return copy;
    }

    public int getWinner() {
        return winner;
    }

    // public int getTurn() {
    //     return turn;
    // }

    public void printBoard(){
        for(int i=0;i<8;i++){
            for(int j=0;j<8;j++){
                System.out.print(board[i][j]+" ");
            }
            System.out.println();
        }
    }
    // public static void main(String[] args) throws Exception {
    //     Othello obj = new Othello("input.txt");
    //     // obj.validMoves();
    //     System.out.println(obj.fullGame(1));
    //     System.out.println(obj.getWinner());
    //     // System.out.println(obj.bestMove(1));
    //     // obj.printBoard();
    //     // obj.fullGame(1);
    // }
}


