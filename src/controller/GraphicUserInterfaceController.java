/**~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
* @author Nicol�s Penagos Montoya
* nicolas.penagosm98@gmail.com
**~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
*/
package controller;

import java.util.ArrayList;
import java.util.Stack;

import customThreads.GUIUpdateControlThread;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import model.Logic;
import model.ModelCircle;
import model.Person;

public class GraphicUserInterfaceController {

	// -------------------------------------
	// Constants
	// -------------------------------------
	public final static double GRAPHIC_SIZE = 78.0;
	public final static double GRAPHIC_BAR_SIZE = 7.0;

	// -------------------------------------
	// FXML
	// -------------------------------------
	@FXML
	private Pane pane;

	@FXML
	private Pane graphicPane;

	@FXML
	private Slider sliderInfectedPeople;

	@FXML
	private Slider sliderHealthyPeople;

	@FXML
	private Slider sliderRecoveredPeople;

	@FXML
	private TextField iPtxtField;

	@FXML
	private TextField hPtxtField;

	@FXML
	private TextField rPtxtField;

	@FXML
	private Label totalPeopleLabel;

	@FXML
	private Label recoveredPeopleLabel;

	@FXML
	private Label healthyPeopleLabel;

	@FXML
	private Label infectedPeopleLabel;

	@FXML
	private Label timeLabel;

	@FXML
	private Label dayLabel;
	
    @FXML
    private Label graphicTotalPeopleLabel;

    @FXML
    private Label graphicHalfPeopleLabel;

	@FXML
	private CheckBox maskChB;

	@FXML
	private CheckBox n95MaskChB;

	@FXML
	private CheckBox glovesChB;

	@FXML
	private CheckBox gownChB;

	@FXML
	private CheckBox handWashingChB;

	@FXML
	private CheckBox allCombinedChB;

	@FXML
	private ImageView playImageView;

	@FXML
	private ImageView stopImageView;

	@FXML
	private ImageView pauseImageView;

	// -------------------------------------
	// Atributtes
	// -------------------------------------
	private Logic logic;
	private boolean playButtonAble;
	private boolean pauseButtonAble;
	private boolean stopButtonAble;
	private boolean removeAllCircles;
	private boolean windowLaunched;
	private boolean pause;
	private Image playButtonOn;
	private Image playButtonOff;
	private Image stopButtonOn;
	private Image stopButtonOff;
	private Image pauseButtonOn;
	private Image pauseButtonOff;
	private Image pausePlayButton;

	private ArrayList<Circle> circles;
	private ArrayList<ModelCircle> people;
	private AlertBox alertBox;
	private Stack<Rectangle> graphic;

	// -------------------------------------
	// Constructor
	// -------------------------------------
	public GraphicUserInterfaceController() {

		logic = new Logic();
		playButtonAble = false;
		pauseButtonAble = false;
		stopButtonAble = false;
		removeAllCircles = false;
		setWindowLaunched(false);
		setPause(false);
		GUIUpdateControlThread guiThread = new GUIUpdateControlThread(this);
		guiThread.setDaemon(true);
		guiThread.start();
		circles = new ArrayList<Circle>();
		people = (ArrayList<ModelCircle>) logic.getPeople();
		graphic = new Stack<Rectangle>();
		
	}

	// -------------------------------------
	// Methods
	// -------------------------------------
	@FXML
	public void initialize() {

		disableTextFields();
		addSlidersEventHandlers();
		alertBox = new AlertBox();

		playButtonOn = new Image("/images/playButtonOn.png");
		playButtonOff = new Image("/images/playButtonOff.png");
		stopButtonOn = new Image("/images/stopButtonOn.png");
		stopButtonOff = new Image("/images/stopButtonOff.png");
		pauseButtonOn = new Image("/images/pauseButtonOn.png");
		pauseButtonOff = new Image("/images/pauseButtonOff.png");
		pausePlayButton = new Image("/images/pausePlayButton.png");

	}

	@FXML
	void pauseButtonClicked(MouseEvent event) {

		if (pauseButtonAble) {

			playButtonAble = false;
			stopButtonAble = true;

			if (!isPause()) {

				pauseImageView.setImage(pausePlayButton);
				pause = true;
				logic.getChronometer().setPause(true);
				logic.setPause(true);

			} else {

				pauseImageView.setImage(pauseButtonOn);
				pause = false;
				logic.setPause(false);
				logic.getChronometer().setPause(false);

			}

		} else {
			pauseImageView.setImage(pauseButtonOff);
		}

	}

	@FXML
	void playButtonClicked(MouseEvent event) {

		if (playButtonAble) {

			stopButtonAble = true;
			pauseButtonAble = true;

			if (playButtonAble) {

				ableConfigButtons(false);
				pause = false;
				logic.setPause(false);
				playImageView.setImage(playButtonOff);
				stopImageView.setImage(stopButtonOn);
				pauseImageView.setImage(pauseButtonOn);
				logic.startMovementThread();
				ableConfigButtons(false);
				logic.setProbability();
				logic.loadPeople();
				loadCircles();
				playButtonAble = false;
				logic.startChronometerThread(this);
				logic.setSimulationEnded(false);
				logic.getChronometer().setPause(false);
				setWindowLaunched(false);
				
			    graphicTotalPeopleLabel.setText(""+logic.getTotalPeople());
			    graphicHalfPeopleLabel.setText(""+(logic.getTotalPeople()/2));
		
			} else {

				playImageView.setImage(playButtonOn);
				pauseImageView.setImage(pauseButtonOff);

			}
		}

	}

	@FXML
	void stopButtonClicked(MouseEvent event) {

		if (stopButtonAble) {

			playButtonAble = true;
			stopButtonAble = false;
			pauseButtonAble = false;

			playImageView.setImage(playButtonOn);
			stopImageView.setImage(stopButtonOff);
			pauseImageView.setImage(pauseButtonOff);
			logic.setPeople(new ArrayList<ModelCircle>());
			removeAllCircles = true;
			logic.killMovThread();
			logic.setOption(-1);
			ableConfigButtons(true);

			sliderHealthyPeople.setValue(0);
			sliderInfectedPeople.setValue(0);
			sliderRecoveredPeople.setValue(0);

			logic.setHealthyPeople(0);
			logic.setInfectedPeople(0);
			logic.setRecoveredPeople(0);
			iPtxtField.setText("");
			rPtxtField.setText("");
			hPtxtField.setText("");
			updateTotalPeople();
			disableButton();

			handWashingChB.setSelected(false);
			maskChB.setSelected(false);
			n95MaskChB.setSelected(false);
			glovesChB.setSelected(false);
			gownChB.setSelected(false);
			allCombinedChB.setSelected(false);

			logic.killChronometerThread();
			logic.getChronometer().reStart();
			
			graphicPane.getChildren().clear();

			graphicTotalPeopleLabel.setText("");
		    graphicHalfPeopleLabel.setText("");
		}

	}

	@FXML
	void frequendHandwashing(ActionEvent event) {

		if (handWashingChB.isSelected()) {

			maskChB.setDisable(true);
			n95MaskChB.setDisable(true);
			glovesChB.setDisable(true);
			gownChB.setDisable(true);
			allCombinedChB.setDisable(true);
			logic.setOption(Logic.OPTION_FHW);

		} else {

			maskChB.setDisable(false);
			n95MaskChB.setDisable(false);
			glovesChB.setDisable(false);
			gownChB.setDisable(false);
			allCombinedChB.setDisable(false);
			logic.setOption(-1);

		}

	}

	@FXML
	void mask(ActionEvent event) {

		if (maskChB.isSelected()) {

			handWashingChB.setDisable(true);
			n95MaskChB.setDisable(true);
			glovesChB.setDisable(true);
			gownChB.setDisable(true);
			allCombinedChB.setDisable(true);
			logic.setOption(Logic.OPTION_M);

		} else {

			handWashingChB.setDisable(false);
			n95MaskChB.setDisable(false);
			glovesChB.setDisable(false);
			gownChB.setDisable(false);
			allCombinedChB.setDisable(false);
			logic.setOption(-1);

		}

	}

	public void toGraph(int min, int sec) {
		
		int t;
		
		if(min == 0) {
			t = sec;
		}else {
			t = min*60 + sec;
		}
		
		double iF = ((double)logic.getInfectedPeople() * GRAPHIC_SIZE / (double)logic.getTotalPeople());
		
		System.out.println(iF);
		
		Rectangle rIf = new Rectangle();
		rIf.setWidth(GRAPHIC_BAR_SIZE);
		rIf.setHeight(iF);
		rIf.setX(GRAPHIC_BAR_SIZE*t+1.5);
		rIf.setY(GRAPHIC_SIZE-iF);
		rIf.setFill(Color.SALMON);
		
		graphic.push(rIf);
		
		double hE = ((double)logic.getHealthyPeople() * GRAPHIC_SIZE / (double)logic.getTotalPeople());
		
		Rectangle rHe = new Rectangle();
		rHe.setWidth(GRAPHIC_BAR_SIZE);
		rHe.setHeight(hE);
		rHe.setX(GRAPHIC_BAR_SIZE*t+1.5);
		rHe.setY(GRAPHIC_SIZE-iF-hE);
		rHe.setFill(Color.MEDIUMSEAGREEN);
		
		graphic.push(rHe);
		
		double rE = ((double)logic.getRecoveredPeople() * GRAPHIC_SIZE / (double)logic.getTotalPeople());
		
		Rectangle rRe = new Rectangle();
		rRe.setWidth(GRAPHIC_BAR_SIZE);
		rRe.setHeight(rE);
		rRe.setX(GRAPHIC_BAR_SIZE*t+1.5);
		rRe.setY(GRAPHIC_SIZE-iF-hE-rE);
		rRe.setFill(Color.STEELBLUE);
		
		graphic.push(rRe);
	
		
	
	}

	public void exit() {

		programmaticallyStopButton();
		Stage stage = (Stage) pane.getScene().getWindow();
		stage.close();

	}

	public void saveCVS() {

		System.out.println("SaveCVS");
		programmaticallyStopButton();
	}

	@FXML
	void n95Mask(ActionEvent event) {

		if (n95MaskChB.isSelected()) {

			handWashingChB.setDisable(true);
			maskChB.setDisable(true);
			glovesChB.setDisable(true);
			gownChB.setDisable(true);
			allCombinedChB.setDisable(true);
			logic.setOption(Logic.OPTION_N95M);

		} else {

			handWashingChB.setDisable(false);
			maskChB.setDisable(false);
			glovesChB.setDisable(false);
			gownChB.setDisable(false);
			allCombinedChB.setDisable(false);
			logic.setOption(-1);

		}

	}

	@FXML
	void gloves(ActionEvent event) {

		if (glovesChB.isSelected()) {

			handWashingChB.setDisable(true);
			maskChB.setDisable(true);
			n95MaskChB.setDisable(true);
			gownChB.setDisable(true);
			allCombinedChB.setDisable(true);
			logic.setOption(Logic.OPTION_GLVS);

		} else {

			handWashingChB.setDisable(false);
			maskChB.setDisable(false);
			n95MaskChB.setDisable(false);
			gownChB.setDisable(false);
			allCombinedChB.setDisable(false);
			logic.setOption(-1);

		}

	}

	@FXML
	void gown(ActionEvent event) {

		if (gownChB.isSelected()) {

			handWashingChB.setDisable(true);
			maskChB.setDisable(true);
			n95MaskChB.setDisable(true);
			glovesChB.setDisable(true);
			allCombinedChB.setDisable(true);
			logic.setOption(Logic.OPTION_GWN);

		} else {

			handWashingChB.setDisable(false);
			maskChB.setDisable(false);
			n95MaskChB.setDisable(false);
			glovesChB.setDisable(false);
			allCombinedChB.setDisable(false);
			logic.setOption(-1);

		}

	}

	@FXML
	void allCombined(ActionEvent event) {

		if (allCombinedChB.isSelected()) {

			handWashingChB.setDisable(true);
			maskChB.setDisable(true);
			n95MaskChB.setDisable(true);
			glovesChB.setDisable(true);
			gownChB.setDisable(true);
			logic.setOption(Logic.OPTION_WALL);

		} else {

			handWashingChB.setDisable(false);
			maskChB.setDisable(false);
			n95MaskChB.setDisable(false);
			glovesChB.setDisable(false);
			gownChB.setDisable(false);
			logic.setOption(-1);

		}

	}

	public void update() {

		updateLabels();
		draw();

	}

	public void updateLabels() {

		infectedPeopleLabel.setText("" + logic.getInfectedPeople());
		healthyPeopleLabel.setText("" + logic.getHealthyPeople());
		recoveredPeopleLabel.setText("" + logic.getRecoveredPeople());
		timeLabel.setText(logic.getChronometer().getTime());
		dayLabel.setText("" + (logic.getChronometer().getSec() + logic.getChronometer().getMin() * 60));

		if (logic.isSimulationEnded()) {

			pauseImageView.setImage(pauseButtonOff);

			if (!isWindowLaunched()) {
				launchAlertBox();
			}

		}

	}

	public void launchAlertBox() {
		alertBox.display("Notification", "THE SIMULATION IS OVER", this);
	}

	public void loadCircles() {

		people = (ArrayList<ModelCircle>) logic.getPeople();

		for (int i = 0; i < people.size(); i++) {

			ModelCircle current = people.get(i);

			double centerX = (double) current.getPosX();
			double centerY = (double) current.getPosY();
			double radius = (double) current.getRadius();

			Color color = Color.MEDIUMSEAGREEN;

			if (current.getHealthCondition() == Person.INFECTED) {
				color = Color.SALMON;
			} else if (current.getHealthCondition() == Person.RECOVERED) {
				color = Color.STEELBLUE;
			}

			Circle circleJfx = new Circle(centerX, centerY, radius, color);
			circles.add(circleJfx);
			pane.getChildren().add(circleJfx);

		}

	}

	public void draw() {

		for (int i = 0; i < circles.size(); i++) {

			Circle current = circles.get(i);
			double currentX = (double) people.get(i).getPosX();
			double currentY = (double) people.get(i).getPosY();

			checkColor(current, people.get(i));
			current.setCenterX(currentX);
			current.setCenterY(currentY);

		}

		if (removeAllCircles) {

			for (int i = 0; i < circles.size(); i++) {

				Circle current = circles.get(i);
				pane.getChildren().remove(current);

			}

			circles = new ArrayList<Circle>();
			removeAllCircles = false;

		}
		
		totalPeopleLabel.toFront();
			
		while(!graphic.empty()) {
			graphicPane.getChildren().add((Rectangle)graphic.pop());
		}
		
			

	}

	public void checkColor(Circle circle, ModelCircle modelCircle) {

		if (modelCircle.getHealthCondition() == Person.INFECTED) {
			circle.setFill(Color.SALMON);
		} else if (modelCircle.getHealthCondition() == Person.HEALTHY) {
			circle.setFill(Color.MEDIUMSEAGREEN);
		} else {
			circle.setFill(Color.STEELBLUE);
		}

	}

	public void changeTextF1Handler(MouseEvent e) {

		int infectedPeople = (int) sliderInfectedPeople.getValue();
		iPtxtField.setText("" + infectedPeople);
		logic.setInfectedPeople(infectedPeople);
		updateTotalPeople();
		disableButton();

	}

	public void changeTextF2Handler(MouseEvent e) {

		int healthyPeople = (int) sliderHealthyPeople.getValue();
		hPtxtField.setText("" + healthyPeople);
		logic.setHealthyPeople(healthyPeople);
		updateTotalPeople();
		disableButton();

	}

	public void ableConfigButtons(boolean able) {

		sliderInfectedPeople.setDisable(!able);
		sliderHealthyPeople.setDisable(!able);
		sliderRecoveredPeople.setDisable(!able);
		handWashingChB.setDisable(!able);
		maskChB.setDisable(!able);
		n95MaskChB.setDisable(!able);
		glovesChB.setDisable(!able);
		gownChB.setDisable(!able);
		allCombinedChB.setDisable(!able);

	}

	public void changeTextF3Handler(MouseEvent e) {

		int recoveredPeople = (int) sliderRecoveredPeople.getValue();
		rPtxtField.setText("" + recoveredPeople);
		logic.setRecoveredPeople(recoveredPeople);
		updateTotalPeople();
		disableButton();

	}

	public void disableTextFields() {

		iPtxtField.setEditable(true);
		hPtxtField.setEditable(false);
		rPtxtField.setEditable(false);

	}

	public void addSlidersEventHandlers() {

		sliderInfectedPeople.addEventHandler(MouseEvent.MOUSE_DRAGGED, this::changeTextF1Handler);
		sliderHealthyPeople.addEventHandler(MouseEvent.MOUSE_DRAGGED, this::changeTextF2Handler);
		sliderRecoveredPeople.addEventHandler(MouseEvent.MOUSE_DRAGGED, this::changeTextF3Handler);

	}

	public void updateTotalPeople() {
		totalPeopleLabel.setText("" + logic.getTotalPeople());
	}

	public void disableButton() {

		if (logic.getTotalPeople() >= 1) {

			playImageView.setImage(playButtonOn);
			playButtonAble = true;

		} else {

			playImageView.setImage(playButtonOff);
			playButtonAble = false;
		}

	}

	public void programmaticallyStopButton() {

		playButtonAble = true;
		stopButtonAble = false;
		pauseButtonAble = false;

		playImageView.setImage(playButtonOn);
		stopImageView.setImage(stopButtonOff);
		pauseImageView.setImage(pauseButtonOff);
		logic.setPeople(new ArrayList<ModelCircle>());
		removeAllCircles = true;
		logic.killMovThread();
		logic.setOption(-1);
		ableConfigButtons(true);

		sliderHealthyPeople.setValue(0);
		sliderInfectedPeople.setValue(0);
		sliderRecoveredPeople.setValue(0);

		logic.setHealthyPeople(0);
		logic.setInfectedPeople(0);
		logic.setRecoveredPeople(0);
		iPtxtField.setText("");
		rPtxtField.setText("");
		hPtxtField.setText("");
		updateTotalPeople();
		disableButton();

		handWashingChB.setSelected(false);
		maskChB.setSelected(false);
		n95MaskChB.setSelected(false);
		glovesChB.setSelected(false);
		gownChB.setSelected(false);
		allCombinedChB.setSelected(false);

		logic.killChronometerThread();
		logic.getChronometer().reStart();
		
		graphicPane.getChildren().clear();

		graphicTotalPeopleLabel.setText("");
	    graphicHalfPeopleLabel.setText("");
	}
	

	public boolean isPause() {
		return pause;
	}

	public void setPause(boolean pause) {
		this.pause = pause;
	}

	public boolean isWindowLaunched() {
		return windowLaunched;
	}

	public void setWindowLaunched(boolean windowLaunched) {
		this.windowLaunched = windowLaunched;
	}

}
