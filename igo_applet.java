// igo_applet.java

import java.applet.Applet;
import java.awt.*;
import java.awt.image.*;
import java.awt.event.*;
//import java.util.*; // for Hashtable
//import java.net.*; // for URL

public class igo_applet extends Applet implements ItemListener, ActionListener, MouseListener{
	Color col_1, col_2;

	/***** panel *****/
	int[] pxl_panel;
	int[] pxl_offset;
	int pxl_margin;
	int pxl_cell;
	int pxl_radius;
	int pxl_radius2;
	int sqr_rad;
	
	int size_board;
	int[][] idx_disp;
	int[] size_disp;

	int[] idx_this;
	int attr_this;
	
	igo igo_hd;
	
	/****** settings *******/
	Label label_mode, label_col, label_dummy, label_disp_x, label_disp_y;
	Choice choice_mode, choice_col, choice_x_from, choice_x_to, choice_y_from, choice_y_to;
	Button button_store, button_restore, button_clear, button_write, button_read;
	TextField write_fname, read_fname;
	
	// 1: no change, 2: change
	int play_mode;	
	

	
	/*********************************************************************************************
	 *                                                                                           *
	 *                             Init                                                          *
	 *                                                                                           *
	 *********************************************************************************************/
	public void init() {
		col_1 = Color.black;
		col_2 = Color.white;
		size_board = 19;		
		
		/************************************************
		 *                                              *
		 *            Panel Setting                     *
		 *                                              *
		 ************************************************/
		setLayout(new BorderLayout());
		Panel panel = new Panel();
		panel.setLayout(new GridLayout(2,9));
		add(panel, "North");
		
		/*** Mode ****/
		label_mode = new Label("Mode");
		choice_mode = new Choice();
		choice_mode.add("Setting");
		choice_mode.add("Play   ");
		choice_mode.addItemListener(this);
		
		/*** Stone color ****/
		label_col = new Label("Stone Color");
		choice_col = new Choice();
		choice_col.add("Black");
		choice_col.add("White");
		choice_col.addItemListener(this);
		
		/*** Store/Restore/Clear board ****/
		read_fname = new TextField(" ", 20);
		write_fname = new TextField( " ", 20);
		read_fname.addActionListener(this);
		write_fname.addActionListener(this);
		
		button_store = new Button("Store");
		button_restore = new Button("Restore");
		button_clear = new Button("Clear");
		label_dummy = new Label(" ");
		button_store.addActionListener(this);
		button_restore.addActionListener(this);
		button_clear.addActionListener(this);

		/*** Read from/Write to file ****/
		button_write = new Button("Write to File");
		button_read = new Button("Read from File");
		button_write.addActionListener(this);
		button_read.addActionListener(this);
			
		/*** (x, y) range to display ***/
		label_disp_x = new Label("Display(x)");
		label_disp_y = new Label("Display(y)");
		choice_x_from = new Choice();
		choice_x_to = new Choice();
		choice_y_from = new Choice();
		choice_y_to = new Choice();
		for (int loop = 0; loop < size_board; loop++) {
			choice_x_from.add(String.valueOf(loop+1));
			choice_x_to.add(String.valueOf(loop+1));
			choice_y_from.add(String.valueOf(loop+1));
			choice_y_to.add(String.valueOf(loop+1));
		}
		choice_x_from.addItemListener(this);
		choice_x_to.addItemListener(this);
		choice_y_from.addItemListener(this);
		choice_y_to.addItemListener(this);
		
		/*** Add to panel ***/
		panel.add(label_mode);
		panel.add(label_col);
		panel.add(button_store);
		panel.add(button_clear);
		panel.add(read_fname);
		panel.add(write_fname);
		panel.add(label_disp_x);
		panel.add(choice_x_from);
		panel.add(choice_x_to);

		panel.add(choice_mode);
		panel.add(choice_col);
		panel.add(button_restore);
		panel.add(label_dummy);
		panel.add(button_read);
		panel.add(button_write);
		panel.add(label_disp_y);
		panel.add(choice_y_from);
		panel.add(choice_y_to);

		addMouseListener(this);
		
		/************************************************
		 *                                              *
		 *        Board Setting                         *
		 *                                              *
		 ************************************************/
		pxl_panel = new int[2];
		pxl_panel[0] = getSize().width;
		pxl_panel[1] = getSize().height;
		pxl_margin = 50; // margin for panel

		idx_disp = new int[2][2];
		idx_disp[0][0] = 0;
		idx_disp[0][1] = 2;
		idx_disp[1][0] = 0;
		idx_disp[1][1] = 2;
		
		size_disp = new int[2];
		pxl_offset = new int[2];
		
		reset();
		/************************************************
		 *                                              *
		 *                                              *
		 *                                              *
		 ************************************************/
		idx_this = new int[2];
		idx_this[0] = idx_this[1] = 0;
		play_mode = 1;
		attr_this = 1;
		
		igo_hd = new igo(size_board);
		
	}
	
	public void reset() {
		/************************************************
		 *                                              *
		 *        Board Setting                         *
		 *                                              *
		 ************************************************/
		for (int loop = 0; loop < 2; loop++) {
			size_disp[loop] = (idx_disp[loop][1] - idx_disp[loop][0]);
		}
		
		pxl_cell = pxl_panel[0] / size_disp[0];
		int tmp = pxl_panel[1] / size_disp[1];
		if (tmp < pxl_cell) {
			pxl_cell = tmp;
		}
		
		pxl_radius = (int)Math.floor(pxl_cell / 2.5);
		pxl_radius2 = pxl_radius * 2;
		sqr_rad = pxl_radius * pxl_radius;
		
		pxl_offset[0] = (pxl_panel[0] - (size_disp[0]-1)*pxl_cell)/2;
		pxl_offset[1] = pxl_panel[1] - (pxl_panel[1] - (size_disp[1]-1)*pxl_cell - pxl_margin)/2;
	}
	
	
	/*********************************************************************************************
	 *                                                                                           *
	 *                         ActionListner / ItemListener                                      *
	 *                                                                                           *
	 *********************************************************************************************/
	public void itemStateChanged(ItemEvent ie) {
		//if (ie.getSource() == choice_mode) {
		//} else if (ie.getSource() == choice_col) {
		//}
		play_mode = choice_mode.getSelectedIndex()+1;
		attr_this = choice_col.getSelectedIndex()+1;
		idx_disp[0][0] = choice_x_from.getSelectedIndex();
		idx_disp[0][1] = choice_x_to.getSelectedIndex()+1;
		idx_disp[1][0] = choice_y_from.getSelectedIndex();
		idx_disp[1][1] = choice_y_to.getSelectedIndex()+1;
		if (idx_disp[0][0] >= idx_disp[0][1]) {
			idx_disp[0][1] = idx_disp[0][0]+2;
		}
		if (idx_disp[1][0] >= idx_disp[1][1]) {
			idx_disp[1][1] = idx_disp[1][0]+2;
		}
		reset();
		repaint();
	}
	
	public void actionPerformed(ActionEvent ae) {
		if (ae.getSource() == button_store) {
			igo_hd.set_snapshot();
		} else if (ae.getSource() == button_restore) {
			igo_hd.get_snapshot();
			repaint();
		} else if (ae.getSource() == button_clear) {
			igo_hd.init();
			igo_hd.get_snapshot();
			repaint();
		} else if (ae.getSource() == button_read) {
			String fname = read_fname.getText().trim();
			igo_hd.read_from_file(fname, 1);
			size_board = igo_hd.get_size();
			for (int loop1 = 0; loop1 < 2; loop1++) {
				for (int loop2 = 0; loop2 < 2; loop2++) {
					idx_disp[loop1][loop2] = igo_hd.get_idx_disp(loop1, loop2);
				}
			}
			reset();
			repaint();
		} else if (ae.getSource() == button_write) {
			String fname = write_fname.getText().trim();
			//igo_hd.write_board_to_file(fname);
			igo_hd.write_board_to_stdout(fname, idx_disp[0][0], idx_disp[0][1], idx_disp[1][0], idx_disp[1][1]);
		}
	}

	/*********************************************************************************************
	 *                                                                                           *
	 *                                                                                           *
	 *                                                                                           *
	 *********************************************************************************************/	
	public void paint(Graphics g) {
		g.setColor(Color.lightGray);
		g.fillRect(0, 0, pxl_panel[0]-1, pxl_panel[1]-1);
		
		draw_grid(g);
		/*
		for (int loop_x = 0; loop_x < size_disp[0]; loop_x++) {
			for (int loop_y = 0; loop_y < size_disp[1]; loop_y++) {
				draw_stone(g, loop_x, loop_y, ((loop_x+loop_y) % 2) + 1);
			}
		}
		*/
		
		// draw stone
		for (int loop_y = 0; loop_y < size_board; loop_y++) {
			for (int loop_x = 0; loop_x < size_board; loop_x++) {
				int attr = igo_hd.get_attr_from_board(loop_x, loop_y);
				if (attr > 0) {
					draw_stone(g, loop_x, loop_y, attr);
				}
			}
		}
	}

	void draw_grid(Graphics g) {
		g.setColor(Color.black);
		// Horizontal
		for (int loop_y = 0; loop_y < size_disp[1]; loop_y++) {
			int margin_1 = 0, margin_2 = 0;
			if (idx_disp[0][0] > 0) {
				margin_1 = (int)Math.floor(pxl_cell / 3);
			}
			if (idx_disp[0][1] < size_board) {
				margin_2 = (int)Math.floor(pxl_cell / 3);
			}
				
			g.drawLine(pxl_offset[0]-margin_1, pxl_offset[1]-pxl_cell*loop_y, pxl_offset[0]+pxl_cell*(size_disp[0]-1)+margin_2, pxl_offset[1]-pxl_cell*loop_y);
		}	
		
		// Vertical
		for (int loop_x = 0; loop_x < size_disp[0]; loop_x++) {
			int margin_1 = 0, margin_2 = 0;
			if (idx_disp[1][0] > 0) {
				margin_1 = (int)Math.floor(pxl_cell / 3);
			}
			if (idx_disp[1][1] < size_board) {
				margin_2 = (int)Math.floor(pxl_cell / 3);
			}
			g.drawLine(pxl_offset[0]+pxl_cell*loop_x, pxl_offset[1]+margin_1, pxl_offset[0]+pxl_cell*loop_x, pxl_offset[1]-pxl_cell*(size_disp[1]-1)-margin_2);
		}
	}
	
	void draw_stone(Graphics g, int idx_x, int idx_y, int attr) {
		if (idx_x >= idx_disp[0][0] && idx_x < idx_disp[0][1] && idx_y >= idx_disp[1][0] && idx_y < idx_disp[1][1]) {
			if (attr == 1) {
				g.setColor(col_1);
			} else if (attr == 2) {
				g.setColor(col_2);
			}
			
			int center_x = pxl_offset[0] + pxl_cell * (idx_x - idx_disp[0][0])-pxl_radius;
			int center_y = pxl_offset[1] - pxl_cell * (idx_y - idx_disp[1][0])-pxl_radius;
			g.fillOval(center_x, center_y, pxl_radius2, pxl_radius2);
		}
	}

	/*********************************************************************************************
	 *                                                                                           *
	 *                        Mouse Listner                                                      *
	 *                                                                                           *
	 *********************************************************************************************/
	public void mousePressed(MouseEvent me){
		int x=me.getX();
		int y=me.getY();

		int flg = 0;
		for (int loop_y = 0; loop_y < size_disp[1]; loop_y++) {
			int center_y = pxl_offset[1] - pxl_cell*loop_y;
			for (int loop_x = 0; loop_x < size_disp[0]; loop_x++) {
				int center_x = pxl_offset[0] + pxl_cell*loop_x;
				if ((x-center_x)*(x-center_x)+(y-center_y)*(y-center_y) < sqr_rad) {
					flg = 1;
					idx_this[0] = idx_disp[0][0] + loop_x;
					idx_this[1] = idx_disp[1][0] + loop_y;
					break;
				}
			}
		}
		
		if (flg == 1) {
			int attr_tmp = igo_hd.get_attr_from_board(idx_this[0], idx_this[1]);
			int attr;
			if (attr_tmp > 0) {
				attr = 0;
				igo_hd.set_attr_to_board(idx_this[0], idx_this[1], attr);
			} else {
				attr = attr_this;
				igo_hd.set_stone(idx_this[0], idx_this[1], attr);
			}
			repaint();
			if (attr_tmp == 0 && play_mode == 2) {
				attr_this = (attr_this % 2) + 1;
			}
		}
   }

   public void mouseEntered(MouseEvent me){}
   public void mouseExited(MouseEvent me){}
   public void mouseClicked(MouseEvent me){}
   public void mouseReleased(MouseEvent me){}
  
	 
}


