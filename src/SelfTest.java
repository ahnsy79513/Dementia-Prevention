import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Font;
import javax.swing.*;
import javax.swing.border.Border;
import org.json.simple.JSONObject;
import java.awt.event.*;

// �ڰ����� ȭ���� �����ϸ� ó���Ѵ�.
public class SelfTest extends JPanel implements ActionListener {

	private final int WARN_MESSAGE_POINT = 7; // 7�� �̻��̸� �����ü� ������ �����Ѵ�.
	private JLabel labelTitle;

	private JButton btnNext; // 1���������� 2�������� �̵���Ű�� ��ư
	private JButton btnSubmit; // ����� ������ ���� �ϰ� ��� �������� �̵���Ű�� ��ư
	private JButton btnOK; // �޴� ȭ������ �̵�

	private CardLayout cardLayout;
	private JPanel selfTestPage1; // 1������
	private JPanel selfTestPage2; // 2������
	private JPanel resultPage; // ��� ������

	private JPanel panelHead; // ȭ�� ���
	private JPanel panelBody; // ȭ�� �ϴ�

	private QuestionItem[] questions; // ���� (15��)
	private JTextArea textResult; // �ڰ����� ����� �ؽ�Ʈ�� ���
	private MainWindow mainWindow;

	public SelfTest(MainWindow main) {
		mainWindow = main;

		Font fontTitle = new Font("����", Font.PLAIN, 30);
		Font fontText = new Font("����", Font.PLAIN, 15);

		this.setLayout(null);

		int headPanelY = (int) (ConfigDatas.WIN_HEIGHT * 0.15f);
		panelHead = new JPanel();
		panelHead.setSize(ConfigDatas.WIN_WIDTH, headPanelY);
		panelHead.setBounds(0, 0, ConfigDatas.WIN_WIDTH, headPanelY);
		panelHead.setLayout(null);
		add(panelHead);

		labelTitle = new JLabel("�ڰ� ���� �׽�Ʈ");
		labelTitle.setFont(fontTitle);
		labelTitle.setBounds(230, 50, 300, 50);
		panelHead.add(labelTitle);

		int bodyPanelY = (int) (ConfigDatas.WIN_HEIGHT * 0.85f);
		panelBody = new JPanel();
		panelBody.setSize(ConfigDatas.WIN_WIDTH, bodyPanelY);
		panelBody.setBounds(0, headPanelY, ConfigDatas.WIN_WIDTH, bodyPanelY);
		add(panelBody);

		cardLayout = new CardLayout(0, 0);
		panelBody.setLayout(cardLayout); // ī�� ���̾ƿ����� ó��. 2�������� �� �������� ��ȯ�ϸ� ��� �Ѵ�.

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

		// ����, ������ 15�� ������.
		initQuestions();

		btnNext = new JButton("����");
		btnNext.setFont(fontText);
		btnNext.addActionListener(this);
		btnNext.setBounds(310, 555, 100, 35);
		selfTestPage1.add(btnNext);

		btnSubmit = new JButton("�Ϸ�");
		btnSubmit.setFont(fontText);
		btnSubmit.addActionListener(this);
		btnSubmit.setBounds(310, 555, 100, 35);
		selfTestPage2.add(btnSubmit);

		btnOK = new JButton("Ȯ��");
		btnOK.setFont(fontText);
		btnOK.addActionListener(this);
		btnOK.setBounds(310, 555, 100, 35);
		resultPage.add(btnOK);

		setSize(ConfigDatas.WIN_WIDTH, ConfigDatas.WIN_HEIGHT);
		this.setVisible(true); // ��� ó�� ���� �� �ٽ� �г��� �����ش�.
	}

	private void initQuestions() {

		Font fontText = new Font("����", Font.PLAIN, 20);
		questions = new QuestionItem[15];

		int number = 0;

		// 1Page selfTestPage1 ���
		QuestionItem q1 = new QuestionItem(1, selfTestPage1, number, "�ڽ��� ���¿� ������ �ִٰ� �����Ͻʴϱ�? ", fontText);
		questions[number++] = q1;

		QuestionItem q2 = new QuestionItem(1, selfTestPage1, number, "�ڽ��� ������ 10�� ������ �������ٰ� �����Ͻʴϱ�? ", fontText);
		questions[number++] = q2;

		QuestionItem q3 = new QuestionItem(1, selfTestPage1, number, "�ڽ��� ������ ���� �Ƿ��� �ٸ� ����鿡 ���� ���ڴٰ� �����Ͻʴϱ�?", fontText);
		questions[number++] = q3;

		QuestionItem q4 = new QuestionItem(1, selfTestPage1, number, "���� ���Ϸ� ���� �ϻ��Ȱ�� ������ �����ʴϱ�? ", fontText);
		questions[number++] = q4;

		QuestionItem q5 = new QuestionItem(1, selfTestPage1, number, "�ֱٿ� �Ͼ ���� ����ϴ� ���� ��ƽ��ϱ�? ", fontText);
		questions[number++] = q5;

		QuestionItem q6 = new QuestionItem(1, selfTestPage1, number, "��ĥ ���� ���� ��ȭ ������ ����ϱ� ��ƽ��ϱ�? ", fontText);
		questions[number++] = q6;

		QuestionItem q7 = new QuestionItem(1, selfTestPage1, number, "��ĥ ���� �� ����� ����ϱ� ��ƽ��ϱ�? ", fontText);
		questions[number++] = q7;

		// 2Page selfTestPage2 ���
		QuestionItem q8 = new QuestionItem(2, selfTestPage2, number, "ģ�� ����� �̸��� ����ϱ� ��ƽ��ϱ�? ", fontText);
		questions[number++] = q8;

		QuestionItem q9 = new QuestionItem(2, selfTestPage2, number, "���� �� ���� ����ϱ� ��ƽ��ϱ�? ", fontText);
		questions[number++] = q9;

		QuestionItem q10 = new QuestionItem(2, selfTestPage2, number, "������ ���� ������ ���� �Ҿ�����ϱ�? ", fontText);
		questions[number++] = q10;

		QuestionItem q11 = new QuestionItem(2, selfTestPage2, number, "�� ��ó���� ���� ���� ���� �ֽ��ϱ�? ", fontText);
		questions[number++] = q11;

		QuestionItem q12 = new QuestionItem(2, selfTestPage2, number, "���Կ��� 2-3���� ������ ����� �� �� �����̸��� ����ϱ� ��ƽ��ϱ�? ", fontText);
		questions[number++] = q12;

		QuestionItem q13 = new QuestionItem(2, selfTestPage2, number, "�������̳� ����� ���� ���� ����ϱ� ��ƽ��ϱ�? ", fontText);
		questions[number++] = q13;

		QuestionItem q14 = new QuestionItem(2, selfTestPage2, number, "���� ����ϴ� ��ȭ��ȣ(�ڽ� Ȥ�� �ڳ��� ��)�� ����ϱ� ��ƽ��ϱ�? ", fontText);
		questions[number++] = q14;

		QuestionItem q15 = new QuestionItem(2, selfTestPage2, number, "���� ���� ���ĸ޴��� �������� �ʽ��ϱ�? ", fontText);
		questions[number++] = q15;
	}

	public void actionPerformed(ActionEvent e) {
		JButton actionBtn = (JButton) e.getSource();

		if (actionBtn == btnNext) { // ���� ������

			for (int i = 0; i < questions.length; ++i) {
				if (questions[i].page != 1)
					continue;

				if (questions[i].SelectedAnser() == false) {
					MessageBoxUtil.ShowMessageBox("���� ���� �������� ���� ������ �ֽ��ϴ�.", "�˸�");
					return;
				}
			}

			cardLayout.show(panelBody, "page2");
			System.out.println("������ ��ȯ");

		} else if (actionBtn == btnSubmit) { // �Ϸ�, ������ ��� ����
			for (int i = 0; i < questions.length; ++i) {
				if (questions[i].page != 1)
					continue;

				if (questions[i].SelectedAnser() == false) {
					MessageBoxUtil.ShowMessageBox("���� ���� �������� ���� ������ �ֽ��ϴ�.", "�˸�");
					return;
				}
			}

			// ������ ��� ���� �� ��� �������� ��ȯ
			SubmitToServer();

		} else if (actionBtn == btnOK) { // ��� ���������� OK ��ư ������.
			this.mainWindow.ShowHomeMenu(); // ���� ȭ������ �̵�
		}
	}

	// ������ ��� ����
	private void SubmitToServer() {
		System.out.println("������ ��� ����!");

		// ��� ������ ���鼭 yes
		int sum = 0;
		for (int i = 0; i < questions.length; ++i) {
			if (questions[i].radioYesOrNo[0].isSelected() == true) {
				++sum; // ������ ���Ѵ�.
			}
		}

		// ����� ������ ����
		TestResultSendToServer(sum);

		// ��� ȭ�� ��ȯ
		cardLayout.show(panelBody, "resultPage");

		String result = "���� ������ " + sum + "�Դϴ�.\n\n";

		// 7�� �̻��̸� ���Ǽҳ� ġ���������� �����ü� ������ ���� �Ѵ�.
		if (sum >= WARN_MESSAGE_POINT) {
			result += "�߾�ġ�ż���\n";
			result += "https://www.nid.or.kr/main/main.aspx \n";
			result += "��ȭ 1666-0921";

			textResult.setText(result);
		} else {
			result += "��� �ܺ� ��ȸ Ȱ���� ���� �Ͻð�,\n";
			result += "ġ�� ���� ��Ģ 333�� �� ��õ�ϼż� ġ�Ÿ� �����ϼ���.";
			textResult.setText(result);
		}
	}

	// ������ ������ ����
	public void TestResultSendToServer(int point) {
		// ���� ����
		JSONObject json = new JSONObject();
		json.put("point", point);
		mainWindow.sendMessage(EMessageCode.eSelfTestPointUpdateRequest, json);
	}
}

class QuestionItem {
	public JLabel labelQustion; // Text ���� ����
	public JRadioButton[] radioYesOrNo; // ��/�ƴϿ� ���� ��ư
	public int number; // ������ ��ȣ
	public int page; // 1������, 2������ ����.

	// ������ ���ο��� ���� ��ư �� ������ �ؽ�Ʈ�� ǥ���� ���� ��ǥ�� ���� �Ѵ�.
	public QuestionItem(int p, JPanel panel, int num, String question, Font font) {
		number = num;
		page = p;

		int y = (num * 70) - ((p - 1) * 490);

		labelQustion = new JLabel((num + 1) + ". " + question); // ��ȣ�� ������ ���δ�.
		labelQustion.setFont(font);
		labelQustion.setBounds(100, y, 600, 35); // ���� �ؽ�Ʈ ��ǥ
		panel.add(labelQustion);

		radioYesOrNo = new JRadioButton[2];
		ButtonGroup group = new ButtonGroup();

		for (int i = 0; i < radioYesOrNo.length; i++) {
			radioYesOrNo[i] = new JRadioButton(i == 0 ? "��" : "�ƴϿ�");
			group.add(radioYesOrNo[i]);

			if (i == 0) {
				radioYesOrNo[i].setBounds(100, y + 35, 100, 35); // '��' ���� ��ư ��ǥ
			} else {
				radioYesOrNo[i].setBounds(200, y + 35, 100, 35); // '�ƴϿ� ���� ��ư ��ǥ
			}

			panel.add(radioYesOrNo[i]);
		}
	}

	// ���� ���� �ߴ� �� Ȯ��. ���� ��ư �� �� �ϳ��� ���� �Ǿ�� �Ѵ�.
	public boolean SelectedAnser() {
		if (radioYesOrNo[0].isSelected() == true || radioYesOrNo[1].isSelected() == true)
			return true;

		return false;
	}
}