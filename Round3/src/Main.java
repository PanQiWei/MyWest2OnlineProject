import java.sql.*;
import java.util.Scanner;

public class Main {
    //驱动程序
    public static final String DBDRIVER="com.mysql.cj.jdbc.Driver";
    //数据库URL
    public static final String DBURL="jdbc:mysql://localhost:3306/users?useSSL=false&useUnicode=true&characterEncoding=UTF-8&serverTimezone=UTC";
    //数据库用户名
    public static final String DBUSER="root";
    //数据库登陆密码
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
                        //执行登陆操作
                        loger.login();
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    break ;
                case "B":
                case "b":
                    try {
                        //执行注册操作
                        register.regist();
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    break ;
                case "C":
                case "c":
                    //退出程序
                    System.exit(1);
                default:System.out.println("请输入正确选项（A,B或C）");
            }
        }
    }
}

//信息类，用于存储和获取使用者输入的信息
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

//注册类，执行与注册相关的一系列操作
class Reg{
    private Info info=new Info();
    //在表中插入用户注册信息的SQL语言
    private String sql="INSERT INTO user(username,password,name,gender,birthday,phone,isadmin) VALUES(?,?,?,?,?,?,?)";
    private Connection conn=null;
    private PreparedStatement pstmt=null;
    private ResultSet rs=null;

    public Reg(){}
    //获取 info 对象
    public Info getInfo(){
        return this.info;
    }
    //获取用户注册信息的方法
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
    //判断用户是否已经存在的方法，通过返回值判断存在与否
    private boolean isExist()throws SQLException{
        //筛选username的SQL语言
        String query="SELECT username FROM user where username=?";
        conn=DriverManager.getConnection(Main.DBURL,Main.DBUSER,Main.DBPASS);
        pstmt=conn.prepareStatement(query);
        pstmt.setString(1,this.info.getUserName());
        this.rs=pstmt.executeQuery();
        //存在，返回true；不存在，返回false
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
    //实现注册相关功能的方法
    public boolean regist()throws Exception{
        //获取注册信息
        this.getRegInfo();
        //判断是否已存在用户
        if(this.isExist()){
            System.out.println("该用户名已存在，注册失败！");
            return false;
        }else {
            conn=DriverManager.getConnection(Main.DBURL,Main.DBUSER,Main.DBPASS);
            pstmt=conn.prepareStatement(this.sql);
            //电话号码为可选项，若为空，则设置为NULL
            if(this.info.getPhone().equals("")){
                this.info.setPhoneToNull();
            }
            //执行预处理语句
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
    //筛选出对应用户的信息的SQL语句
    private String query="SELECT * FROM user WHERE username=?";
    //筛选出所有用户信息的SQL语句，并按先管理员后普通用户的顺序排列
    private String allInfoQuery="SELECT * FROM user ORDER BY isadmin DESC";
    private Connection conn=null;
    private PreparedStatement pstmt=null;
    private ResultSet rs=null;

    public Log(){
        scanner.useDelimiter("\n");
    }
    //获取登陆信息的方法
    private void getInfo(){
        System.out.print("账号:");
        this.info.setUserName();
        System.out.print("密码:");
        this.info.setPassword();
    }
    //实现登陆相关功能的方法
    public boolean login()throws Exception{
        //获取登陆信息
        this.getInfo();
        conn=DriverManager.getConnection(Main.DBURL,Main.DBUSER,Main.DBPASS);
        pstmt=conn.prepareStatement(query);
        pstmt.setString(1,this.info.getUserName());
        rs=pstmt.executeQuery();
        //判断用户名是否已注册，注册则进入下一判断，否则登陆失败
        if(rs.next()){
            //取出对应账户名的用户密码
            String pass=rs.getString("password");
            //比对密码是否匹配，是则登陆成功，否则登陆失败
            if(this.info.getPassword().equals(pass)){
                String name=rs.getString("name");
                String gender=rs.getString("gender");
                java.util.Date birthday=rs.getDate("birthday");
                String phone=rs.getString("phone");
                //判断是否是管理员的标签
                int isAdmin=rs.getInt("isadmin");
                //展示登陆账户的信息
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
                //是管理员则进入管理员操作界面，否则进入普通用户操作界面
                if(isAdmin==1){
                    System.out.println("欢迎回来，尊敬的管理员同志！\n 以下为所有用户信息：");

                    //管理员界面，展示所有的用户信息，仅展示前一百条
                    this.showAllInfo(100);

                    //管理员操作，进行增、删、改三项操作
                    loop:while (true){
                        System.out.println("请选择要进行的操作：A.添加新管理员  B.删除用户信息 C.修改管理员信息 D.展示所有用户 信息E.退出");
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
                                this.showAllInfo(100);
                                break ;
                            case "E":
                            case "e":
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
    //用户用于更改自己信息的方法
    private void updateInfo()throws Exception{
        loop:while (true){
            System.out.println("请选择您要进行的操作： A.修改密码  B.修改生日  C.修改电话 D.退出");
            String choice=scanner.next();
            //更改对应信息的SQL语句，不同的更改请求赋予不同的语句
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
    //展示所有用户信息的方法
    private void showAllInfo(int showNum)throws Exception{
        pstmt=conn.prepareStatement(this.allInfoQuery);
        rs=pstmt.executeQuery();
        int resultNum=0;
        while (rs.next()) {
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
            if(resultNum>showNum){
                System.out.println("只显示前"+resultNum+"条信息！");
                break;
            }
        }
        rs.close();
        pstmt.close();
    }
}
