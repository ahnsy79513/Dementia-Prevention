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

// �δ��� ��� ����
public class GameMole extends JPanel implements MouseListener, Runnable {

	private Image imgBackground; // ���ȭ�� �̹���
	private Image imgMole; // �δ��� �̹���
	private Image imgTunnel; // �δ��� �ͳ� �̹���
	private Image imgHammer; // �и�ġ �̹���

	private Mole[][] moles; // �δ����� �� 3x3���� 9 ��.

	private Thread thread;
	private boolean gameStop; // ���� �ߴ�
	private boolean gamePause; // �Ͻ����� true �� �ٽý��� false

	private int score; // ��ġ�� �δ��� ����ŭ ������ ���� �Ѵ�.
	private Random randomShowMole; // 1.5�ʸ��� 9ĭ�� �δ��� �� � �δ����� ������ �� ���ΰ�? ���� ����ϴ� ������
	private MainWindow mainWindow;
	private int remainTimeSec; // ���� �ð�(��)

	public GameMole(MainWindow main) {
		mainWindow = main;

		// �δ��� ���ӿ� �ʿ��� �̹����� �о���δ�.
		imgBackground = Toolkit.getDefaultToolkit().getImage(".\\bin\\Image2\\field.png"); // ���
		imgMole = Toolkit.getDefaultToolkit().getImage(".\\bin\\Image2\\molerat.png"); // �δ���
		imgTunnel = Toolkit.getDefaultToolkit().getImage(".\\bin\\Image2\\tunnel.png"); // �δ�������
		imgHammer = Toolkit.getDefaultToolkit().getImage(".\\bin\\Image2\\hammer.png"); // �и�ġ (�浹 ��ǥ�� ����)

		moles = new Mole[3][3]; // �δ����� ������� 3x3 �̴�.
		randomShowMole = new Random(System.currentTimeMillis()); // ���� ��ü ����, ���ڷ� �ѱ� ���� �ý��� �ð�����, �������̺��� �����ϴ� �� ���

		for (int iWidth = 0; iWidth < 3; ++iWidth) {
			for (int iHeight = 0; iHeight < 3; ++iHeight) {
				int number = iWidth + iHeight * 3 + 1;
				moles[iWidth][iHeight] = new Mole(number); // 3x3�� �δ��� ���� �����.
			}
		}

		setBackground(Color.black);
		setSize(ConfigDatas.WIN_WIDTH, ConfigDatas.WIN_HEIGHT);
		setVisible(true);
		setLayout(null); // ���̾ƿ� ���� ó���Ѵ�.

		this.addMouseListener(this);
		reset(); // ���� �� �ð�, �δ��� ���� �ʱ�ȭ

		gameStop = false; // ���� �ߴܿ� ���
		gamePause = false; // �Ͻ����� �� �簳

		thread = new Thread(this);
		thread.start();
	}

	public void stopGame() {
		this.gameStop = true;
	}

	// ���� �� �ð�, �δ��� ���� �ʱ�ȭ
	private void reset() {
		for (int iWidth = 0; iWidth < 3; ++iWidth) {
			for (int iHeight = 0; iHeight < 3; ++iHeight) {
				moles[iWidth][iHeight].reset(); // ��� �δ������� ���¸� �ʱ�ȭ
			}
		}

		score = 0; // ���� �ʱ�ȭ
		remainTimeSec = 30; // 30��
	}

	// ���� ���۸����� �ӽ� �̹��� ��ü�� ��� �׸��� �׸� ��, ȭ�鿡 ��� ���ش�.
	public void paint(Graphics g) {
		// ���� ���۸��� ���� �ӽ� �̹��� ��ü�� �����.
		Image bufferImage = createImage(ConfigDatas.WIN_WIDTH, ConfigDatas.WIN_HEIGHT);

		// ������ ���� �̹��� ��ü�� �׸��� �׸� �� �ֵ��� �׷��Ƚ� ��ü�� ���� �Ѵ�.
		Graphics bufferGraphics = bufferImage.getGraphics();

		drawBackgroud(bufferGraphics);
		drawTunnel(bufferGraphics);
		drawHelp(bufferGraphics);

		// ��� �׸��� �׷��� �ӽ� �̹��� ��ü�� ȭ�鿡 ��� �Ѵ�. (������۸�)
		g.drawImage(bufferImage, 0, 0, null);
	}

	// ����� �׸���.
	private void drawBackgroud(Graphics g) {
		g.drawImage(imgBackground, 30, 120, this);

	}

	// �δ��� ���� �׸���.
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

				// �δ����� �׸���.
				if (state == EMoleTunnelState.eShowMole || state == EMoleTunnelState.eAttackedMole) {
					g.drawImage(imgMole, x, y - 50, this);
				}

				if (state == EMoleTunnelState.eAttackedMole) {
					// �δ����� ���� ���¶�� ��ġ�� ��� �׸���.
					g.drawImage(imgHammer, x - 10, y - 100, this);
				}
			}
		}
	}

	// ������ �׸���. ���� �� ���� �ٽ� ���� ��ư, ������ ��ư
	private void drawHelp(Graphics g) {

		// �⺻ ���� ��Ʈ ���� ����
		Font font = new Font("����", Font.PLAIN, 30);
		g.setFont(font);

		// ���� ��ư
		g.drawRect(20, 20, 100, 50);
		g.drawString("����", 40, 55);

		// ������ �Ͻ������� ���¿����� �������� ���� ���
		if (this.gamePause == true) {
			g.setColor(Color.white);
			g.fillRect(20, 80, 450, 100);
			g.setColor(Color.black);

			g.drawRect(20, 80, 450, 100);
			g.drawString("���ѽð� ���� Ƣ�����", 40, 120);
			g.drawString("�δ��㸦 ��ġ �ϼ���.", 40, 155);

			// ���� ���� ��ư
			g.setColor(Color.blue);
			g.fillRect(380, 125, 100, 50);
			g.setColor(Color.black);
			g.drawRect(380, 125, 100, 50);

			Font fontGameResume = new Font("����", Font.PLAIN, 22);
			g.setFont(fontGameResume);

			// ���� �ٽ� �����ϴ� ��ư
			g.setColor(Color.white);
			g.drawString("���ӽ���", 390, 160);
			g.setColor(Color.black);
		}

		// ����
		g.setFont(font);
		g.drawString("���� : " + score, 200, 55);

		// ���� �ð�
		String remainTimeText = this.GetRaminTimeText(this.remainTimeSec);
		g.drawString(remainTimeText, 370, 55);

		// ������ ��ư
		g.drawRect(570, 700, 100, 50);
		g.drawString("������", 580, 735);
	}

	// ������ ��ư
	private void OnClickExit(MouseEvent e) {
		int eventX = e.getX();
		int eventY = e.getY();

		if (eventX >= 570 && eventX < 670 && eventY >= 700 && eventY < 750) {
			this.gameStop = true;
			this.mainWindow.ShowHomeMenu();
			System.out.println("�δ��� ���� ������");
		}
	}

	// ���� �Ͻ����� �� �ٽý���
	private void OnGamePauseOrResult(MouseEvent e) {
		int eventX = e.getX();
		int eventY = e.getY();

		// �Ͻ������� �����.
		if (this.gamePause == false) {

			// ��ǥ�� �ش��ϴ� �簢 �ڽ��� Ŭ�� �ߴٸ�
			if (eventX >= 20 && eventX < 120 && eventY >= 20 && eventY < 70) {
				this.gamePause = true;
				System.out.println("�Ͻ� ����");
			}
			return;
		}

		// �ٽ� ���� ��ư ( ���� �� "���� ����" ��ư )
		// ��ǥ�� �ش��ϴ� �簢 �ڽ��� Ŭ�� �ߴٸ�
		if (eventX >= 380 && eventX < 480 && eventY >= 125 && eventY < 175) {
			this.gamePause = false;
			System.out.println("���� �簳");
		}
	}

	// ���� �ð��� - ���� �ð� 30:00 �� ���� ���ڿ��� ����� ��ȯ
	private String GetRaminTimeText(int timeSec) {
		String result = "���� �ð� ";

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
		long timeMills = System.currentTimeMillis(); // ���� �ð�, �и������� ( 1 / 1000 �� )
		long timeMills2 = System.currentTimeMillis(); // ���� �ð�, �и������� ( 1 / 1000 �� )
		int fps = (int) (1.0f / 60.0f * 1000); // 1�ʿ� 60�� ���ϸ� ���� �ǵ��� Sleep Ÿ���� �ش�.

		try {
			while (gameStop == false) {
				long currentTime = System.currentTimeMillis();

				// �Ͻ�����
				if (gamePause == true) {
					timeMills = currentTime;
					timeMills2 = currentTime;
					repaint();
					Thread.sleep(fps);
					continue;
				}

				// 1�ʰ� ���� �� ���� ����
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

				// 1.5�ʸ��� �������� �δ��� ����
				if (currentTime - timeMills2 >= ConfigDatas.MOLE_SHOW_TIME) {
					timeMills2 = currentTime;

					int random_mole_number = Math.abs(randomShowMole.nextInt() % 10 + 1); // �ִ� ��ȣ�� 9�����̹Ƿ�, 10�� ������ ���� ���Ѵ�.
					
					for (int iWidth = 0; iWidth < 3; ++iWidth) {
						for (int iHeight = 0; iHeight < 3; ++iHeight) {
							if (moles[iWidth][iHeight].GetNumber() == random_mole_number) {
								moles[iWidth][iHeight].ShowMole(2); // ����� �巯�� �δ����� 2�ʰ� �״�� �����ְ� �Ѵ�.
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

	// ���콺�� Ŭ�� ���� ��
	@Override
	public void mouseClicked(MouseEvent e) {
		AttackMole(e);
	}

	// ���콺�� ������ ��
	@Override
	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub
		AttackMole(e);
		OnClickExit(e);
		OnGamePauseOrResult(e);
	}

	// ���콺���� ���� ������ ��
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

	// ���� ����
	public void GameOver() {

		MessageBoxUtil.ShowMessageBox("���� ����, ���� " + score, "�˸�");

		// ���� ����
		JSONObject json = new JSONObject();
		json.put("gametype", "mole");
		json.put("score", score);

		mainWindow.sendMessage(EMessageCode.eUpdateGameScoreRequest, json); // ����� ������ ����
		mainWindow.ShowHomeMenu(); // �޴� ���� ȭ������ �̵�
	}

	// �и�ġ�� �δ��� ����
	private void AttackMole(MouseEvent e) {
		for (int iWidth = 0; iWidth < 3; ++iWidth) {
			for (int iHeight = 0; iHeight < 3; ++iHeight) {
				if (moles[iWidth][iHeight].Attacked(e) == true) {
					score++;
					System.out.println(moles[iWidth][iHeight].number + " �� �δ��� ����, ���� ���� " + score);
					return;
				}
			}
		}
	}
}

enum EMoleTunnelState {
	eNone, // �׳� �δ������� �����ش�
	eShowMole, // �δ����� ��������
	eAttackedMole, // �δ����� �¾Ҵ�
}

// �δ���
class Mole {
	public EMoleTunnelState eState = EMoleTunnelState.eNone;
	public int showTime = 0; // �ش� �ð��� ����ϸ� eState�� eNone�� �ٲ۴�.
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

	// ��ġ�� �¾Ҵ�.
	public boolean Attacked(MouseEvent e) {
		// �δ����� �ش��ϴ� �簢 �ڽ� �浹 üũ
		if (IsCollision(e) == false)
			return false;

		System.out.println("�浹�� �δ����� ���� " + eState);

		// �δ����� ���Դ� ���¶��, ��ġ ���� ���·� �ٲ۴�.
		if (eState == EMoleTunnelState.eShowMole) {
			eState = EMoleTunnelState.eAttackedMole;
			showTime = 2; // 1�ʰ� ��������.
			return true; // �޼��带 ȣ���� �ʿ��� true ��� ������ �ø� �� �ְ� �ϵ��� �Ѵ�.
		}

		return false;
	}

	private boolean IsCollision(MouseEvent e) {
		int iSpaceInterval = 120;
		int iBlockSizeX = 100;
		int iBlockSizeY = 80;
		int iStartX = 55;
		int iStartY = 150;

		// �ش� �δ������� �浹�� �߻��߳� Ȯ��
		for (int iWidth = 0; iWidth < 3; ++iWidth) {
			for (int iHeight = 0; iHeight < 3; ++iHeight) {
				if (number != (iHeight * 3) + iWidth + 1)
					continue;

				int x1 = iWidth * iBlockSizeX + iWidth * iSpaceInterval + iStartX - 20;
				int y1 = iHeight * iBlockSizeY + iHeight * iSpaceInterval + iStartY - 30;

				int x2 = x1 + 160; // �簢���� ���� 160 �ȼ� ũ��
				int y2 = y1 + 200; // �簢���� ���� 200 �ȼ� ũ��

				int eventX = e.getX();
				int eventY = e.getY();
				if (eventX >= x1 && eventX < x2 && eventY >= y1 && eventY < y2) {
					System.out.println("�δ����� �浹 ��ǥ x " + eventX + ", y " + eventY + ", ��ȣ " + number);
					return true;
				}
			}
		}

		return false;
	}

	public int GetNumber() {
		return number;
	}

	// �δ����� ������ �Ѵ�.
	public void ShowMole(int time) {
		// �δ������� ���̴� ���¿��ٸ�, �δ����� �����ش�.
		if (eState == EMoleTunnelState.eNone) {
			eState = EMoleTunnelState.eShowMole;
			showTime = time; // �Ķ���ͷ� ���� ���� �ð�(��) ��ŭ �δ��� ���¸� ���� �ش�.
			System.out.println("���� ���� �δ�������ȣ " + number);
		}
	}

	public EMoleTunnelState GetState() {
		return eState;
	}

	// �ð��� ����Ǹ� ���¸� �ٲ۴�.
	public void playTime() {
		if (showTime == 0)
			return;

		--showTime;
		switch (eState) {
		case eShowMole: // �δ����� ������ ����
			if (showTime == 0) {
				eState = EMoleTunnelState.eNone; // �δ������� ���̵��� ����
				System.out.println("�� �����ٰ� ���� �� " + number);
			}

			break;
		case eAttackedMole: // �δ����� �и�ġ�� ������ ����
			if (showTime == 0) {
				eState = EMoleTunnelState.eNone; // �δ������� ���̵��� ����
				System.out.println("��ġ �¾Ҵ� �� �ʱ�ȭ " + number);
			}

			break;
		}
	}
}