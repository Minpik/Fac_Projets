var express = require("express");
var bodyParser = require("body-parser");
var sha1 = require('sha1');

var app = express();
var server = require("http").createServer(app); 
server.listen(8080);
app.use(bodyParser.urlencoded({ extended: true }));
app.use(express.static("public"));

//----------SQL----------//
var mysql = require("mysql");
var con = mysql.createConnection({
	host: "localhost",
	user: "root",
	password: "",
	database: "uber"
});

con.connect(function(err) {
	if (err) throw err;
	console.log("Connected to mysql!");
});
//----------END----------//


//----------EXPRESS-SESSION----------//
var session = require("express-session")({
	secret: "my-secret",
	resave: true,
	saveUninitialized: true
});
app.use(session);

app.use(function(req, res, next){
    if (typeof(req.session.myId) == 'undefined') {
        req.session.myId = -1;
		req.session.type = "none";
    }
    next();
});
//----------END----------//

//----------SOCKET.IO----------//
var io = require("socket.io")(server);
var sharedSession = require("express-socket.io-session");

io.use(sharedSession(session));

io.on("connection", function(socket) {
	// un quelqu'un se connecte, on cherche les messages à envoyer
	for (var i = 0; i < listUtilisateurs.length; i++) {
		if (listUtilisateurs[i].id == socket.handshake.session.myId && listUtilisateurs[i].type == socket.handshake.session.type) {
			listUtilisateurs[i].socket = socket;
			for (var j = listMessages.length - 1; j >= 0; j--) {
				if (listMessages[j].id == socket.handshake.session.myId && listMessages[j].type == socket.handshake.session.type) {
					listUtilisateurs[i].socket.emit("message", listMessages[j].mess);
					listMessages.splice(j, 1);
				}
			}
		}
	}

	// autocompletion, on envoie au plus 5 rues
	socket.on("search", function(word) {
		var sql = "SELECT nom FROM france_rue WHERE nom LIKE '%" + word + "%' LIMIT 0, 5";
		con.query(sql, function(err, results) {
			if (err) throw err;
			var datas = [];
			if (results.length > 0)
				for (var i = 0; i < results.length; i++) {
					datas.push(results[i].nom);
				}
			socket.emit("autocompletion", datas);
		});
	});
});
//----------END----------//

app.get("/", function(req, res) {
	if (req.session.myId == -1)
		res.render("acceuil.ejs", {session: req.session});
	else {
		if (req.session.type == "passager")
			res.redirect("/reservation");
		else
			res.redirect("/courses");
	}
});

app.post("/inscription", function(req, res) {
	function select(type) {
		var sql = "SELECT id FROM " + type + "s WHERE mail = ?";
		var datas = req.body.mail;
		con.query(sql, datas, function(err, results) {
			if (err) throw err;
			if (results.length == 0) {
				insert(type);
			} else
				res.render("erreur.ejs", {msg: "Mail déjà utilisé !"});	
		});	
	}
	
	function insert(type) {
		var sql = "INSERT INTO " + type + "s SET ?";
		if (type == "passager") {
			var datas = {
				prenom: req.body.prenom,
				nom:    req.body.nom,
				mail:   req.body.mail,
				mdp:    sha1(req.body.mdp)
			};
		} else {
			var datas = {
				prenom: req.body.prenom,
				nom:    req.body.nom,
				mail:   req.body.mail,
				mdp:    sha1(req.body.mdp),
				service: req.body.service
			};
		}
		con.query(sql, datas, function(err) {
			if (err) throw err;
			selectId(type);
			res.redirect("/");
		});
	}

	function selectId(type) {
		var sql = "SELECT id FROM " + type + "s WHERE mail = ?"
		var datas = req.body.mail;
		con.query(sql, datas, function(err, results) {
			if (err) throw err;
			donnerUneCourse(results[0].id);
		});
	}

	(req.body.type == "passager") ? select("passager") : select("chauffeur");
});

app.post("/connexion", function(req, res) {
	function connexion(type) {
		var sql = "SELECT id FROM " + type + "s WHERE mail = ? AND mdp = ?";
		var datas = [req.body.mail, sha1(req.body.mdp)];
		con.query(sql, datas, function(err, results) {
			if (err) throw err;
			if (results.length > 0) {
				req.session.myId = results[0].id;
				req.session.type = type;
				addUtilisateur(results[0].id, type);
				res.redirect("/");
			} else if (type == "passager") {
				connexion("chauffeur");
			} else {
				res.render("erreur.ejs", {msg: "Mail ou mot de passe incorrect !"});
			}
		});
	}

	connexion("passager");
});

app.get("/deconnexion", function(req, res) {
	if (req.session.myId != -1) {
		removeUtilisateur(req.session.myId, req.session.type);
		req.session.myId = -1;
		req.session.type = "none";
	}
	res.redirect("/");
});

app.get("/reservation", function(req, res) {
	res.render("reservation.ejs", {session: req.session});
});

app.post("/reservationPost", function(req, res) {
	function select() {
		var sql = "SELECT id FROM chauffeurs WHERE libre = 1 AND service = ?";
		var datas = req.body.service;
		con.query(sql, datas, function(err, results) {
			if (err) throw err;
			if (results.length > 0) {
				insert(results[0].id);
				sendMessage(results[0].id, "chauffeur", "Une course vous a été attribuée");
			} else {
				insert(-1);
			}
		});
	}
	
	function insert(idChauffeur) {
		if (req.session.myId != -1) {
			var sql = "INSERT INTO courses SET ?";
			var datas = {
				idPassager:  req.session.myId,
				idChauffeur: idChauffeur,
				depart:      req.body.depart,
				destination: req.body.destination,
				service:     req.body.service,
				dateTime:    req.body.date + " " + req.body.heure + ":00"
			};
			con.query(sql, datas, function(err) {
				if (err) throw err;
				res.redirect("/courses");
			});
		}
	}

	select();
});

app.get("/courses", function(req, res) {
	var sql = "SELECT *, DATE_FORMAT(dateTime, '%y-%m-%d à %H:%i:%s') as dateTime FROM courses WHERE idPassager = ? OR idChauffeur = ? ORDER BY dateTime desc";
	var datas = [req.session.myId, req.session.myId];
	con.query(sql, datas, function(err, results) {
		if (err) throw err;
		var aVenir = [];
		var enCours = [];
		var terminees = [];
		for (var i = 0; i < results.length; i++) {
			if (results[i].etat == 0)
				aVenir.push(results[i]);
			else if (results[i].etat == 1)
				enCours.push(results[i]);
			else
				terminees.push(results[i]);
		}
		var courses = [aVenir, enCours, terminees];
		res.render("courses.ejs", {session: req.session, courses: courses});
	});
});

app.post("/annulerCourse", function(req, res) {
	function select() {
		var sql = "SELECT id FROM courses WHERE id = ? AND etat = 0";
		var datas = req.body.idCourse;
		con.query(sql, datas, function(err, results) {
			if (err) throw err;
			if (results.length > 0)
				updateCourse();
			else
				res.render("erreur.ejs", {msg: "Vous ne pouvez pas annuler cette course !"});	
		});
	}

	function updateCourse() {
		var sql = "UPDATE courses SET etat = 2 WHERE id = ?";
		var datas = req.body.idCourse;
		con.query(sql, datas, function(err, results) {
			if (err) throw err;
			res.redirect("/courses");
		});	
	}

	select();
});

app.post("/demarrerCourse", function(req, res) {
	function select() {
		var sql = "SELECT id, idPassager FROM courses WHERE id = ?";
		var datas = req.body.idCourse;
		con.query(sql, datas, function(err, results) {
			if (err) throw err;
			if (results.length > 0) {
				updateCourse();
				updateChauffeur();
				sendMessage(results[0].idPassager, "passager", "Le chauffeur viens d'arriver au lieu de rendez-vous");
			}
		});
	}

	function updateCourse() {
		var sql = "UPDATE courses SET etat = 1 WHERE id = ?";
		var datas = req.body.idCourse;
		con.query(sql, datas, function(err, results) {
			if (err) throw err;
		});	
	}

	function updateChauffeur() {
		var sql = "UPDATE chauffeurs SET libre = 0 WHERE id = ?";
		var datas = req.session.myId;
		con.query(sql, datas, function(err, results) {
			if (err) throw err;
			res.redirect("/courses");
		});	
	}

	select();
});

app.post("/terminerCourse", function(req, res) {
	function select() {
		var sql = "SELECT id FROM courses WHERE id = ?";
		var datas = req.body.idCourse;
		con.query(sql, datas, function(err, results) {
			if (err) throw err;
			if (results.length > 0) {
				updateCourse();
				updateChauffeur();
				donnerUneCourse(req.session.myId);
			}
		});
	}

	function updateCourse() {
		var sql = "UPDATE courses SET etat = 2 WHERE id = ?";
		var datas = req.body.idCourse;
		con.query(sql, datas, function(err, results) {
			if (err) throw err;
		});	
	}

	function updateChauffeur() {
		var sql = "UPDATE chauffeurs SET libre = 1 WHERE id = ?";
		var datas = req.session.myId;
		con.query(sql, datas, function(err, results) {
			if (err) throw err;
		});	
	}
	
	select();
	res.redirect("/courses");
});

app.get("/profil", function(req, res) {
	if (req.session.type == "passager")
		var sql = "SELECT nom, prenom, DATE_FORMAT(dateInscription,'%y-%m-%d à %H:%i:%s')"
			+ "as dateInscription FROM passagers WHERE id = ?";
	else
		var sql = "SELECT nom, prenom, service, DATE_FORMAT(dateInscription,'%y-%m-%d à %H:%i:%s')"
			+ "as dateInscription FROM chauffeurs WHERE id = ?";
	var datas = [req.session.myId];
	con.query(sql, datas, function(err, results) {
		if (err) throw err;
		if (results.length > 0)
			res.render("profil.ejs", {session : req.session, user: results[0]});
	});
});

app.post("/modifierProfil", function(req, res) {
	function testMdp(type) {
		var sql = "SELECT id FROM " + type + "s WHERE id = ? AND mdp = ?";
		var datas = [req.session.myId, sha1(req.body.mdp)]
		con.query(sql, datas, function(err, results) {
			if (err) throw err;
			if (results.length > 0) {
				(type == "passager") ? updatePassager() : updateChauffeur();
			} else {
				res.render("erreur.ejs", {msg: "Mot de passe incorrect !"});
			}
		});
	}
	
	function updatePassager() {
		var sql = "UPDATE passagers SET mdp = ?";
		var datas = sha1(req.body.nouveauMdp);
		con.query(sql, datas, function(err) {
			if (err) throw err;
			res.redirect("/");
		});
	}

	function updateChauffeur() {
		if (req.body.nouveauMdp == "") {
			var sql = "UPDATE chauffeurs SET service = ?";
			var datas = [req.body.service]
		} else {
			var sql = "UPDATE chauffeurs SET mdp = ? AND service = ?";
			var datas = [sha1(req.body.nouveauMdp), req.body.service]
		}
		con.query(sql, datas, function(err) {
			if (err) throw err;
			res.redirect("/");
		});
	}

	testMdp(req.session.type);
});

// se lance si aucune page ne correspond
app.use(function(req, res, next) {
    res.setHeader("Content-Type", "text/plain");
    res.status(404).send("Page introuvable !");
});


// listes des utilisateurs connectés
var listUtilisateurs = [];
function addUtilisateur(id, type) {
	for (var i = 0; i < listUtilisateurs.length; i++) {
		if (listUtilisateurs[i].id == id && listUtilisateurs[i].type == type)
			return;
	}
	
	listUtilisateurs.push({id:id, type:type, socket:null});
}

function removeUtilisateur(id, type) {
	for (var i = 0; i < listUtilisateurs.length; i++) {
		if (listUtilisateurs[i].id == id && listUtilisateurs[i].type == type) {
			listUtilisateurs.splice(i, 1);
			break;
		}
	}	
}

// listes des messages à envoyés
var listMessages = [];
function addMessage(id, type, mess) {
	listMessages.push({id:id, type:type, mess:mess});
}

function sendMessage(id, type, mess) {
	for (var i = 0; i < listUtilisateurs.length; i++) {
		if (listUtilisateurs[i].id == id && listUtilisateurs[i].type == type) {
			listUtilisateurs[i].socket.emit('message', mess);
			return;
		}
	}

	addMessage(id, type, mess);
}

// utiliser lorsqu'un chauffeur termine une course ou vient de s'inscrire sur le site
function donnerUneCourse(idChauffeur) {
	function selectChauffeur() {
		var sql = "SELECT service FROM chauffeurs WHERE id = ?";
		var datas = idChauffeur;
		con.query(sql, datas, function(err, results) {
			if (err) throw err;
			if (results.length > 0) {
				selectCourse(results[0].service)
			}
		});
	}

	function selectCourse(service) {
		var sql = "SELECT id FROM courses WHERE idChauffeur = -1 AND service = ?";
		var datas = service;
		con.query(sql, datas, function(err, results) {
			if (err) throw err;
			if (results.length > 0) {
				console.log("bbb");
				updateCourse(results[0].id);
				sendMessage(idChauffeur, "chauffeur", "Une course vous a été attribuée");
				//updateChauffeur();
			}
		});
	}

	function updateCourse(id) {
		var sql = "UPDATE courses SET idChauffeur = ? WHERE id = ?";
		var datas = [idChauffeur, id];
		con.query(sql, datas, function(err) {
			if (err) throw err;
		});
	}

	selectChauffeur();
}
