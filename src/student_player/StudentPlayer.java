package student_player;


import java.util.ArrayList;

import boardgame.Move;
import tablut.TablutBoardState;
import tablut.TablutMove;
import tablut.TablutPlayer;

/** A player file submitted by a student. */
public class StudentPlayer extends TablutPlayer {
	
	// iterative deepening with alpha beta pruning
//	https://github.com/nealyoung/CS171/blob/master/AI.java
	// use transposition table
	
	public static int MAX_TIME = 1990; // time limit is 2 seconds - buffer
	public static int FIRST_MAX_TIME = 29990;
	public static boolean FIRST_TURN = true;
	
    /**
     * You must modify this constructor to return your student number. This is
     * important, because this is what the code that runs the competition uses to
     * associate you with your agent. The constructor should do nothing else.
     */
    public StudentPlayer() {
        super("260631443");
    }

    /**
     * This is the primary method that you need to implement. The ``boardState``
     * object contains the current state of the game, which your agent must use to
     * make decisions.
     */
    public Move chooseMove(TablutBoardState boardState) {
    	
    		long timeLimit;
    	
    		if (FIRST_TURN) {
    			timeLimit = FIRST_MAX_TIME;
    			FIRST_TURN = false;
    		} else {
    			timeLimit = MAX_TIME;
    		}
    	
    		int best_score = Integer.MIN_VALUE;
    		TablutMove bestMove = (TablutMove) boardState.getRandomMove();
    		
    		ArrayList<TablutMove> legal_moves = boardState.getAllLegalMoves();
    		for (TablutMove m: legal_moves) {
    			TablutBoardState bs = (TablutBoardState) boardState.clone();
    			bs.processMove(m);
    			
    			timeLimit = timeLimit / legal_moves.size();
    		    int curr_score = MyTools.TimeLimitedIDS(bs, player_id, timeLimit);
    			
    			if(curr_score >= 500 ){
    				return m;
    			}
    			
    			if(curr_score > best_score) {
    				best_score = curr_score;
    				bestMove = m;
    			}
    		}
    			
  
    	  return bestMove;
    }
}