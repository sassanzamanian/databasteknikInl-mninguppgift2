import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.concurrent.Callable;

import javax.swing.JOptionPane;

public class Fråga3 {

	public static void main(String[] args) {
		
		
		Connection connection = null;
		
		try 
		{
			connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/webbshop?autoReconnect=true&useSSL=false", "root", "vattenfall9");
			
			Statement statement = connection.createStatement();
			
			ResultSet result = statement.executeQuery("select name, address,ort from kund order by name");
			
			//boolean för att skriva ut huvudrubriken : namn, address, ort en gång, som senare ändras till false i slutet av while-loopen dvs 1 utskrivning
			boolean y = true;
			
			//itererare för att tilldela ett nr till varje kund för användaren
			int z = 1;
			
			
			//spara undan varje utskriven rad för att sedan jämföra inputens val av kundnr mot listen för att hitta rätt kund
			ArrayList<String> allaKunderSomString = new ArrayList<String>();
			
			while (result.next())
			{
				if (y) 
				{
					System.out.format("%20s %20s %20s ","namn", "address", "ort");
					System.out.println();
					System.out.println("--------------------------------------------------------------");
				}
				
				String currentKund = result.getString("name") +"-" + result.getString("address") +"-"+ result.getString("ort");
				allaKunderSomString.add(currentKund);
				
				System.out.format("%20s %20s %20s ",z+ " "+ result.getString("name"), result.getString("address"),result.getString("ort"));
				System.out.println();
			
				y = false;
				z++;
			}
			
			System.out.println("\n\n");
			String inputKundNamn = JOptionPane.showInputDialog("skriv in kund (nr)");
			
			

			
			int KundNr = (Integer.parseInt(inputKundNamn)) -1;
			
			String kunden = allaKunderSomString.get(KundNr);
			String [] kundInfo = kunden.split("-");
			String namnet = kundInfo[0];
			String address = kundInfo[1];
			String ort = kundInfo[2];
			
		
			//hitta kundID genom inparametrar namn, address, ort och spottar ut kundID som int
			CallableStatement callable = connection.prepareCall(" {call findCustomerID( ?, ?, ? , ?)}");
			
			callable.setString(1, namnet);
			callable.setString(2, address);
			callable.setString(3, ort);
			callable.registerOutParameter(4, Types.INTEGER);
			
			callable.execute();
			
			int kundDataBasID = callable.getInt(4);
		
			
			
			
			//skriva ut alla tillgängliga produkter
			result = statement.executeQuery("select * from sko_information");
			System.out.println();
			
			//itererare och string-list som föregående kodblock av samma anledning
			int r = 1;
			ArrayList<String> allaSkorSomString = new ArrayList<String>();
			
			
			System.out.format("%2s %10s %10s %10s ","vara","märke", "färg", "storlek");
			System.out.println();
			System.out.println("------------------------------------");
			
			while(result.next())
			{
				 
				String currentSko = result.getString("märke") +" " + result.getString("färg") +" "+ result.getString("storlek");
				allaSkorSomString.add(currentSko);
				System.out.format("%2s %10s %10s %10s ", r,  result.getString("märke"), result.getString("färg"),result.getString("storlek"));
				System.out.println();
			
			
				r++;
				
			}
			
			
			//jämföra input-int mot index i list för att peka ut rätt produkt
			String inputVaransNr = JOptionPane.showInputDialog("skriv in önskad vara (varans nr)");
			
			int varansNr = (Integer.parseInt(inputVaransNr)) -1;
			
			String skon = allaSkorSomString.get(varansNr);
			String [] skoInfo = skon.split(" ");
			String märke = skoInfo[0];
			String färg = skoInfo[1];
			int storlek = Integer.parseInt(skoInfo[2]);
			
			
			//hitta skoID genom angivet märke, färg, storlek & spottar ut skoID till skoDataBasID-variabeln
			CallableStatement call = connection.prepareCall(" {call findShoeID( ?, ?, ? , ?)}");
			
			call.setString(1, märke);
			call.setInt(2, storlek);
			call.setString(3, färg);
			call.registerOutParameter(4, Types.INTEGER);
			
			call.execute();
			
			int skoDataBasID = call.getInt(4);
			
			
			
			
			
			//skriva ut alla kundens icke-expedierade beställningar utifrån kundID ( som vi fick ut tidigare)
			PreparedStatement prep = connection.prepareStatement("select * from ordrar where kundID = ? and expedierad = false");
			
			prep.setInt(1, kundDataBasID);
			
			result = prep.executeQuery();
			
			System.out.println("\n\n");
			System.out.println("Icke-expedierade beställningar");
			System.out.println("-------------------------------");
			
			int p = 1;
			ArrayList<String> allaOrdrarSomString = new ArrayList<String>();
			
			while(result.next())
			{
				String order = result.getString("datum");
				allaOrdrarSomString.add(order);
				System.out.println(p+". " + order);
				p++;
			}
			
			int orderDataBasID = 0;
			
			
			
			String nyBeställningID = JOptionPane.showInputDialog("Om du vill lägga till varan på befintlig order, ange beställningsID, annars lämna fältet blankt för ny order");
			
			//om nyBeställningID input är tom blir orderDataBasID = 0 vilket skapar ny order i addToCart
			if (nyBeställningID.isEmpty()) 
			{
				orderDataBasID = 0;
			} 
			else 
			{
				int orderNr = (Integer.parseInt(nyBeställningID)) -1;
				String datumet = allaOrdrarSomString.get(orderNr);
				
				//annars hitta rätt order för att lägga till vald produkt
				CallableStatement callOrder = connection.prepareCall(" {call findOrderID( ?, ?, ?)}");
				
				callOrder.setInt(1,kundDataBasID);
				callOrder.setString(2, datumet);
				callOrder.registerOutParameter(3, Types.INTEGER);
				
				callOrder.executeQuery();
				
				orderDataBasID = callOrder.getInt(3);
				

			}
			
			try 
			{
				//lägga till produkt till befintlig eller ny order till angiven kund
				CallableStatement callAddToCart = connection.prepareCall(" {call addToCart( ?, ?, ?)}");
				
				callAddToCart.setInt(1, kundDataBasID);
				callAddToCart.setInt(2, orderDataBasID);
				callAddToCart.setInt(3, skoDataBasID);

				callAddToCart.execute();
				
				if(orderDataBasID == 0)
				{
					System.out.println();
					System.out.println("Produkten har nu lagts till till en ny order");
				}
				else
				{
					System.out.println();
					System.out.println("Produkten har nu lagts till till din befintliga order");
				}
				
				
			} 
			catch (Exception e) 
			{
				e.printStackTrace();
				System.out.println("Något gick snett, produkten har ej blivit registrerad till din order");
			}
			
			
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}
		
		
		
	

	}

}
