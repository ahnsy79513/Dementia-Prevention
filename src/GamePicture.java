import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import javax.swing.*;

import org.json.simple.JSONObject;

import java.awt.event.*;
import java.util.Random;

// ���� �׸� ���߱� ��� ����
public class GamePicture extends JPanel implements ActionListener, Runnable {

	private ImageIcon[] imgIcons; // �̹��� �������� ����ϴ� 15���� �̸� �ҷ��ΰ� ��� �Ѵ�.

	private PictureData[][] pictures;
	private JButton[][] btnPicture;

	private int sizeWidth;
	private int sizeHeight;

	private JButton viewButton;
	private int viewButtonIndexX;
	private int viewButtonIndexY;

	private JButton dismatchButton;
	private int dismatchHideTimer;

	private JButton btnDiffi_Easy;
	private JButton btnDiffi_Normal;
	private JButton btnDiffi_Hard;
	private JButton btnPrevPage1;
	private JButton btnPrevPage2;
	private JPanel panelHead;
	private JPanel panelHead_Difficulty;
	private JPanel panelHead_Play;
	private JLabel labelGamePlay;
	private JLabel labelSelectedDifficulty;
	private CardLayout cardLayout; // ȭ�� ����� ���̵� ���� �Ŀ��� ���� ���� �ð� �� ���� �ߴ� ��ư�� �����־�� �ϹǷ� ��ȯ�� ���� ī�� ���̾ƿ��� ��� �Ѵ�.
	
	private JPanel panelBody;
	private JLabel label;

	private EDifficulty difficulty; // ������ ���̵�
	private boolean gameStop;
	private Random randomMaker;

	private int[] randomNumbers;
	private int previewTimer;
	private Thread thread;
	private boolean gameStart;
	private MainWindow mainWindow;
	private int remainTimeSec;
	
	public GamePicture(MainWindow main) {
		mainWindow = main;
		remainTimeSec = 0; // ���� �ð�
		
		gameStop = false;
		difficulty = EDifficulty.eNone; // ���̵� ���� ���� ���� ����
		randomMaker = new Random(System.currentTimeMillis()); // ���� ��ü ����, ���ڷ� �ѱ� ���� �ý��� �ð�����, �������̺��� �����ϴ� �� ���

		randomNumbers = new int[30];
		previewTimer = 0;

		// �̹����� �� 15������ ����Ͽ�, �ִ� 6x5���� �������� ����� ����.
		imgIcons = new ImageIcon[15];

		// ���ӿ� �ʿ��� ������ �̹������� �о���δ�.
		for (int i = 1; i <= 15; ++i) {
			// ���� �̸��� icon + ����.png �̸�, ���ڴ� 1~15 ���� �̴�.
			System.out.println(".\\bin\\Image1\\icon" + i + ".png");
			imgIcons[i - 1] = new ImageIcon(".\\bin\\Image1\\icon" + i + ".png");
		}

		viewButtonIndexX = 0;
		viewButtonIndexY = 0;
		viewButton = null;
		gameStart = false;
		
		dismatchButton = null;
		dismatchHideTimer = 0;

		// ���̵� ���� ��ư �� ���� ������ �����ִ� �г��� �ʱ�ȭ �Ѵ�.
		InitHeadPanels();
		
		panelBody = new JPanel();	
		panelBody.setBounds(0, 150, ConfigDatas.WIN_WIDTH, 650);
		add(panelBody);		
		
		gameStop = false;
		thread = new Thread(this);
		thread.start();
	}

	public void stopGame() {
		gameStop = true;
	}

	// ���̵� ���� ��ư�� ���� �Ѵ�.
	private void InitHeadPanels() {
		setSize(ConfigDatas.WIN_WIDTH, 200);
		setLayout(null); // ���̾ƿ� ���� ó���Ѵ�.
		
		panelHead = new JPanel();	
		panelHead.setBounds(0, 0, ConfigDatas.WIN_WIDTH, 150);
		add(panelHead);
		
		cardLayout = new CardLayout(0, 0);
		panelHead.setLayout(cardLayout); // ī�� ���̾ƿ����� ó��. ���̵� ����  ȭ���� ������� ���� ���� ȭ���� ���´�.
		
		
		panelHead_Difficulty = new JPanel();
		panelHead_Difficulty.setLayout(null);
		panelHead_Difficulty.setBounds(0, 0, ConfigDatas.WIN_WIDTH, 150);
		
		panelHead_Play = new JPanel();	
		panelHead_Play.setLayout(null);
		panelHead_Play.setBounds(0, 0, ConfigDatas.WIN_WIDTH, 150);
		
		
		panelHead.add("difficulty", panelHead_Difficulty);
		panelHead.add("gameplay", panelHead_Play);
		cardLayout.show(panelHead, "difficulty");
		
		
		label = new JLabel("���̵� ����");

		Font font = new Font("����", Font.PLAIN, 30);
		label.setFont(font);

		btnDiffi_Hard = new JButton("��");
		btnDiffi_Hard.setFont(font);

		btnDiffi_Normal = new JButton("��");
		btnDiffi_Normal.setFont(font);

		btnDiffi_Easy = new JButton("��");
		btnDiffi_Easy.setFont(font);

		btnPrevPage1 = new JButton("��������");

		// "���̵� ����" ��
		label.setBounds(250, 10, 300, 50);
		panelHead_Difficulty.add(label);

		// ��
		int iButtonX = 140;
		btnDiffi_Hard.setBounds(iButtonX, 60, 100, 50);
		panelHead_Difficulty.add(btnDiffi_Hard);

		// ��
		iButtonX += 150;
		btnDiffi_Normal.setBounds(iButtonX, 60, 100, 50);
		panelHead_Difficulty.add(btnDiffi_Normal);

		// ��
		iButtonX += 150;
		btnDiffi_Easy.setBounds(iButtonX, 60, 100, 50);
		panelHead_Difficulty.add(btnDiffi_Easy);

		btnPrevPage1.setBounds(500, 120, 100, 30);
		panelHead_Difficulty.add(btnPrevPage1);
				
		labelGamePlay = new JLabel("���� �ð� 00:00");
		labelGamePlay.setFont(font);
		labelGamePlay.setBounds(250, 10, 250, 50);
		panelHead_Play.add(labelGamePlay);
		
		labelSelectedDifficulty = new JLabel("���ó��̵�");
		labelSelectedDifficulty.setFont(font);
		labelSelectedDifficulty.setBounds(250, 60, 250, 50);
		panelHead_Play.add(labelSelectedDifficulty);
		
		btnPrevPage2 = new JButton("������");
		btnPrevPage2.setBounds(500, 15, 100, 40);
		panelHead_Play.add(btnPrevPage2);
		

		btnPrevPage1.addActionListener(this);
		btnPrevPage2.addActionListener(this);
		btnDiffi_Hard.addActionListener(this);
		btnDiffi_Normal.addActionListener(this);
		btnDiffi_Easy.addActionListener(this);

		panelHead_Difficulty.setBackground(Color.white);
		panelHead_Difficulty.setVisible(true);
		
		setVisible(true);
	}

	private void initPictureToButtons(EDifficulty diffi) {
		if (difficulty != EDifficulty.eNone) {
			System.out.println("�Է� ����, �̹� ���̵��� ������ ���� �Դϴ�.");
			return;
		}

		// ���� �÷��� ȭ���� �����ش�.
		cardLayout.show(panelHead, "gameplay");

		difficulty = diffi; // ���̵� ����

		switch (diffi) {
		case eEasy_2x2:
			sizeWidth = 2;
			sizeHeight = 2;
			remainTimeSec = 30; // ���� �ð�
			labelSelectedDifficulty.setText("���̵� : ��");
			break;
		case eNormal_4x4:
			sizeWidth = 4;
			sizeHeight = 4;
			remainTimeSec = 60; // ���� �ð�
			labelSelectedDifficulty.setText("���̵� : ��");
			break;
		case eHard_6x5:
			sizeWidth = 5;
			sizeHeight = 6;
			remainTimeSec = 120; // ���� �ð�
			labelSelectedDifficulty.setText("���̵� : ��");
			break;
		}

		btnPicture = new JButton[sizeWidth][sizeHeight];
		pictures = new PictureData[sizeWidth][sizeHeight];

		for (int i = 0; i < sizeWidth; i++) {
			for (int j = 0; j < sizeHeight; j++) {
				JButton button = new JButton();
				btnPicture[i][j] = button;
				pictures[i][j] = new PictureData();
			}
		}

		initPictureToButtons(); // ��ư �ʱ�ȭ
		shufflePictures(); // �׸��� ���� �����ش�.

		previewTimer = 2;
	}

	// ���ӿ��� ��� �� ��ư �ʱ�ȭ
	private void initPictureToButtons() {
		this.setVisible(false); // �г��� ��� �������� �ʰ� �Ѵ�.
		panelBody.setLayout(new GridLayout(sizeWidth, sizeHeight));

		for (int i = 0; i < sizeWidth; i++) {
			for (int j = 0; j < sizeHeight; j++) {
				panelBody.add(btnPicture[i][j]);
				btnPicture[i][j].addActionListener(this);
			}
		}

		this.add(panelBody);
		this.setSize(ConfigDatas.WIN_WIDTH, ConfigDatas.WIN_HEIGHT);
		this.setVisible(true); // ��� ó�� ���� �� �ٽ� �г��� �����ش�.
	}

	// �����ϰ� �������� ��ġ �Ѵ�.
	private void shufflePictures() {

		// ������ �ʱ�ȭ
		for (int i = 0; i < sizeWidth; i++) {
			for (int j = 0; j < sizeHeight; j++) {
				pictures[i][j] = new PictureData();
			}
		}

		// ��ȣ�� ���� ��.
		int totalCount = sizeWidth * sizeHeight;
		shuffleRandomNumbers(totalCount);

		// ���� ���� ��ȣ�� �°� ��ư�� �̹����� ���� �Ѵ�.
		for (int index = 0; index < totalCount; ++index) {

			int i = index % sizeWidth;
			int j = index / sizeWidth;

			System.out.println("i " + i + ", j " + j + ", imageNum " + randomNumbers[index] + ", idx " + index);

			int imageNum = randomNumbers[index];

			PictureData data = pictures[i][j];
			data.image = imgIcons[imageNum - 1];
			data.number = imageNum;

			btnPicture[i][j].setIcon(data.image);
		}
	}

	// �������� ���� ��.
	private void shuffleRandomNumbers(int count) {

		initShuffleNumbers();

		// 10�� ���� �迭�� ���׹��� ���� ��. ������ ���� ��ġ�� ���� �� ���� �ݺ��ϴ� ó�� ���� ������ ���� ���ɼ��� ����.
		int minxCount = 10;
		for (int shuffleCount = 0; shuffleCount < minxCount; ++shuffleCount) {
			for (int i = 0; i < count; ++i) {
				int rnd = (Math.abs(randomMaker.nextInt())) % count;

				int temp = randomNumbers[rnd];
				randomNumbers[rnd] = randomNumbers[i];
				randomNumbers[i] = temp;
			}
		}

		// �ܼ� ȭ�鿡 �����ϰ� ������ �� ��� �غ���.
		for (int i = 0; i < randomNumbers.length; ++i) {
			if (randomNumbers[i] != 0) {
				System.out.println("index " + i + ", Value " + randomNumbers[i]);
			}
		}
	}

	// ���ӿ��� ����� ��ȣ�� �ʱ�ȭ �Ѵ�. �⺻ ���� (�������� ���� ���̴�)
	private void initShuffleNumbers() {
		for (int i = 0; i < randomNumbers.length / 2; ++i) {
			int number = i + 1;
			randomNumbers[i * 2] = number;
			randomNumbers[i * 2 + 1] = number;
		}
	}

	// �̸� ���⸦ ������
	private void hidePreview() {
		for (int i = 0; i < sizeWidth; i++) {
			for (int j = 0; j < sizeHeight; j++) {
				if (pictures[i][j].show == false) {
					btnPicture[i][j].setIcon(null);
				}
			}
		}
		
		System.out.println("������ ����");
		gameStart = true;
	}

	public void actionPerformed(ActionEvent e) {

		JButton actionBtn = (JButton) e.getSource();

		// ���̵� ����
		if (actionBtn == btnDiffi_Easy) {
			initPictureToButtons(EDifficulty.eEasy_2x2); // ��
			
		} else if (actionBtn == btnDiffi_Normal) {
			initPictureToButtons(EDifficulty.eNormal_4x4); // ��
			
		} else if (actionBtn == btnDiffi_Hard) {
			initPictureToButtons(EDifficulty.eHard_6x5); // ��
			
		} else if (actionBtn == btnPrevPage1 || actionBtn == btnPrevPage2) {
			// ���� ȭ������ �̵�
			this.mainWindow.ShowHomeMenu();
		} else {

			if( gameStart == false )
			{
				System.out.println("�����䰡 ���� �� ������ ��ø� ��ٷ� �ּ���");
				return;
			}
			
			// �׸����߱� ó�� ���� ��ư�� �ִ�.
			if (dismatchButton != null)
			{
				System.out.println("��Ī���� ���� �̹����� ������ ������ ���");
				return;
			}

			// �׸� ���߱�
			for (int i = 0; i < sizeWidth; i++) {
				for (int j = 0; j < sizeHeight; j++) {
					if (actionBtn == btnPicture[i][j]) {

						PictureData currentPicture = pictures[i][j];

						// 2���� ¦ �� ó�� ������ �����ܿ� ���� ó��
						if (viewButton == null) {
							viewButton = actionBtn;
							viewButton.setIcon(currentPicture.image); // �������� �̹����� �����ش�
							viewButtonIndexX = i;
							viewButtonIndexY = j;
							return;
						}

						PictureData viewPicture = pictures[viewButtonIndexX][viewButtonIndexY];

						// 2���� ¦�� ���߾��� ���
						if (viewPicture.number == currentPicture.number && viewPicture != currentPicture) {
							currentPicture.show = true;
							viewPicture.show = true;
							actionBtn.setIcon(currentPicture.image); // 2��° ������ �������� �̹����� �����ش�
							viewButton = null;
							dismatchHideTimer = 0;
							dismatchButton = null;
							
							// ��� �Ϸ� �ߴ�.
							if( IsClear() == true ) {
								this.GameOver(); // ������ ����� �����ϰ� ���� ��Ų��.
							}
							return;
						}
						
						// 2��° �������� �������� ��, ¦�� ���� �ʴ� ��쿡 ���� ó��.
						// 2��° �������� ����������, run�� Ÿ�̸� ��ɿ� ���� 2�� �� �ٽ� �������� �������� ���� �� �д�.
						actionBtn.setIcon(currentPicture.image);
						dismatchButton = actionBtn;
						dismatchHideTimer = 2;
					}

				}
			}
		}
	}
	
	// ���� ���� �ð��� ���ڿ��� ��ȯ (ex: "���� �ð� 30:00" ) 
	private String GetRaminTimeText(int timeSec) {
		String result = "���� �ð� ";
		
		int min = remainTimeSec / 60;
		int sec = remainTimeSec % 60;
		
		if( min >= 10 ) {
			result += min;
		}
		else {
			result += ("0" + min);
		}
		
		result += ":";
		
		if( sec >= 10 ) {
			result += sec;
		}
		else {
			result += ("0" + sec);
		}
		
		return result;
	}

	public void run() {
		long timeMills = System.currentTimeMillis(); // ���� �ð�, �и������� ( 1 / 1000 �� )

		int fps = (int) (1.0f / 30.0f * 1000); // 1�ʿ� 30�� ���ϸ� ���� �ǵ��� Sleep Ÿ���� �ش�.

		try {
			while (gameStop == false) {
				long currentTime = System.currentTimeMillis();

				// 1�ʰ� ���� �� ���� ����
				if (currentTime - timeMills >= 1000) {
					timeMills = currentTime;
					
					// ���� �÷��� ���� �ð�
					// �����䰡 ������ ī��Ʈ �ٿ� ����
					if( previewTimer == 0 ) {
						if (remainTimeSec > 0) {
							--remainTimeSec;

							if (remainTimeSec == 0) {
								GameOver();
								gameStop = true;
								break;
							}
							
							this.labelGamePlay.setText(GetRaminTimeText(remainTimeSec));
						}					
					}
					else {
						this.labelGamePlay.setText(GetRaminTimeText(remainTimeSec));
					}
										
					// ���� ���� �� �̸����⸦ �ٽ� ������ ó��
					if (previewTimer > 0) {
						--previewTimer;

						// �����ִ� �͵��� ��� ������.
						if (previewTimer == 0) {
							hidePreview();
						}
					}
					
					// ������ ��� �������� �������� ������� �ǵ�����.
					if (dismatchHideTimer > 0) {
						--dismatchHideTimer;

						if (dismatchHideTimer == 0) {
							// �߸� ����ٸ� �����ִ� ���� ��ư�� �������� �ٽ� �����ش�.
							if (dismatchButton != null) {
								dismatchButton.setIcon(null);
								dismatchButton = null;
							}

							if (viewButton != null) {
								viewButton.setIcon(null);
								viewButton = null;
							}
						}
					}
				}
			}

			Thread.sleep(fps);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	// �׸��� ��� ���߾���?
	private boolean IsClear() {
		for (int i = 0; i < sizeWidth; i++) {
			for (int j = 0; j < sizeHeight; j++) {
				 if(pictures[i][j].show == false) {
					 return false;
				 }
			}
		}
		
		return true;
	}
	
	// ���� ����
	public void GameOver() {
		
		int score = this.remainTimeSec; // ���� �ð��� ������.
		MessageBoxUtil.ShowMessageBox("���� ����, ���� �ð�(����) " + score, "�˸�");
		
		// ���� ����
		JSONObject json = new JSONObject();
		json.put("gametype", "picture");
		json.put("score", score);
		
		mainWindow.sendMessage(EMessageCode.eUpdateGameScoreRequest, json); // ������ ����
		mainWindow.ShowHomeMenu(); // ���� �޴��� �̵�
	}
	
}

class PictureData {
	public ImageIcon image;
	public int number;
	public boolean show;

	public PictureData() {
		image = null;
		number = 0;
		show = false;
	}
}

enum EDifficulty {
	eNone, // ���� ���̵� ���� ��
	eEasy_2x2, // ����2 ����2
	eNormal_4x4, // ����4 ����4
	eHard_6x5, // ���� 6 ���ķ� 5
}