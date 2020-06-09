const fs = require("fs");
const readline = require("readline");

module.exports = {
  filtrer: function (liste_mots_cles, mots_interdits) {
    mots = new Set(liste_mots_cles.map((v) => v.toLowerCase()));
    for (mot of mots) {
      if (mots_interdits.includes(mot) || mot == "") {
        mots.delete(mot);
      }
    }

    return mots;
  },

  getPages: function (liste_mots_cles, mots_pages, titres) {
    console.log(mots);
    let start = process.hrtime();
    let first = true;
    let selected_pages = [];
    let count = 0;
    let MAX = 20;
    for (mot of mots) {
      let pages_to_compare = mots_pages[mot];
      if (pages_to_compare != undefined) {
        if (first) {
          selected_pages = pages_to_compare.slice();
          first = false;
        } else {
          let i = 0,
            j = 0;

          let new_selected_pages = [];
          let last_selected_j = 0;
          while (i < selected_pages.length && j < pages_to_compare.length) {
            if (selected_pages[i] == pages_to_compare[j]) {
              new_selected_pages.push(selected_pages[i]);
              last_selected_j = j;
              i++;

              count++;
              if (count == MAX) break;
            }

            j++;

            if (
              j == pages_to_compare.length &&
              i != selected_pages.length - 1
            ) {
              i++;
              j = last_selected_j;
            }
          }
          selected_pages = new_selected_pages;

          if (count == MAX) break;
        }
      } else {
        return [[], process.hrtime(start)[1] / 1000000];
      }
    }

    let pages_titres = new Array(MAX);
    for (let i = 0; i < selected_pages.length; i++) {
      pages_titres[i] = titres[selected_pages[i]];
      if (i == MAX) break;
    }

    return [pages_titres, process.hrtime(start)[1] / 1000000];
  },

  load: function (fileName, callBack) {
    let mots_pages = {};
    let mots_interdits;
    let titres;

    const r1 = readline.createInterface({
      input: fs.createReadStream("../cli_sorted_" + fileName + ".txt"),
      output: process.stdout,
      terminal: false,
    });

    let count = 0;
    let C_length = 0,
      L_length = 0;
    let etape = null;
    let L = null,
      I = null;
    r1.on("line", (line) => {
      if (etape == null) {
        C_length = parseInt(line);
        etape = "C";
      } else if (etape == "C") {
        if (count == C_length - 1) {
          etape = "I";
          count = 0;
        } else {
          count++;
        }
      } else if (etape == "I") {
        if (I == null) I = new Array(C_length);

        I[count] = parseInt(line);

        if (count == C_length - 1) {
          etape = "L";
          count = 0;
        } else {
          count++;
        }
      } else {
        if (L_length == 0) {
          L_length = parseInt(line);
        } else {
          if (L == null) L = new Array(L_length);

          L[count] = parseInt(line);
          count++;
        }
      }
    });

    r1.on("close", (line) => {
      console.log("fin lecture 1");

      count = 0;
      const r2 = readline.createInterface({
        input: fs.createReadStream("../most_count_sorted_" + fileName + ".txt"),
        output: process.stdout,
        terminal: false,
      });

      r2.on("line", (line) => {
        let end;
        /*if (count != L_length - 1) {
					end = L[count + 1];
				} else {
					end = C_length;
				}*/
        end = L[count + 1];

        let pages = new Array(end - L[count]);

        let n = 0;
        for (let i = L[count]; i < end; i++) {
          pages[n] = I[i];
          n++;
        }

        mots_pages[line] = pages;

        count++;
      });

      r2.on("close", (line) => {
        console.log("fin lecture 2");
        try {
          mots_interdits = fs.readFileSync("../fw", "utf8").split("\n");
        } catch (err) {
          console.error(err);
        }

        try {
          titres = fs
            .readFileSync("../title_" + fileName + ".txt", "utf8")
            .split("\n");
        } catch (err) {
          console.error(err);
        }

        callBack(mots_pages, mots_interdits, titres);

        console.log("Serveur lancé");
      });
    });
  },
};
