const express = require("express");
const app = express();
const bodyParser = require("body-parser");
const functions = require("./functions");

args = "cinema";
if (process.argv.length == 3) {
  args = process.argv[2];
}
console.log(args);

app.use(express.static("public"));
app.use(bodyParser.urlencoded({ extended: true }));

app.get("/", function (req, res) {
  res.setHeader("Content-Type", "text/html");
  res.render("acceuil.ejs");
});

app.post("/rechercher", function (req, res) {
  let liste_filtree = functions.filtrer(
    req.body.mots_cles.split(" "),
    mots_interdits
  );
  const [pages, time] = functions.getPages(liste_filtree, mots_pages, titres);

  res.setHeader("Content-Type", "text/html");
  res.render("resultats.ejs", {
    mots_clefs: req.body.mots_cles,
    pages: pages,
    time: time,
  });
});

app.use(function (req, res, next) {
  res.setHeader("Content-Type", "text/plain");
  res.status(404).send("Page introuvable !");
});

let mots_pages, mots_interdits, titres;
functions.load(args, function (x, y, z) {
  app.listen(8080);
  mots_pages = x;
  mots_interdits = y;
  titres = z;
});
