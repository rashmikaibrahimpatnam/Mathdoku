import java.util.*;
import java.io.*;

//A class that solves a mathdoku puzzle for the loaded puzzle

public class Mathdoku {

	static ArrayList<String> ar = new ArrayList<>();       //stores the puzzle pattern
	static Set<Character> set = new HashSet<>();           //stores the unique groups present in the puzzle
	static Map<Character,Data> map = new HashMap<>();      //stores the group as key, operator and outcome as Data(value)
	static int[][] matrix;                                 //stores the puzzle
	static char[][] temp;                                  //stores groups for validation
	int choice=0;                                   //stores the choices made to solve the puzzle

	public boolean loadPuzzle(BufferedReader stream)
	{
		//Functionality that takes input from the user and loads the mathdoku puzzle
		//part 1 : pattern of the puzzle to be created - includes caging with the group name
		//part 2 : includes group name operator and outcome 

		try {
			String line = stream.readLine();
			if ((line != null) && (!line.equals("")) && (line.matches("^[a-zA-Z]*$")))     //input must contain only alphabets
			{
				int mat_size = line.length();
				matrix = new int[mat_size][mat_size];
				ar.add(line);
				while((line != null)&&(ar.size()<mat_size))
				{
					String new_line = stream.readLine();

					if (new_line.length() == line.length())
						ar.add(new_line);

					line = new_line;
				}
			}
			else
				return false;

			Set<Character> st = set_data();
			for(int val=0;val<st.size();val++)
			{
				String op = stream.readLine();

				if ((op != null) && (!op.equals("")))      
				{

					String format = op.replaceAll(" ", "");
					if (st.contains(format.charAt(0)))           //checking if the group is given in the pattern
					{
						//part 1 : takes only alphabets part 2 : only integers containing 0-9 part 3 : only operators +,-,/,*,=

						if ((format.substring(0, 1).matches("^[a-zA-Z]*$") == true) && (format.substring(1,format.length()-1).matches("^[0-9]*$") == true) && (format.substring(format.length()-1).matches("[-+*/=]")) == true)
						{
							char group = format.charAt(0);
							String value = format.substring(1,format.length()-1);
							char operator = format.charAt(format.length()-1);
							Data dt = new Data(value,operator);
							map.put(group, dt);              
						}
						else
							return false;
					}
					else
						return false;

				}
				else
					return false;
			}
			return true;
		}catch(Exception e)
		{
			e.getMessage();
			return false;
		}
	}

	private static Set<Character> set_data()
	{
		//Functionality that categorizes groups given in the puzzle pattern and fetches unique set

		for(int cell_type=0;cell_type<ar.size();cell_type++)
		{

			for(int cell_name =0;cell_name<ar.size();cell_name++)
			{
				char c = ar.get(cell_type).charAt(cell_name);
				if (!set.contains(c))
					set.add(c);

			}
		}
		return set;

	}

	public boolean readyToSolve()
	{
		//Functionality that checks the puzzle for all the requirements before solving it

		try {
			if (!set.isEmpty() && set.size() != 1)
			{
				Iterator<Character> itr = set.iterator();
				//int count =0;
				temp();   //stores the pattern given as input
				while(itr.hasNext())
				{
					char grp =(char) (itr.next());
					int outcome = Integer.parseInt(map.get(grp).operand);
					char operator = map.get(grp).operation;

					if ((outcome == 0)) //checking for only zero outcome
						return false;

					else if (checkGroup(grp) == false)   //validates the given pattern for the puzzle
						return false;
					else if ((operator == '-') || (operator == '/'))  //only two cells allowed for / and -
					{
						int count =0;
						for(int index =0;index <ar.size();index++)
						{
							for(int cha =0;cha<ar.get(index).length();cha++)
							{
								if (grp == ar.get(index).charAt(cha))
									count +=1;
							}
						}
						if (count!=2)     //returns false if more cells are present for / and -

							return false;
					}

				}

			}
			else
			{
				return false;
			}
			return true;
		}catch(Exception e)
		{
			e.getMessage();
			return false;
		}
	}

	private static char[][] temp()
	{
		//Functionality that stores the pattern of the puzzle into temp matrix before validating the group

		temp = new char[ar.size()][ar.size()];
		for(int i =0;i<ar.size();i++)
		{
			for(int j=0;j<ar.size();j++)
			{
				temp[i][j] = ar.get(i).charAt(j);
			}
		}
		return temp;
	}

	private static boolean checkGroup(char grp)
	{
		//Functionality that validates the group from the given puzzle pattern

		int value = 0;
		boolean flag = false;
		for(int row =0;row<ar.size();row++)
		{
			for(int col=0;col<ar.size();col++)
			{
				if (temp[row][col] == grp)     
				{
					value += 1;   //visited cell count
					if ((col == ar.size()-1) && ((row != ar.size()-1)))      
					{
						if ((temp[row][col-1] == grp) || (temp[row+1][col] == grp))      //checking if previous column or next row are holding the same group
						{
							//next element
							return true;
						}
					}
					else if ((row == ar.size()-1) && ((col != ar.size()-1)))   //checking if previous row or next column are holding the same group
					{
						if ((temp[row-1][col] == grp) || (temp[row][col+1] == grp))
						{
							//below element
							return true;
						}	
					}
					else if ((row == ar.size()-1) && (col == ar.size()-1))   //checking the corners of the matrix
					{
						if ((temp[row-1][col] == grp) || (temp[row][col-1] == grp))
							return true;
					}

					else if ((temp[row][col+1] == grp) )   
					{
						if ((col != 0) && (col != ar.size()-1) && (flag == true))   //if the group is broken inbetween it returns false
							if ((temp[row][col-1] == grp))
								return true;
							else
								return false;
						else		 
							return true;
					}
					else if ((temp[row+1][col] == grp))
					{
						if ((row != 0) && (row != ar.size()-1) && (flag == true))
							if ((temp[row-1][col] == grp))
								return true;
							else
								return false;
						else
							return true;
					}
					else
						flag = true;   //visited group


				}




			}
		}
		if (value == 1) //group has occurred only once
			return true;
		else
			return false;

	}

	public boolean solve()
	{
		//Functionality that solves the loaded puzzle

		try {
			for(int row=0;row<matrix.length;row++)
			{
				for(int col=0;col<matrix.length;col++)
				{
					if(matrix[row][col] == 0)
					{
						char grp = ar.get(row).charAt(col);
						ArrayList<Integer> data = data(grp);
						for(int num=1;num<=matrix.length;num++)
						{
							if((SearchRow(row,num)==false) && (SearchCol(col,num)==false) && (CalGrid(data,row,col,num)==true)) //checks if the assumed number is present in row/column and if it satisfies the cage rule
							{
								matrix[row][col] = num;
								if(solve())         
								{
									return true;
								}
								else
								{
									choice += 1;       //counting the false guesses which do not satisfy the rule
									matrix[row][col] = 0;   //setting back to zero if the guess is incorrect
								}

							}
						}
						return false;
					}
				}
			}
			return true;
		}catch(Exception e)
		{
			e.getMessage();
			return false;
		}


	}

	private static ArrayList<Integer> data(char grp)
	{
		//Functionality that finds the index values of the given group that form a cage
		ArrayList<Integer> index = new ArrayList<>();
		for(int val=0;val<ar.size();val++)
		{
			for(int ch=0;ch<ar.size();ch++)
			{

				if (grp == ar.get(val).charAt(ch))
				{

					index.add(val);
					index.add(ch);

				}
			}
		}
		return index;

	}

	private static boolean SearchRow(int row, int value)
	{
		//Functionality that checks complete row for the given value
		for(int index = 0;index<matrix.length;index++)
			if(matrix[row][index] == value)
				return true;
		return false;
	}

	private static boolean SearchCol(int col, int value)
	{
		//Functionality that checks complete column for the given value
		for(int index = 0;index<matrix.length;index++)
			if(matrix[index][col] == value)
				return true;
		return false;
	}

	private static boolean CalGrid(ArrayList<Integer> grid,int row,int col,int value)
	{
		//Functionality that checks the cage and performs action based on operator

		char grp = ar.get(row).charAt(col);
		int outcome = Integer.parseInt(map.get(grp).operand);
		char operator = map.get(grp).operation;
		if (operator == '=')
		{
			if( value == outcome)

				return true;
			else
				return false;
		}
		if (operator == '+')
		{
			int cell = 0;	
			int sum = 0;
			int flag =0;
			while(flag < grid.size()/2)
			{
				int cell_val = matrix[grid.get(cell)][grid.get(cell+1)];
				sum = sum +cell_val;
				flag += 1;
				cell = cell +2;
			}

			if (sum + value <= outcome)
			{
				return true;
			}
			else 
				return false;						
		}
		else if (operator == '-')
		{
			int sub = 0;
			int cell_val1 = matrix[grid.get(0)][grid.get(1)];
			int cell_val2 = matrix[grid.get(2)][grid.get(3)];
			if ((cell_val1 == 0)&&(cell_val2 == 0))
				return true;

			else if (cell_val1 == 0 || cell_val2 == 0)
			{
				if(cell_val1 != 0)
				{
					if(cell_val1>value)
						sub = cell_val1-value;
					else if (cell_val1<value)
						sub = value-cell_val1;
				}
				else if (cell_val2 != 0)
				{
					if(cell_val2>value)
						sub = cell_val2-value;
					else if (cell_val2<value)
						sub = value-cell_val2;
				}
			}
			if (sub == outcome)
			{

				return true;
			}
			else 
				return false;

		}
		else if (operator == '*')
		{
			int cell = 0;	
			int mult = 1;
			int flag =0;
			while(flag < grid.size()/2)
			{
				int cell_val = matrix[grid.get(cell)][grid.get(cell+1)];
				mult = mult * cell_val;
				flag += 1;
				cell = cell +2;
			}

			if (mult * value<= outcome)
			{

				return true;
			}
			else 
				return false;

		}
		else if (operator == '/')
		{
			int div = 0;
			int cell_val1 = matrix[grid.get(0)][grid.get(1)];
			int cell_val2 = matrix[grid.get(2)][grid.get(3)];
			if ((cell_val1 == 0)&&(cell_val2 == 0))
				return true;

			else if (cell_val1 == 0 || cell_val2 == 0)
			{
				if(cell_val1 != 0)
				{
					if(cell_val1>value)
						div = cell_val1/value;
					else if (cell_val1<value)
						div = value/cell_val1;
				}
				else if (cell_val2 != 0)
				{
					if(cell_val2>value)
						div = cell_val2/value;
					else if (cell_val2<value)
						div = value/cell_val2;
				}

			}
			if (div == outcome)
			{

				return true;
			}
			else 
				return false;

		}
		return false;

	}



	public String print()
	{
		//Functionality that prints the numbers stores after solving the puzzle
		try {
			StringBuilder stb =new StringBuilder();  
			for(int row=0;row<matrix.length;row++)
			{
				for(int col=0;col<matrix.length;col++)
				{
					if (matrix[row][col] == 0)
					{
						stb.append(ar.get(row).charAt(col));
					}
					else
					{
						stb.append(Integer.toString(matrix[row][col]));
					}
				}
				stb.append("\\n");
			}
			return stb.toString();
		}catch(Exception e)
		{
			e.getMessage();
			return null;
		}

	}


	public int choices()
	{
		//Functionality that returns choices made for solving the puzzle
		return choice;
	}


}