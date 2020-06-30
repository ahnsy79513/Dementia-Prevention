import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Random;
import javax.swing.JPanel;

import org.json.simple.JSONObject;

import java.awt.Toolkit;

// 두더지 잡기 게임
public class GameMole extends JPanel implements MouseListener, Runnable {

	private Image imgBackground; // 배경화면 이미지
	private Image imgMole; // 두더지 이미지
	private Image imgTunnel; // 두더지 터널 이미지
	private Image imgHammer; // 뿅망치 이미지

	private Mole[][] moles; // 두더지는 총 3x3으로 9 다.

	private Thread thread;
	private boolean gameStop; // 게임 중단
	private boolean gamePause; // 일시정지 true 및 다시시작 false

	private int score; // 터치한 두더지 수만큼 점수가 증가 한다.
	private Random randomShowMole; // 1.5초마다 9칸의 두더지 중 어떤 두더지를 나오게 할 것인가? 에서 사용하는 랜덤값
	private MainWindow mainWindow;
	private int remainTimeSec; // 남은 시간(초)

	public GameMole(MainWindow main) {
		mainWindow = main;

		// 두더지 게임에 필요한 이미지를 읽어들인다.
		imgBackground = Toolkit.getDefaultToolkit().getImage(".\\bin\\Image2\\field.png"); // 배경
		imgMole = Toolkit.getDefaultToolkit().getImage(".\\bin\\Image2\\molerat.png"); // 두더지
		imgTunnel = Toolkit.getDefaultToolkit().getImage(".\\bin\\Image2\\tunnel.png"); // 두더지구멍
		imgHammer = Toolkit.getDefaultToolkit().getImage(".\\bin\\Image2\\hammer.png"); // 뿅망치 (충돌 별표시 포함)

		moles = new Mole[3][3]; // 두더지굴 사이즈는 3x3 이다.
		randomShowMole = new Random(System.currentTimeMillis()); // 랜덤 객체 생성, 인자로 넘긴 값은 시스템 시간으로, 난수테이블을 셋팅하는 데 사용

		for (int iWidth = 0; iWidth < 3; ++iWidth) {
			for (int iHeight = 0; iHeight < 3; ++iHeight) {
				int number = iWidth + iHeight * 3 + 1;
				moles[iWidth][iHeight] = new Mole(number); // 3x3의 두더지 굴을 만든다.
			}
		}

		setBackground(Color.black);
		setSize(ConfigDatas.WIN_WIDTH, ConfigDatas.WIN_HEIGHT);
		setVisible(true);
		setLayout(null); // 레이아웃 없이 처리한다.

		this.addMouseListener(this);
		reset(); // 점수 및 시간, 두더지 상태 초기화

		gameStop = false; // 게임 중단에 사용
		gamePause = false; // 일시정지 및 재개

		thread = new Thread(this);
		thread.start();
	}

	public void stopGame() {
		this.gameStop = true;
	}

	// 점수 및 시간, 두더지 상태 초기화
	private void reset() {
		for (int iWidth = 0; iWidth < 3; ++iWidth) {
			for (int iHeight = 0; iHeight < 3; ++iHeight) {
				moles[iWidth][iHeight].reset(); // 모든 두더지굴의 상태를 초기화
			}
		}

		score = 0; // 점수 초기화
		remainTimeSec = 30; // 30초
	}

	// 더블 버퍼링으로 임시 이미지 객체에 모든 그림을 그린 뒤, 화면에 출력 해준다.
	public void paint(Graphics g) {
		// 더블 버퍼링을 위한 임시 이미지 객체를 만든다.
		Image bufferImage = createImage(ConfigDatas.WIN_WIDTH, ConfigDatas.WIN_HEIGHT);

		// 위에서 만든 이미지 객체에 그림을 그릴 수 있도록 그래픽스 객체를 생성 한다.
		Graphics bufferGraphics = bufferImage.getGraphics();

		drawBackgroud(bufferGraphics);
		drawTunnel(bufferGraphics);
		drawHelp(bufferGraphics);

		// 모든 그림이 그려진 임시 이미지 객체를 화면에 출력 한다. (더블버퍼링)
		g.drawImage(bufferImage, 0, 0, null);
	}

	// 배경을 그린다.
	private void drawBackgroud(Graphics g) {
		g.drawImage(imgBackground, 30, 120, this);

	}

	// 두더지 굴을 그린다.
	private void drawTunnel(Graphics g) {

		int iSpaceInterval = 120;
		int iBlockSizeX = 100;
		int iBlockSizeY = 80;
		int iStartX = 55;
		int iStartY = 150;

		for (int iWidth = 0; iWidth < 3; ++iWidth) {
			for (int iHeight = 0; iHeight < 3; ++iHeight) {

				int x = iWidth * iBlockSizeX + iWidth * iSpaceInterval + iStartX;
				int y = iHeight * iBlockSizeY + iHeight * iSpaceInterval + iStartY;

				g.drawImage(imgTunnel, x, y, this);

				EMoleTunnelState state = moles[iWidth][iHeight].GetState();

				// 두더지를 그린다.
				if (state == EMoleTunnelState.eShowMole || state == EMoleTunnelState.eAttackedMole) {
					g.drawImage(imgMole, x, y - 50, this);
				}

				if (state == EMoleTunnelState.eAttackedMole) {
					// 두더지가 맞은 상태라면 망치도 덮어서 그린다.
					g.drawImage(imgHammer, x - 10, y - 100, this);
				}
			}
		}
	}

	// 도움말을 그린다. 설명 및 게임 다시 시작 버튼, 나가기 버튼
	private void drawHelp(Graphics g) {

		// 기본 공통 폰트 포맷 설정
		Font font = new Font("돋움", Font.PLAIN, 30);
		g.setFont(font);

		// 설명 버튼
		g.drawRect(20, 20, 100, 50);
		g.drawString("설명", 40, 55);

		// 게임을 일시정지한 상태에서만 보여지는 도움말 기능
		if (this.gamePause == true) {
			g.setColor(Color.white);
			g.fillRect(20, 80, 450, 100);
			g.setColor(Color.black);

			g.drawRect(20, 80, 450, 100);
			g.drawString("제한시간 내에 튀어나오는", 40, 120);
			g.drawString("두더쥐를 터치 하세요.", 40, 155);

			// 게임 시작 버튼
			g.setColor(Color.blue);
			g.fillRect(380, 125, 100, 50);
			g.setColor(Color.black);
			g.drawRect(380, 125, 100, 50);

			Font fontGameResume = new Font("돋움", Font.PLAIN, 22);
			g.setFont(fontGameResume);

			// 게임 다시 시작하는 버튼
			g.setColor(Color.white);
			g.drawString("게임시작", 390, 160);
			g.setColor(Color.black);
		}

		// 점수
		g.setFont(font);
		g.drawString("점수 : " + score, 200, 55);

		// 남은 시간
		String remainTimeText = this.GetRaminTimeText(this.remainTimeSec);
		g.drawString(remainTimeText, 370, 55);

		// 나가기 버튼
		g.drawRect(570, 700, 100, 50);
		g.drawString("나가기", 580, 735);
	}

	// 나가기 버튼
	private void OnClickExit(MouseEvent e) {
		int eventX = e.getX();
		int eventY = e.getY();

		if (eventX >= 570 && eventX < 670 && eventY >= 700 && eventY < 750) {
			this.gameStop = true;
			this.mainWindow.ShowHomeMenu();
			System.out.println("두더지 게임 나가기");
		}
	}

	// 게임 일시정지 및 다시시작
	private void OnGamePauseOrResult(MouseEvent e) {
		int eventX = e.getX();
		int eventY = e.getY();

		// 일시정지로 만든다.
		if (this.gamePause == false) {

			// 좌표에 해당하는 사각 박스를 클릭 했다면
			if (eventX >= 20 && eventX < 120 && eventY >= 20 && eventY < 70) {
				this.gamePause = true;
				System.out.println("일시 정지");
			}
			return;
		}

		// 다시 시작 버튼 ( 게임 내 "게임 시작" 버튼 )
		// 좌표에 해당하는 사각 박스를 클릭 했다면
		if (eventX >= 380 && eventX < 480 && eventY >= 125 && eventY < 175) {
			this.gamePause = false;
			System.out.println("게임 재개");
		}
	}

	// 남은 시간을 - 남은 시간 30:00 과 같이 문자열을 만들어 반환
	private String GetRaminTimeText(int timeSec) {
		String result = "남은 시간 ";

		int min = remainTimeSec / 60;
		int sec = remainTimeSec % 60;

		if (min >= 10) {
			result += min;
		} else {
			result += ("0" + min);
		}

		result += ":";

		if (sec >= 10) {
			result += sec;
		} else {
			result += ("0" + sec);
		}

		return result;
	}

	public void run() {
		long timeMills = System.currentTimeMillis(); // 현재 시간, 밀리세컨드 ( 1 / 1000 초 )
		long timeMills2 = System.currentTimeMillis(); // 현재 시간, 밀리세컨드 ( 1 / 1000 초 )
		int fps = (int) (1.0f / 60.0f * 1000); // 1초에 60번 이하만 수행 되도록 Sleep 타임을 준다.

		try {
			while (gameStop == false) {
				long currentTime = System.currentTimeMillis();

				// 일시정지
				if (gamePause == true) {
					timeMills = currentTime;
					timeMills2 = currentTime;
					repaint();
					Thread.sleep(fps);
					continue;
				}

				// 1초가 지날 때 마다 실행
				if (currentTime - timeMills >= 1000) {
					timeMills = currentTime;

					for (int iWidth = 0; iWidth < 3; ++iWidth) {
						for (int iHeight = 0; iHeight < 3; ++iHeight) {
							moles[iWidth][iHeight].playTime();
						}
					}

					if (remainTimeSec > 0) {
						--remainTimeSec;

						if (remainTimeSec == 0) {
							GameOver();
							gameStop = true;
							return;
						}
					}
				}

				// 1.5초마다 랜덤으로 두더지 등장
				if (currentTime - timeMills2 >= ConfigDatas.MOLE_SHOW_TIME) {
					timeMills2 = currentTime;

					int random_mole_number = Math.abs(randomShowMole.nextInt() % 10 + 1); // 최대 번호는 9까지이므로, 10의 나머지 값을 구한다.
					
					for (int iWidth = 0; iWidth < 3; ++iWidth) {
						for (int iHeight = 0; iHeight < 3; ++iHeight) {
							if (moles[iWidth][iHeight].GetNumber() == random_mole_number) {
								moles[iWidth][iHeight].ShowMole(2); // 모습을 드러낸 두더지는 2초간 그대로 나와있게 한다.
							}
						}
					}
				}

				repaint();
				Thread.sleep(fps);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// 마우스를 클릭 했을 때
	@Override
	public void mouseClicked(MouseEvent e) {
		AttackMole(e);
	}

	// 마우스를 눌렀을 때
	@Override
	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub
		AttackMole(e);
		OnClickExit(e);
		OnGamePauseOrResult(e);
	}

	// 마우스에서 손을 떼었을 때
	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub
		AttackMole(e);
		OnClickExit(e);
		OnGamePauseOrResult(e);
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	// 게임 종료
	public void GameOver() {

		MessageBoxUtil.ShowMessageBox("게임 종료, 점수 " + score, "알림");

		// 점수 갱신
		JSONObject json = new JSONObject();
		json.put("gametype", "mole");
		json.put("score", score);

		mainWindow.sendMessage(EMessageCode.eUpdateGameScoreRequest, json); // 결과를 서버로 전송
		mainWindow.ShowHomeMenu(); // 메뉴 선택 화면으로 이동
	}

	// 뿅망치로 두더지 공격
	private void AttackMole(MouseEvent e) {
		for (int iWidth = 0; iWidth < 3; ++iWidth) {
			for (int iHeight = 0; iHeight < 3; ++iHeight) {
				if (moles[iWidth][iHeight].Attacked(e) == true) {
					score++;
					System.out.println(moles[iWidth][iHeight].number + " 번 두더지 적중, 현재 점수 " + score);
					return;
				}
			}
		}
	}
}

enum EMoleTunnelState {
	eNone, // 그냥 두더지굴만 보여준다
	eShowMole, // 두더지가 보여진다
	eAttackedMole, // 두더지가 맞았다
}

// 두더지
class Mole {
	public EMoleTunnelState eState = EMoleTunnelState.eNone;
	public int showTime = 0; // 해당 시간이 경과하면 eState를 eNone로 바꾼다.
	public int number = 0;

	public Mole(int num) {
		eState = EMoleTunnelState.eNone;
		showTime = 0;
		number = num;
	}

	public void reset() {
		eState = EMoleTunnelState.eNone;
		showTime = 0;
	}

	// 망치에 맞았다.
	public boolean Attacked(MouseEvent e) {
		// 두더지에 해당하는 사각 박스 충돌 체크
		if (IsCollision(e) == false)
			return false;

		System.out.println("충돌한 두더지굴 상태 " + eState);

		// 두더지가 나왔던 상태라면, 망치 맞은 상태로 바꾼다.
		if (eState == EMoleTunnelState.eShowMole) {
			eState = EMoleTunnelState.eAttackedMole;
			showTime = 2; // 1초간 보여진다.
			return true; // 메서드를 호출한 쪽에서 true 라면 점수를 올릴 수 있게 하도록 한다.
		}

		return false;
	}

	private boolean IsCollision(MouseEvent e) {
		int iSpaceInterval = 120;
		int iBlockSizeX = 100;
		int iBlockSizeY = 80;
		int iStartX = 55;
		int iStartY = 150;

		// 해당 두더지굴과 충돌이 발생했나 확인
		for (int iWidth = 0; iWidth < 3; ++iWidth) {
			for (int iHeight = 0; iHeight < 3; ++iHeight) {
				if (number != (iHeight * 3) + iWidth + 1)
					continue;

				int x1 = iWidth * iBlockSizeX + iWidth * iSpaceInterval + iStartX - 20;
				int y1 = iHeight * iBlockSizeY + iHeight * iSpaceInterval + iStartY - 30;

				int x2 = x1 + 160; // 사각형은 가로 160 픽셀 크기
				int y2 = y1 + 200; // 사각형은 세로 200 픽셀 크기

				int eventX = e.getX();
				int eventY = e.getY();
				if (eventX >= x1 && eventX < x2 && eventY >= y1 && eventY < y2) {
					System.out.println("두더지굴 충돌 좌표 x " + eventX + ", y " + eventY + ", 번호 " + number);
					return true;
				}
			}
		}

		return false;
	}

	public int GetNumber() {
		return number;
	}

	// 두더지를 나오게 한다.
	public void ShowMole(int time) {
		// 두더지굴만 보이던 상태였다면, 두더지를 보여준다.
		if (eState == EMoleTunnelState.eNone) {
			eState = EMoleTunnelState.eShowMole;
			showTime = time; // 파라메터로 전달 받은 시간(초) 만큼 두더지 상태를 보여 준다.
			System.out.println("얼굴을 내민 두더지굴번호 " + number);
		}
	}

	public EMoleTunnelState GetState() {
		return eState;
	}

	// 시간이 경과되면 상태를 바꾼다.
	public void playTime() {
		if (showTime == 0)
			return;

		--showTime;
		switch (eState) {
		case eShowMole: // 두더지가 보여진 상태
			if (showTime == 0) {
				eState = EMoleTunnelState.eNone; // 두더지굴만 보이도록 변경
				System.out.println("얼굴 보였다가 숨는 다 " + number);
			}

			break;
		case eAttackedMole: // 두더지와 뿅망치가 보여진 상태
			if (showTime == 0) {
				eState = EMoleTunnelState.eNone; // 두더지굴만 보이도록 변경
				System.out.println("망치 맞았던 것 초기화 " + number);
			}

			break;
		}
	}
}