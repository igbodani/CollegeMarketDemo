package Backend.Models;

import java.io.File;
import java.io.FileOutputStream;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DAL {


    /**
     * * DAL methods used query the DataBase
     * * for model data.
     * *
     */


    /**
     * Private variables
     *
     * @URL DataBase location
     * @user DataBase user
     * @password DataBase password
     * @callableStatement Object used to execute stored procedures
     * @sqlcommand Calling the strored procedures
     * @sqlDate Object used to convert java Date to sql
     */

    static String url;
    static String user;
    static String password;
    static Connection connection;
    static CallableStatement callableStatement;
    static String sqlCommand;
    static java.sql.Date sqlDate;


    /**
     * Creates Connection to the Database
     *
     * @return
     * @throws SQLException
     */

    private static Connection connectToDatabase() throws SQLException {

        url = "jdbc:mysql://localhost:3306/collegemarket";
        user = "root";
        password = "smileyface";

        try {
            connection = DriverManager.getConnection(url, user, password);
            return connection;

        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return connection;
    }


    /**
     *
     * * USER DAL METHODS
     * *
     */


    /**
     * * Adds User to the Database
     * @return The User ID
     * @param user User object
     */

    protected static int addUser(User user) throws SQLException {

        int id = -1;

        sqlCommand = "{call AddUser(?,?,?,?,?,?)}";
        sqlDate = new Date(user.getDateCreated().getTime());

        try {

            callableStatement = connectToDatabase().prepareCall(sqlCommand);

            callableStatement.registerOutParameter(1,Types.INTEGER);
            userCall(user);

            callableStatement.execute();

            id = callableStatement.getInt(1);

        } catch (SQLException e) {
            e.printStackTrace();

        } finally {
            callableStatement.close();
            connectToDatabase().close();
        }

        return id;
    }

    private static void userCall(User user) throws SQLException {
        callableStatement.setInt(1,user.getId());
        callableStatement.setString(2, user.getFirstName());
        callableStatement.setString(3, user.getLastName());
        callableStatement.setString(4, user.getEmail());
        callableStatement.setString(5, user.getPassword());
        callableStatement.setDate(6, sqlDate);
    }


    /**
     * @param user Edited User
     * @return int The number of rows affected should return 1
     * returns 0 or less on failure 
     * @throws SQLException
     */
    protected static int updateUser(User user) throws SQLException {

        int rowsAffected = 0;

        sqlCommand = "{call EditUser(?,?,?,?,?,?)}";
        sqlDate = new Date(user.getDateCreated().getTime());

        try {

            callableStatement = connectToDatabase().prepareCall(sqlCommand);
            userCall(user);


            rowsAffected = callableStatement.executeUpdate();


        } catch (SQLException e) {
            e.printStackTrace();

        } finally {
            callableStatement.close();
            connectToDatabase().close();
        }

        return rowsAffected;

    }

    /**
     * @param email User email
     * @return User of the email parameter
     * @throws SQLException
     */

    static User getUserByEmail(String email) throws SQLException {
        sqlCommand = "{call GetUserByEmail(?) }";
        User user = null;

        try {
            callableStatement = connectToDatabase().prepareCall(sqlCommand);
            callableStatement.setString(1, email);
            ResultSet result = callableStatement.executeQuery();

            while (result.next()) {
                user = new User(result);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            callableStatement.close();
            connectToDatabase().close();
        }

        return user;
    }

    public static User getUser(String email, String password) throws SQLException {

        // Retries the user via email
        User user = getUserByEmail(email);


        //If the User isn't null
        if (user != null) {

            if (user.getPassword().equals(password)) {
                /**
                 *
                 * We are going to implemnet a hasher here to
                 * fot security purposes
                 */

                //Password match
                return user;

            } else {
                user = null;
            }
        }

        return user;
    }


    /**
     * @param id of the User
     * @return User of the id given
     * @throws SQLException
     */
    static User getUser(int id) throws SQLException {
        User user = null;

        sqlCommand = "{call GetUser(?)}";

        try {
            callableStatement = connectToDatabase().prepareCall(sqlCommand);
            callableStatement.setInt(1, id);
            ResultSet resultSet = callableStatement.executeQuery();

            while (resultSet.next()) {
                user = new User(resultSet);
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            callableStatement.close();
            connectToDatabase().close();
        }

        return user;

    }


    /**
     * @param product The product that needs to know its user
     * @return User The owner of the product
     * @throws SQLException
     */

    protected static User getUserForProduct(Product product) throws SQLException {

        User user = null;
        sqlCommand = "{call GetUserForProduct(?)}";

        try {
            callableStatement = connectToDatabase().prepareCall(sqlCommand);
            callableStatement.setInt(1, product.getId());
            ResultSet resultSet = callableStatement.executeQuery();

            while (resultSet.next()) {
                user = new User(resultSet);
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            callableStatement.close();
            connectToDatabase().close();
        }

        return user;
    }

    /**
     * @return The list of all users
     * @throws SQLException
     */
    protected static List<User> getAllUsers() throws SQLException {
        List<User> users = new ArrayList<>();

        sqlCommand = "{call GetAllUsers}";
        try {
            callableStatement = connectToDatabase().prepareCall(sqlCommand);
            ResultSet resultSet = callableStatement.executeQuery();

            while (resultSet.next()) {
                users.add(new User(resultSet));
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            callableStatement.close();
        }

        return users;
    }


    /**
     *
     * IMAGE DAL METHODS
     *
     */

    /**
     * Has not been tested yet, probably have to work on this again
     *
     * @param image The product that needs to an image added
     * @return id Image ID if the image is successfully stored in the DB
     * returns int less than 1 on failure
     * @throws SQLException
     */

    protected static int addImage(Image image) throws SQLException {
        
        int id = -1;

        sqlCommand = "{call AddImages(?,?,?,?)}";
        sqlDate = new Date(image.getDateCreated().getTime());


        try {
            callableStatement = connectToDatabase().prepareCall(sqlCommand);

            callableStatement.registerOutParameter(1,Types.INTEGER);
            callImage(image);


             callableStatement.execute();

             id = callableStatement.getInt(1);


        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            callableStatement.close();

        }

        return id;
    }

    /**
     *
     * @param image The image been updated
     * @return The number of rows affected
     * returns 0 or less than 0 on failure
     */

    protected static int updateImage(Image image) throws SQLException {

        int rowsAffected = -1;

        sqlCommand = "{call EditImage(?,?,?,?)}";
        sqlDate = new Date(image.getDateCreated().getTime());


        try {


            callableStatement = connectToDatabase().prepareCall(sqlCommand);

            callImage(image);

            rowsAffected = callableStatement.executeUpdate();


        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            callableStatement.close();
            connectToDatabase().close();

        }

        return rowsAffected;
    }

    /**
     * Fills in stored procedure parameters using model
     * @param image
     * @throws SQLException
     */

    private static void callImage(Image image) throws SQLException {
        callableStatement.setInt(1, image.getId());
        callableStatement.setString(2, image.getFile().getPath());
        callableStatement.setInt(3, image.getId());
        callableStatement.setDate(4, sqlDate);

    }

    protected static List<Image> getImages(Product product) throws SQLException {
        List<Image> imageList = new ArrayList<>();

        sqlCommand = "{call GetImages(?)}";

        try {

            callableStatement = connectToDatabase().prepareCall(sqlCommand);
            callableStatement.setInt(1, product.getId());
            ResultSet resultSet = callableStatement.executeQuery();

            byte[] byteArray;
            Blob blob;
            FileOutputStream writer = null;
            int imageName = 0;

            while (resultSet.next()) {
                imageName++;
                File file = new File("C:\\Users\\Daniel\\Desktop\\CollegeMarketDemo\\src\\main\\resources\\public" + "\\" + imageName + ".jpg");
                writer = new FileOutputStream(file);
                blob = resultSet.getBlob(Image.DB_IMAGE);
                byteArray = blob.getBytes(1, (int) blob.length());
                writer.write(byteArray);

                Image image = new Image(file);
                //image.id = resultSet.getInt(Image.DB_ID);
                imageList.add(image);
            }


        } catch (Exception ex) {
            ex.printStackTrace();

        } finally {
            callableStatement.close();
            connectToDatabase().close();
        }
        return imageList;
    }

    protected static List<Image> getAllImages() throws SQLException {
        List<Image> images = new ArrayList<>();

        sqlCommand = "{call GetAllImages}";

        try {

            //Connects to the Database and executes the stored procedure
            //Returns the procedure result as resultSet
            callableStatement = connectToDatabase().prepareCall(sqlCommand);
            ResultSet resultSet = callableStatement.executeQuery();


            byte[] byteArray;
            Blob blob;
            FileOutputStream writer = null;
            int imageName = 0;

            while (resultSet.next()) {
                imageName++;
                File file = new File("C:\\Users\\Daniel\\Desktop\\CollegeMarketDemo\\src\\main\\resources\\public" + "\\" + imageName + ".jpg");
                writer = new FileOutputStream(file);
                blob = resultSet.getBlob("image");
                byteArray = blob.getBytes(1, (int) blob.length());
                writer.write(byteArray);
                images.add(new Image(file));
                //  image.id = resultSet.getInt(Image.DB_ID);

            }

            if (writer != null) {
                writer.close();
            }


        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            callableStatement.close();

        }
        return images;
    }


    /**
     * * PRODUCTS DAL METHODS
     *
     * @return
     */


    protected static int addProduct(Product product) throws SQLException {

        int id = -1;

        sqlCommand = "{call AddProducts(?,?,?,?,?,?,?)}";
        sqlDate = new Date(product.getDateCreated().getTime());

        try{

            callableStatement = connectToDatabase().prepareCall(sqlCommand);
            callableStatement.registerOutParameter(1,Types.INTEGER);
            productCall(product);



            callableStatement.execute();
            id = callableStatement.getInt(1);

        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            callableStatement.close();
            connectToDatabase().close();
        }


        return id;
    }



    protected static int updateProduct(Product product) throws SQLException {
        int rowsAffected = -1;

        sqlCommand = "{call EditProduct(?,?,?,?,?,?,?)}";
        sqlDate = new Date(product.getDateCreated().getTime());

        try{
            callableStatement = connectToDatabase().prepareCall(sqlCommand);

            productCall(product);


            rowsAffected =  callableStatement.executeUpdate();


        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            callableStatement.close();
            connectToDatabase().close();
        }


        return rowsAffected;

    }


    /**
     *
     * @param product
     * @throws SQLException
     */
    private static void productCall(Product product) throws SQLException {
        callableStatement.setInt(1, product.getId());
        callableStatement.setString(2, product.getName());
        callableStatement.setString(3,String.valueOf(product.getType()));
        callableStatement.setString(4,product.getDescription());
        callableStatement.setFloat(5, product.getPrice());
        callableStatement.setString(6, product.getEmail());
        callableStatement.setDate(7, sqlDate);


    }


    /**
     * @param user The user who is requesting a list of products owned
     * @return products The list of products owned by the user
     * @throws SQLException
     */

    protected static List<Product> getAllUserProducts(User user) throws SQLException {

        List<Product> products = new ArrayList<>();

        sqlCommand = "{call GetAllUserProducts(?)}";

        try {
            callableStatement = connectToDatabase().prepareCall(sqlCommand);
            callableStatement.setString(1, user.getEmail());
            ResultSet resultSet = callableStatement.executeQuery();

            while (resultSet.next()) {
                products.add(new Product(resultSet));
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            callableStatement.close();
            connectToDatabase().close();
        }

        return products;
    }


    /**
     * @return The List of all products
     * @throws SQLException
     */
    protected static List<Product> getAllProducts() throws SQLException {
        List<Product> products = new ArrayList<>();

        sqlCommand = "{call GetAllProducts}";
        try {
            callableStatement = connectToDatabase().prepareCall(sqlCommand);
            ResultSet resultSet = callableStatement.executeQuery();

            while (resultSet.next()) {
                products.add(new Product(resultSet));
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            callableStatement.close();
            connectToDatabase().close();
        }

        return products;
    }


    public static void main(String[] args) throws SQLException {

        User usr = new User("Niklaus", "Mikaelson", "ste@gmail.com","original");
        usr.setId(7);
        int id = DAL.updateUser(usr);
        System.out.println(id);

        System.out.println(getAllUsers());


        /*

        User usr = new User("Elijah", "Mikaelson", "elijah@email.com","original");
        int id = DAL.addUser(usr);
        System.out.println(DAL.getAllUsers());
        System.out.println(id);
        usr.setFirstName("Niklaus");
        System.out.println(DAL.updateUser(usr));
        System.out.println(DAL.getAllUsers());

         */




    }


}
