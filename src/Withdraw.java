import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class Withdraw extends JFrame
{
    Withdraw(String username)
    {
        Font f = new Font("Futura", Font.BOLD, 40);
        Font f2 = new Font("Calibri", Font.PLAIN, 22);

        JLabel title = new JLabel("Withdraw Money", JLabel.CENTER);
        JLabel label = new JLabel("Enter Amount:");
        JTextField t1 = new JTextField(10);
        JButton b1 = new JButton("Withdraw");
        JButton b2 = new JButton("Back");

        title.setFont(f);
        label.setFont(f2);
        t1.setFont(f2);
        b1.setFont(f2);
        b2.setFont(f2);

        Container c = getContentPane();
        c.setLayout(null);

        title.setBounds(200, 30, 400, 50);
        label.setBounds(250, 120, 300, 30);
        t1.setBounds(250, 160, 300, 30);
        b1.setBounds(300, 220, 200, 40);
        b2.setBounds(300, 280, 200, 40);

        c.add(title);
        c.add(label);
        c.add(t1);
        c.add(b1);
        c.add(b2);

        b2.addActionListener(a->
        {
         new Home(username);
         dispose();
        });

        b1.addActionListener(a->
        {
            double balance=0.0;
            double wlimit=0.0;
            double result=0.0;

            String url="jdbc:mysql://localhost:3306/3dec";

            try(Connection conn = DriverManager.getConnection(url,"root","yourpassword"))
            {
                String sql = "select balance, wlimit from users where username=?";
                try (PreparedStatement pst= conn.prepareStatement(sql))
                {
                    pst.setString(1,username);

                    try(ResultSet rs = pst.executeQuery())
                    {
                        if(rs.next())
                        {
                            balance = rs.getDouble("balance");
                            wlimit = rs.getDouble("wlimit");
                        }
                    }


                }

            }
            catch(SQLException e)
            {
                JOptionPane.showMessageDialog(null,e.getMessage());
            }
            String amountText = t1.getText().trim();

            if(amountText.isEmpty())
            {
                JOptionPane.showMessageDialog(null,"Enter amount");
                return;
            }

            double Newamount;

            try
            {
                Newamount = Double.parseDouble(amountText);
                if(Newamount <= 0)
                {
                    JOptionPane.showMessageDialog(null,
                            "Amount must be greater than 0");
                    return;
                }
            }
            catch(NumberFormatException ex)
            {
                JOptionPane.showMessageDialog(null,"Enter valid number");
                return;
            }

                if(Newamount>balance)
                {
                    JOptionPane.showMessageDialog(null,"Insufficient Balance");
                }

                else if(Newamount > wlimit)
                {
                    JOptionPane.showMessageDialog(null,"Limit Exceeded");
                }
                else {
                    result = balance - Newamount;

                    String des="Withdraw";

                    try (Connection conn = DriverManager.getConnection(url, "root", "yourpassword")) {
                        String sql = "update users set BALANCE=? where username=?";
                        try (PreparedStatement pst = conn.prepareStatement(sql)) {
                            pst.setDouble(1, result);
                            pst.setString(2, username);
                            pst.executeUpdate();
                            JOptionPane.showMessageDialog(null, "Successfully Withdrawn");
                            t1.setText("");

                            updatepassbook(username,des,Newamount,result);
                        }

                    } catch (Exception e) {
                        JOptionPane.showMessageDialog(null, e.getMessage());
                    }
                }


        });




        setSize(800, 550);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setTitle("Withdraw Money");
        setVisible(true);
    }

    void updatepassbook(String username,String description,double amount,double balance)
    {
        String url="jdbc:mysql://localhost:3306/3dec";

        try(Connection conn = DriverManager.getConnection(url,"root","yourpassword"))
        {
            String sql= "insert into transactions (username,description,amount,balance) values(?,?,?,?);";
            try (PreparedStatement pst= conn.prepareStatement(sql)) {
                pst.setString(1, username);
                pst.setString(2, description);
                pst.setDouble(3, -amount);
                pst.setDouble(4, balance);

                pst.executeUpdate();
            }

        }
        catch(SQLException e)
        {
            JOptionPane.showMessageDialog(null,e.getMessage());
        }

    }

    public static void main(String[] args) {
        new Withdraw("kaustubh");
    }
}

