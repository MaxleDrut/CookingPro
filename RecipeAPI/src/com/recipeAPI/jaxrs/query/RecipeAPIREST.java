package com.recipeAPI.jaxrs.query;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import com.google.gson.*;
import com.recipeAPI.jaxrs.query.RecipeQuery;

import java.io.InputStream;

@Path("/query")
public class RecipeAPIREST {
    public RecipeQuery query;

    public RecipeAPIREST() {
        query = new RecipeQuery();
    }

    @GET
    @Path("/getUsers")
    @Produces(MediaType.TEXT_PLAIN)
    public String getUsers() {
        String output = "";
        try {
            output = query.getUsers().toString();
        } catch(Exception e) {
            e.printStackTrace();
        } finally {
            return output;
        }
    }

    @GET
    @Path("/getUserRecipes/{id}")
    @Produces(MediaType.TEXT_PLAIN)
    public String getUserRecipes(@PathParam("id") int id) {
        String output = "";
        try {
            output = query.getUserRecipes(id).toString();
        } catch(Exception e) {
            e.printStackTrace();
        } finally {
            return output;
        }
    }

    @GET
    @Path("/getRecipeTitle/{id}")
    @Produces(MediaType.TEXT_PLAIN)
    public String getRecipeTitle(@PathParam("id") int id) {
        String output = "";
        try {
            output = query.getRecipeTitle(id);
        } catch(Exception e) {
            e.printStackTrace();
        } finally {
            return output;
        }
    }

    @GET
    @Path("/getRecipeContent/{id}")
    @Produces(MediaType.TEXT_HTML)
    public String getRecipeContent(@PathParam("id") int id) {
        String output = "";
        try {
            output = query.getRecipeContent(id);
        } catch(Exception e) {
            e.printStackTrace();
        } finally {
            return output;
        }
    }

    @GET
    @Path("/getRecipeIngredients/{id}")
    @Produces(MediaType.TEXT_PLAIN)
    public String getRecipeIngredients(@PathParam("id") int id) {
        String output = "";
        try {
            output = query.getRecipeIngredients(id).toString();
        } catch(Exception e) {
            e.printStackTrace();
        } finally {
            return output;
        }
    }

    @POST
    @Path("/createUser")
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.TEXT_PLAIN)
    public String createUser(String json) {
        String status = "Error";
        try {
            query.createUser(json);
            System.out.println("The user "+json+" was added to the database");
            status = "Success";
        } catch(Exception e) {
            e.printStackTrace();
        } finally {
            return status;
        }
    }

    @POST
    @Path("/createRecipe")
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.TEXT_PLAIN)
    public String createRecipe(String json) {
        String status = "Error";
        try {
            JsonParser Jparser = new JsonParser();
            JsonObject input = Jparser.parse(json).getAsJsonObject();
            query.createRecipe(input);
            System.out.println("The recipe was added to the database");
            status = "Success";
        } catch(Exception e) {
            e.printStackTrace();
        } finally {
            return status;
        }
    }

    @POST
    @Path("/addIngredient")
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.TEXT_PLAIN)
    public String addIngredient(String json) {
        String status = "Error";
        try {
            JsonParser Jparser = new JsonParser();
            JsonObject input = Jparser.parse(json).getAsJsonObject();
            query.addIngredient(input);
            System.out.println("The ingredient was added to the database");
            status = "Success";
        } catch(Exception e) {
            e.printStackTrace();
        } finally {
            return status;
        }
    }

    @PUT
    @Path("/updateIngredientQuantity")
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.TEXT_PLAIN)
    public String updateIngredientQuantity(String json) {
        String status = "Error";
        try {
            JsonParser Jparser = new JsonParser();
            JsonObject input = Jparser.parse(json).getAsJsonObject();
            query.updateIngredientQuantity(input);
            System.out.println("The ingredient quantity was updated");
            status = "Success";
        } catch(Exception e) {
            e.printStackTrace();
        } finally {
            return status;
        }
    }

    @DELETE
    @Path("/deleteUser")
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.TEXT_PLAIN)
    public String deleteUser(String userID) {
        String status = "Error";
        try {
            query.deleteUser(Integer.parseInt(userID));
            System.out.println("Successfully removed user "+userID+" from the database");
            status = "Success";
        } catch(Exception e) {
            e.printStackTrace();
        } finally {
            return status;
        }
    }

    @DELETE
    @Path("/deleteRecipe")
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.TEXT_PLAIN)
    public String deleteRecipe(String recipeID) {
        String status = "Error";
        try {
            query.deleteRecipe(Integer.parseInt(recipeID));
            System.out.println("Successfully removed recipe "+recipeID+" from the database");
            status = "Success";
        } catch(Exception e) {
            e.printStackTrace();
        } finally {
            return status;
        }
    }

    @DELETE
    @Path("/deleteIngredient")
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.TEXT_PLAIN)
    public String deleteIngredient(String json) {
        String status = "Error";
        try {
            JsonParser Jparser = new JsonParser();
            JsonObject input = Jparser.parse(json).getAsJsonObject();
            query.deleteIngredient(input);
            System.out.println("Successfully removed ingredient "+input.get("ingredientName").getAsString()+" of recipe "+input.get("recipeID").getAsInt()+" from the database");
            status = "Success";
        } catch(Exception e) {
            e.printStackTrace();
        } finally {
            return status;
        }
    }
}
