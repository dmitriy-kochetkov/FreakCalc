package com.mietdevelopers.FreakCalc;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.URL;
import java.util.ArrayList;


/**
 * Created by dmitriy on 08.12.15.
 */
public class FreakCalc {

    JFrame frame;
    JPanel mainPanel;
    JPanel tablePanel;
    JPanel selectPanel;
    JPanel headerPanel;
    JPanel totalPanel;
    JComboBox<String> combobox;
    GridLayout grid;
    JMenuBar menuBar;

    File stateFile;

    float totalBalance;

    boolean actualState;

    ArrayList<Freak> freakList;
    String[] items = {"1", "2", "3", "4", "5", "6", "7", "8"};

    private static final int MAX_FREAK_COUNT = 8;
    private static final String VERSSION = "v0.1.3";

    public static void main(String[] args) {
        FreakCalc fc = new FreakCalc();
        fc.go();
    }

    public void go() {

        try {
            UIManager.setLookAndFeel(
                    UIManager.getSystemLookAndFeelClassName());
        }
        catch (Exception e) {
            System.out.println("Something wrong with system GUI.");
        }

        stateFile = new File("state.fcl");

        loadState(stateFile);

        if (freakList.size() == 0) {
            freakList.add(new Freak("Борис", 0.0f));
            freakList.add(new Freak("Вован", 0.0f));
            freakList.add(new Freak("Диман", 0.0f));
        }

        totalBalance = 0.0f;

        buildGUI();
    }

    public void saveState(File file) {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(file));

            for(Freak freak : freakList) {
                writer.write(freak.getName() + "/");
                writer.write(freak.getPayment() + "\n");
            }
            writer.close();

        } catch (Exception ex) {
            ex.printStackTrace();
            System.out.println("Save state error");
        }
    }

    public void loadState(File file) {
        freakList = new ArrayList<>();
        String line;

        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            while ((line = reader.readLine()) != null) {
                makeFreak(line);
            }
            reader.close();
        } catch (FileNotFoundException ex) {
            System.out.println("First application start");
        }catch (Exception ex) {
            ex.printStackTrace();
            System.out.println("Couldn't read the state file");
        }
    }

    private void makeFreak(String lineToParse) {
        String[] result = lineToParse.split("/");

        if(result.length != 2)
            return;

        if(freakList.size() == MAX_FREAK_COUNT)
            return;

        try {
            Freak freak = new Freak(result[0], Float.parseFloat(result[1]));
            freakList.add(freak);
        } catch (NumberFormatException ex) {
            System.out.println("Payment format error");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void buildGUI() {

        frame = new JFrame("FreakCalc");
        frame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);

        frame.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                //saveState(stateFile);
                //System.exit(0);
                exit();
            }
        });

        frame.setIconImage(getImageIcon("icon.png").getImage());

        selectPanel = new JPanel(new GridLayout(1,2));

        JLabel label = new JLabel("Num of freaks");
        label.setHorizontalAlignment(SwingConstants.CENTER);

        combobox = new JComboBox<>(items);
        combobox.addActionListener(new NumChangedListener());

        selectPanel.add(label);
        selectPanel.add(combobox);

        mainPanel = new JPanel(new BorderLayout());
        headerPanel = new JPanel(new GridLayout(1,3));

        JLabel nameLabel = new JLabel("Name");
        JLabel paymentLabel = new JLabel("Payment");
        JLabel balanceLabel = new JLabel("Balance");

        nameLabel.setHorizontalAlignment(SwingConstants.CENTER);
        paymentLabel.setHorizontalAlignment(SwingConstants.CENTER);
        balanceLabel.setHorizontalAlignment(SwingConstants.CENTER);

        headerPanel.add(nameLabel);
        headerPanel.add(paymentLabel);
        headerPanel.add(balanceLabel);

        mainPanel.add(BorderLayout.NORTH, headerPanel);
        grid = new GridLayout(freakList.size(), 3);
        tablePanel = new JPanel(grid);

        getTotalBalance();

        for (int i=0; i<freakList.size(); i++) {
            Freak freak = freakList.get(i);

            JTextField name = new JTextField(freak.getName());
            JTextField payment = new JTextField(freak.getPayment() + "");
            JTextField balance = new JTextField(totalBalance / freakList.size() + " ");
            balance.setEditable(false);

            payment.addActionListener(new PaymentChangedListener());

            tablePanel.add(name);
            tablePanel.add(payment);
            tablePanel.add(balance);
        }

        totalPanel = new JPanel(new GridLayout(1,2));
        JLabel totalLabel = new JLabel("Total: ");
        totalLabel.setHorizontalAlignment(SwingConstants.RIGHT);

        JTextField totalField = new JTextField();
        totalField.setText(totalBalance + "");
        totalField.setEditable(false);

        totalPanel.add(totalLabel);
        totalPanel.add(totalField);

        mainPanel.add(BorderLayout.CENTER, tablePanel);

        menuBar = new JMenuBar();
        JMenu fileMenu = new JMenu("File");
        JMenuItem saveItem = new JMenuItem("Save state");
        JMenuItem exitItem = new JMenuItem("Exit");

        saveItem.addActionListener(new SaveListener());
        exitItem.addActionListener(new ExitListener());

        fileMenu.add(saveItem);
        fileMenu.add(exitItem);

        JMenu helpMenu = new JMenu("Help");
        JMenuItem aboutMenuItem = new JMenuItem("About FreakCalc");

        aboutMenuItem.addActionListener(new AboutListener());

        helpMenu.add(aboutMenuItem);

        menuBar.add(fileMenu);
        menuBar.add(helpMenu);

        frame.setJMenuBar(menuBar);

        frame.getContentPane().add(BorderLayout.NORTH, selectPanel);
        frame.getContentPane().add(BorderLayout.SOUTH, totalPanel);
        frame.getContentPane().add(BorderLayout.CENTER, mainPanel);

        combobox.setSelectedIndex(freakList.size() - 1);

        frame.setBounds(50, 50, 300, 400);
        frame.setVisible(true);
    }

    public void getTotalBalance() {
        totalBalance = 0.0f;

        for(Freak freak : freakList) {
            totalBalance += freak.getPayment();
        }
    }

    public void saveFreakList() {

        for( int i=0; i < freakList.size(); i++)
        {
            JTextField name    = (JTextField) tablePanel.getComponent(i*3);
            JTextField payment = (JTextField) tablePanel.getComponent(i*3 + 1);

            Freak freak = freakList.get(i);
            freak.setName(name.getText());
            freak.setPayment(Float.parseFloat(payment.getText()));
        }
    }

    public void updateTable() {
        actualState = false;

        int num = Integer.parseInt((String) combobox.getSelectedItem());
        int size = freakList.size();

        saveFreakList();

        if ( num > size ) {

            for (int i = size; i < num; i++) {

                Freak freak = new Freak( "Freak" + (i+1) , 0.0f);
                freakList.add(freak);
            }
        }
        else if (num < size)
        {
            for (int i = size; i >num; i--) {
                freakList.remove(i-1);
            }
        }

        getTotalBalance();

        grid = new GridLayout(freakList.size(), 3);

        tablePanel.removeAll();
        tablePanel.setLayout(grid);

        StringBuilder sBalanceBuilder = new StringBuilder();

        for (int i=0; i<freakList.size(); i++) {
            Freak freak = freakList.get(i);

            float fBalance = freak.getPayment() - (totalBalance / freakList.size());
            fBalance = round(fBalance, 2);

            sBalanceBuilder.delete(0, sBalanceBuilder.length());

            if (fBalance > 0)
                sBalanceBuilder.append("+");

            sBalanceBuilder.append(fBalance);

            JTextField name = new JTextField(freak.getName());
            JTextField payment = new JTextField(freak.getPayment() + "");
            JTextField balance = new JTextField(sBalanceBuilder.substring(0, sBalanceBuilder.length()));

            payment.addActionListener(new PaymentChangedListener());
            balance.setEditable(false);

            Color bg = (fBalance >= 0) ? new Color(191, 255, 182) : new Color(255, 131, 125) ;

            balance.setBackground(bg);

            tablePanel.add(name);
            tablePanel.add(payment);
            tablePanel.add(balance);
        }

        totalPanel.removeAll();

        JLabel totalLabel = new JLabel("Total: ");
        totalLabel.setHorizontalAlignment(SwingConstants.RIGHT);

        JTextField totalField = new JTextField();
        totalField.setText(totalBalance + "");
        totalField.setEditable(false);

        totalPanel.add(totalLabel);
        totalPanel.add(totalField);

        mainPanel.add(BorderLayout.CENTER, tablePanel);

        frame.getContentPane().add(BorderLayout.SOUTH, totalPanel);
        frame.getContentPane().add(BorderLayout.CENTER, mainPanel);
        frame.validate();
    }

    public class NumChangedListener implements ActionListener {
        public void actionPerformed(ActionEvent a) {
            updateTable();
        }
    }

    public class PaymentChangedListener implements ActionListener {
        public void actionPerformed(ActionEvent a) {
            updateTable();
        }
    }

    private float round(float number, int scale) {
        int pow = 10;
        for (int i = 1; i < scale; i++)
            pow *= 10;
        float tmp = number * pow;
        return (float) (int) ((tmp - (int) tmp) >= 0.5f ? tmp + 1 : tmp) / pow;
    }

    public ImageIcon getImageIcon(String path) {
        URL imgURL = FreakCalc.class.getResource(path);

        if (imgURL != null) {
            return new ImageIcon(imgURL);
        } else {
            System.err.println("File not found " + path);
            return null;
        }
    }

    public class AboutListener implements ActionListener {
        public void actionPerformed(ActionEvent a) {
            ImageIcon icon = getImageIcon("icon.png");
            String msg = "FreakCalc " + VERSSION;

            JLabel iconLabel = new JLabel(icon);
            JPanel iconPanel = new JPanel(new GridBagLayout());
            iconPanel.add(iconLabel);

            JPanel textPanel = new JPanel(new GridLayout(0, 1));

            Font smallFont = new Font("arial", Font.BOLD, 8);
            Font boldFont = new Font("arial", Font.BOLD, 12);

            JLabel webLable = new JLabel("www.miet-developers.com");
            JLabel titleLable = new JLabel(msg);

            webLable.setFont(smallFont);
            titleLable.setFont(boldFont);

            textPanel.add(new JLabel(" "));
            textPanel.add(titleLable);
            textPanel.add(new JLabel(" "));
            textPanel.add(new JLabel("Simple java application   "));
            textPanel.add(new JLabel("for calculating freaks   "));
            textPanel.add(new JLabel("expenses."));
            textPanel.add(new JLabel(" "));
            textPanel.add(new JLabel(" "));
            textPanel.add(new JLabel("_______________________"));
            textPanel.add(webLable);

            JPanel aboutPanel = new JPanel(new BorderLayout());
            aboutPanel.add(BorderLayout.WEST, iconPanel);
            aboutPanel.add(BorderLayout.CENTER, textPanel);

            JOptionPane.showMessageDialog(
                    null,
                    aboutPanel,
                    "About FreakCalc",
                    JOptionPane.PLAIN_MESSAGE);
        }
    }

    public class SaveListener implements ActionListener {
        public void actionPerformed(ActionEvent a) {
            actualState = true;
            saveState(stateFile);
        }
    }

    public class ExitListener implements ActionListener {
        public void actionPerformed(ActionEvent a) {
            exit();
        }
    }

    public void exit() {

        if(!actualState) {

            int dialogButton = JOptionPane.YES_NO_OPTION;
            int dialogResult = JOptionPane.showConfirmDialog(frame, "Save current state?", "Save", dialogButton);
            if(dialogResult == 0) {
                saveState(stateFile);
            }
        }

        System.exit(0);
    }
}
