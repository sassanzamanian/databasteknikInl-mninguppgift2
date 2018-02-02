
import java.sql.*;

import javax.swing.JOptionPane;
public class Fråga1 
{

	public static void main(String[] args) 
	{	
		String input = JOptionPane.showInputDialog("skriv in kundID");
		
		ResultSet result;
		
		try 
		{
			Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/webbshop?autoReconnect=true&useSSL=false", "root", "vattenfall9");
			
			if (input.isEmpty()) 
			{
				
				Statement statement = connection.createStatement();
				
				result = statement.executeQuery("select * from totalVärde");
				
			} 
			
			else 
			{
				int inputInt = Integer.parseInt(input);
				
				PreparedStatement statement = connection.prepareStatement("select * from totalVärde where kundID = ?");
				
				statement.setInt(1, inputInt);
				
				result = statement.executeQuery();
			}

			System.out.println("Kundens namn"+"\t\t" +"Totalt beställningsbelopp");
			System.out.println("----------------------------------------------------");
			
			
			while (result.next()) 
			{
				System.out.println(result.getString("kundNamn") +"\t"+ result.getString("summa_belopp"));
			}
		}
		
		catch (Exception e) 
		{
			e.printStackTrace();
		}
	}

}
