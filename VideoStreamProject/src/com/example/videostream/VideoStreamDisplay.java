package com.example.videostream;

import javafx.application.Application;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.Scene;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.videoio.VideoCapture;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;

public class VideoStreamDisplay extends Application {
    static { System.loadLibrary(Core.NATIVE_LIBRARY_NAME); }

    @Override
    public void start(Stage stage) {
        ImageView imageView = new ImageView();
        WritableImage writableImage = new WritableImage(640, 480);

        VideoCapture camera = new VideoCapture(0);
        Mat frame = new Mat();

        new Thread(() -> {
            while (camera.read(frame)) {
                BufferedImage image = MatToBufferedImage(frame);
                SwingFXUtils.toFXImage(image, writableImage);
                imageView.setImage(writableImage);
            }
            camera.release();
        }).start();

      
        StackPane root = new StackPane(imageView);
        Scene scene = new Scene(root, 640, 480);

        stage.setScene(scene);
        stage.setTitle("Real-Time Video Stream");
        stage.show();
    }

    private BufferedImage MatToBufferedImage(Mat mat) {
       
        int width = mat.width();
        int height = mat.height();
        int channels = mat.channels();

        byte[] sourcePixels = new byte[width * height * channels];
        mat.get(0, 0, sourcePixels); // Get all the pixels from the Mat

        BufferedImage image;
        if (channels == 3) {

            image = new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR);
            final byte[] targetPixels = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
            System.arraycopy(sourcePixels, 0, targetPixels, 0, sourcePixels.length);
        } else if (channels == 1) {
           
            image = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY);
            final byte[] targetPixels = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
            System.arraycopy(sourcePixels, 0, targetPixels, 0, sourcePixels.length);
        } else {
            throw new IllegalArgumentException("Unsupported number of channels: " + channels);
        }

        return image;
    }


    public static void main(String[] args) {
        launch(args);
    }
}
