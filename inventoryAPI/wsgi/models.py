from app import db

class Ingredient(db.Model):
    id = db.Column(db.Integer, primary_key = True)
    kitchen = db.Column(db.String(50), index = True)
    hasUnit = db.Column(db.Boolean) #eggs have no unit, whereas milk or flour have a mass/volume
    name = db.Column(db.String(50), index = True)
    unitName = db.Column(db.String(10), nullable = True) #g, ml, etc...
    count = db.Column(db.Integer) #4 eggs, 400ml flour etc...
    expires = db.Column(db.DateTime)

class Kitchen(db.Model):
    id = db.Column(db.Integer, primary_key = True)
    name = db.Column(db.String(50), index = True, unique = True)