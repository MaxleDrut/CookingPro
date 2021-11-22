from flask import render_template, Markup, redirect, request
from app import app
import requests, json

spoonacularKey = "8890571c003f4164a9bcc226784b191a"

@app.route('/')
def index():
    return render_template("index.html")

@app.route('/recipes', methods=["GET","POST"])
def recipesUsers():
    if request.method == "POST":
        username = request.form["username"]
        output = requests.post('http://127.0.0.1:8888/recipeAPI/query/createUser/',data=username)
        return redirect('/recipes')
    else:
        users = requests.get('http://127.0.0.1:8888/recipeAPI/query/getUsers')
        return render_template("recipes.html",users=users.json())

@app.route('/deleteUser/<int:userID>')
def deleteUser(userID):
    output = requests.delete('http://127.0.0.1:8888/recipeAPI/query/deleteUser',data=str(userID))
    return redirect('/recipes')

@app.route('/recipes/<int:userID>', methods=["GET"])
def showUserRecipes(userID):
    recipes = requests.get('http://127.0.0.1:8888/recipeAPI/query/getUserRecipes/'+str(userID))
    return render_template("userRecipes.html",userID=userID,recipes=recipes.json())

@app.route('/recipes/details/<int:recipeID>', methods=["GET","POST"])
def showRecipeDetails(recipeID):
    if request.method == "POST":
        content = {}
        content['recipeID'] = recipeID
        content['ingredientName'] = request.form["ingredient"]
        content['quantity'] = request.form["quantity"]
        content['unit'] = request.form['unit']

        output = requests.post('http://127.0.0.1:8888/recipeAPI/query/addIngredient/',data=json.dumps(content))
        return redirect('/recipes/details/'+str(recipeID))
    else:
        strID = str(recipeID)
        title = requests.get('http://127.0.0.1:8888/recipeAPI/query/getRecipeTitle/'+strID)
        ingredients = requests.get('http://127.0.0.1:8888/recipeAPI/query/getRecipeIngredients/'+strID)
        content = requests.get('http://127.0.0.1:8888/recipeAPI/query/getRecipeContent/'+strID)
        return render_template("recipeDetails.html",id=recipeID,title=title.text,ingredients=ingredients.json(),content=Markup(content.text))

@app.route('/recipes/add/<int:userID>',methods=["GET","POST"])
def createRecipe(userID):
    if request.method == "POST":
        content = {
            "userID" : userID,
            "recipeTitle" : request.form["recipeTitle"],
            "recipeContent" : request.form["recipeContent"],
            "ingredients" : ""
        }
        output = requests.post('http://127.0.0.1:8888/recipeAPI/query/createRecipe/',data=json.dumps(content))

        #Get the newly created recipe for the redirection
        recipes = requests.get('http://127.0.0.1:8888/recipeAPI/query/getUserRecipes/'+str(userID)).json()

        for r in recipes:
            print(r)
            if r["recipeTitle"] == request.form["recipeTitle"]:
                return redirect('/recipes/details/'+str(r["recipeID"]))

        return redirect('/recipes/details/1')
    else:
        return render_template("addRecipe.html",userID=userID)

@app.route('/recipes/delete/<int:userID>/<int:recipeID>')
def deleteRecipe(userID,recipeID):
    output = requests.delete('http://127.0.0.1:8888/recipeAPI/query/deleteRecipe',data=str(recipeID))
    return redirect('/recipes/'+str(userID))

@app.route('/updateRecipeIngredient/<int:recipeID>/<ingredientName>/<int:quantity>/<unit>', methods=["GET","POST"])
def updateRecipeIngredient(recipeID,ingredientName,quantity,unit):
    if request.method == "POST":
        content = {
            "recipeID" : recipeID,
            "ingredientName" : ingredientName,
            "quantity" : request.form["quantity"],
            "unit" : unit
        }
        print(content)
        output = requests.put("http://127.0.0.1:8888/recipeAPI/query/updateIngredientQuantity",data=json.dumps(content))
        return redirect('/recipes/details/'+str(recipeID))
    else:
        return render_template("updateRecipeIngredient.html",recipeID=recipeID,ingredientName=ingredientName,quantity=quantity,unit=unit)

@app.route('/deleteRecipeIngredient/<int:recipeID>/<ingredientName>')
def deleteRecipeIngredient(recipeID,ingredientName):
    content = {
        "recipeID" : recipeID,
        "ingredientName" : ingredientName
    }
    output = requests.delete('http://127.0.0.1:8888/recipeAPI/query/deleteIngredient',data=json.dumps(content))
    return redirect('/recipes/details/'+str(recipeID))

@app.route('/kitchens', methods=["GET"])
def kitchens():
    kitchens = requests.get('http://127.0.0.1:5050/').json()['output']
    return render_template("kitchens.html",kitchens=kitchens)

@app.route('/add/<kitchen>', methods=['GET'])
def addKitchen(kitchen):
    a = requests.post('http://127.0.0.1:5050/' + kitchen)
    return render_template("kitchens.html",kitchens=kitchen)

@app.route('/kitchens/<kitchen_name>', methods = ['GET'])
def showIngredients(kitchen_name):
    if request.method == 'GET':
        ingredients = requests.get('http://127.0.0.1:5050/' + kitchen_name).json()['output']
        return render_template("showKitchenInfo.html", kitchen = kitchen_name, ingredients = ingredients)

@app.route('/kitchens/<kitchen>/delete', methods = ['GET'])
def deleteKitchen(kitchen):
    success = requests.delete('http://127.0.0.1:5050/' + kitchen)
    #ingredients = requests.get('http://127.0.0.1:5050/' + kitchen).json()['output']
    return redirect('/kitchens')
    #return render_template("showKitchenInfo.html", kitchen = kitchen, ingredients = ingredients)

@app.route('/kitchens/<kitchen_name>/<name>/<count>/<unit>', methods = ['POST'])
def sendIngredients(kitchen_name, name, count, unit):
    print(unit)
    if unit == "none":
        url = "http://127.0.0.1:5050/" + kitchen_name + "/" + name + "/" + count
    else:
        url = "http://127.0.0.1:5050/" + kitchen_name + "/" + name + "/" + count + "/" + unit
    a = requests.post(url).json()['success']
    ingredients = requests.get('http://127.0.0.1:5050/' + kitchen_name).json()['output']
    return render_template("showKitchenInfo.html", kitchen = kitchen_name, ingredients = ingredients)

@app.route('/kitchens/<kitchen_name>/<name>/<count>', methods = ['PUT'])
def updateIngredients(kitchen_name, name, count):
    url = "http://127.0.0.1:5050/" + kitchen_name + "/" + name + "/" + count
    a = requests.put(url).json()['success']
    print(a)
    ingredients = requests.get('http://127.0.0.1:5050/' + kitchen_name).json()['output']
    return render_template("showKitchenInfo.html", kitchen = kitchen_name, ingredients = ingredients)

@app.route('/kitchens/<kitchen_name>/<name>', methods = ['DELETE'])
def deleteIngredients(kitchen_name, name):
    url = "http://127.0.0.1:5050/" + kitchen_name + "/" + name
    a = requests.delete(url).json()['success']
    print(a)
    ingredients = requests.get('http://127.0.0.1:5050/' + kitchen_name).json()['output']
    return render_template("showKitchenInfo.html", kitchen = kitchen_name, ingredients = ingredients)

@app.route('/spoonacular/<int:recipeID>/<recipeName>', methods= ["GET"])
def checkSpoonacular(recipeID,recipeName):
    #Search a recipe on spoonacular matching the name of the input
    urlSearch = "https://api.spoonacular.com/recipes/complexSearch?apiKey="+spoonacularKey
    urlSearch+="&query=" + recipeName
    
    searchOutput = requests.get(urlSearch).json()["results"]

    #Iterate until we find a recipe with the same name
    if len(searchOutput) > 0:
        id = 0
        for o in searchOutput:
            if o["title"] == recipeName.capitalize():
                id = o["id"]
                recipeImage = o["image"]
                name = recipeName.capitalize()
                break
        #We don't find the exact name? We'll consider the first result
        if id==0:
            name = searchOutput[0]["title"]
            id = searchOutput[0]["id"]
            recipeImage = searchOutput[0]["image"]

        status = "OK"
        urlGetNutrition = "https://api.spoonacular.com/recipes/"+str(id)+"/nutritionWidget.json?apiKey="+spoonacularKey
        outputNutrition = requests.get(urlGetNutrition).json()
        urlGetPrice = "https://api.spoonacular.com/recipes/"+str(id)+"/priceBreakdownWidget.json?apiKey="+spoonacularKey
        outputPrice = requests.get(urlGetPrice).json()

        nutrition = {
            "calories" : outputNutrition["calories"],
            "carbs" : outputNutrition["carbs"],
            "fat" : outputNutrition["fat"],
            "protein" : outputNutrition["protein"]
        }

        price = {
            "total" : outputPrice["totalCost"]/1000.0,
            "perServing" : outputPrice["totalCostPerServing"]/1000.0
        }
    else:
        status = "ERROR"
        recipeImage = "";
        nutrition = {}
        price = {}
        name = recipeName
    return render_template("spoonacular.html",status=status,recipeID=recipeID,recipeName=name,recipeImage=recipeImage,nutrition=nutrition,price=price)
