package com.recipeAPI.jaxrs.query;

import java.io.*;
import java.sql.*;
import java.util.*;
import com.google.gson.*;

public class RecipeQuery {

    public static final String propsFile = "jdbc.properties";
    public Connection database;

    public RecipeQuery(){
        try {
            database = getConnection();
        } catch(Exception e) {
            database = null;
            e.printStackTrace();
        }
    }

    /**
     * Establishes a connection to the database.
     *
     * @return Connection object representing the connection
     * @throws IOException if properties file cannot be accessed
     * @throws SQLException if connection fails
     */

    public Connection getConnection() throws IOException, SQLException
    {
        // Load properties

        FileInputStream in = new FileInputStream(propsFile);
        Properties props = new Properties();
        props.load(in);

        // Define JDBC driver

        /*String drivers = props.getProperty("jdbc.drivers");
        System.out.println(drivers);
        if (drivers != null)
            System.setProperty("jdbc.drivers", drivers);*/
        // Setting standard system property jdbc.drivers
        // is an alternative to loading the driver manually
        // by calling Class.forName()

        // Obtain access parameters and use them to create connection

        String url = props.getProperty("jdbc.url");
        String user = props.getProperty("jdbc.user");
        String password = props.getProperty("jdbc.password");

        return DriverManager.getConnection(url, user, password);
    }

    /**
     * Query all of the DB's users
     *
     * @return JsonArray with all of the user's data
     * @throws SQLException if request fails
     */
    public JsonArray getUsers() throws SQLException {
        Statement statement= database.createStatement();
        ResultSet results = statement.executeQuery("Select * from Users");

        LinkedList<String> headers = new LinkedList<String>();
        ResultSetMetaData rsMetaData = results.getMetaData();

        for(int i=1; i<=rsMetaData.getColumnCount(); i++) {
            headers.add(rsMetaData.getColumnName(i));
        }

        JsonArray returnArray = new JsonArray();

        while(results.next()) {
            JsonObject obj = new JsonObject();
            for(String s : headers) {
                obj.addProperty(s,results.getString(s));
            }
            returnArray.add(obj);
        }

        return returnArray;
    }

    /**
     * Query all of the User's owned recipes in the DB
     *
     * @param  userid The ID of the user to query
     * @return JsonArray with all user's recipes
     * @throws SQLException if request fails
     */
    public JsonArray getUserRecipes(int userid) throws SQLException {
        Statement statement= database.createStatement();
        ResultSet results = statement.executeQuery("Select recipeID, recipeTitle from Recipes where userID ="+userid);

        LinkedList<String> headers = new LinkedList<String>();
        ResultSetMetaData rsMetaData = results.getMetaData();

        for(int i=1; i<=rsMetaData.getColumnCount(); i++) {
            headers.add(rsMetaData.getColumnName(i));
        }

        JsonArray returnArray = new JsonArray();

        while(results.next()) {
            JsonObject obj = new JsonObject();
            for(String s : headers) {
                obj.addProperty(s,results.getString(s));
            }
            returnArray.add(obj);
        }

        return returnArray;
    }

    /**
     * Query the HTML content of a recipe by its ID
     *
     * @param  recipeID The ID of the recipe to query
     * @return String with the HTML of the recipe
     * @throws SQLException if request fails
     */
    public String getRecipeContent(int recipeID) throws SQLException {
        Statement statement = database.createStatement();
        ResultSet results = statement.executeQuery("Select recipeContent from Recipes where recipeID="+recipeID);

        results.next();
        return results.getString("recipeContent");
    }

    /**
     * Query the name of a recipe by its ID
     *
     * @param  recipeID The ID of the recipe to query
     * @return String with the title of the recipe
     * @throws SQLException if request fails
     */
    public String getRecipeTitle(int recipeID) throws SQLException {
        Statement statement = database.createStatement();
        ResultSet results = statement.executeQuery("Select recipeTitle from Recipes where recipeID="+recipeID);

        results.next();
        return results.getString("recipeTitle");
    }

    /**
     * Query all of the ingredients of a given recipe
     *
     * @param  recipeID The ID of the recipe to query
     * @return JsonArray with all of the informations about the recipe's ingredients
     * @throws SQLException if request fails
     */
    public JsonArray getRecipeIngredients(int recipeID) throws SQLException {
        Statement statement= database.createStatement();
        ResultSet results = statement.executeQuery("Select ingredientName, quantity, unit from Ingredients where recipeID ="+recipeID);

        LinkedList<String> headers = new LinkedList<String>();
        ResultSetMetaData rsMetaData = results.getMetaData();

        for(int i=1; i<=rsMetaData.getColumnCount(); i++) {
            headers.add(rsMetaData.getColumnName(i));
        }

        JsonArray returnArray = new JsonArray();

        while(results.next()) {
            JsonObject obj = new JsonObject();
            for(String s : headers) {
                obj.addProperty(s,results.getString(s));
            }
            returnArray.add(obj);
        }

        return returnArray;
    }

    /**
     * Insert a user in the database from its username
     *
     * @param  username The name of the user to add
     * @throws SQLException if request fails
     */
    public void createUser(String username) throws SQLException {
        PreparedStatement statement = database.prepareStatement("Insert into Users(name) values(?)");
        statement.setString(1,username);
        statement.execute();
    }

    /**
     * Insert a recipe in the database, along with all of its related ingredients
     *
     * @param  recipe Object containing all of the informations about the object to add
     * @throws SQLException if request fails
     */
    public void createRecipe(JsonObject recipe) throws SQLException {
        //Add the new recipe to the DB
        PreparedStatement insertRecipe = database.prepareStatement("Insert into Recipes(recipeTitle,recipeContent,userID) values(?,?,?)");
        insertRecipe.setString(1,recipe.get("recipeTitle").getAsString());
        insertRecipe.setString(2,recipe.get("recipeContent").getAsString());
        insertRecipe.setInt(3,recipe.get("userID").getAsInt());

        insertRecipe.execute();

        //Recover the ID of the newly created recipe:
        Statement getRecipeID = database.createStatement();
        ResultSet result = getRecipeID.executeQuery("Select recipeID from Recipes order by recipeID desc");

        result.next();
        int recipeID = result.getInt("recipeID");

        //Insert the recipe's ingredients in the database from the recipeID
        PreparedStatement insertIngredients = database.prepareStatement("Insert into Ingredients(recipeID,ingredientName,quantity,unit) values(?,?,?,?)");

        JsonArray ingredients = recipe.getAsJsonArray("ingredients");
        for(JsonElement elem : ingredients) {
            JsonObject ingredient = elem.getAsJsonObject();
            insertIngredients.setInt(1,recipeID);
            insertIngredients.setString(2,ingredient.get("ingredientName").getAsString());
            insertIngredients.setInt(3,ingredient.get("quantity").getAsInt());
            insertIngredients.setString(4,ingredient.get("unit").getAsString());

            insertIngredients.executeUpdate();
        }

        insertIngredients.close();
    }

    /**
     * Adds an ingredient to an already existing recipe
     *
     * @param  ingredient JsonObject with the informations about the ingredient
     * @throws SQLException if request fails
     */
    public void addIngredient(JsonObject ingredient) throws SQLException {
        int recipeID = ingredient.get("recipeID").getAsInt();
        String ingredientName = ingredient.get("ingredientName").getAsString();
        int quantity = ingredient.get("quantity").getAsInt();
        String unit = ingredient.get("unit").getAsString();

        PreparedStatement statement = database.prepareStatement("Insert into Ingredients values(?,?,?,?)");
        statement.setInt(1,recipeID);
        statement.setString(2,ingredientName);
        statement.setInt(3,quantity);
        statement.setString(4,unit);

        statement.execute();
    }

    /**
     * Updates the quantity of an already existing ingredient
     *
     * @param  ingredient JsonObject with all informations about the ingredient, including the new quantity
     * @throws SQLException if request fails
     */
    public void updateIngredientQuantity(JsonObject ingredient) throws SQLException {
        int recipeID = ingredient.get("recipeID").getAsInt();
        String ingredientName = ingredient.get("ingredientName").getAsString();
        int quantity = ingredient.get("quantity").getAsInt();

        PreparedStatement statement = database.prepareStatement("Update Ingredients set quantity = ? where recipeID = ? and ingredientName = ?");
        statement.setInt(1,quantity);
        statement.setInt(2,recipeID);
        statement.setString(3,ingredientName);

        statement.execute();
    }

    /**
     * Removes an user from the database by its id
     *
     * @param  userID the ID of the user to remove
     * @throws SQLException if request fails
     */
    public void deleteUser(int userID) throws SQLException {
        PreparedStatement statement = database.prepareStatement("DELETE from Users where userID="+userID);
        statement.execute();
    }

    /**
     * Removes a recipe from the database by its id
     *
     * @param  recipeID the ID of the recipe to remove
     * @throws SQLException if request fails
     */
    public void deleteRecipe(int recipeID) throws SQLException {
        //We must delete the recipe and all its related ingredients
        PreparedStatement deleteIngredients = database.prepareStatement("Delete from Ingredients where recipeID="+recipeID);
        deleteIngredients.execute();
        PreparedStatement deleteRecipe = database.prepareStatement("DELETE from Recipes where recipeID="+recipeID);
        deleteRecipe.execute();
    }

    /**
     * Removes an ingredient of a recipe
     *
     * @param  ingredient JsonObject with infos about the ingredient
     * @throws SQLException if request fails
     */
    public void deleteIngredient(JsonObject ingredient) throws SQLException {
        int recipeID = ingredient.get("recipeID").getAsInt();
        String ingredientName = ingredient.get("ingredientName").getAsString();
        PreparedStatement statement = database.prepareStatement("Delete from Ingredients where recipeID="+recipeID+" and ingredientName = '"+ingredientName+"'");
        statement.execute();
    }

}