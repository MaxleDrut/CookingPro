from app import app, api, models, db
from flask import request, session, jsonify

import time

@app.route('/test', methods = ['GET'])
def test():
    testService()

@app.route('/', methods = ['GET'])
def getKitchens():
    data = []
    kitchens = models.Kitchen.query.all()
    for k in kitchens:
        data.append(k.name)
    return jsonify(output = data)

@app.route('/<name>', methods = ['GET', 'POST', 'DELETE'])
def kitchen(name):
    #get ingredients, example: get(localhost:5000/kitchen_1)
    data = []
    if request.method == 'GET':
        ingredients = models.Ingredient.query.all()
        for i in ingredients:
            ingredient = []
            if i.kitchen == name:
                unit = "none"
                if i.hasUnit == True:
                    unit = i.unitName
                ingredient.append(i.name)
                ingredient.append(i.count)
                ingredient.append(unit)
                print(i.name, i.count)
                data.append(ingredient)
        return jsonify(output = data)
    #create new kitchen, example: post(localhost:5000/kitchen_1)
    elif request.method == 'POST':
        kitchen = models.Kitchen()
        kitchen.name = name
        try:
            db.session.add(kitchen)
            db.session.commit()
            return {"post" : name}
        except:
            db.session.rollback()
            return {"post" : name}
    #delete a kitchen, example: delete(localhost:5000/kitchen_1)
    elif request.method == 'DELETE':
        ingredients = models.Ingredient.query.filter_by(kitchen = name)
        for i in ingredients:
            try:
                db.session.delete(i)
                db.session.commit()
            except:
                db.session.rollback()
        try:
            kitchen = models.Kitchen.query.filter_by(name=name).all()[0]
            db.session.delete(kitchen)
            db.session.commit()
            return {"delete" : name}
        except:
            db.session.rollback()
            return {"delete" : name}

@app.route('/<kitchen>/<name>/<count>', methods = ['PUT', 'POST'])
def add_ingredient_no_unit(kitchen, name, count):
    #add a new ingredient
    if request.method == 'POST':
        return add_ingredient(kitchen, name, count, "none")
    #update an ingredient amount, example: put(localhost:5000/kitchen_1/flour/350)
    elif request.method == 'PUT':
        ingredients = models.Ingredient.query.filter_by(kitchen = kitchen)
        for i in ingredients:
            if i.name == name:
                i.count = count
                try:
                    db.session.commit()
                    return {"success" : "true"}, 201
                except Exception as e:
                    print(e)
                    db.session.rollback()
                    return {"success" : "false"}, 404

@app.route('/<kitchen>/<name>/<count>/<unit>', methods = ['POST'])
def add_ingredient(kitchen, name, count, unit):
    #add a new ingredient, example: post(localhost:5000/kitchen_1/flour/400/g)
    if request.method == 'POST':
        k = models.Kitchen.query.filter_by(name=kitchen).all()[0]
        if k == None:
            return {"success" : "false"}, 404

        ingredients = models.Ingredient.query.filter_by(kitchen = kitchen)
        for i in ingredients:
            if i.name == name:
                return {"success" : "already exists"}, 201

        ingredient = models.Ingredient()
        if unit == "none":
            ingredient.hasUnit = False
        else:
            ingredient.hasUnit = True
            ingredient.unitName = unit

        ingredient.name = name
        ingredient.count = count
        ingredient.kitchen = kitchen

        try:
            db.session.add(ingredient)
            db.session.commit()
            return {"success" : "true"}, 201
        except Exception as e:
            print(e)
            db.session.rollback()
            return {"success" : "false"}, 404
    else:
        return {"success" : "false"}, 404


@app.route('/<kitchen>/<name>', methods = ['DELETE'])
def remove_ingredient(kitchen, name):
    #delete an ingredient, example: delete(localhost:5000/kitchen_1/flour)
    if request.method == 'DELETE':
        ingredients = models.Ingredient.query.filter_by(kitchen = kitchen)
        for i in ingredients:
            if(i.name == name):
                try:
                    db.session.delete(i)
                    db.session.commit()
                    return {"success" : "true"}
                except:
                    db.session.rollback()
                    return {"success" : "false"}

    return {"success" : "false"}


def testService():
    numTests = 20
    now = time.time() * 1000

    for i in range(numTests):
        getKitchens()

        
    getKitchensElapsed = now - (time.time() * 1000)
    print(getKitchensElapsed, "ms")