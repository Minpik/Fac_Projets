function changeMinDate() {
	var d = new Date();
	document.getElementById("date").min = d.toJSON().split("T")[0];
	document.getElementById("date").defaultValue = d.toJSON().split("T")[0];
	document.getElementById("heure").min = d.getHours() + ":" + d.getMinutes();
	document.getElementById("heure").defaultValue = d.getHours() + ":" + d.getMinutes();
}

var socket = io.connect("http://localhost:8080");
socket.on("message", function(message) {
    alert(message);
});

// envoie socket autocompletion depart
var depart = document.getElementsByName("depart")[0];
depart.addEventListener("input", function( event ) {
	if (depart.value.length > 0)
		socket.emit("search", depart.value);
}, false);

// envoie socket autocompletion destination
var destination = document.getElementsByName("destination")[0];
destination.addEventListener("input", function( event ) {
	if (destination.value.length > 0)
		socket.emit("search", destination.value);
}, false);

// on recoit les noms de rues
socket.on("autocompletion", function(datas) {
	var datasList = document.getElementById("datasList");
	datasList.innerHTML = "";
	datas.forEach(function(item){
		var option = document.createElement("option");
		option.value = item;
		datasList.appendChild(option);
	});
});
