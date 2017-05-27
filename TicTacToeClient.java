//Joshua Itagaki
//CS 380

package TicTacToe; //Given files

import java.net.Socket;
import java.util.Scanner;
import java.io.*;

public class TicTacToeClient {
	private Scanner kb = new Scanner(System.in);
	
	public static void main(String[] args)throws IOException{
		new TicTacToeClient();
	}

	public TicTacToeClient(){
		try{
			Socket socket = new Socket("codebank.xyz", 38006);
			ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
			ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());		
			out.writeObject(new ConnectMessage(getUser()));
			System.out.println("Starting New Game");
			out.writeObject(new CommandMessage(CommandMessage.Command.NEW_GAME));
			Message message;	
		run:	while(true){
				message = (Message)in.readObject();
				switch(message.getType()){
					case BOARD:
						BoardMessage bm = (BoardMessage)message;
						if(bm.getStatus()!=BoardMessage.Status.IN_PROGRESS){	//Game ended
							break run;
						}
						board(bm.getBoard());			//Prints board
						spaceOptions(bm.getBoard());	//Prints spaces open
						int position = 0;
						int turn = 2;	//Player 1 or 2 turn (1 is even 2 is odd)
						while(!(position >= 1 && position <= 9)){
							System.out.print("Select a space (S to surrender): ");
							if(kb.nextLine() == "S"){
								if(turn % 2 == 0){
									System.out.println(BoardMessage.Status.PLAYER1_SURRENDER);
								}
								System.out.println(BoardMessage.Status.PLAYER2_SURRENDER);
							}
							else{
								position = kb.nextInt();
								kb.nextLine();
							}
						}
						byte row = (byte)((position-1)/3);
						byte col = (byte)((position-1)%3);
						System.out.println();
						out.writeObject(new MoveMessage(row, col));
						turn++;
						break;
					case ERROR:
						System.out.println(((ErrorMessage)message).getError());
				default:
					break;
				}
			}
			BoardMessage bm = (BoardMessage)message;
			board(bm.getBoard());
			System.out.println(bm.getStatus());	
		}
		catch(Exception e){
			System.out.println(e);
			System.exit(1);
		}
	}


	private void board(byte[][] board){
		for(int i = 0; i < 3; i++){
			for(int j = 0; j < 3; j++){
				char c = board[i][j] == 0? ' ':(board[i][j] == 1? 'X':'O');
				System.out.print(c);
				if(j != 2)
					System.out.print("|");
			}
			if(i != 2)
				System.out.println("\n-+-+-");
		}
		System.out.println("\n");
	}

	private void spaceOptions(byte[][] board){
		for(int i = 0; i < 3; i++){
			for(int j = 0; j < 3; j++){
				if(board[i][j]==0)
					System.out.print(3*i+j+1);
				else
					System.out.print(' ');
				if(j != 2)
					System.out.print("|");
			}
			if(i != 2)
				System.out.println("\n-+-+-");
		}
		System.out.println("\n");
	}


	private String getUser(){
		System.out.print("Enter a username: ");
		return kb.nextLine();
	}

}

