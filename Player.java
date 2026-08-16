package entity;

import main.GamePanel;
import main.KeyHandler;
import object.SuperObject;
import java.awt.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;

public class Player extends Entity {

    GamePanel gp;
    KeyHandler keyH;
    public final int screenX, screenY;

    public boolean hasKey = false;
    public boolean gameFinished = false;

    public Player(GamePanel gp, KeyHandler keyH) {
        this.gp = gp;
        this.keyH = keyH;

        screenX = gp.screenWidth / 2 - gp.tileSize / 2;
        screenY = gp.screenHeight / 2 - gp.tileSize / 2;

        solidArea = new Rectangle(8, 16, 32, 32);

        setDefaults();
        loadImages();
    }

    private void setDefaults() {
        worldX = gp.tileSize * 23;
        worldY = gp.tileSize * 21;
        speed = 4;
        direction = "down";
    }

    private void loadImages() {
        try {
            up1 = ImageIO.read(getClass().getResourceAsStream("/player/boy_up_1.png"));
            up2 = ImageIO.read(getClass().getResourceAsStream("/player/boy_up_2.png"));
            down1 = ImageIO.read(getClass().getResourceAsStream("/player/boy_down_1.png"));
            down2 = ImageIO.read(getClass().getResourceAsStream("/player/boy_down_2.png"));
            left1 = ImageIO.read(getClass().getResourceAsStream("/player/boy_left_1.png"));
            left2 = ImageIO.read(getClass().getResourceAsStream("/player/boy_left_2.png"));
            right1 = ImageIO.read(getClass().getResourceAsStream("/player/boy_right_1.png"));
            right2 = ImageIO.read(getClass().getResourceAsStream("/player/boy_right_2.png"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void update() {
        if (gp.ui.dialogueOn || gp.ui.titleScreenOn || gameFinished) return;

        collisionOn = false;

        int objIndex = gp.cChecker.checkObject(this);
        pickUpObject(objIndex);

        gp.cChecker.checkTile(this);

        if (keyH.up) direction = "up";
        else if (keyH.down) direction = "down";
        else if (keyH.left) direction = "left";
        else if (keyH.right) direction = "right";

        if ((keyH.up || keyH.down || keyH.left || keyH.right) && !collisionOn) {
            switch (direction) {
                case "up" -> worldY -= speed;
                case "down" -> worldY += speed;
                case "left" -> worldX -= speed;
                case "right" -> worldX += speed;
            }

            spriteCounter++;
            if (spriteCounter > 12) {
                spriteNum = (spriteNum == 1) ? 2 : 1;
                spriteCounter = 0;
            }
        }
    }

    private void pickUpObject(int i) {
        if (i == 999 || gp.obj[i] == null) return;

        SuperObject obj = gp.obj[i];

        switch (obj.name) {
            case "Key" -> {
                hasKey = true;
                gp.obj[i] = null;
                gp.ui.showMessage("Key found!");
                gp.playSEFile("coin.wav"); // key sound

                // Resume gameplay music
                gp.stopMusic();
                gp.playMusicFile("MainTheme.wav", true);
            }

            case "Door" -> {
                if (hasKey) {
                    hasKey = false;
                    gp.obj[i] = null;
                    gp.ui.showMessage("Door opened!");
                    gp.playSEFile("dooropen.wav"); // door sound

                    // Play main gameplay music after opening door
                    gp.stopMusic();
                    gp.playMusicFile("MainTheme.wav", true);
                } else {
                    gp.ui.showMessage("You need a key!");
                }
            }

            case "Chest" -> {
                gp.obj[i] = null;
                gp.ui.showMessage("You found the treasure!");
                gp.playSEFile("fanfare.wav");
                gameFinished = true;

                // Play title screen music after finishing game
                gp.stopMusic();
                gp.playMusicFile("Merchant.wav", true); // same as title screen
            }
        }
    }

    public void interact() {
        if (gp.ui.dialogueOn) {
            gp.ui.nextDialogue();
            return;
        }

        Rectangle interactArea = new Rectangle(worldX + solidArea.x, worldY + solidArea.y, solidArea.width, solidArea.height);
        int range = gp.tileSize;

        switch (direction) {
            case "up" -> interactArea.y -= range;
            case "down" -> interactArea.y += range;
            case "left" -> interactArea.x -= range;
            case "right" -> interactArea.x += range;
        }

        if (gp.npc != null) {
            for (var n : gp.npc) {
                if (n == null) continue;

                Rectangle npcArea = new Rectangle(n.worldX + n.solidArea.x, n.worldY + n.solidArea.y, n.solidArea.width, n.solidArea.height);
                if (interactArea.intersects(npcArea)) {
                    gp.ui.startDialogue(n);
                    return;
                }
            }
        }
    }

    public void draw(Graphics2D g2) {
        BufferedImage image = switch (direction) {
            case "up" -> (spriteNum == 1) ? up1 : up2;
            case "down" -> (spriteNum == 1) ? down1 : down2;
            case "left" -> (spriteNum == 1) ? left1 : left2;
            case "right" -> (spriteNum == 1) ? right1 : right2;
            default -> down1;
        };
        g2.drawImage(image, screenX, screenY, gp.tileSize, gp.tileSize, null);
    }
}
