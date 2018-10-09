import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

import java.util.List;
import java.util.stream.Collectors;

public class SpaceInvaders extends Application {

    private double time;
    private boolean moveLeft = false;
    private boolean moveRight = false;
    private Pane root = new Pane();

    private Sprite player = new Sprite(300, 750, 40, 40, "player", Color.BLUE);



    private Parent createContent(){
        root.setPrefSize(600, 800);

        root.getChildren().add(player);

        AnimationTimer timer = new AnimationTimer(){
            @Override
            public void handle(long now){
                update();
            }
        };

        timer.start();

        nextLevel();

        return root;
    }

    private void nextLevel(){
        for(int i = 0; i < 5; i++){
            Sprite s = new Sprite(90 + i * 100, 150, 30, 30, "enemy", Color.RED);

            root.getChildren().add(s);
        }
    }

    private List<Sprite> sprites(){
        return root.getChildren().stream().map(n -> (Sprite) n ).collect(Collectors.toList());
    }

    private void update(){
        time += 0.016;

        sprites().forEach(s -> {
            switch (s.type){
                case "player":
                    if(moveLeft)
                        player.moveLeft();
                    if(moveRight)
                        player.moveRight();
                    break;
                case "enemybullet":
                    s.moveDown();
                    if(s.getBoundsInParent().intersects(player.getBoundsInParent())){
                        player.dead = true;
                        s.dead = true;
                    }
                    break;
                case "playerbullet":
                    s.moveUp();
                    sprites().stream().filter(e -> e.type.equals("enemy")).forEach( enemy -> {
                        if(s.getBoundsInParent().intersects(enemy.getBoundsInParent())) {
                            enemy.dead = true;
                            s.dead = true;
                        }
                    });
                    break;
                case "enemy":

                    if(time > 1){
                        if(Math.random() < 0.3){
                            shoot(s);
                        }
                    }
                    break;
            }
        });
        root.getChildren().removeIf(n -> {
            Sprite s = (Sprite) n;
            return s.dead;
        });

        if(time > 1){
            time = 0;
        }
    }

    private void shoot(Sprite who){
        Sprite s = new Sprite((int) who.getTranslateX() + 20,(int) who.getTranslateY(), 5, 20, who.type + "bullet", Color.BLACK);

        root.getChildren().add(s);
    }


    @Override
    public void start(Stage stage) {
        Scene scene = new Scene(createContent());

        scene.setOnKeyPressed(e -> {
            switch(e.getCode()){
                case A:
                    moveLeft = true;
                    break;
                case D:
                    moveRight = true;
                    break;
                case SPACE:
                    shoot(player);
                    break;
            }
        });
        scene.setOnKeyReleased(e -> {
            switch (e.getCode()){
                case A:
                    moveLeft = false;
                    break;
                case D:
                    moveRight = false;
                    break;
            }
        });

        stage.setScene(scene);
        stage.show();
    }

    private static class Sprite extends Rectangle{
        final String type;
        boolean dead = false;

        Sprite(int x, int y, int w, int h, String type, Color color){
            super(w, h, color);
            this.type = type;
            setTranslateX(x);
            setTranslateY(y);
        }

        void moveLeft(){
            setTranslateX(getTranslateX() - 5);
        }
        void moveRight(){
            setTranslateX(getTranslateX() + 5);
        }
        void moveUp(){
            setTranslateY(getTranslateY() - 5);
        }
        void moveDown(){
            setTranslateY(getTranslateY() + 5);
        }
    }


    public static void main(String[] args) {
        System.setProperty("quantum.multithreaded", "false");
        Application.launch(SpaceInvaders.class, args);
    }
}
