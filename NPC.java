package entity;

import main.GamePanel;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;

public class NPC extends Entity {

    GamePanel gp;
    public String name;

    private String[] dialogue;
    private int dialogueIndex = 0;

    public NPC(GamePanel gp, int worldX, int worldY, String name, String[] dialogue) {
        this.gp = gp;
        this.worldX = worldX;
        this.worldY = worldY;
        this.name = name;
        this.dialogue = dialogue;

        solidArea = new Rectangle(8, 16, 32, 32);
        direction = "down";
        speed = 0;

        loadImages();
    }

    private void loadImages() {
        try {
            up1 = ImageIO.read(getClass().getResourceAsStream("/npc/oldman_up_1.png"));
            up2 = ImageIO.read(getClass().getResourceAsStream("/npc/oldman_up_2.png"));
            down1 = ImageIO.read(getClass().getResourceAsStream("/npc/oldman_down_1.png"));
            down2 = ImageIO.read(getClass().getResourceAsStream("/npc/oldman_down_2.png"));
            left1 = ImageIO.read(getClass().getResourceAsStream("/npc/oldman_left_1.png"));
            left2 = ImageIO.read(getClass().getResourceAsStream("/npc/oldman_left_2.png"));
            right1 = ImageIO.read(getClass().getResourceAsStream("/npc/oldman_right_1.png"));
            right2 = ImageIO.read(getClass().getResourceAsStream("/npc/oldman_right_2.png"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void update() {
        spriteCounter++;
        if (spriteCounter > 30) {
            spriteNum = (spriteNum == 1) ? 2 : 1;
            spriteCounter = 0;
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

        int screenX = worldX - gp.player.worldX + gp.player.screenX;
        int screenY = worldY - gp.player.worldY + gp.player.screenY;

        g2.drawImage(image, screenX, screenY, gp.tileSize, gp.tileSize, null);
    }

    public void resetDialogue() {
        dialogueIndex = 0;
    }

    public boolean hasMoreDialogue() {
        return dialogueIndex < dialogue.length;
    }

    public String getNextDialogue() {
        if (hasMoreDialogue()) return dialogue[dialogueIndex++];
        return null;
    }
}
