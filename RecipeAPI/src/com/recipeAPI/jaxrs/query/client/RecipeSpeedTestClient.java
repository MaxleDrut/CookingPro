package com.recipeAPI.jaxrs.query.client;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;

import javax.security.auth.kerberos.DelegationPermission;
import javax.ws.rs.core.MediaType;
import java.util.LinkedList;

public class RecipeSpeedTestClient {
    static final String REST_URI = "http://localhost:8888/recipeAPI";
    static WebResource service;


    public static void testPostUser(int num) {
        WebResource postUser = service.path("/query/createUser");
        postUser.accept(MediaType.TEXT_PLAIN).post(String.class,"Charles Perrault"+num);
    }

    public static void testGetUsers() {
        WebResource getUsers = service.path("/query/getUsers");
        getUsers.accept(MediaType.TEXT_PLAIN).get(String.class);
    }

    //Delete lasts user of the list
    public static void testDeleteUser() {
        WebResource getUsers = service.path("/query/getUsers");
        String jsonString = getUsers.accept(MediaType.TEXT_PLAIN).get(String.class);
        JsonParser jParser = new JsonParser();

        JsonArray jsonOutput = jParser.parse(jsonString).getAsJsonArray();

        JsonElement e = null;
        //Go to the end of the JsonArray
        for(JsonElement e2 : jsonOutput) {
            e = e2;
        }

        WebResource deleteUser = service.path("/query/deleteUser");
        deleteUser.accept(MediaType.TEXT_PLAIN).delete(String.class,e.getAsJsonObject().get("userID").getAsString());
    }

    public static void testGetUserRecipes() {
        WebResource get = service.path("/query/getUserRecipes/1");
        get.accept(MediaType.TEXT_PLAIN).get(String.class);
    }

    public static void testGetRecipeTitle() {
        WebResource get = service.path("/query/getRecipeTitle/"+ 1);
        get.accept(MediaType.TEXT_PLAIN).get(String.class);
    }

    public static void testGetRecipeContent() {
        WebResource get = service.path("/query/getRecipeContent/"+ 1);
        get.accept(MediaType.TEXT_HTML).get(String.class);
    }

    public static void testGetRecipeIngredients() {
        WebResource get = service.path("/query/getRecipeIngredients/"+ 1);
        get.accept(MediaType.TEXT_PLAIN).get(String.class);
    }

    public static void testPostRecipe() {
        //Create an example recipe
        JsonObject recipe = new JsonObject();
        recipe.addProperty("recipeTitle","Ratatouille");
        recipe.addProperty("recipeContent","<h1>Recette de Ratatouille</h1>" +
                "<p>Cut the tomatoes in small dices, and make them cook in a covered hob along with olive oil until they become soft and release their juice</p>" +
                "<p>Then add the small cut dices of courgettes, aubergines and onions. Mix until all vegetables are cooked. Add salt, pepper and Herbes de provence</p>" +
                "<p>Serve while it is still hot and bon appétit!</p>"
        );
        recipe.addProperty("userID",1);

        JsonArray array = new JsonArray();
        //All of the ingredients:
        //Tomato
        JsonObject ingredient = new JsonObject();
        ingredient.addProperty("ingredientName","Tomato");
        ingredient.addProperty("quantity",4);
        ingredient.addProperty("unit"," units");
        array.add(ingredient);

        //Courgette
        ingredient = new JsonObject();
        ingredient.addProperty("ingredientName","Courgette");
        ingredient.addProperty("quantity",2);
        ingredient.addProperty("unit"," units");
        array.add(ingredient);

        //Tomato
        ingredient = new JsonObject();
        ingredient.addProperty("ingredientName","Aubergine");
        ingredient.addProperty("quantity",1);
        ingredient.addProperty("unit"," unit");
        array.add(ingredient);

        //Onion
        ingredient = new JsonObject();
        ingredient.addProperty("ingredientName","Onion");
        ingredient.addProperty("quantity",1);
        ingredient.addProperty("unit"," unit");
        array.add(ingredient);

        //Olive oil
        ingredient = new JsonObject();
        ingredient.addProperty("ingredientName","Olive oil");
        ingredient.addProperty("quantity",50);
        ingredient.addProperty("unit","mL");
        array.add(ingredient);

        recipe.add("ingredients",array);

        WebResource postRecipe = service.path("/query/createRecipe");
        postRecipe.accept(MediaType.TEXT_PLAIN).post(String.class,recipe.toString());
    }

    public static void testAddIngredient() {
        //Adds the ingredient Camembert for the lastly added recipe of Ratatouille (please don't do it in real life)
        JsonObject ingredient = new JsonObject();
        ingredient.addProperty("recipeID",getLastRecipeID());
        ingredient.addProperty("ingredientName","Camembert");
        ingredient.addProperty("quantity",50);
        ingredient.addProperty("unit","g");

        WebResource postIngredient = service.path("/query/addIngredient");
        postIngredient.accept(MediaType.TEXT_PLAIN).post(String.class,ingredient.toString());
    }

    public static void testUpdateIngredientQuantity(int num) {
        //Changes the quantity of Camembert from 50g to 70g (whyyyyyy it is disgusting!!)
        JsonObject ingredient = new JsonObject();
        ingredient.addProperty("recipeID","1");
        ingredient.addProperty("ingredientName","Golden Syrup");
        ingredient.addProperty("quantity",160+num);
        ingredient.addProperty("unit","g");

        WebResource putIngredient = service.path("/query/updateIngredientQuantity");
        putIngredient.accept(MediaType.TEXT_PLAIN).put(String.class,ingredient.toString());
    }

    public static void testDeleteRecipe() {
        WebResource deleteRecipe = service.path("/query/deleteRecipe");
        deleteRecipe.accept(MediaType.TEXT_PLAIN).delete(String.class,getLastRecipeID());
    }

    public static void testDeleteIngredient() {
        //Delete the ingredient Aubergine (who likes them anyway!)
        //For this request, we have to format a JSON!

        JsonObject input = new JsonObject();
        input.addProperty("recipeID",getLastRecipeID());
        input.addProperty("ingredientName","Aubergine");

        WebResource deleteIngredient = service.path("/query/deleteIngredient");
        deleteIngredient.accept(MediaType.TEXT_PLAIN).delete(String.class, input.toString());
    }


    public static String getLastRecipeID() {
        //Get the last recipe of user number 1
        WebResource getUserRecipes = service.path("/query/getUserRecipes/1");
        String jsonString = getUserRecipes.accept(MediaType.TEXT_PLAIN).get(String.class);
        JsonParser jParser = new JsonParser();

        JsonArray jsonOutput = jParser.parse(jsonString).getAsJsonArray();

        JsonElement e = null;
        //Go to the end of the JsonArray
        for(JsonElement e2 : jsonOutput) {
            e = e2;
        }

        //Get the ingredients of the last recipe (the previously added Ratatouille)
        String recipeID = e.getAsJsonObject().get("recipeID").getAsString();
        return recipeID;
    }

    //Calls 20 get request to the server (4 of each type) and gives some statistics
    public static double GETSpeedTest() {
        System.out.println("----- GetSpeedTest -----");
        LinkedList<Double> executionTime = new LinkedList<Double>();
        for(int i=0;i<4;i++) {
            long start = System.currentTimeMillis();
            testGetRecipeContent();
            testGetRecipeIngredients();
            testGetRecipeTitle();
            testGetUserRecipes();
            testGetUsers();
            long end = System.currentTimeMillis();
            double timePerCall = (end - start)/5.0;
            executionTime.addLast(timePerCall);
        }

        //Calculates the average and deviation
        double sum = 0;
        for(double k : executionTime) {
            sum+=k;
        }

        double average = sum/executionTime.size();

        double deviation = 0;
        for(double k : executionTime) {
            deviation += Math.pow(k-average,2);
        }

        deviation = Math.sqrt(deviation/executionTime.size());
        System.out.println("Average execution time: "+average+"ms");
        System.out.println("Standard deviation: "+deviation+"ms");
        System.out.println();

        return average;
    }

    //Sends 20 Post request to the API and gives some statistics
    public static void POSTSpeedTest() {
        System.out.println("----- Post Speed Test -----");
        LinkedList<Double> executionTime = new LinkedList<Double>();
        for(int i=0;i<20;i++) {
            long start = System.currentTimeMillis();
            testPostUser(i);
            long end = System.currentTimeMillis();
            double timePerCall = (end - start);
            executionTime.addLast(timePerCall);
        }

        //Calculates the average and deviation
        double sum = 0;
        for(double k : executionTime) {
            sum+=k;
        }

        double average = sum/executionTime.size();

        double deviation = 0;
        for(double k : executionTime) {
            deviation += Math.pow(k-average,2);
        }

        deviation = Math.sqrt(deviation/executionTime.size());
        System.out.println("Average execution time: "+average+"ms");
        System.out.println("Standard deviation: "+deviation+"ms");
        System.out.println();
    }

    //Delete the 20 users previously created and makes some statistics
    public static void DELETESpeedTest(double avgGET) {
        System.out.println("----- Delete Speed Test -----");
        LinkedList<Double> executionTime = new LinkedList<Double>();
        for(int i=0;i<20;i++) {
            long start = System.currentTimeMillis();
            testDeleteUser();
            long end = System.currentTimeMillis();
            //To get the user to delete, we also do a get call to find the ID, so we deduce the average time of the call
            double timePerCall = (end - start - avgGET);
            executionTime.addLast(timePerCall);
        }

        //Calculates the average and deviation
        double sum = 0;
        for(double k : executionTime) {
            sum+=k;
        }

        double average = sum/executionTime.size();

        double deviation = 0;
        for(double k : executionTime) {
            deviation += Math.pow(k-average,2);
        }

        deviation = Math.sqrt(deviation/executionTime.size());
        System.out.println("Average execution time: "+average+"ms");
        System.out.println("Standard deviation: "+deviation+"ms");
        System.out.println();
    }

    //Send 20 Updates
    public static void UPDATESpeedTest() {
        System.out.println("----- Update Speed Test -----");
        LinkedList<Double> executionTime = new LinkedList<Double>();
        for(int i=0;i<20;i++) {
            long start = System.currentTimeMillis();
            testUpdateIngredientQuantity(i);
            long end = System.currentTimeMillis();
            double timePerCall = (end - start);
            executionTime.addLast(timePerCall);
        }

        //Calculates the average and deviation
        double sum = 0;
        for(double k : executionTime) {
            sum+=k;
        }

        double average = sum/executionTime.size();

        double deviation = 0;
        for(double k : executionTime) {
            deviation += Math.pow(k-average,2);
        }

        deviation = Math.sqrt(deviation/executionTime.size());
        System.out.println("Average execution time: "+average+"ms");
        System.out.println("Standard deviation: "+deviation+"ms");
        System.out.println();
    }

    public static void main(String args[]) {
        ClientConfig config = new DefaultClientConfig();
        Client client = Client.create(config);
        service = client.resource(REST_URI);

        double averageGET = GETSpeedTest();
        POSTSpeedTest();
        DELETESpeedTest(averageGET);
        UPDATESpeedTest();

        System.out.println("PLEASE RESTART THE API TO DO ANOTHER SPEEDTEST");
        System.out.println("(due to the limitations of our database)");
    }
}
