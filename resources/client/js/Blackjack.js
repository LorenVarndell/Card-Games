function pageLoad() {
    var canvas = document.getElementById("myCanvas");
    window.addEventListener('resize', resizeCanvas(canvas), false);

    //document.getElementById("game").style.display ="none";
    document.getElementById("getBlackjackSession").addEventListener("click", getBlackjackSessionCode);
    document.getElementById("createBlackjackSession").addEventListener("click", createBlackjackSessionCode);
    console.log("Creating cookie/UserID");

    if (sessionStorage.getItem("UserID") === null) {
        var url = "/Users/add/";

        fetch(url, {
            method: "POST",
        }).then(response => {
            return response.json()
        }).then(response => {
            if (response.hasOwnProperty("Error")) {   //checks if response from server has a key "Error"
                alert(JSON.stringify(response));        // if it does, convert JSON object to string and alert
            } else {
                //console.log(localStorage.getItem("UserID"));
                //Cookies.set("UserID",response.UserID);
                sessionStorage.setItem("UserID", response.UserID);
                console.log("Cookie set")
            }
        });
    }
}

function resizeCanvas(canvas) {
    canvas.height = window.innerHeight;
    canvas.width = window.innerWidth;
}


function getBlackjackSessionCode(){
    const SessionID = document.getElementById("getBlackjackSessionID").value;
    let isNum = /^\d+$/.test(SessionID);
    // checks to see if they're any numbers in the String, using Boolean
    if (isNum === true && SessionID.indexOf(' ') == -1) {
        console.log("Fetching session code");

        var url = "/Blackjack/get/";

        fetch(url + SessionID, {
            method: "GET",
        }).then(response => {
            return response.json();                 //return response to JSON
        }).then(response => {
            if (response.hasOwnProperty("Error2")) { //checks if response from server has a key "Error"
                alert(JSON.stringify(response));    // if it does, convert JSON object to string and alert
            } else if (response.hasOwnProperty("Error1")) {
                console.log(JSON.stringify(response));
            } else {
                console.log(response);
                document.getElementById("game").style.display = "block";
                document.getElementById("inputs").style.display="none";
                //this function will create an HTML table of the data (as we
                // did in lesson 2
            }
        });
    }
}

function createBlackjackSessionCode(){
    console.log("Invoked postBlackjackAdd()");

    const formData = new FormData(document.getElementById("formCreateBlackjackSession"));
    var url = "/Blackjack/add";

    fetch(url, {
        method: "POST",
        body: formData,
    }).then(response => {
        return response.json()
    }).then(response => {
        if (response.hasOwnProperty("Error")) {   //checks if response from server has a key "Error"
        alert(JSON.stringify(response));        // if it does, convert JSON object to string and alert
    } else {
        console.log(response);
        document.getElementById("game").style.display = "block";
        document.getElementById("inputs").style.display="none";
        //window.open("/client/home.html", "_self");   //if not open this page (for example!)
    }
    });

}


var ws = new WebSocket("ws://localhost:8081/client/blackjack.html");
ws.onopen = function() {
    console.log("Socket opened");
    ws.send("Hello Server");
};
