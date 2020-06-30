import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import javax.swing.*;

import org.json.simple.JSONObject;

import java.awt.event.*;
import java.util.Random;

// 같은 그림 맞추기 잡기 게임
public class GamePicture extends JPanel implements ActionListener, Runnable {

	private ImageIcon[] imgIcons; // 이미지 아이콘은 사용하는 15개를 미리 불러두고 사용 한다.

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
	private CardLayout cardLayout; // 화면 상단은 난이도 선택 후에는 게임 남은 시간 및 게임 중단 버튼을 보여주어야 하므로 전환을 위해 카드 레이아웃을 사용 한다.
	
	private JPanel panelBody;
	private JLabel label;

	private EDifficulty difficulty; // 선택한 난이도
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
		remainTimeSec = 0; // 남은 시간
		
		gameStop = false;
		difficulty = EDifficulty.eNone; // 난이도 선택 하지 않은 상태
		randomMaker = new Random(System.currentTimeMillis()); // 랜덤 객체 생성, 인자로 넘긴 값은 시스템 시간으로, 난수테이블을 셋팅하는 데 사용

		randomNumbers = new int[30];
		previewTimer = 0;

		// 이미지는 총 15종류를 사용하여, 최대 6x5개의 아이콘을 만들어 낸다.
		imgIcons = new ImageIcon[15];

		// 게임에 필요한 아이콘 이미지들을 읽어들인다.
		for (int i = 1; i <= 15; ++i) {
			// 파일 이름은 icon + 숫자.png 이며, 숫자는 1~15 까지 이다.
			System.out.println(".\\bin\\Image1\\icon" + i + ".png");
			imgIcons[i - 1] = new ImageIcon(".\\bin\\Image1\\icon" + i + ".png");
		}

		viewButtonIndexX = 0;
		viewButtonIndexY = 0;
		viewButton = null;
		gameStart = false;
		
		dismatchButton = null;
		dismatchHideTimer = 0;

		// 난이도 선택 버튼 및 게임 정보를 보여주는 패널을 초기화 한다.
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

	// 난이도 조절 버튼을 셋팅 한다.
	private void InitHeadPanels() {
		setSize(ConfigDatas.WIN_WIDTH, 200);
		setLayout(null); // 레이아웃 없이 처리한다.
		
		panelHead = new JPanel();	
		panelHead.setBounds(0, 0, ConfigDatas.WIN_WIDTH, 150);
		add(panelHead);
		
		cardLayout = new CardLayout(0, 0);
		panelHead.setLayout(cardLayout); // 카드 레이아웃으로 처리. 난이도 선택  화면이 사라지면 게임 정보 화면이 나온다.
		
		
		panelHead_Difficulty = new JPanel();
		panelHead_Difficulty.setLayout(null);
		panelHead_Difficulty.setBounds(0, 0, ConfigDatas.WIN_WIDTH, 150);
		
		panelHead_Play = new JPanel();	
		panelHead_Play.setLayout(null);
		panelHead_Play.setBounds(0, 0, ConfigDatas.WIN_WIDTH, 150);
		
		
		panelHead.add("difficulty", panelHead_Difficulty);
		panelHead.add("gameplay", panelHead_Play);
		cardLayout.show(panelHead, "difficulty");
		
		
		label = new JLabel("난이도 선택");

		Font font = new Font("돋움", Font.PLAIN, 30);
		label.setFont(font);

		btnDiffi_Hard = new JButton("상");
		btnDiffi_Hard.setFont(font);

		btnDiffi_Normal = new JButton("중");
		btnDiffi_Normal.setFont(font);

		btnDiffi_Easy = new JButton("하");
		btnDiffi_Easy.setFont(font);

		btnPrevPage1 = new JButton("이전으로");

		// "난이도 선택" 라벨
		label.setBounds(250, 10, 300, 50);
		panelHead_Difficulty.add(label);

		// 상
		int iButtonX = 140;
		btnDiffi_Hard.setBounds(iButtonX, 60, 100, 50);
		panelHead_Difficulty.add(btnDiffi_Hard);

		// 중
		iButtonX += 150;
		btnDiffi_Normal.setBounds(iButtonX, 60, 100, 50);
		panelHead_Difficulty.add(btnDiffi_Normal);

		// 하
		iButtonX += 150;
		btnDiffi_Easy.setBounds(iButtonX, 60, 100, 50);
		panelHead_Difficulty.add(btnDiffi_Easy);

		btnPrevPage1.setBounds(500, 120, 100, 30);
		panelHead_Difficulty.add(btnPrevPage1);
				
		labelGamePlay = new JLabel("남은 시간 00:00");
		labelGamePlay.setFont(font);
		labelGamePlay.setBounds(250, 10, 250, 50);
		panelHead_Play.add(labelGamePlay);
		
		labelSelectedDifficulty = new JLabel("선택난이도");
		labelSelectedDifficulty.setFont(font);
		labelSelectedDifficulty.setBounds(250, 60, 250, 50);
		panelHead_Play.add(labelSelectedDifficulty);
		
		btnPrevPage2 = new JButton("나가기");
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
			System.out.println("입력 오류, 이미 난이도를 선택한 상태 입니다.");
			return;
		}

		// 게임 플레이 화면을 보여준다.
		cardLayout.show(panelHead, "gameplay");

		difficulty = diffi; // 난이도 선택

		switch (diffi) {
		case eEasy_2x2:
			sizeWidth = 2;
			sizeHeight = 2;
			remainTimeSec = 30; // 남은 시간
			labelSelectedDifficulty.setText("난이도 : 하");
			break;
		case eNormal_4x4:
			sizeWidth = 4;
			sizeHeight = 4;
			remainTimeSec = 60; // 남은 시간
			labelSelectedDifficulty.setText("난이도 : 중");
			break;
		case eHard_6x5:
			sizeWidth = 5;
			sizeHeight = 6;
			remainTimeSec = 120; // 남은 시간
			labelSelectedDifficulty.setText("난이도 : 상");
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

		initPictureToButtons(); // 버튼 초기화
		shufflePictures(); // 그림을 섞고 보여준다.

		previewTimer = 2;
	}

	// 게임에서 사용 할 버튼 초기화
	private void initPictureToButtons() {
		this.setVisible(false); // 패널을 잠시 보여주지 않게 한다.
		panelBody.setLayout(new GridLayout(sizeWidth, sizeHeight));

		for (int i = 0; i < sizeWidth; i++) {
			for (int j = 0; j < sizeHeight; j++) {
				panelBody.add(btnPicture[i][j]);
				btnPicture[i][j].addActionListener(this);
			}
		}

		this.add(panelBody);
		this.setSize(ConfigDatas.WIN_WIDTH, ConfigDatas.WIN_HEIGHT);
		this.setVisible(true); // 모든 처리 종료 후 다시 패널을 보여준다.
	}

	// 랜덤하게 아이콘을 배치 한다.
	private void shufflePictures() {

		// 데이터 초기화
		for (int i = 0; i < sizeWidth; i++) {
			for (int j = 0; j < sizeHeight; j++) {
				pictures[i][j] = new PictureData();
			}
		}

		// 번호를 섞는 다.
		int totalCount = sizeWidth * sizeHeight;
		shuffleRandomNumbers(totalCount);

		// 섞은 랜덤 번호에 맞게 버튼의 이미지를 설정 한다.
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

	// 아이콘을 섞는 다.
	private void shuffleRandomNumbers(int count) {

		initShuffleNumbers();

		// 10번 정도 배열을 뒤죽박죽 섞는 다. 랜덤한 값이 겹치지 않을 때 까지 반복하는 처리 보다 빠르고 문제 가능성이 적다.
		int minxCount = 10;
		for (int shuffleCount = 0; shuffleCount < minxCount; ++shuffleCount) {
			for (int i = 0; i < count; ++i) {
				int rnd = (Math.abs(randomMaker.nextInt())) % count;

				int temp = randomNumbers[rnd];
				randomNumbers[rnd] = randomNumbers[i];
				randomNumbers[i] = temp;
			}
		}

		// 콘솔 화면에 랜덤하게 생성된 값 출력 해본다.
		for (int i = 0; i < randomNumbers.length; ++i) {
			if (randomNumbers[i] != 0) {
				System.out.println("index " + i + ", Value " + randomNumbers[i]);
			}
		}
	}

	// 게임에서 사용할 번호를 초기화 한다. 기본 셋팅 (랜덤으로 섞기 전이다)
	private void initShuffleNumbers() {
		for (int i = 0; i < randomNumbers.length / 2; ++i) {
			int number = i + 1;
			randomNumbers[i * 2] = number;
			randomNumbers[i * 2 + 1] = number;
		}
	}

	// 미리 보기를 가린다
	private void hidePreview() {
		for (int i = 0; i < sizeWidth; i++) {
			for (int j = 0; j < sizeHeight; j++) {
				if (pictures[i][j].show == false) {
					btnPicture[i][j].setIcon(null);
				}
			}
		}
		
		System.out.println("프리뷰 종료");
		gameStart = true;
	}

	public void actionPerformed(ActionEvent e) {

		JButton actionBtn = (JButton) e.getSource();

		// 난이도 선택
		if (actionBtn == btnDiffi_Easy) {
			initPictureToButtons(EDifficulty.eEasy_2x2); // 하
			
		} else if (actionBtn == btnDiffi_Normal) {
			initPictureToButtons(EDifficulty.eNormal_4x4); // 중
			
		} else if (actionBtn == btnDiffi_Hard) {
			initPictureToButtons(EDifficulty.eHard_6x5); // 상
			
		} else if (actionBtn == btnPrevPage1 || actionBtn == btnPrevPage2) {
			// 이전 화면으로 이동
			this.mainWindow.ShowHomeMenu();
		} else {

			if( gameStart == false )
			{
				System.out.println("프리뷰가 종료 될 때까지 잠시만 기다려 주세요");
				return;
			}
			
			// 그림맞추기 처리 중인 버튼이 있다.
			if (dismatchButton != null)
			{
				System.out.println("매칭되지 않은 이미지가 가려질 때까지 대기");
				return;
			}

			// 그림 맞추기
			for (int i = 0; i < sizeWidth; i++) {
				for (int j = 0; j < sizeHeight; j++) {
					if (actionBtn == btnPicture[i][j]) {

						PictureData currentPicture = pictures[i][j];

						// 2개의 짝 중 처음 뒤집은 아이콘에 대한 처리
						if (viewButton == null) {
							viewButton = actionBtn;
							viewButton.setIcon(currentPicture.image); // 아이콘의 이미지를 보여준다
							viewButtonIndexX = i;
							viewButtonIndexY = j;
							return;
						}

						PictureData viewPicture = pictures[viewButtonIndexX][viewButtonIndexY];

						// 2개의 짝을 맞추었을 경우
						if (viewPicture.number == currentPicture.number && viewPicture != currentPicture) {
							currentPicture.show = true;
							viewPicture.show = true;
							actionBtn.setIcon(currentPicture.image); // 2번째 뒤집은 아이콘의 이미지도 보여준다
							viewButton = null;
							dismatchHideTimer = 0;
							dismatchButton = null;
							
							// 모두 완료 했다.
							if( IsClear() == true ) {
								this.GameOver(); // 서버로 결과를 전송하고 종료 시킨다.
							}
							return;
						}
						
						// 2번째 아이콘을 뒤집었는 데, 짝이 맞지 않는 경우에 대한 처리.
						// 2번째 아이콘을 보여주지면, run의 타이머 기능에 의해 2초 뒤 다시 아이콘을 가리도록 셋팅 해 둔다.
						actionBtn.setIcon(currentPicture.image);
						dismatchButton = actionBtn;
						dismatchHideTimer = 2;
					}

				}
			}
		}
	}
	
	// 남은 게임 시간을 문자열로 반환 (ex: "남은 시간 30:00" ) 
	private String GetRaminTimeText(int timeSec) {
		String result = "남은 시간 ";
		
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
		long timeMills = System.currentTimeMillis(); // 현재 시간, 밀리세컨드 ( 1 / 1000 초 )

		int fps = (int) (1.0f / 30.0f * 1000); // 1초에 30번 이하만 수행 되도록 Sleep 타임을 준다.

		try {
			while (gameStop == false) {
				long currentTime = System.currentTimeMillis();

				// 1초가 지날 때 마다 실행
				if (currentTime - timeMills >= 1000) {
					timeMills = currentTime;
					
					// 게임 플레이 남은 시간
					// 프리뷰가 끝나야 카운트 다운 시작
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
										
					// 게임 시작 전 미리보기를 다시 가리는 처리
					if (previewTimer > 0) {
						--previewTimer;

						// 보여주던 것들을 모두 가린다.
						if (previewTimer == 0) {
							hidePreview();
						}
					}
					
					// 오답인 경우 뒤집었던 아이콘을 원래대로 되돌린다.
					if (dismatchHideTimer > 0) {
						--dismatchHideTimer;

						if (dismatchHideTimer == 0) {
							// 잘못 맞췄다면 보여주던 이전 버튼의 아이콘을 다시 가려준다.
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
	
	// 그림을 모두 맞추었나?
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
	
	// 게임 종료
	public void GameOver() {
		
		int score = this.remainTimeSec; // 남은 시간이 점수다.
		MessageBoxUtil.ShowMessageBox("게임 종료, 남은 시간(점수) " + score, "알림");
		
		// 점수 갱신
		JSONObject json = new JSONObject();
		json.put("gametype", "picture");
		json.put("score", score);
		
		mainWindow.sendMessage(EMessageCode.eUpdateGameScoreRequest, json); // 서버로 전송
		mainWindow.ShowHomeMenu(); // 메인 메뉴로 이동
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
	eNone, // 아직 난이도 선택 전
	eEasy_2x2, // 가로2 세로2
	eNormal_4x4, // 가로4 세로4
	eHard_6x5, // 가로 6 새ㅔ로 5
}