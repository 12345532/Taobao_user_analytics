from flask import render_template,Flask
app = Flask(__name__)

@app.route('/')

def req_file():
	return render_template("view.html")
	
if __name__ == '__main__':
	app.DEBUG = True
	app.run(debug=True, port=5000, host='127.0.0.1')
