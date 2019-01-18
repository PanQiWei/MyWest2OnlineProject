import java.sql.*;
import java.util.Scanner;

public class Main {
    public static final String DBDRIVER="com.mysql.cj.jdbc.Driver";
    public static final String DBURL="jdbc:mysql://localhost:3306/users?useSSL=false&useUnicode=true&characterEncoding=UTF-8&serverTimezone=UTC";
    public static final String DBUSER="root";
    public static final String DBPASS="pqw990804";

    public static void main(String[] args){
        Scanner scanner=new Scanner(System.in);
        scanner.useDelimiter("\n");
        try {
            Class.forName(DBDRIVER);
        }catch (ClassNotFoundException e){
            e.printStackTrace();
        }
        Reg register=new Reg();
        Log loger=new Log();
        loop:while (true){
            System.out.println("************潘其威的首页************");
            System.out.println("请选择操作：  A.登陆  B.注册  C.退出");
            String choice=scanner.next();
            switch (choice){
                case "A":
                case "a":
                    try {
                        loger.login();
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    break ;
                case "B":
                case "b":
                    try {
                        register.regist();
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    break ;
                case "C":
                case "c":
                    System.exit(1);
                default:System.out.println("请输入正确选项（A,B或C）");
            }
        }
    }
}

class Info{
    private String userName=null;
    private String password=null;
    private String name=null;
    private String gender=null;
    private java.sql.Date birthday=null;
    private Scanner scanner=new Scanner(System.in);
    private String phone;
    private int isAdmin=0;
    public Info(){
        scanner.useDelimiter("\n");
    }

    public void setUserName() {
        this.userName = scanner.next();
    }

    public void setPassword() {
        this.password = scanner.next();
    }

    public void setName() {
        this.name = scanner.next();
    }

    public void setGender() {
        this.gender = scanner.next();
    }

    public void setBirthday()throws Exception {
        String birthday=scanner.next();
        java.util.Date temp=new java.text.SimpleDateFormat("yyyy-mm-dd").parse(birthday);
        java.sql.Date birth=new java.sql.Date(temp.getTime());
        this.birthday = birth;
    }

    public void setIsAdmin(int i) {
        this.isAdmin =i;
    }

    public void setPhone() {
        this.phone = scanner.next();
    }

    public void setPhoneToNull(){this.phone=null;}

    public String getUserName() {
        return userName;
    }

    public String getPassword() {
        return password;
    }

    public String getName() {
        return name;
    }

    public String getGender() {
        return gender;
    }

    public java.sql.Date getBirthday() {
        return birthday;
    }

    public String getPhone() {
        return phone;
    }

    public int getIsAdmin() {
        return isAdmin;
    }
}

class Reg{
    private Info info=new Info();
    private String sql="INSERT INTO user(username,password,name,gender,birthday,phone,isadmin) VALUES(?,?,?,?,?,?,?)";
    private Connection conn=null;
    private PreparedStatement pstmt=null;
    private ResultSet rs=null;

    public Reg(){}
    public Info getInfo(){
        return this.info;
    }

    private void getRegInfo()throws Exception{
        Scanner scanner=new Scanner(System.in);
        scanner.useDelimiter("\n");
        System.out.print("账号：");
        this.info.setUserName();
        System.out.print("密码：");
        this.info.setPassword();
        System.out.print("姓名：");
        this.info.setName();
        System.out.print("性别：");
        this.info.setGender();
        System.out.print("生日(yyyy-mm-dd)：");
        this.info.setBirthday();
        System.out.print("电话（选填）：");
        this.info.setPhone();
    }
    private boolean isExist()throws SQLException{
        String query="SELECT username FROM user where username=?";
        conn=DriverManager.getConnection(Main.DBURL,Main.DBUSER,Main.DBPASS);
        pstmt=conn.prepareStatement(query);
        pstmt.setString(1,this.info.getUserName());
        this.rs=pstmt.executeQuery();
        if(rs.next()){
            rs.close();
            pstmt.close();
            conn.close();
            return true;
        }else {
            rs.close();
            pstmt.close();
            conn.close();
            return false;
        }
    }
    public boolean regist()throws Exception{
        this.getRegInfo();
        if(this.isExist()){
            System.out.println("该用户名已存在，注册失败！");
            return false;
        }else {
            conn=DriverManager.getConnection(Main.DBURL,Main.DBUSER,Main.DBPASS);
            pstmt=conn.prepareStatement(this.sql);
            if(this.info.getPhone().equals("")){
                this.info.setPhoneToNull();
            }
            pstmt.setString(1,this.info.getUserName());
            pstmt.setString(2,this.info.getPassword());
            pstmt.setString(3,this.info.getName());
            pstmt.setString(4,this.info.getGender());
            pstmt.setDate(5,this.info.getBirthday());
            pstmt.setString(6,this.info.getPhone());
            pstmt.setInt(7,this.info.getIsAdmin());
            pstmt.executeUpdate();
            pstmt.close();
            conn.close();
            System.out.println("注册成功！");
            return true;
        }
    }
}

class Log{
    private Info info=new Info();
    private Scanner scanner=new Scanner(System.in);
    private String query="SELECT * FROM user WHERE username=?";
    private String allInfoQuery="SELECT * FROM user ORDER BY isadmin DESC";
    private Connection conn=null;
    private PreparedStatement pstmt=null;
    private ResultSet rs=null;

    public Log(){
        scanner.useDelimiter("\n");
    }

    private void getInfo(){
        System.out.print("账号:");
        this.info.setUserName();
        System.out.print("密码:");
        this.info.setPassword();
    }
    public boolean login()throws Exception{
        this.getInfo();
        conn=DriverManager.getConnection(Main.DBURL,Main.DBUSER,Main.DBPASS);
        pstmt=conn.prepareStatement(query);
        pstmt.setString(1,this.info.getUserName());
        rs=pstmt.executeQuery();
        if(rs.next()){
            String pass=rs.getString("password");
            if(this.info.getPassword().equals(pass)){
                String name=rs.getString("name");
                String gender=rs.getString("gender");
                java.util.Date birthday=rs.getDate("birthday");
                String phone=rs.getString("phone");
                int isAdmin=rs.getInt("isadmin");
                System.out.println("登陆成功！");
                System.out.println("---------------------------------------------------------------------------------");
                System.out.println("您的账户信息如下：");
                System.out.print("账号："+this.info.getUserName()+"\t\t");
                System.out.print("密码："+this.info.getPassword()+"\t\t");
                System.out.print("姓名："+name+"\t\t");
                System.out.print("性别："+gender+"\t\t");
                System.out.print("生日："+birthday+"\t\t");
                System.out.println("电话："+phone);
                System.out.println("---------------------------------------------------------------------------------");
                rs.close();
                pstmt.close();
                if(isAdmin==1){
                    int resultNum=0;
                    System.out.println("欢迎回来，尊敬的管理员同志！\n 以下为所有用户信息：");
                    pstmt=conn.prepareStatement(this.allInfoQuery);
                    rs=pstmt.executeQuery();
                    while (rs.next()){
                        int id0=rs.getInt("id");
                        String username0=rs.getString("username");
                        String password0=rs.getString("password");
                        String name0=rs.getString("name");
                        String gender0=rs.getString("gender");
                        java.util.Date birthday0=rs.getDate("birthday");
                        String phone0=rs.getString("phone");
                        int isAdmin0=rs.getInt("isadmin");
                        System.out.print("id："+id0+"\t\t");
                        System.out.print("账号："+username0+"\t\t");
                        System.out.print("密码："+password0+"\t\t");
                        System.out.print("姓名："+name0+"\t\t");
                        System.out.print("性别："+gender0+"\t\t");
                        System.out.print("生日："+birthday0+"\t\t");
                        System.out.print("电话："+phone0+"\t\t");
                        if(isAdmin0==1)
                            System.out.println("是否是管理员：是");
                        else
                            System.out.println("是否是管理员：否");
                        resultNum++;
                        if(resultNum>100){
                            System.out.println("只显示前100条信息！");
                            break;
                        }
                    }
                    rs.close();
                    pstmt.close();
                    loop:while (true){
                        System.out.println("请选择要进行的操作：A.添加新管理员  B.删除用户信息 C.修改管理员信息 D.退出");
                        String choice1=scanner.next();
                        switch (choice1){
                            case "A":
                            case "a":
                                Reg r=new Reg();
                                r.getInfo().setIsAdmin(1);
                                r.regist();
                                break;
                            case "B":
                            case "b":
                                System.out.print("请输入要删除的用户账号：");
                                String deleteUser=scanner.next();
                                String querySql="SELECT username FROM user WHERE username='"+deleteUser+"'";
                                String deletSql="DELETE FROM user WHERE username='"+deleteUser+"'";
                                Statement stmt=null;
                                stmt=conn.createStatement();
                                rs=stmt.executeQuery(querySql);
                                    if(rs.next()){
                                        loop2:while (true) {
                                            System.out.println("确认删除此用户？  A.是  B.否");
                                            String choice2 = scanner.next();
                                            switch (choice2) {
                                                case "A":
                                                    stmt = conn.createStatement();
                                                    stmt.executeUpdate(deletSql);
                                                    break loop2;
                                                case "B":
                                                    break loop2;
                                                default:
                                                    System.out.println("请输入正确选项（A或B）");
                                            }
                                        }
                                    }else{
                                        System.out.println("此账号不存在！");
                                    }
                                rs.close();
                                stmt.close();
                                break;
                            case "C":
                            case "c":
                                this.updateInfo();
                                break ;
                            case "D":
                            case "d":
                                break loop;
                            default:System.out.println("请输入正确选项（A,B,C或D）");
                        }
                    }
                }else {//非管理员用户进行的操作：更改信息
                    System.out.println("亲爱的"+name+"("+this.info.getUserName()+"),欢迎回来！");
                    this.updateInfo();
                }
                conn.close();
                return true;
            }else {
                System.out.println("密码错误！");
                rs.close();
                pstmt.close();
                conn.close();
                return false;
            }
        }else {
            System.out.println("此账号不存在！");
            rs.close();
            pstmt.close();
            conn.close();
            return false;
        }
    }
    private void updateInfo()throws Exception{
        loop:while (true){
            System.out.println("请选择您要进行的操作： A.修改密码  B.修改生日  C.修改电话 D.退出");
            String choice=scanner.next();
            String updateSql=null;
            switch (choice){
                case "A":
                case "a":
                    System.out.print("请输入新密码：");
                    String newPassword=scanner.next();
                    updateSql="UPDATE user SET password=? WHERE username='"+this.info.getUserName()+"'";
                    pstmt=conn.prepareStatement(updateSql);
                    pstmt.setString(1,newPassword);
                    pstmt.executeUpdate();
                    System.out.println("密码修改成功！");
                    System.out.println("新的密码为："+newPassword);
                    pstmt.close();
                    break;
                case "B":
                case "b":
                    System.out.print("请输入新的日期（yyyy-mm-dd）：");
                    String newBirthday=scanner.next();
                    java.util.Date temp=new java.text.SimpleDateFormat("yyyy-mm-dd").parse(newBirthday);
                    java.sql.Date birth=new java.sql.Date(temp.getTime());
                    updateSql="UPDATE user SET birthday=? WHERE username='"+this.info.getUserName()+"'";
                    pstmt=conn.prepareStatement(updateSql);
                    pstmt.setDate(1,birth);
                    pstmt.executeUpdate();
                    System.out.println("生日日期修改成功！");
                    System.out.println("新的日期为："+birth.toString());
                    pstmt.close();
                    break ;
                case "C":
                case "c":
                    System.out.print("请输入新的电话号码：");
                    String newPhone=scanner.next();
                    updateSql="update user SET phone=? WHERE username='"+this.info.getUserName()+"'";
                    pstmt=conn.prepareStatement(updateSql);
                    pstmt.setString(1,newPhone);
                    pstmt.executeUpdate();
                    System.out.println("电话号码修改成功！");
                    System.out.println("新的电话号码为："+newPhone);
                    pstmt.close();
                    break ;
                case "D":
                case "d":
                    break loop;
                default:
                    System.out.println("请输入正确选项（A,B,C或D）");
            }
        }
    }
}
