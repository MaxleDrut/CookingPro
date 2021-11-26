move the "inventoryAPI" folder out of the repository folder so it is seperate
	to setup flask for both inventoryAPI and the web app:
	In a terminal run these commands
	1. module add anaconda3; python3 -m venv flask; source flask/bin/activate
	2. flask/bin/pip install flask; flask/bin/pip install flask-login; flask/bin/pip install flask-mail
	3. flask/bin/pip install flask-sqlalchemy; flask/bin/pip install flask-migrate; flask/bin/pip install flask-whooshalchemy
	4. flask/bin/pip install flask-wtf; flask/bin/pip install flask-babel; flask/bin/pip install coverage;
	5. flask/bin/pip install flask-restful; flask/bin/pip requests
  
  to start the service use: flask run --port=5050
