package student_player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import boardgame.Board;
import boardgame.BoardState;
import boardgame.Move;
import coordinates.Coord;
import coordinates.Coordinates;
import tablut.TablutBoardState;
import tablut.TablutMove;

public class MyTools {
	
	public static int player;
	public static int opponent;
	public static boolean first_play = true;
    private static Random rand = new Random(1842);
 	public static int MAX_TIME = 1990; // time limit is 2 seconds - buffer
 	public static int FIRST_MAX_TIME = 29990;
	
	// returns the number of simulations we can run, based on whether it's our first move or not
	public static int getNumSimulations() {
		if (first_play) {
			first_play = false;
			return 120;
		} else {
			return 8;
		}
	}
	
	public static TablutMove MonteCarlo(TablutBoardState bs, int player_id) {
		
		player = player_id;
		opponent = (player_id == TablutBoardState.SWEDE) ? TablutBoardState.MUSCOVITE : TablutBoardState.SWEDE;
		long timeLimit;
		
		List<TablutMove> legal = bs.getAllLegalMoves();
		int best_win_rate = 0;
		TablutMove best_move = (TablutMove) bs.getRandomMove();
		
		// set a time limit
		if(first_play) {
			timeLimit = System.currentTimeMillis() + FIRST_MAX_TIME;
		} else {
			timeLimit = System.currentTimeMillis() + MAX_TIME;
		}
		
		for(TablutMove m: legal) {
			
			int win_rate = 0;
			
			for(int i = 0; i < getNumSimulations(); i++) { // run a number of simulations per move
				
				// check if we are timed out
				if(System.currentTimeMillis() >= timeLimit) {
					return best_move;
				}
				
				TablutBoardState clone = (TablutBoardState) bs.clone();
				int score = simulateGame(clone, m);
				if(score == Integer.MAX_VALUE) {
					win_rate++;
				}
			}
			if(win_rate > best_win_rate) {
				best_win_rate = win_rate;
				best_move = m;
			}
			
		}
		
		return best_move;
	}
	
	// simulates the game until the end
	// instead of making random moves as per regular Monte Carlo simulation, we use a greedy move
	public static int simulateGame(TablutBoardState bs, TablutMove m) {
		
		bs.processMove(m);
		while(bs.getWinner() == Board.NOBODY) {
			if(Math.random() < 0.05) {
				TablutMove move = (TablutMove) bs.getRandomMove();
				bs.processMove(move);
			} else {
				TablutMove move = (TablutMove) chooseGreedyMove(bs, bs.getTurnPlayer());
				bs.processMove(move);
			}
		}
		
		if(bs.getWinner() == player) {
			return Integer.MAX_VALUE;
		} else if (bs.getWinner() == opponent) {
			return Integer.MIN_VALUE;
		} else {
			return 0;
		}
	}
	
	// Greedy function used in the game simulation.
	// Chosen because it performed better than simulating with random moves.
	public static Move chooseGreedyMove(TablutBoardState bs, int player_id) {
	    List<TablutMove> options = bs.getAllLegalMoves();


	    TablutMove bestMove = options.get(rand.nextInt(options.size()));

	    int opponent = bs.getOpponent();
	    int minNumberOfOpponentPieces = bs.getNumberPlayerPieces(opponent);
	    boolean moveCaptures = false;

	    for (TablutMove move : options) {

	        TablutBoardState cloneBS = (TablutBoardState) bs.clone();

	        cloneBS.processMove(move);

	        int newNumberOfOpponentPieces = cloneBS.getNumberPlayerPieces(opponent);

	        if (newNumberOfOpponentPieces < minNumberOfOpponentPieces) {
	            bestMove = move;
	            minNumberOfOpponentPieces = newNumberOfOpponentPieces;
	            moveCaptures = true;
	        }

	        if (cloneBS.getWinner() == player_id) {
	            bestMove = move;
	            moveCaptures = true;
	            break;
	        }
	    }

	 
	    if (player_id == TablutBoardState.SWEDE && !moveCaptures) {
	        Coord kingPos = bs.getKingPosition();

	        int minDistance = Coordinates.distanceToClosestCorner(kingPos);

	        for (TablutMove move : bs.getLegalMovesForPosition(kingPos)) {
	            int moveDistance = Coordinates.distanceToClosestCorner(move.getEndPosition());
	            if (moveDistance < minDistance) {
	                minDistance = moveDistance;
	                bestMove = move;
	            }
	        }
	    }
	    return bestMove;
	}
	
}



