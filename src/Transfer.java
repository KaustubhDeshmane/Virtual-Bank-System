import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class Transfer extends JFrame {
    Transfer(String username) {
        Font f = new Font("Futura", Font.BOLD, 30);
        Font f2 = new Font("Calibri", Font.PLAIN, 18);

        JLabel title = new JLabel("Transfer Funds", JLabel.CENTER);
        JLabel l1 = new JLabel("Receiver:");
        JTextField t1 = new JTextField(10);

        JLabel l2 = new JLabel("Amount:");
        JTextField t2 = new JTextField(10);

        JButton b1 = new JButton("Transfer");
        JButton b2 = new JButton("Back");

        title.setFont(f);
        l1.setFont(f2);
        t1.setFont(f2);
        l2.setFont(f2);
        t2.setFont(f2);
        b1.setFont(f2);
        b2.setFont(f2);

        Container c = getContentPane();
        c.setLayout(null);

        int labelX = 200, fieldX = 400, yStart = 80, width = 150, height = 30, gap = 40;

        title.setBounds(250, 20, 300, 40);

        l1.setBounds(labelX, yStart, width, height);
        t1.setBounds(fieldX, yStart, width, height);

        l2.setBounds(labelX, yStart + gap, width, height);
        t2.setBounds(fieldX, yStart + gap, width, height);

        b1.setBounds(250, yStart + 2 * gap, 120, 40);
        b2.setBounds(400, yStart + 2 * gap, 120, 40);

        c.add(title);
        c.add(l1);
        c.add(t1);
        c.add(l2);
        c.add(t2);
        c.add(b1);
        c.add(b2);

        b2.addActionListener(a->
        {
            new Home(username);
            dispose();
        });

        b1.addActionListener(a->
        {
            String Reciver = t1.getText().trim();
            if(Reciver.equals(username))
            {
                JOptionPane.showMessageDialog(null,
                        "Cannot transfer to your own account");
                return;
            }
            String s1 = t2.getText().trim();

            if(Reciver.isEmpty() || s1.isEmpty())
            {
                JOptionPane.showMessageDialog(null,"Enter value");
                t1.setText("");
                t2.setText("");
                return;
            }

            if(!ReceiverExist(Reciver))
            {
                JOptionPane.showMessageDialog(null,"Transfer cancelled user does not Exist");
                t1.setText("");
                t2.setText("");
                return;
            }

            double amount;

            try {
                amount = Double.parseDouble(s1);
            }
            catch(NumberFormatException ex)
            {
                JOptionPane.showMessageDialog(null,
                        "Enter valid amount");
                return;
            }
            if(amount <= 0)
            {
                JOptionPane.showMessageDialog(null,
                        "Amount must be greater than 0");
                return;
            }
            double balance=fetchbalance(username);

            if(amount>balance)
            {
                JOptionPane.showMessageDialog(null,"Insufficient balance");
                t2.setText("");
                return;
            }

            //Round2

            String des="Transfer to "+Reciver;
            String url="jdbc:mysql://localhost:3306/3dec";
            try (Connection conn = DriverManager.getConnection(url, "root", "yourpassword")) {
                String sql = "update users set balance=? where username=?";
                try (PreparedStatement pst = conn.prepareStatement(sql)) {
                    pst.setDouble(1, balance-amount);
                    pst.setString(2,username);
                    pst.executeUpdate();

                    updatepassbook(username,des,-amount,balance-amount);


                }

            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, e.getMessage());
            }



            //Round 3

            String desc="Transfer from "+username;
            balance= fetchbalance(Reciver);

            //Round 4

            try (Connection conn = DriverManager.getConnection(url, "root", "yourpassword")) {
                String sql = "update users set balance=? where username=?";
                try (PreparedStatement pst = conn.prepareStatement(sql)) {

                    pst.setDouble(1, amount+balance);
                    pst.setString(2,Reciver);

                    pst.executeUpdate();
                    JOptionPane.showMessageDialog(null,"Transfer Successfully ");
                    t1.setText("");
                    t2.setText("");

                    updatepassbook(Reciver,desc,amount,amount+balance);
                }

            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, e.getMessage());
            }


        });


        setSize(800, 550);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setTitle("Transfer Funds");
        setVisible(true);
    }

    double fetchbalance(String Username)
    {
        double balance=0.0;
        String url="jdbc:mysql://localhost:3306/3dec";

        try(Connection conn = DriverManager.getConnection(url,"root","yourpassword"))
        {
            String sql= "select balance  from users where username=?";
            try (PreparedStatement pst= conn.prepareStatement(sql))
            {
                pst.setString(1,Username);

                try(ResultSet rs = pst.executeQuery())
                {
                    if(rs.next())
                    {
                        balance = rs.getDouble("balance");
                    }
                }


            }


        }
        catch(SQLException e)
        {
            JOptionPane.showMessageDialog(null,e.getMessage());
        }
        return balance;
    }

    void updatepassbook(String username,String description,double amount,double balance) {
        String url = "jdbc:mysql://localhost:3306/3dec";

        try (Connection conn = DriverManager.getConnection(url, "root", "yourpassword")) {
            String sql = "insert into transactions (username,description,amount,balance) values(?,?,?,?);";
            try (PreparedStatement pst = conn.prepareStatement(sql)) {
                pst.setString(1, username);
                pst.setString(2, description);
                pst.setDouble(3, amount);
                pst.setDouble(4, balance);

                pst.executeUpdate();
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, e.getMessage());
        }
    }
    boolean ReceiverExist(String receiver) {
        String url = "jdbc:mysql://localhost:3306/3dec";
        boolean exists = false;

        try (Connection conn = DriverManager.getConnection(url, "root", "yourpassword")) {
            String sql = "SELECT username FROM users WHERE username = ?";
            try (PreparedStatement pst = conn.prepareStatement(sql)) {
                pst.setString(1, receiver);
                try(ResultSet rs = pst.executeQuery())
                {
                    exists = rs.next();
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, e.getMessage());
        }

        return exists;
    }

    public static void main(String[] args) {
        new Transfer("kaustubh");
    }
}

