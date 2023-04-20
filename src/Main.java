import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.Buffer;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;

public class Main {

    public static final String OUTPUT_PATH = "Output Images/";
    /**
     * In pixels
     */
    public static final int REASONABLE_IMAGE_SIZE = 50;

    private static String greyScale = "$@B%8&WM#*oahkbdpqwmZO0QLCJUYXzcvunxrjft/\\|()1{}[]?-_+~<>i!lI;:,\"^`'.       ";

    public static void main(String[] args) {
        BufferedImage image;
        PrintWriter writer = null;
        File output = new File(OUTPUT_PATH + "output.txt");
        try {
            writer = new PrintWriter(new FileWriter(output));
            String path = selectImage();
            if (path == null || (image = readImage(path)) == null) {
                System.err.println("Image could not be read");
                System.exit(1);
                return;
            }

            // If image is too big, rescale to a reasonable size
            if (image.getHeight() > REASONABLE_IMAGE_SIZE) {
                image = scaleImage(image);
            }
            int avg;
            for (int i = 0; i < image.getHeight(); i++) {
                for (int j = 0; j < image.getWidth(); j++) {
                    avg = getPixelColorAverage(image.getRGB(j, i));
                    int charPosition = (int) Math.round(getCharToUse(avg));
                    char usedChar = greyScale.charAt(charPosition);
                    writer.print(usedChar);
                }
                writer.println();
            }
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            writer.close();
        }
        System.exit(0);
    }

    private static int getPixelColorAverage(int rgb) {
        Color color = new Color(rgb);
        int red = color.getRed();
        int blue = color.getBlue();
        int green = color.getGreen();
        return (red + blue + green) / 3;
    }

    private static double getCharToUse(int brightness) {
        double charPosition = 69 * (brightness / 255.0);
        return charPosition;
    }

    private static BufferedImage readImage(String name) {
        BufferedImage image = null;
        try {
            FileInputStream IO = new FileInputStream(name);
            image = ImageIO.read(IO);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return image;
    }

    private static BufferedImage scaleImage(BufferedImage image) {
        double scaleFactor = (double) REASONABLE_IMAGE_SIZE / image.getHeight();
        Image newImage = image.getScaledInstance((int) Math.round(image.getWidth() * scaleFactor), REASONABLE_IMAGE_SIZE, Image.SCALE_DEFAULT);
        return toBuffered(newImage);
    }

    private static BufferedImage toBuffered(Image image) {
        if (image instanceof BufferedImage)
            return (BufferedImage) image;

        BufferedImage buffered = new BufferedImage(image.getWidth(null), image.getHeight(null), BufferedImage.TYPE_INT_ARGB);

        Graphics2D bufferredGraphics = buffered.createGraphics();
        bufferredGraphics.drawImage(image, 0, 0, null);
        bufferredGraphics.dispose();

        return buffered;
    }

    private static String selectImage() {
        FileDialog fileChooser = new FileDialog(new Frame());
        FilenameFilter filter = new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                String extension = name.substring(name.length() - 4);
                if (extension == ".jpg" || extension == ".png") return true;
                return false;
            }
        };
        fileChooser.setFilenameFilter(filter);
        fileChooser.setDirectory("Source Images");
        fileChooser.setVisible(true);
        String file = fileChooser.getDirectory() + fileChooser.getFile();
        System.out.println(file);
        return file;
    }
}