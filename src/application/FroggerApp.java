package application;
	
import java.time.LocalTime;
import javafx.animation.AnimationTimer;
import javafx.animation.FadeTransition;
import javafx.animation.TranslateTransition;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;


public class FroggerApp extends Application {
	private Pane root;
	private AnimationTimer timer;
	private LocalTime startTime, endTime;
	private ObservableList<Node> cars = FXCollections.observableArrayList();
	private Node frog;
	//FadeTransition fadeOut;
	private Boolean isAlive = false;
	
	public Parent createContent(){
		startTime = LocalTime.now();
		isAlive = true;
		root = new Pane();
		root.setPrefSize(800, 600);
		root.setBackground(new Background(
				new BackgroundFill(Color.DARKSLATEGREY, new CornerRadii(5.0), new Insets(0, 10, 0, 20))
				));
		frog = initFrog();
		
		root.getChildren().add(frog);
		timer = new AnimationTimer() {
			
			private long lastUpdate = 0 ;
			@Override
			public void handle(long arg0) {
				//onUpdate();
				//Throttling the update method by 50ms using if condition
				if (arg0 - lastUpdate >= 50_000_000) {
                    onUpdate();
                    lastUpdate = arg0 ;
                }
			}
		};
		timer.start();
		
		return root;
	}
	
	private Node initFrog(){
		Rectangle rect = new Rectangle(38, 38, Color.BLACK);
		rect.setArcHeight(100);
		rect.setArcWidth(100);
		rect.setTranslateX(20);
		rect.setTranslateY(600-39);
		//rect.setOpacity(0.2);
		blinking(rect);
		
		return rect;
	}
	
	private void blinking(Node obj){
		FadeTransition fadeOut = new FadeTransition(Duration.millis(500), obj);
		fadeOut.setCycleCount(TranslateTransition.INDEFINITE);
		fadeOut.setFromValue(1.0);
		fadeOut.setToValue(0.4);
		fadeOut.setAutoReverse(true);
		fadeOut.play();
	}
	
	private Node spawnCar(){
		Rectangle rect = new Rectangle(40, 40, new Color(Math.random()%245, Math.random()%255, Math.random()%255, 1.0));
		rect.setArcHeight(20);
		rect.setArcWidth(20);
		rect.setTranslateX(20);
		rect.setTranslateY((int)(Math.random() * 14) * 40);
		root.getChildren().add(rect);
		return rect;
	}
	
	private void onUpdate(){
//		if(frog == null)
//			frog = initFrog();
		
		/*for (int i=0; i< cars.size(); i++){
			// Accessing the index position in "cars" list
			Node car = cars.get(i);
			if(car.getTranslateX() > 800){
				cars.remove(car);
			}
		}
		
		// for removing the car from cars list
		for(Node car : cars){
			if(car.getTranslateX() > 800){
				int index = cars.indexOf(car);
				cars.remove(index);
			}		
		}*/
		
		// for moving the cars in horizontally right
		for(Node car : cars){
			Platform.runLater(new Runnable() {
				@Override
				public void run() {
					if(car.getTranslateX() > 850)
						cars.remove(car);	
				}
			});
			car.setTranslateX(car.getTranslateX() + Math.random() * 16);
			//Math.random() * 20
			if(car.getTranslateX()+40 >= 790)
				car.setVisible(false);
		}
		
		System.out.println("Total number of Cars in Array is: " + cars.size() + "\n");
		
		if(Math.random() < 0.25){
			cars.add(spawnCar());
		}
		checkState();
	}
	
	private void checkState(){
		for(Node car: cars){
			
			 //Bounds objCar = car.localToScene(car.getBoundsInLocal());
		     //Bounds objFrog = frog.localToScene(frog.getBoundsInLocal());
			
			//condition for game over
			if(car.getBoundsInParent().intersects(frog.getBoundsInParent())){
		     //if(objCar.intersects(objFrog)){
			//collision detection logic
//			if((frog.getTranslateX() + 38 >car.getTranslateX() &&
//				frog.getTranslateX()<car.getTranslateX()+40)
//				&& ((int)car.getTranslateY() == (int)frog.getTranslateY() + 1)){
				
//				frog.setTranslateX(20);
//				frog.setTranslateY(600-39);
				//initFrog();
				//System.out.println("Game Over");
				
				blinking(car);
				//System.out.println("X value: "+car.getTranslateX()+" Y value: "+car.getTranslateY());
				timer.stop();
				isAlive = false;
				gameOverOrsucceed();
			}
		}
		//condition for winning the game
		if(frog.getTranslateY() <= 0){
			timer.stop();
			isAlive = true;
			gameOverOrsucceed();
			endTime = LocalTime.now();
			//System.out.println(startTime.getSecond() + " " + endTime.getSecond());
			calculateTimeDiiferenceAndShow();
		}
	}
	
	private void calculateTimeDiiferenceAndShow(){
		int minutes = Math.abs(endTime.getMinute() - startTime.getMinute());
		int seconds = Math.abs(endTime.getSecond() - startTime.getSecond());
		Text timeDiff = new Text("Required Time\n        " + minutes + " : " +
				seconds +"\n  to complete.");
		timeDiff.setFont(Font.font(20));
		timeDiff.setTranslateX(350);
		timeDiff.setTranslateY(100);
		root.getChildren().add(timeDiff);
	}
	
	public void gameOverOrsucceed(){
		frog.setVisible(false);
		
		HBox hBox = new HBox();
		Button playBtn = makeButton("Play Again");
		Button exit = makeButton("Quit");
		
		playBtn.setTranslateX(340);
		playBtn.setTranslateY(250);
		playBtn.setCursor(Cursor.HAND);
		playBtn.setOnMousePressed(e->{
			playBtn.setCursor(Cursor.CLOSED_HAND);
		});
		exit.setTranslateX(340);
		exit.setTranslateY(380);
		exit.setCursor(Cursor.HAND);
		exit.setBackground(new Background(new BackgroundFill(Color.DARKRED, new CornerRadii(100), null)));
		exit.setOnMousePressed(e->{
			exit.setCursor(Cursor.CLOSED_HAND);
			Platform.exit();
		});
		blinking(playBtn);
		playBtn.setOnMouseClicked(e->{
			isAlive = true;
			root.getChildren().clear();
			// you have to remove all car from the car array.
			// Other wise it contains some hidden car that causes collision.
			cars.removeAll(cars);
			//frog = initFrog();
			root.getChildren().add(frog);
			frog.setTranslateX(20);
			frog.setTranslateY(600-39);
			frog.setVisible(true);
			timer.start();
			startTime = LocalTime.now();
			return;
		});
		if(isAlive)
			hBox.setTranslateX(350);
		else
			hBox.setTranslateX(320);
		hBox.setTranslateY(300);
		root.getChildren().add(hBox);
		root.getChildren().add(playBtn);
		root.getChildren().add(exit);
		
		String winOrLoss = isAlive? "You Win" : "Game Over";
		for(int i=0; i< winOrLoss.toCharArray().length; i++){
			char letter = winOrLoss.charAt(i);
			Text text = new Text(String.valueOf(letter));
			text.setFont(Font.font(48));
			text.setOpacity(0);
			hBox.getChildren().add(text);
			FadeTransition ft = new FadeTransition(Duration.seconds(0.66), text);
			ft.setToValue(1);
			ft.setDelay(Duration.seconds(i * 0.15));
			ft.play();
		}
	}
	
	private Button makeButton(String btnTitle){
		Button btn = new Button(btnTitle);
		btn.setTextFill(Color.BURLYWOOD);
		btn.setBackground(new Background(new BackgroundFill(Color.web("#3f3f3f", 0.8), new CornerRadii(10.0), null)));
		btn.setFont(Font.font(STYLESHEET_CASPIAN, FontWeight.EXTRA_BOLD, 20));
		btn.setPrefSize(200,50);
		return btn;
	}
	
	@Override
	public void start(Stage primaryStage) {
		primaryStage.setScene(new Scene(createContent()));
		
		primaryStage.getScene().setOnKeyPressed(event->{
			KeyCode code = event.getCode();
			if(code == KeyCode.UP){
				frog.setTranslateY(frog.getTranslateY() - 40);
			}else if(code == KeyCode.DOWN){
				if(frog.getTranslateY() < (600-39))
					frog.setTranslateY(frog.getTranslateY() + 40);
			}else if(code == KeyCode.LEFT){
				if(frog.getTranslateX() > 20)
					frog.setTranslateX(frog.getTranslateX() - 40);
			}else if(code == KeyCode.RIGHT){
				if(frog.getTranslateX() < 740)
					frog.setTranslateX(frog.getTranslateX() + 40);
			}
				
			
//			switch(event.getCode()) {
//				case I:
//					frog.setTranslateY(frog.getTranslateY() - 40);
//					break;
//				case K:
//					if(frog.getTranslateY() < (600-39))
//						frog.setTranslateY(frog.getTranslateY() + 40);
//					break;
//				case J:
//					if(frog.getTranslateX() > 20)
//						frog.setTranslateX(frog.getTranslateX() - 40);
//					break;
//				case L:
//					if(frog.getTranslateX() < 740)
//						frog.setTranslateX(frog.getTranslateX() + 40);
//					break;
//				default:
//				break;
//			}
		});
		
		primaryStage.setTitle("Frogger Game");
		
		primaryStage.setResizable(false);
		primaryStage.sizeToScene();
		primaryStage.show();
	}
	
	public static void main(String[] args) {
		launch(args);
	}
}

