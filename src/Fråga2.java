import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;

public class Fråga2 
{

	public static void main(String[] args) 
	{
		
		try 
		{
			Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/webbshop?autoReconnect=true&useSSL=false", "root", "vattenfall9");
			
			Statement statement = connection.createStatement();
			
			ResultSet result = statement.executeQuery("select * from kategori_och_produkter");
			
			
			String k;
			ArrayList<String> arrayList = new ArrayList<String>();
			
			
			
			while (result.next()) {
				
				k = result.getString("kategori");
				
				if (!arrayList.contains(k)) 
				{	
					arrayList.add(k);
					System.out.println();
					System.out.println(k+ ":");
				}
					
				System.out.println(result.getString("märke") +"\t" + result.getString("färg") +"\t" + result.getString("storlek"));
				

				
			}
		} 
		
		catch (Exception e) 
		{
			e.printStackTrace();
		}

	}

}
