public class igo_hist{
	int x, y;
	int attr;

	public igo_hist(){
		x = 0;
		y = 0;
		attr = 0;
	}

	public igo_hist(int this_x, int this_y, int this_attr){
		 x = this_x;
		 y = this_y;
		 attr = this_attr;
	}

	public int get_x(){
		 return x;
	}

	public int get_y(){
		 return y;
	}

	public int get_attr(){
		 return attr;
	}
}   
