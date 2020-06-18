package gui.apps.service.attack.hacked;

import information.Information;
import items.Device;
import gui.util.Panel;

import javax.swing.*;
import java.awt.*;

public class HackedDevicePane extends Panel {

    Device device;

    JLabel name = new JLabel();
    JLabel uuid = new JLabel();
    JButton connect = new JButton("connect");

    public HackedDevicePane(Device device){
        this.device = device;
        init();
    }

    private void init(){
        this.setLayout(new BorderLayout());
        this.setHeight(30);

        name.setText(device.getName() + "------>");
        uuid.setText(device.getUuid());
        connect.addActionListener(actionEvent -> Information.Desktop.startTerminal(device));

        this.add(name, BorderLayout.WEST);
        this.add(uuid, BorderLayout.CENTER);
        this.add(connect, BorderLayout.EAST);
    }

    public void openTerminal(){
        if(device.isOnline())
            Information.Desktop.startTerminal(device);
        else
            JOptionPane.showInternalMessageDialog(Information.Desktop, "This Device is not online");

    }
}
