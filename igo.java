import java.util.ArrayList;

import java.io.File;
import java.io.FileWriter;
import java.io.BufferedWriter;
import java.io.PrintWriter;
import java.io.IOException;

class igo {
	int SIZE;
	int board[][];
	int board_snapshot[][];
	ArrayList<igo_hist> hist;
	int igo_loop;
	
	String outdelim = "\t";

	// variable for applet (read from file)
	int idx_disp[][];
	
	/***********************************************************************
	 *                                                                     *
	 *                                                                     *
	 *                                                                     *
	 *                                                                     *
	 *                                                                     *
	 ***********************************************************************/
	igo() {
		set_size(9);
	}
	
	igo(int this_size) {
		set_size(this_size);
	}

	public void init() {
		igo_loop = 0;
		board = new int[SIZE][SIZE];
		board_snapshot = new int[SIZE][SIZE];
		for (int loop_y = 0; loop_y < SIZE; loop_y++) {
			for (int loop_x = 0; loop_x < SIZE; loop_x++) {
				board[loop_y][loop_x] = 0;
				board_snapshot[loop_y][loop_x] = 0;
			}
		}
		hist = new ArrayList<igo_hist>();
		
		idx_disp = new int[2][2];
		// Temporary
		//read_hist_from_file("sample_data.txt");
	}
	
	/**************************************************************
	 *                                                            *
	 *                get / set functions                         *
	 *                                                            *
	 **************************************************************/
	public void set_size(int size) {
		SIZE = size;
		init();
	}
	
	public int get_size() {
		return SIZE;
	}
	
	public void set_stone(int x, int y, int attr) {
		set_attr_to_board(x, y, attr);
		set_attr_to_hist(x, y, attr);
	}
	 
	public int get_attr_from_board(int x, int y) {
		return board[y][x];
	}
	
	public void set_attr_to_board(int x, int y, int attr) {
		board[y][x] = attr;
	}

	public igo_hist get_hist(int this_loop) {
		return hist.get(this_loop);
	}
	
	public void set_attr_to_hist(int x, int y, int attr) {
		igo_hist h = new igo_hist(x, y, attr);
		hist.add(h);
		igo_loop++;
	}

	// for applet
	public int get_idx_disp(int idx1, int idx2) {
		return idx_disp[idx1][idx2];
	}
	
	/**************************************************************
	 *                                                            *
	 *                get / set snapshort of the board            *
	 *                                                            *
	 **************************************************************/
	public void set_snapshot() {
		for (int loop_y = 0; loop_y < SIZE; loop_y++) {
			for (int loop_x = 0; loop_x < SIZE; loop_x++) {
				board_snapshot[loop_y][loop_x] = board[loop_y][loop_x];
			}
		}
	}
	
	public void get_snapshot() {
		for (int loop_y = 0; loop_y < SIZE; loop_y++) {
			for (int loop_x = 0; loop_x < SIZE; loop_x++) {
				board[loop_y][loop_x] = board_snapshot[loop_y][loop_x];
			}
		}
	}
	
	/**************************************************************
	 *                                                            *
	 *                read hist from / write to file              *
	 *                                                            *
	 **************************************************************/
	// mode: 1 (board only) 2: (board and hist)
	public void read_from_file(String fname, int mode) {
		String indelim = "\t";
		String[] data;
		boolean res;
		
		csv_handler csv_hd = new csv_handler(fname, indelim);

		// get board data
		// -- SIZE --
		res = csv_hd.read_line();
		data = csv_hd.get_data();
		SIZE = Integer.valueOf(data[0]);

		// -- disp_x --
		res = csv_hd.read_line();
		data = csv_hd.get_data();
		idx_disp[0][0] = Integer.valueOf(data[0]);
		idx_disp[0][1] = Integer.valueOf(data[1]);
			
		// -- disp_y --
		res = csv_hd.read_line();
		data = csv_hd.get_data();
		idx_disp[1][0] = Integer.valueOf(data[0]);
		idx_disp[1][1] = Integer.valueOf(data[1]);

		// get location data
		csv_hd.read_header();
		while ((res = csv_hd.read_line()) == true) {
			data = csv_hd.get_data();
			
			int x = Integer.valueOf(data[0]);
			int y = Integer.valueOf(data[1]);
			int attr = Integer.valueOf(data[2]);
			/****************************
			System.out.print(igo_loop);
			System.out.print(":");
			System.out.print(x);
			System.out.print("|");
			System.out.print(y);
			System.out.print("|");
			System.out.print(attr);
			System.out.print("\n");
			******************************/
			
			set_attr_to_board(x, y, attr);
			if (mode == 2) {
				set_attr_to_hist(x, y, attr);
			}
		}
		csv_hd.close_file();
	}
	
	public void write_hist_to_file(String fname) {
	}
	
	public void write_board_to_file(String fname) {
		try {
			File fp = new File(fname);
			PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(fp)));
			pw.println("x"+outdelim+"y"+outdelim+"attr");
			for (int loop_y = 0; loop_y < SIZE; loop_y++) {
				for (int loop_x = 0; loop_x < SIZE; loop_x++) {
					if (board[loop_y][loop_x] > 0) {
						pw.print(loop_x);
						pw.print(outdelim);
						pw.print(loop_y);
						pw.print(outdelim);
						pw.println(board[loop_y][loop_x]);
					}
				}
			}
			pw.close();
		} catch(IOException e) {
			System.out.println(e);
		}
	}
	
	public void write_board_to_stdout(String fname, int disp_x_from, int disp_x_to, int disp_y_from, int disp_y_to) {
		// Header
		System.out.println("#"+outdelim+fname);

		System.out.println(SIZE);
		System.out.println(disp_x_from+outdelim+disp_x_to);
		System.out.println(disp_y_from+outdelim+disp_y_to);
		
		System.out.println("x"+outdelim+"y"+outdelim+"attr");
		for (int loop_y = 0; loop_y < SIZE; loop_y++) {
			for (int loop_x = 0; loop_x < SIZE; loop_x++) {
				if (board[loop_y][loop_x] > 0) {
					System.out.print(loop_x);
					System.out.print(outdelim);
					System.out.print(loop_y);
					System.out.print(outdelim);
					System.out.println(board[loop_y][loop_x]);
				}
			}
		}
	}
	
	public void write_board_matrix_to_stdout() {
		String val;
		for (int loop_y = 0; loop_y < SIZE; loop_y++) {
			for (int loop_x = 0; loop_x < SIZE; loop_x++) {
				if (board[loop_y][loop_x] == 1) {
					val = "o";
				} else if (board[loop_y][loop_x] == 2) {
					val = "x";
				} else {
					val = " ";
				}
				
				if (loop_x > 0) {
					System.out.print(" ");
				}
				System.out.print(val);
			}
			System.out.println("");
		}
	}
}

