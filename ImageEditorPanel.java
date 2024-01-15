import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.File;

import javax.imageio.ImageIO;
import java.awt.*;
import javax.swing.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class ImageEditorPanel extends JPanel implements KeyListener {

    Color[][] pixels;
    
    public ImageEditorPanel() {
        BufferedImage imageIn = null;
        try {
            imageIn = ImageIO.read(new File(pickPicture()));
        } catch (IOException e) {
            System.out.println(e);
            System.exit(1);
        }
        pixels = makeColorArray(imageIn);
        setPreferredSize(new Dimension(pixels[0].length, pixels.length));
        setBackground(Color.BLACK);
        addKeyListener(this);
    }

    public void paintComponent(Graphics g) {
        // paints the array pixels onto the screen
        for (int row = 0; row < pixels.length; row++) {
            for (int col = 0; col < pixels[0].length; col++) {
                g.setColor(pixels[row][col]);
                g.fillRect(col, row, 1, 1);
            }
        }
    }

    public Color[][] makeColorArray(BufferedImage image) {
        int width = image.getWidth();
        int height = image.getHeight();
        Color[][] result = new Color[height][width];
        
        for (int row = 0; row < height; row++) {
            for (int col = 0; col < width; col++) {
                Color c = new Color(image.getRGB(col, row), true);
                result[row][col] = c;
            }
        }
        // System.out.println("Loaded image with parameters...\nWidth: " + width + " Height: " + height);
        return result;
    }

    public String pickPicture() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setCurrentDirectory(new File(System.getProperty("user.home")));
        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            System.out.println("Selected file: " + selectedFile.getAbsolutePath());
            return selectedFile.getAbsolutePath();
        }
        System.out.println("ERROR: No File Provided!");
        System.exit(0);
        return null;
        // followed method found here: https://www.codejava.net/java-se/swing/show-simple-open-file-dialog-using-jfilechooser
    }
    
    public String savePicture() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setCurrentDirectory(new File(System.getProperty("user.home")));
        int result = fileChooser.showSaveDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            System.out.println("Saving image to: " + selectedFile.getAbsolutePath());
            return selectedFile.getAbsolutePath();
        }
        return null;
    }

    public void saveImage() {
        String dir = savePicture();
        if (dir != null) {
            BufferedImage bufferedImage = new BufferedImage(pixels.length, pixels[0].length,
            BufferedImage.TYPE_INT_RGB);    
            for (int x = 0; x < pixels.length; x++) {
                for (int y = 0; y < pixels[x].length; y++) {
                    bufferedImage.setRGB(x, y, pixels[y][x].getRGB());
                }
            }
            // done via https://stackoverflow.com/questions/13391404/create-image-from-2d-color-array
            
            File outputfile = new File(dir);
            try {
                ImageIO.write(bufferedImage, "jpg", outputfile);
                System.out.println("Successfully saved your image!");
            } catch (IOException e) {
                System.out.println("There was an issue saving your image!");
                e.printStackTrace();
            }
            // done via https://stackoverflow.com/questions/12674064/how-to-save-a-bufferedimage-as-a-file
        } else {
            System.out.println("Save image process interrupted by user!");
        }
    }

    public Color[][] flipHorizontal(Color[][] oldImg) {
        Color[][] newImg = new Color[oldImg.length][oldImg[0].length];
        for (int row = 0; row < oldImg.length; row++) {
            int regCol = 0;
            for (int col = oldImg[0].length - 1; col > -1; col--) {
                newImg[row][regCol] = oldImg[row][col];
                regCol++;
            }
        }
        return newImg;
    }

    public Color[][] flipVertical(Color[][] oldImg) {
        Color[][] newImg = new Color[oldImg.length][oldImg[0].length];
        int regRow = 0;
        for (int row = oldImg.length - 1; row > -1; row--) {
            for (int col = 0; col < oldImg[0].length; col++) {
                newImg[regRow][col] = oldImg[row][col];
            }
            regRow++;
        }
        return newImg;
    }

    public Color[][] greyscale(Color[][] oldImg) {
        Color[][] newImg = new Color [oldImg.length][oldImg[0].length];
        for (int row = 0; row < oldImg.length; row++) {
            for (int col = 0; col < oldImg[0].length; col++) {
                Color c = oldImg[row][col];
                int grey = ((c.getRed() + c.getGreen() + c.getBlue()) / 3);
                newImg[row][col] = new Color(grey, grey, grey);
            }
        }
        return newImg;
    }

    public Color[][] blur(Color[][] oldImg) {
        Color[][] newImg = new Color [oldImg.length][oldImg[0].length];
        for (int row = 0; row < oldImg.length; row++) {
            for (int col = 0; col < oldImg[0].length; col++) {
                // add up all rgb averages here
                int pixelCount = 0;
                int redAvg = 0;
                int greenAvg = 0;
                int blueAvg= 0;
                final int gridsize = 3;
                // focus area of a prefered gridsize
                for (int row2 = row - gridsize; row2 < row + gridsize; row2++) {
                    for (int col2 = col - gridsize; col2 < col + gridsize; col2++) {
                        if (row2 > 0 && col2 > 0 && row2 < newImg.length && col2 < newImg[0].length) {
                            Color c = oldImg[row2][col2];
                            redAvg += c.getRed();
                            greenAvg += c.getGreen();
                            blueAvg += c.getBlue();
                            pixelCount++;
                        }
                    }
                }
                redAvg /= pixelCount;
                greenAvg /= pixelCount;
                blueAvg /= pixelCount;
                newImg[row][col] = new Color (redAvg, greenAvg, blueAvg);
            }
        }
        return newImg;
    }
    
    public Color[][] rotate90(Color[][] oldImg) {
        Color[][] newImg = new Color [oldImg[0].length][oldImg.length];
        for (int row = 0; row < oldImg[0].length; row++) {
            for (int col = 0; col < oldImg.length; col++) {
                newImg[col][oldImg.length - 1 - row] = oldImg[row][col];
            }
        }
        return newImg;
    }

    public Color[][] rotate270(Color[][] oldImg) {
        Color[][] newImg = new Color [oldImg.length][oldImg[0].length];
        newImg = rotate90(oldImg);
        newImg = rotate90(newImg);
        newImg = rotate90(newImg);
        return newImg;
    }

    public Color[][] upContrast(Color[][] oldImg) {
        Color[][] newImg = new Color [oldImg.length][oldImg[0].length];
        for (int row = 0; row < oldImg.length; row++) {
            for (int col = 0; col < oldImg[0].length; col++) {
                Color c = oldImg[row][col];
                int[] rgb = {c.getRed(), c.getGreen(), c.getBlue()};
                for (int i = 0; i < rgb.length; i++) {
                    if (rgb[i] >= 128) {
                        rgb[i] *= 1.4;
                    } else {
                        rgb[i] *= 0.8;
                    }
                }
                for (int i = 0; i < rgb.length; i++) {
                    if (rgb[i] > 255) {
                        rgb[i] = 255;
                    } else if (rgb[i] < 0){
                        rgb[i] = 0;
                    }
                }
                newImg[row][col] = new Color (rgb[0], rgb[1], rgb[2]);
            }
        }
        return newImg;
    }
    
    public Color[][] downContrast(Color[][] oldImg) {
        Color[][] newImg = new Color [oldImg.length][oldImg[0].length];
        for (int row = 0; row < oldImg.length; row++) {
            for (int col = 0; col < oldImg[0].length; col++) {
                Color c = oldImg[row][col];
                int[] rgb = {c.getRed(), c.getGreen(), c.getBlue()};
                for (int i = 0; i < rgb.length; i++) {
                    if (rgb[i] >= 128) {
                        rgb[i] *= 0.8;
                    } else {
                        rgb[i] *= 1.4;
                    }
                }
                for (int i = 0; i < rgb.length; i++) {
                    if (rgb[i] > 255) {
                        rgb[i] = 255;
                    } else if (rgb[i] < 0){
                        rgb[i] = 0;
                    }
                }
                newImg[row][col] = new Color (rgb[0], rgb[1], rgb[2]);
            }
        }
        return newImg;
    }

    @Override
    public void keyTyped(KeyEvent e) {
        if (e.getKeyChar() == 'q') {
            System.out.println("Closing Application...");
            System.exit(0);
        }
        if (e.getKeyChar() == 'h') {
            pixels = flipHorizontal(pixels);
        }
        if (e.getKeyChar() == 'v') {
            pixels = flipVertical(pixels);
        }
        if (e.getKeyChar() == 'g') {
            pixels = greyscale(pixels);
        }
        if (e.getKeyChar() == 'b') {
            pixels = blur(pixels);
        }
        if (e.getKeyChar() == 'o') {
            pixels = upContrast(pixels);
        }
        if (e.getKeyChar() == 'p') {
            pixels = downContrast(pixels);
        }
        if (e.getKeyChar() == 'r') {
            pixels = rotate90(pixels);
            setPreferredSize(new Dimension(pixels[0].length,pixels.length));
            JFrame jf = (JFrame)SwingUtilities.getAncestorOfClass(JFrame.class, this);
            jf.pack();

        }
        if (e.getKeyChar() == 't') {
            pixels = rotate270(pixels);
            setPreferredSize(new Dimension(pixels[0].length,pixels.length));
            JFrame jf = (JFrame)SwingUtilities.getAncestorOfClass(JFrame.class, this);
            jf.pack();
        }
        if (e.getKeyChar() == 's') {
            saveImage();
        }
        repaint();
    }

    @Override
    public void keyPressed(KeyEvent e) {
        // unused method
    }

    @Override
    public void keyReleased(KeyEvent e) {
        // unused method
    }
}