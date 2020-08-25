package swing;


public class Game
{

	public static void main(String args[])
	{
		App.construct(654, 465, "Test");
		
		App.setColor(138, 255, 255, 255);
		App.fillRect(345, 345, 235, 213);
		App.frame();
	}
	
}
