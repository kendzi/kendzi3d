/*
 * This software is provided "AS IS" without a warranty of any kind.
 * You use it on your own risk and responsibility!!!
 *
 * This file is shared under BSD v3 license.
 * See readme.txt and BSD3 file for details.
 *
 */

package kendzi.josm.kendzi3d.jogl.photos;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

public class PhotoParmPanel extends JPanel {


    private JTextField latText;
    private JTextField lonText;
    private JTextField rollText;
    private JTextField pitchText;
    private JTextField pathText;
    private JTextField yawTest;
    private JTextField angleWithtText;
    private JTextField angleHeigthText;

    public PhotoParmPanel() {
        createLayout();
    }

    void createLayout() {

        JButton cameraButton = new JButton("camera");
        cameraButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                camera();
            }
        }
        );
        setLayout(new BorderLayout(0, 0));

        textPanel = new JPanel();
        textPanel.setBorder(new EmptyBorder(2, 2, 0, 2));
        add(textPanel);
                        textPanel.setLayout(new GridLayout(0, 2, 0, 0));


                        JLabel label = new JLabel("lat");
                        textPanel.add(label);
                this.latText = new JTextField();
                textPanel.add(latText);

                        JLabel label_1 = new JLabel("lon");
                        textPanel.add(label_1);
                this.lonText = new JTextField();
                textPanel.add(lonText);

                        JLabel label_2 = new JLabel("rollText");
                        textPanel.add(label_2);
                this.rollText = new JTextField();
                textPanel.add(rollText);

                        JLabel label_3 = new JLabel("pitchText");
                        textPanel.add(label_3);
                this.pitchText = new JTextField();
                textPanel.add(pitchText);

                                JLabel label_4 = new JLabel("yawTest");
                                textPanel.add(label_4);
                        this.yawTest = new JTextField();
                        textPanel.add(yawTest);

                                JLabel label_5 = new JLabel("angleWithtText");
                                textPanel.add(label_5);
                        this.angleWithtText = new JTextField();
                        textPanel.add(angleWithtText);

                                JLabel label_6 = new JLabel("angleHeigthText");
                                textPanel.add(label_6);
                        this.angleHeigthText = new JTextField();
                        textPanel.add(angleHeigthText);

                        JLabel label_7 = new JLabel("path");
                        textPanel.add(label_7);
                this.pathText = new JTextField();
                textPanel.add(pathText);

                lblTransparent = new JLabel("Transparent");
                textPanel.add(lblTransparent);

                txtTransparent = new JTextField();
                txtTransparent.setText("50");
                textPanel.add(txtTransparent);
                txtTransparent.setColumns(10);

                                                                        buttonPanel = new JPanel();
                                                                        add(buttonPanel, BorderLayout.SOUTH);

                                                                                JButton newButton = new JButton("new");
                                                                                buttonPanel.add(newButton);

                                                                                        JButton saveButton = new JButton("save");
                                                                                        buttonPanel.add(saveButton);
                                                                                        saveButton.addActionListener(new ActionListener() {

                                                                                            @Override
                                                                                            public void actionPerformed(ActionEvent e) {
                                                                                                savePhoto();
                                                                                            }
                                                                                        }
                                                                                        );
                                                                                newButton.addActionListener(new ActionListener() {

                                                                                    @Override
                                                                                    public void actionPerformed(ActionEvent e) {
                                                                                        newPhoto();
                                                                                    }
                                                                                }
                                                                                );

    }

    void newPhoto() {
        this.latText.setText("52.141809265470116");
        this.lonText.setText("15.764873979360045");

        this.angleHeigthText.setText("30");
        this.angleWithtText.setText("60");
        this.pathText.setText("test.png");
        this.pitchText.setText("0");
        this.rollText.setText("0");
        this.yawTest.setText("0");

        this.pathText.setText("/photos/home.png");

    }


    void fillPhoto(Photo pPhoto) {
    }

    Photo savePhoto() {

        Photo photo = new Photo();

        photo.setLat(Double.parseDouble(this.latText.getText()));
        photo.setLon(Double.parseDouble(this.lonText.getText()));

        photo.setAngleHeigth(
                Math.toRadians(Double.parseDouble(this.angleHeigthText.getText())));
        photo.setAngleWitht(
                Math.toRadians(Double.parseDouble(this.angleWithtText.getText())));
        photo.setPath( this.pathText.getText());
        photo.setPitch(
                Math.toRadians(Double.parseDouble(this.pitchText.getText())));
        photo.setRoll(
                Math.toRadians(Double.parseDouble(this.rollText.getText())));
        photo.setYaw(
                Math.toRadians(Double.parseDouble(this.yawTest.getText())));
//        photo.set

        photo.setTransparent(
                (Double.parseDouble(this.txtTransparent.getText())/100d));

        PhotoChangeEvent photoChangeEvent = new PhotoChangeEvent();
        photoChangeEvent.setPhoto(photo);

        fireCameraChange(photoChangeEvent);
        return photo;

    }

    void camera() {

    }


    void fireEvent() {

    }


    List<CameraChangeListener> cameraChangeListeners = new ArrayList<CameraChangeListener>();
    private JPanel textPanel;
    private JPanel buttonPanel;
    private JLabel lblTransparent;
    private JTextField txtTransparent;

    public void addCameraChangeListener(CameraChangeListener listener)     {
         this.cameraChangeListeners.add(listener);
    }
    public void removeCameraChangeListener(CameraChangeListener listener)     {
         this.cameraChangeListeners.remove(listener);
    }
    protected void fireCameraChange(PhotoChangeEvent pPhotoChangeEvent)     {

         for (CameraChangeListener photoChangeListener : this.cameraChangeListeners) {

             // pass the event to the listeners event dispatch method
             photoChangeListener.dispatchCameraChange(pPhotoChangeEvent);

         }
    }


}
