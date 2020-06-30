import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Font;
import javax.swing.*;
import javax.swing.border.Border;
import org.json.simple.JSONObject;
import java.awt.event.*;

// 자가진단 화면을 구성하며 처리한다.
public class SelfTest extends JPanel implements ActionListener {

	private final int WARN_MESSAGE_POINT = 7; // 7점 이상이면 공공시설 정보를 제공한다.
	private JLabel labelTitle;

	private JButton btnNext; // 1페이지에서 2페이지로 이동시키는 버튼
	private JButton btnSubmit; // 결과를 서버로 전송 하고 결과 페이지로 이동시키는 버튼
	private JButton btnOK; // 메뉴 화면으로 이동

	private CardLayout cardLayout;
	private JPanel selfTestPage1; // 1페이지
	private JPanel selfTestPage2; // 2페이지
	private JPanel resultPage; // 결과 페이지

	private JPanel panelHead; // 화면 상단
	private JPanel panelBody; // 화면 하단

	private QuestionItem[] questions; // 질문 (15개)
	private JTextArea textResult; // 자가진단 결과를 텍스트로 출력
	private MainWindow mainWindow;

	public SelfTest(MainWindow main) {
		mainWindow = main;

		Font fontTitle = new Font("돋움", Font.PLAIN, 30);
		Font fontText = new Font("돋움", Font.PLAIN, 15);

		this.setLayout(null);

		int headPanelY = (int) (ConfigDatas.WIN_HEIGHT * 0.15f);
		panelHead = new JPanel();
		panelHead.setSize(ConfigDatas.WIN_WIDTH, headPanelY);
		panelHead.setBounds(0, 0, ConfigDatas.WIN_WIDTH, headPanelY);
		panelHead.setLayout(null);
		add(panelHead);

		labelTitle = new JLabel("자가 진단 테스트");
		labelTitle.setFont(fontTitle);
		labelTitle.setBounds(230, 50, 300, 50);
		panelHead.add(labelTitle);

		int bodyPanelY = (int) (ConfigDatas.WIN_HEIGHT * 0.85f);
		panelBody = new JPanel();
		panelBody.setSize(ConfigDatas.WIN_WIDTH, bodyPanelY);
		panelBody.setBounds(0, headPanelY, ConfigDatas.WIN_WIDTH, bodyPanelY);
		add(panelBody);

		cardLayout = new CardLayout(0, 0);
		panelBody.setLayout(cardLayout); // 카드 레이아웃으로 처리. 2페이지를 한 페이지씩 전환하며 사용 한다.

		selfTestPage1 = new JPanel();
		selfTestPage1.setSize(ConfigDatas.WIN_WIDTH, ConfigDatas.WIN_HEIGHT);
		selfTestPage1.setLayout(null);

		selfTestPage2 = new JPanel();
		selfTestPage2.setSize(ConfigDatas.WIN_WIDTH, ConfigDatas.WIN_HEIGHT);
		selfTestPage2.setLayout(null);

		resultPage = new JPanel();
		resultPage.setSize(ConfigDatas.WIN_WIDTH, ConfigDatas.WIN_HEIGHT);
		resultPage.setLayout(null);

		panelBody.add("page1", selfTestPage1);
		panelBody.add("page2", selfTestPage2);
		panelBody.add("resultPage", resultPage);
		cardLayout.show(panelBody, "page1");

		textResult = new JTextArea();
		textResult.setFont(fontText);
		textResult.setBounds(150, 0, 400, 520);
		Border border = BorderFactory.createLineBorder(Color.GRAY);
		textResult.setBorder(border);
		resultPage.add(textResult);

		// 질문, 설문을 15개 만들어낸다.
		initQuestions();

		btnNext = new JButton("다음");
		btnNext.setFont(fontText);
		btnNext.addActionListener(this);
		btnNext.setBounds(310, 555, 100, 35);
		selfTestPage1.add(btnNext);

		btnSubmit = new JButton("완료");
		btnSubmit.setFont(fontText);
		btnSubmit.addActionListener(this);
		btnSubmit.setBounds(310, 555, 100, 35);
		selfTestPage2.add(btnSubmit);

		btnOK = new JButton("확인");
		btnOK.setFont(fontText);
		btnOK.addActionListener(this);
		btnOK.setBounds(310, 555, 100, 35);
		resultPage.add(btnOK);

		setSize(ConfigDatas.WIN_WIDTH, ConfigDatas.WIN_HEIGHT);
		this.setVisible(true); // 모든 처리 종료 후 다시 패널을 보여준다.
	}

	private void initQuestions() {

		Font fontText = new Font("돋움", Font.PLAIN, 20);
		questions = new QuestionItem[15];

		int number = 0;

		// 1Page selfTestPage1 사용
		QuestionItem q1 = new QuestionItem(1, selfTestPage1, number, "자신의 기억력에 문제가 있다고 생각하십니까? ", fontText);
		questions[number++] = q1;

		QuestionItem q2 = new QuestionItem(1, selfTestPage1, number, "자신의 기억력이 10년 전보다 나빠졌다고 생각하십니까? ", fontText);
		questions[number++] = q2;

		QuestionItem q3 = new QuestionItem(1, selfTestPage1, number, "자신의 기억력이 같은 또래의 다른 사람들에 비해 나쁘다고 생각하십니까?", fontText);
		questions[number++] = q3;

		QuestionItem q4 = new QuestionItem(1, selfTestPage1, number, "기억력 저하로 인해 일상생활에 불편을 느끼십니까? ", fontText);
		questions[number++] = q4;

		QuestionItem q5 = new QuestionItem(1, selfTestPage1, number, "최근에 일어난 일을 기억하는 것이 어렵습니까? ", fontText);
		questions[number++] = q5;

		QuestionItem q6 = new QuestionItem(1, selfTestPage1, number, "며칠 전에 나눈 대화 내용을 기억하기 어렵습니까? ", fontText);
		questions[number++] = q6;

		QuestionItem q7 = new QuestionItem(1, selfTestPage1, number, "며칠 전에 한 약속을 기억하기 어렵습니까? ", fontText);
		questions[number++] = q7;

		// 2Page selfTestPage2 사용
		QuestionItem q8 = new QuestionItem(2, selfTestPage2, number, "친한 사람의 이름을 기억하기 어렵습니까? ", fontText);
		questions[number++] = q8;

		QuestionItem q9 = new QuestionItem(2, selfTestPage2, number, "물건 둔 곳을 기억하기 어렵습니까? ", fontText);
		questions[number++] = q9;

		QuestionItem q10 = new QuestionItem(2, selfTestPage2, number, "이전에 비해 물건을 자주 잃어버립니까? ", fontText);
		questions[number++] = q10;

		QuestionItem q11 = new QuestionItem(2, selfTestPage2, number, "집 근처에서 길을 잃은 적이 있습니까? ", fontText);
		questions[number++] = q11;

		QuestionItem q12 = new QuestionItem(2, selfTestPage2, number, "가게에서 2-3가지 물건을 사려고 할 때 물건이름을 기억하기 어렵습니까? ", fontText);
		questions[number++] = q12;

		QuestionItem q13 = new QuestionItem(2, selfTestPage2, number, "가스불이나 전기불 끄는 것을 기억하기 어렵습니까? ", fontText);
		questions[number++] = q13;

		QuestionItem q14 = new QuestionItem(2, selfTestPage2, number, "자주 사용하는 전화번호(자신 혹은 자녀의 집)를 기억하기 어렵습니까? ", fontText);
		questions[number++] = q14;

		QuestionItem q15 = new QuestionItem(2, selfTestPage2, number, "전날 먹은 음식메뉴가 생각나지 않습니까? ", fontText);
		questions[number++] = q15;
	}

	public void actionPerformed(ActionEvent e) {
		JButton actionBtn = (JButton) e.getSource();

		if (actionBtn == btnNext) { // 다음 페이지

			for (int i = 0; i < questions.length; ++i) {
				if (questions[i].page != 1)
					continue;

				if (questions[i].SelectedAnser() == false) {
					MessageBoxUtil.ShowMessageBox("아직 답을 선택하지 않은 설문이 있습니다.", "알림");
					return;
				}
			}

			cardLayout.show(panelBody, "page2");
			System.out.println("페이지 전환");

		} else if (actionBtn == btnSubmit) { // 완료, 서버로 결과 전송
			for (int i = 0; i < questions.length; ++i) {
				if (questions[i].page != 1)
					continue;

				if (questions[i].SelectedAnser() == false) {
					MessageBoxUtil.ShowMessageBox("아직 답을 선택하지 않은 설문이 있습니다.", "알림");
					return;
				}
			}

			// 서버로 결과 전송 및 결과 페이지로 전환
			SubmitToServer();

		} else if (actionBtn == btnOK) { // 결과 페이지에서 OK 버튼 눌렀다.
			this.mainWindow.ShowHomeMenu(); // 메인 화면으로 이동
		}
	}

	// 서버로 결과 전송
	private void SubmitToServer() {
		System.out.println("서버로 결과 전송!");

		// 모든 문항을 돌면서 yes
		int sum = 0;
		for (int i = 0; i < questions.length; ++i) {
			if (questions[i].radioYesOrNo[0].isSelected() == true) {
				++sum; // 점수를 더한다.
			}
		}

		// 결과를 서버에 전송
		TestResultSendToServer(sum);

		// 결과 화면 전환
		cardLayout.show(panelBody, "resultPage");

		String result = "최종 점수는 " + sum + "입니다.\n\n";

		// 7점 이상이면 보건소나 치매지원센터 공공시설 정보를 제공 한다.
		if (sum >= WARN_MESSAGE_POINT) {
			result += "중앙치매센터\n";
			result += "https://www.nid.or.kr/main/main.aspx \n";
			result += "전화 1666-0921";

			textResult.setText(result);
		} else {
			result += "운동과 외부 사회 활동을 유지 하시고,\n";
			result += "치매 예방 수칙 333을 잘 시천하셔서 치매를 예방하세요.";
			textResult.setText(result);
		}
	}

	// 서버로 데이터 전송
	public void TestResultSendToServer(int point) {
		// 점수 갱신
		JSONObject json = new JSONObject();
		json.put("point", point);
		mainWindow.sendMessage(EMessageCode.eSelfTestPointUpdateRequest, json);
	}
}

class QuestionItem {
	public JLabel labelQustion; // Text 질문 내용
	public JRadioButton[] radioYesOrNo; // 예/아니오 라디오 버튼
	public int number; // 문항의 번호
	public int page; // 1페이지, 2페이지 구분.

	// 생성자 내부에서 라디오 버튼 및 질문지 텍스트를 표기할 라벨의 좌표를 설정 한다.
	public QuestionItem(int p, JPanel panel, int num, String question, Font font) {
		number = num;
		page = p;

		int y = (num * 70) - ((p - 1) * 490);

		labelQustion = new JLabel((num + 1) + ". " + question); // 번호와 질문을 붙인다.
		labelQustion.setFont(font);
		labelQustion.setBounds(100, y, 600, 35); // 질문 텍스트 좌표
		panel.add(labelQustion);

		radioYesOrNo = new JRadioButton[2];
		ButtonGroup group = new ButtonGroup();

		for (int i = 0; i < radioYesOrNo.length; i++) {
			radioYesOrNo[i] = new JRadioButton(i == 0 ? "예" : "아니오");
			group.add(radioYesOrNo[i]);

			if (i == 0) {
				radioYesOrNo[i].setBounds(100, y + 35, 100, 35); // '예' 라디오 버튼 좌표
			} else {
				radioYesOrNo[i].setBounds(200, y + 35, 100, 35); // '아니오 라디오 버튼 좌표
			}

			panel.add(radioYesOrNo[i]);
		}
	}

	// 답을 선택 했는 지 확인. 라디오 버튼 둘 중 하나는 선택 되어야 한다.
	public boolean SelectedAnser() {
		if (radioYesOrNo[0].isSelected() == true || radioYesOrNo[1].isSelected() == true)
			return true;

		return false;
	}
}