package com.recipeAPI.jaxrs.query.client;

import javax.print.attribute.standard.Media;
import javax.ws.rs.core.MediaType;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.google.gson.*;

public class RecipeUnitaryTestsClient {
    static final String REST_URI = "http://localhost:8888/recipeAPI";
    static WebResource service;

    public static void testPostUser() {
        WebResource postUser = service.path("/query/createUser");
        postUser.accept(MediaType.TEXT_PLAIN).post(String.class,"Charles Perrault");
    }

    public static void testGetUsers() {
        WebResource getUsers = service.path("/query/getUsers");
        System.out.println(getUsers.accept(MediaType.TEXT_PLAIN).get(String.class));
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
        System.out.println(get.accept(MediaType.TEXT_PLAIN).get(String.class));
    }

    public static void testGetRecipeTitle() {
        WebResource get = service.path("/query/getRecipeTitle/"+ getLastRecipeID());
        System.out.println(get.accept(MediaType.TEXT_PLAIN).get(String.class));
    }

    public static void testGetRecipeContent() {
        WebResource get = service.path("/query/getRecipeContent/"+ getLastRecipeID());
        System.out.println(get.accept(MediaType.TEXT_HTML).get(String.class));
    }

    public static void testGetRecipeIngredients() {
        WebResource get = service.path("/query/getRecipeIngredients/"+ getLastRecipeID());
        System.out.println(get.accept(MediaType.TEXT_PLAIN).get(String.class));
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

    public static void testUpdateIngredientQuantity() {
        //Changes the quantity of Camembert from 50g to 70g (whyyyyyy it is disgusting!!)
        JsonObject ingredient = new JsonObject();
        ingredient.addProperty("recipeID",getLastRecipeID());
        ingredient.addProperty("ingredientName","Camembert");
        ingredient.addProperty("quantity",70);
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

    public static void main(String args[]) {
        ClientConfig config = new DefaultClientConfig();
        Client client = Client.create(config);
        service = client.resource(REST_URI);

        System.out.println("----- User tests -----");
        System.out.println();
        System.out.println("Adding a new user \"Charles Perrault \"");
        testPostUser();
        testGetUsers();
        System.out.println();
        System.out.println("Removing the user \"Charles Perrault \"");
        testDeleteUser();
        testGetUsers();
        System.out.println();

        System.out.println("----- Recipe Tests -----");
        System.out.println();
        System.out.println("Adding an user made recipe of Ratatouille");
        testPostRecipe();
        testGetUserRecipes();
        System.out.println();

        System.out.println("Get the title, content and ingredients of the Ratatouille");
        testGetRecipeTitle();
        testGetRecipeContent();
        testGetRecipeIngredients();
        System.out.println();

        System.out.println("Delete the Aubergine and add the Camembert ingredient from the Ratatouille ");
        testDeleteIngredient();
        testAddIngredient();
        testGetRecipeIngredients();
        System.out.println();

        System.out.println("Changes the Camembert's quantity from 50 to 70g");
        testUpdateIngredientQuantity();
        testGetRecipeIngredients();
        System.out.println();

        System.out.println("Delete the recipe of Ratatouille");
        testDeleteRecipe();
        testGetUserRecipes();
    }
}
