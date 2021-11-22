from flask import Flask
from flask_sqlalchemy import SQLAlchemy
from flask_migrate import Migrate
from flask_restful import Api

inventoryApp = Flask(__name__)
api = Api(inventoryApp)
inventoryApp.config.from_object('config')
db = SQLAlchemy(inventoryApp)

migrate = Migrate(inventoryApp, db)

from inventoryApp import inventory_api, models
