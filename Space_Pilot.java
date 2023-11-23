
import javax.swing.Timer;
import java.util.ArrayList;
import java.util.Random;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.net.URL;
import java.io.IOException;
import javax.imageio.*;
import java.awt.image.BufferedImage;
import java.text.DecimalFormat;
import javax.swing.JDialog;
import java.awt.Point;

class Data { // the class to store all the images component for the game
	public static ImageIcon[] spaceship;
	public static BufferedImage[] rotatedSpaceship;
	public static ImageIcon life;
	public static ImageIcon star;
	public static ImageIcon LandingSign;

	public static BufferedImage rotateImage(BufferedImage originalImage, int width, int height, int degrees) {
		Image resizedImage = originalImage.getScaledInstance(width, height, Image.SCALE_SMOOTH);
		BufferedImage rotatedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

		Graphics2D g2d = rotatedImage.createGraphics();

		// Calculate the center of the image
		g2d.rotate(Math.toRadians(degrees), width / 2, height / 2);
		g2d.drawImage(resizedImage, 0, 0, null);
		g2d.dispose();

		return rotatedImage;
	}

	static {
		spaceship = new ImageIcon[37];
		rotatedSpaceship = new BufferedImage[37];
		try {
			URL spaceshipURL = Space_Pilot.class.getResource("/resource_pack/spaceship.png");
			BufferedImage oriSpaceship = ImageIO.read(spaceshipURL);
			for (int i = 0; i <= 36; i++) {
				rotatedSpaceship[i] = rotateImage(oriSpaceship, 60, 40, -(10 * i));
				spaceship[i] = new ImageIcon(rotatedSpaceship[i]);
			}

			Image resizedLife = oriSpaceship.getScaledInstance(45, 30, Image.SCALE_SMOOTH);
			life = new ImageIcon(resizedLife);

			URL starURL = Space_Pilot.class.getResource("/resource_pack/star.png");
			BufferedImage oristar = ImageIO.read(starURL);
			Image resizedstar = oristar.getScaledInstance(30, 40, Image.SCALE_SMOOTH);
			star = new ImageIcon(resizedstar);

			URL landingSignURL = Space_Pilot.class.getResource("/resource_pack/LandingSign.png");
			BufferedImage oriLandingSign = ImageIO.read(landingSignURL);
			Image resizedLandingSign = oriLandingSign.getScaledInstance(200, 200, Image.SCALE_SMOOTH);
			LandingSign = new ImageIcon(resizedLandingSign);

		} catch (IOException e) {
		}
	}

}

class GamePanel extends JPanel implements KeyListener { // create the panel designed for the game
	public static int ThrustSpeed = 0;
	public static double xComponent = 0.0;
	public static double yComponent = 400.0;
	public static int invincibleTime = 0;
	public static int angle = 40;
	public static int direction = 0;
	public static int currentLevelScore = 0;
	public static Color[][] colorMap;

	public static Point[] positionOfStar; // getting star mode, star spinning
	public static Point[] inipositionOfStar;
	public static Point[] retrieveInipositionOfStar;
	public static int cirdelx = 0;
	public static int cirdely = 0;
	public static double theta = 0.0;
	public static double delt = 0.2;

	public static Timer timer;

	public GamePanel() {
		this.setPreferredSize(new Dimension(1400, 800));
		this.setLayout(null);
		setFocusable(true);
		this.requestFocusInWindow();
		addKeyListener(this);

		positionOfStar = new Point[10];
		inipositionOfStar = new Point[10];
		retrieveInipositionOfStar = new Point[10];

		for (int i = 0; i < 10; i++) {
			positionOfStar[i] = new Point(-1, -1);
			inipositionOfStar[i] = new Point(-1, -1);
			retrieveInipositionOfStar[i] = new Point(-1, -1);
		}

		colorMap = new Color[800][1400];
		for (int i = 0; i < 80; i++)
			for (int j = 0; j < 140; j++)
				colorMap[i][j] = Color.BLACK;

		timer = new Timer(50, new TimerCallback());
		timer.start();
	}

	class TimerCallback implements ActionListener {
		// because in the space, there is not air friction to consider
		public void actionPerformed(ActionEvent e) {
			if (invincibleTime > 0)
				invincibleTime--;
			yComponent += 0.5 * 150 * Math.pow(0.1, 2) - (ThrustSpeed * Math.sin(Math.toRadians(angle)) * 0.05);
			// gravity+yComponent s=(1/2)*g*t^2
			xComponent += ThrustSpeed * Math.cos(Math.toRadians(angle)) * 0.05;

			for (int i = 0; i < 10; i++) {

				if (inipositionOfStar[i].x != -1 && inipositionOfStar[i].y != -1) {

					cirdelx = (int) (Math.sqrt(400) * (Math.cos(theta + delt) - Math.cos(theta)));
					cirdely = (int) (Math.sqrt(400) * (Math.sin(theta + delt) - Math.sin(theta)));
					positionOfStar[i].x += cirdelx;
					positionOfStar[i].y += cirdely;
				}
			}

			theta += delt;
			if (theta > 2 * Math.PI) {
				theta = 0.0;
				for (int i = 0; i < 10; i++) {
					positionOfStar[i].x = inipositionOfStar[i].x;
					positionOfStar[i].y = inipositionOfStar[i].y;
				}
			}

			repaint();

		}

	}

	public static void decorateColorMap(Color[][] map, int level) { // to decorate the map
		int NumberOfObstacles = level;
		int NumberOfStars = (int) level / 3;
		if (NumberOfStars > 10)
			NumberOfStars = 10; // limit the maximum number of stars to 10

		for (int i = 0; i < 80; i++)
			for (int j = 0; j < 140; j++)
				map[i][j] = Color.BLACK;

		if (MenuPanel.gameLevel == 2) {
			for (int i = 59; i <= 79; i++)
				for (int j = 9; j <= 39; j++)
					map[i][j] = Color.ORANGE;
			for (int i = 49; i <= 79; i++)
				for (int j = 39; j <= 79; j++)
					map[i][j] = Color.ORANGE;
			for (int i = 54; i <= 79; i++)
				for (int j = 79; j <= 119; j++)
					map[i][j] = Color.ORANGE;
			for (int i = 0; i <= 28; i++)
				for (int j = 19; j <= 123; j++)
					map[i][j] = Color.ORANGE;
		}

		if (MenuPanel.gameLevel == 3) {
			for (int i = 59; i <= 79; i++)
				for (int j = 9; j <= 29; j++)
					map[i][j] = new Color(139, 128, 0);
			for (int i = 0; i <= 35; i++)
				for (int j = 0; j <= 19; j++)
					map[i][j] = new Color(255, 127, 80);
			for (int i = 29; i <= 79; i++)
				for (int j = 30; j <= 70; j++)
					map[i][j] = new Color(255, 99, 71);
			for (int i = 0; i <= 18; i++)
				for (int j = 20; j <= 90; j++)
					map[i][j] = new Color(255, 215, 0);
			for (int i = 49; i <= 79; i++)
				for (int j = 71; j <= 110; j++)
					map[i][j] = new Color(253, 218, 13);
			for (int i = 0; i <= 39; i++)
				for (int j = 91; j <= 140; j++)
					map[i][j] = new Color(250, 213, 165);
		}

		if (MenuPanel.gameLevel > 3) {
			Random random = new Random();

			int[] RecordiniCeilingRock = new int[140];
			int[] RecordiniGroundRock = new int[140];

			int iniCeilingRock = random.nextInt(35);
			int iniGroundRock = random.nextInt(31) + 49;

			RecordiniCeilingRock[0] = iniCeilingRock;
			RecordiniGroundRock[0] = iniGroundRock;

			for (int j = 0; j <= iniCeilingRock; j++) { // filling the area above the iniCeilingRock
				if (j <= (int) (iniCeilingRock) * 0.3)
					map[j][0] = new Color(255, 99, 71); // tomato color
				if (j <= (int) (iniCeilingRock) * 0.65 && j > (int) (iniCeilingRock) * 0.3)
					map[j][0] = new Color(255, 127, 80); // coral color
				if (j <= (int) (iniCeilingRock) * 0.85 && j > (int) (iniCeilingRock) * 0.65)
					map[j][0] = new Color(255, 165, 0); // orange color
				if (j <= (int) (iniCeilingRock) && j > (int) (iniCeilingRock) * 0.85)
					map[j][0] = new Color(255, 215, 0); // gold color
			}
			for (int j = iniGroundRock; j < 80; j++) { //// filling the area above the iniGroundRock
				if (j >= (int) (79 - (79 - iniGroundRock) * 0.25))
					map[j][0] = new Color(139, 128, 0); // 暗黄色
				if (j >= (int) (79 - (79 - iniGroundRock) * 0.55) && j < (int) (79 - (79 - iniGroundRock) * 0.25))
					map[j][0] = new Color(228, 155, 15); // 藤黄色
				if (j >= (int) (79 - (79 - iniGroundRock) * 0.8) && j < (int) (79 - (79 - iniGroundRock) * 0.55))
					map[j][0] = new Color(253, 218, 13); // 镉黄色
				if (j >= iniGroundRock && j < (int) (79 - (79 - iniGroundRock) * 0.8))
					map[j][0] = new Color(250, 213, 165); // 沙漠黄
			}

			for (int i = 1; i < 120; i++) {

				int changeCeilingValue = random.nextInt(11) - 5;
				int changeGroundValue = random.nextInt(11) - 5;

				if (changeCeilingValue >= 0) {
					while (iniCeilingRock + changeCeilingValue > iniGroundRock - 10
							|| iniCeilingRock + changeCeilingValue > 60) {
						changeCeilingValue = random.nextInt(6);
					}
					iniCeilingRock += changeCeilingValue;

					while (iniGroundRock + changeGroundValue - 10 < iniCeilingRock
							|| iniGroundRock + changeGroundValue < 20 || iniGroundRock + changeGroundValue > 79) {
						changeGroundValue = random.nextInt(11) - 5;
					}
					iniGroundRock += changeGroundValue;
				}

				if (changeCeilingValue < 0) {
					while (iniGroundRock + changeGroundValue - 10 < iniCeilingRock
							|| iniGroundRock + changeGroundValue < 20 || iniGroundRock + changeGroundValue > 79) {
						changeGroundValue = random.nextInt(11) - 5;
					}
					iniGroundRock += changeGroundValue;

					while (iniCeilingRock + changeCeilingValue < 0) {
						changeCeilingValue = random.nextInt(6) - 5;
					}
					iniCeilingRock += changeCeilingValue;
				}

				RecordiniCeilingRock[i] = iniCeilingRock;
				RecordiniGroundRock[i] = iniGroundRock;

				for (int j = 0; j <= iniCeilingRock; j++) {
					if (j <= (int) (iniCeilingRock) * 0.3)
						map[j][i] = new Color(255, 99, 71); // tomato color
					if (j <= (int) (iniCeilingRock) * 0.65 && j > (int) (iniCeilingRock) * 0.3)
						map[j][i] = new Color(255, 127, 80); // coral color
					if (j <= (int) (iniCeilingRock) * 0.85 && j > (int) (iniCeilingRock) * 0.65)
						map[j][i] = new Color(255, 165, 0); // orange color
					if (j <= (int) (iniCeilingRock) && j > (int) (iniCeilingRock) * 0.85)
						map[j][i] = new Color(255, 215, 0); // gold color
				}
				for (int j = iniGroundRock; j < 80; j++) {
					if (j >= (int) (79 - (79 - iniGroundRock) * 0.25))
						map[j][i] = new Color(139, 128, 0); // 暗黄色
					if (j >= (int) (79 - (79 - iniGroundRock) * 0.55) && j < (int) (79 - (79 - iniGroundRock) * 0.25))
						map[j][i] = new Color(228, 155, 15); // 藤黄色
					if (j >= (int) (79 - (79 - iniGroundRock) * 0.8) && j < (int) (79 - (79 - iniGroundRock) * 0.55))
						map[j][i] = new Color(253, 218, 13); // 镉黄色
					if (j >= iniGroundRock && j < (int) (79 - (79 - iniGroundRock) * 0.8))
						map[j][i] = new Color(250, 213, 165); // 沙漠黄
				}
			}

			for (int i = 120; i < 140; i++) {
				int changeCeilingValue = random.nextInt(11) - 5;
				while (iniCeilingRock + changeCeilingValue < 0 || iniCeilingRock + changeCeilingValue > 60) {
					changeCeilingValue = random.nextInt(11) - 5;
				}
				iniCeilingRock += changeCeilingValue;

				RecordiniCeilingRock[i] = iniCeilingRock;
				RecordiniGroundRock[i] = 80;

				for (int j = 0; j <= iniCeilingRock; j++) {
					if (j <= (int) (iniCeilingRock) * 0.3)
						map[j][i] = new Color(255, 99, 71); // tomato color
					if (j <= (int) (iniCeilingRock) * 0.65 && j > (int) (iniCeilingRock) * 0.3)
						map[j][i] = new Color(255, 127, 80); // coral color
					if (j <= (int) (iniCeilingRock) * 0.85 && j > (int) (iniCeilingRock) * 0.65)
						map[j][i] = new Color(255, 165, 0); // orange color
					if (j <= (int) (iniCeilingRock) && j > (int) (iniCeilingRock) * 0.85)
						map[j][i] = new Color(255, 215, 0); // gold color
				}
			}

			if (MenuPanel.gameLevel > 5) {
				while (NumberOfObstacles > 0) // generate stationary rock
				{
					int ObstacleX = random.nextInt(127) + 10;
					int ObstacleY = random.nextInt(75) + 2;
					while (ObstacleY - 2 < RecordiniCeilingRock[ObstacleX] + 10
							|| ObstacleY + 2 > RecordiniGroundRock[ObstacleX] - 10
							|| ObstacleY - 1 < RecordiniCeilingRock[ObstacleX - 1] + 10
							|| ObstacleY + 1 > RecordiniGroundRock[ObstacleX - 1] - 10
							|| ObstacleY - 1 < RecordiniCeilingRock[ObstacleX + 1] + 10
							|| ObstacleY + 1 > RecordiniGroundRock[ObstacleX + 1] - 10
							|| ObstacleY < RecordiniCeilingRock[ObstacleX - 2] + 10
							|| ObstacleY > RecordiniGroundRock[ObstacleX + 2] - 10) {
						ObstacleX = random.nextInt(127) + 10;
						ObstacleY = random.nextInt(75) + 2;
					}

					for (int j = ObstacleX - 2; j <= ObstacleX + 2; j++) // create the obstacle image in the map
						map[ObstacleY][j] = Color.BLUE;
					for (int j = ObstacleX - 1; j <= ObstacleX + 1; j++) {
						map[ObstacleY - 1][j] = Color.BLUE;
						map[ObstacleY + 1][j] = Color.BLUE;
					}
					map[ObstacleY - 2][ObstacleX] = Color.BLUE;
					map[ObstacleY + 2][ObstacleX] = Color.BLUE;
					NumberOfObstacles--;
				}
			}

			int starCount = 0;
			while (NumberOfStars > 0) // generate non-stationary stars
			{
				int positionX = random.nextInt(127) + 10;
				int positionY = random.nextInt(51) + 8;
				while (map[positionY - 1][positionX - 1] != Color.BLACK || map[positionY - 1][positionX] != Color.BLACK
						|| map[positionY - 1][positionX + 1] != Color.BLACK
						|| map[positionY + 2][positionX - 1] != Color.BLACK
						|| map[positionY + 2][positionX] != Color.BLACK
						|| map[positionY + 2][positionX + 1] != Color.BLACK) {
					positionX = random.nextInt(127) + 10;
					positionY = random.nextInt(51) + 8;
				}
				positionOfStar[starCount] = new Point(positionX * 10 + 5, positionY * 10 + 10);
				inipositionOfStar[starCount] = new Point(positionX * 10 + 5, positionY * 10 + 10);
				retrieveInipositionOfStar[starCount] = new Point(positionX * 10 + 5, positionY * 10 + 10);
				starCount++;
				NumberOfStars--;
			}

		}
	}

	public void paintComponent(Graphics g) {
		super.paintComponent(g);

		for (int i = 0; i < 80; i++)
			for (int j = 0; j < 140; j++) {
				g.setColor(colorMap[i][j]);
				g.fillRect(j * 10, i * 10, 10, 10);
			}

		for (int i = 0; i < 10; i++) {
			if (positionOfStar[i].getX() != -1 && positionOfStar[i].getY() != -1)
				Data.star.paintIcon(this, g, (int) positionOfStar[i].getX() - 15, (int) positionOfStar[i].getY() - 20);
		}

		Data.LandingSign.paintIcon(this, g, 1200, 600);
		Data.spaceship[direction].paintIcon(this, g, (int) xComponent, (int) yComponent);

		if (xComponent + 30 >= 1200 && xComponent + 30 <= 1400 && yComponent + 20 >= 600 && yComponent + 20 <= 800) {
			// arrive the landingArea
			timer.stop();
			new GameDialog(1, 0);
		}

		if (xComponent + 30 < 0 || xComponent + 30 > 1400 || yComponent + 20 > 800 || yComponent + 20 < 0) {
			// exceed boundary
			timer.stop();
			new GameDialog(2, 1);
		}

		if (colorMap[(int) (yComponent + 20) / 10][(int) (xComponent + 30) / 10] == Color.BLUE) // crash at the obstacle
		{
			if (invincibleTime <= 0) {
				timer.stop();
				new GameDialog(2, 2);
			}

		}

		if (MenuPanel.FuelAmount == 0) { // fuel used up
			timer.stop();
			new GameDialog(3, 0);
		}

		if (xComponent + 30 >= 0 && xComponent + 30 <= 1400 && yComponent + 20 <= 800 && yComponent + 20 >= 0) {
			// crash at the rock
			if (invincibleTime <= 0) {
				int locationx = (int) (xComponent + 30) / 10;
				int locationy = (int) (yComponent + 20) / 10;
				if (colorMap[locationy][locationx] != Color.BLACK && colorMap[locationy][locationx] != Color.BLUE) {
					timer.stop();
					new GameDialog(2, 2);
				}
			}
		}

		// get the bonus point for stars
		for (int i = 0; i < 10; i++) {
			if (positionOfStar[i].getX() != -1 && positionOfStar[i].getY() != -1) {
				if (xComponent + 30 >= positionOfStar[i].getX() - 15 && xComponent + 30 <= positionOfStar[i].getX() + 15
						&& yComponent + 20 >= positionOfStar[i].getY() - 20
						&& yComponent + 20 <= positionOfStar[i].getY() + 20) {
					positionOfStar[i] = new Point(-1, -1);
					inipositionOfStar[i] = new Point(-1, -1);
					MenuPanel.Score += (30 + i * 10);
					currentLevelScore += (30 + i * 10);
					MenuPanel.updateTotalScore();
				}
			}
		}

	}

	@Override
	public void keyPressed(KeyEvent e) {
		// This method is called when a key is pressed
		if (e.getKeyCode() == KeyEvent.VK_W) {
			if (MenuPanel.FuelAmount >= 50) {
				MenuPanel.FuelAmount -= 50;
				ThrustSpeed += 20;
				MenuPanel.updateFuelAmount();
			}
		}

		if (e.getKeyCode() == KeyEvent.VK_S) {
			if (MenuPanel.FuelAmount >= 50) {
				if (ThrustSpeed != 0) {
					MenuPanel.FuelAmount -= 50;
					ThrustSpeed -= 20;
					MenuPanel.updateFuelAmount();
				}
			}
		}

		if (e.getKeyCode() == KeyEvent.VK_A) {
			if (MenuPanel.FuelAmount >= 10) {
				MenuPanel.FuelAmount -= 10;
				MenuPanel.updateFuelAmount();
				if (direction == 36 || direction == 0) {
					angle = 40;
					direction = 0;
					angle += 10;
					direction++;
				} else {
					direction++;
					angle += 10;
				}
			}
		}

		if (e.getKeyCode() == KeyEvent.VK_D) {
			if (MenuPanel.FuelAmount >= 10) {

				MenuPanel.FuelAmount -= 10;
				MenuPanel.updateFuelAmount();
				if (direction == 0 || direction == 36) {
					angle = 40;
					direction = 36;
					angle -= 10;
					direction--;
				} else {
					direction--;
					angle -= 10;
				}
			}
		}
	}

	public void keyReleased(KeyEvent e) {
	}

	public void keyTyped(KeyEvent e) {
	}
}

class GameDialog extends JDialog implements ActionListener { // dialog window for win or loss

	public JLabel losemessage;
	public JRadioButton losebutton1;
	/*
	 * GameDialog(status, condition) status = 1, condition =0 successfully land at
	 * the landing area status = 2, condition = 1 fly Beyond the game Border
	 * (GamePanel) status = 2, condition = 2 crash at the obstacle status = 3,
	 * condition =0 fuel depleted
	 */

	public GameDialog(int status, int condition) {
		this.setBounds(600, 250, 400, 300);
		this.setBackground(Color.lightGray);
		this.setLayout(new GridLayout(3, 1));
		switch (status) {
		case 1:
			JLabel message = new JLabel("You win!", JLabel.CENTER);
			message.setFont(new Font(message.getFont().getName(), Font.BOLD, 20));
			this.add(message);

			JPanel winPanel = new JPanel();
			JLabel scoreLabel = new JLabel("Score + " + MenuPanel.gameLevel * 10, JLabel.CENTER);
			scoreLabel.setFont(new Font(scoreLabel.getFont().getName(), Font.ITALIC, 18));
			this.add(scoreLabel);
			JRadioButton button1 = new JRadioButton("Next Level");
			JRadioButton button2 = new JRadioButton("Exit to Desktop");
			button1.addActionListener(this);
			button2.addActionListener(this);
			ButtonGroup group = new ButtonGroup();
			group.add(button1);
			group.add(button2);
			winPanel.add(button1);
			winPanel.add(button2);
			this.add(winPanel);
			break;

		case 2:
			if (MenuPanel.Lifecount > 0) {
				if (condition == 1) {
					losemessage = new JLabel("Exceed the Boundary!", JLabel.CENTER);
				}
				if (condition == 2) {
					losemessage = new JLabel("You Crashed!", JLabel.CENTER);
				}
				losemessage.setFont(new Font(losemessage.getFont().getName(), Font.BOLD, 20));
				this.add(losemessage);

				JPanel losePanel = new JPanel();
				if (condition == 1)
					losebutton1 = new JRadioButton("Relocate the Spaceship");
				if (condition == 2)
					losebutton1 = new JRadioButton("Rebuild the Spaceship");
				JRadioButton losebutton2 = new JRadioButton("Exit to Desktop");
				losebutton1.addActionListener(this);
				losebutton2.addActionListener(this);
				ButtonGroup losegroup = new ButtonGroup();
				losegroup.add(losebutton1);
				losegroup.add(losebutton2);
				losePanel.add(losebutton1);
				losePanel.add(losebutton2);
				this.add(losePanel);
			} else {
				JLabel gameLosemessage = new JLabel("You Lose! Want to Restart?", JLabel.CENTER);
				gameLosemessage.setFont(new Font(gameLosemessage.getFont().getName(), Font.BOLD, 20));
				this.add(gameLosemessage);

				JLabel gameLosemessage2 = new JLabel("Scores Got: " + MenuPanel.Score, JLabel.CENTER);
				gameLosemessage2.setFont(new Font(gameLosemessage2.getFont().getName(), Font.BOLD, 20));
				this.add(gameLosemessage2);

				JPanel gameLosePanel = new JPanel();
				JRadioButton gameLosebutton1 = new JRadioButton("YES");
				JRadioButton gameLosebutton2 = new JRadioButton("NO");
				gameLosebutton1.addActionListener(this);
				gameLosebutton2.addActionListener(this);
				ButtonGroup gameLosegroup = new ButtonGroup();
				gameLosegroup.add(gameLosebutton1);
				gameLosegroup.add(gameLosebutton2);
				gameLosePanel.add(gameLosebutton1);
				gameLosePanel.add(gameLosebutton2);
				this.add(gameLosePanel);
			}
			break;

		case 3:
			JLabel fuelEmptyMessage = new JLabel("Fuel Depleted!", JLabel.CENTER);
			fuelEmptyMessage.setFont(new Font(fuelEmptyMessage.getFont().getName(), Font.BOLD, 20));
			this.add(fuelEmptyMessage);

			JPanel fuelEmptyPanel = new JPanel();
			JRadioButton fuelEmptybutton1 = new JRadioButton("Restart this level");
			JRadioButton fuelEmptybutton2 = new JRadioButton("Exit to Desktop");
			fuelEmptybutton1.addActionListener(this);
			fuelEmptybutton2.addActionListener(this);
			ButtonGroup fuelEmptygroup = new ButtonGroup();
			fuelEmptygroup.add(fuelEmptybutton1);
			fuelEmptygroup.add(fuelEmptybutton2);
			fuelEmptyPanel.add(fuelEmptybutton1);
			fuelEmptyPanel.add(fuelEmptybutton2);
			this.add(fuelEmptyPanel);
		}

		this.setVisible(true);

	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getActionCommand().equals("Exit to Desktop") || e.getActionCommand().equals("NO")) {
			System.exit(0);
		}
		if (e.getActionCommand().equals("Next Level")) {
			MenuPanel.Score += MenuPanel.gameLevel * 10;
			MenuPanel.gameLevel++;
			MenuPanel.Lifecount = 3;

			if (MenuPanel.gameLevel >= 5) {
				if (MenuPanel.fuelBar.getMaximum() > 1100) {
					MenuPanel.FuelAmount = 4000 - (MenuPanel.gameLevel - 5) * 100;
				} else
					MenuPanel.FuelAmount = 1000;
			} else if (MenuPanel.gameLevel <= 5)
				MenuPanel.FuelAmount = 10000;

			GamePanel.currentLevelScore = 0;
			GamePanel.xComponent = 0.0;
			GamePanel.yComponent = 400.0;
			GamePanel.ThrustSpeed = 0;
			GamePanel.direction = 0;
			GamePanel.angle = 40;
			GamePanel.theta = 0.0;
			GamePanel.cirdelx = 0;
			GamePanel.cirdely = 0;
			MenuPanel.updateGameLevel();
			MenuPanel.updateFuelAmount();
			MenuPanel.updateChangeLifecountStatus();
			MenuPanel.updateTotalScore();

			GamePanel.timer.start();
			this.dispose();
		}

		if (e.getActionCommand().equals("Relocate the Spaceship")) {
			MenuPanel.Lifecount--;
			GamePanel.ThrustSpeed = 0;
			GamePanel.xComponent = 0.0;
			GamePanel.yComponent = 400.0;
			GamePanel.direction = 0;
			GamePanel.angle = 40;
			MenuPanel.updateChangeLifecountStatus();
			GamePanel.timer.start();
			this.dispose();
		}

		if (e.getActionCommand().equals("Rebuild the Spaceship")) {
			GamePanel.invincibleTime = 60; // 3 seconds invincible time
			MenuPanel.Lifecount--;
			GamePanel.ThrustSpeed = 0;
			GamePanel.direction = 0;
			GamePanel.angle = 40;
			MenuPanel.updateChangeLifecountStatus();
			GamePanel.timer.start();
			this.dispose();
		}

		if (e.getActionCommand().equals("YES")) {
			MenuPanel.FuelAmount = 10000;
			MenuPanel.Lifecount = 3;
			MenuPanel.gameLevel = 1;
			MenuPanel.Score = 0;
			GamePanel.currentLevelScore = 0;
			GamePanel.ThrustSpeed = 0;
			GamePanel.xComponent = 0.0;
			GamePanel.yComponent = 400.0;
			GamePanel.direction = 0;
			GamePanel.angle = 40;
			GamePanel.theta = 0.0;
			GamePanel.cirdelx = 0;
			GamePanel.cirdely = 0;

			for (int i = 0; i < 10; i++) {
				GamePanel.positionOfStar[i] = new Point(-1, -1);
				GamePanel.inipositionOfStar[i] = new Point(-1, -1);
				GamePanel.retrieveInipositionOfStar[i] = new Point(-1, -1);
			}

			MenuPanel.updateTotalScore();
			MenuPanel.updateGameLevel();
			MenuPanel.updateFuelAmount();
			MenuPanel.updateChangeLifecountStatus();
			GamePanel.timer.start();
			this.dispose();
		}

		if (e.getActionCommand().equals("Restart this level")) {
			MenuPanel.Lifecount = 3;
			MenuPanel.FuelAmount = MenuPanel.fuelBar.getMaximum();

			MenuPanel.Score -= GamePanel.currentLevelScore;
			GamePanel.currentLevelScore = 0;

			GamePanel.xComponent = 0.0;
			GamePanel.yComponent = 400.0;
			GamePanel.ThrustSpeed = 0;
			GamePanel.direction = 0;
			GamePanel.angle = 40;
			GamePanel.theta = 0.0;
			GamePanel.cirdelx = 0;
			GamePanel.cirdely = 0;
			for (int i = 0; i < 10; i++) {
				GamePanel.inipositionOfStar[i] = GamePanel.retrieveInipositionOfStar[i];
				GamePanel.positionOfStar[i] = GamePanel.inipositionOfStar[i];
			}
			MenuPanel.updateFuelAmount();
			MenuPanel.updateTotalScore();
			MenuPanel.updateChangeLifecountStatus();
			GamePanel.timer.start();
			this.dispose();
		}
	}
}

class MenuPanel extends JPanel { // the status Bar for the game

	public static int Lifecount = 3;
	public static int LifecountPaint = Lifecount;
	public static boolean changeLifecountStatus = false;

	public static int FuelAmount = 10000;
	public static int gameLevel = 1;
	public static int Score = 0;
	public static MyJProgressBar fuelBar;
	public Timer menuTimer;
	public static JLabel LifeRemaining;
	public static JLabel TotalScore;
	public static JLabel Fuel;
	public static JLabel GameLevel;

	public MenuPanel() {

		this.setPreferredSize(new Dimension(1400, 100));
		this.setLayout(null);
		this.setBackground(Color.magenta);
		menuTimer = new Timer(100, new TimerCallback());
		menuTimer.start();

		LifeRemaining = new JLabel("Life Remaining:");
		LifeRemaining.setFont(new Font(LifeRemaining.getFont().getName(), Font.ITALIC, 17));
		this.add(LifeRemaining);
		LifeRemaining.setBounds(20, 10, 150, 60);

		Fuel = new JLabel("Fuel: " + FuelAmount);
		Fuel.setFont(new Font(Fuel.getFont().getName(), Font.ITALIC, 17));
		this.add(Fuel);
		Fuel.setBounds(400, 10, 500, 60);

		fuelBar = new MyJProgressBar(0, 10000);
		this.add(fuelBar);

		GameLevel = new JLabel("Level: " + gameLevel);
		GameLevel.setFont(new Font(GameLevel.getFont().getName(), Font.ITALIC, 17));
		this.add(GameLevel);
		GameLevel.setBounds(900, 10, 1000, 60);

		TotalScore = new JLabel("Total Score: " + Score);
		TotalScore.setFont(new Font(TotalScore.getFont().getName(), Font.BOLD, 17));
		this.add(TotalScore);
		TotalScore.setBounds(1100, 10, 300, 60);
	}

	public static void updateGameLevel() {
		GameLevel.setText("Level: " + gameLevel);
		GamePanel.decorateColorMap(GamePanel.colorMap, gameLevel);

		if (gameLevel >= 5) {
			fuelBar.setMaximum(FuelAmount);
		}
	}

	public static void updateFuelAmount() {
		Fuel.setText("Fuel: " + FuelAmount);
	}

	public static void updateTotalScore() {
		TotalScore.setText("Total Score: " + Score);
	}

	public static void updateChangeLifecountStatus() {
		changeLifecountStatus = true;
	}

	class TimerCallback implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			if (changeLifecountStatus == true) {
				repaint();
				changeLifecountStatus = false;
			}
			fuelBar.setValue(FuelAmount);
		}
	}

	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		LifecountPaint = Lifecount;
		int inix = 160; // initial position for the spaceship model on the life remaining menu
		int delx = 0;
		while (LifecountPaint > 0) {
			Data.life.paintIcon(this, g, inix + delx, 25);
			delx += 45;
			LifecountPaint--;
		}
	}

	class MyJProgressBar extends JProgressBar // inner class to personalize the fuelBar
	{
		private DecimalFormat decimalFormat = new DecimalFormat("0.00%");

		public MyJProgressBar(int minn, int maxx) {
			super(minn, maxx);
			this.setValue(maxx);
			this.setBounds(520, 27, 250, 30);
			this.setVisible(true);
			this.setStringPainted(true);
		}

		@Override
		public String getString() {
			// 计算当前进度的百分比并格式化为小数形式
			double percent = (double) this.getValue() / (this.getMaximum() - this.getMinimum());
			return decimalFormat.format(percent);
		}
	}
}

public class Space_Pilot {
	public static void main(String[] args) {
		JFrame frame = new JFrame();
		frame.setBounds(100, 100, 1400, 900);
		frame.setResizable(false);

		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		MenuPanel panel1 = new MenuPanel();
		GamePanel panel2 = new GamePanel();
		frame.add(panel1, BorderLayout.NORTH);
		frame.add(panel2, BorderLayout.SOUTH);
		frame.setVisible(true);
	}
}