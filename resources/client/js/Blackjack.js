function pageLoad() {
    var canvas = document.getElementById("myCanvas");
    //document.getElementById("game").style.display ="none";
    document.getElementById("getBlackjackSession").addEventListener("click", getBlackjackSessionCode);
    document.getElementById("createBlackjackSession").addEventListener("click", createBlackjackSessionCode);
    console.log("Creating cookie/UserID");
    var UserID = localStorage.getItem("UserID");
    var cookie = false;
    setInterval(function(){
        if (localStorage.getItem("UserID") === null) {
            if (UserID === null) {
                UserID = 1;
            }
            console.log(UserID);
            var url = "/Users/delete/";

            fetch(url + UserID, {
                method: "POST",
            }).then(response => {
                return response.json()
            }).then(response => {
                if (response.hasOwnProperty("Error")) {   //checks if response from server has a key "Error"
                    alert(JSON.stringify(response));        // if it does, convert JSON object to string and alert
                } else {
                    //console.log(localStorage.getItem("UserID"));
                    //Cookies.set("UserID",response.UserID);
                    localStorage.setItem("UserID", response.UserID);
                    console.log("Cookie set")
                    cookie = true;
                    UserID = localStorage.getItem("UserID");
                    console.log(UserID);
                    cookie = false;
                }
            });
        }
    }, 1000);

}


function getBlackjackSessionCode(){
    const SessionID = document.getElementById("getBlackjackSessionID").value;
    let isNum = /^\d+$/.test(SessionID);
    // checks to see if they're any numbers in the String, using Boolean
    if (isNum === true && SessionID.indexOf(' ') == -1) {
        console.log("Fetching session code");
        UserID = localStorage.getItem("UserID");
        var url = "/Blackjack/get/";

        fetch(url + SessionID +  "/" + UserID , {
            method: "GET",
        }).then(response => {
            return response.json();                 //return response to JSON
        }).then(response => {
            if (response.hasOwnProperty("Error2")) { //checks if response from server has a key "Error"
                alert(JSON.stringify(response));    // if it does, convert JSON object to string and alert
            } else if (response.hasOwnProperty("Error1")) {
                console.log(JSON.stringify(response));
            } else if (response.hasOwnProperty("Error")) {
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

    //const formData = new FormData(document.getElementById("formCreateBlackjackSession"));
    UserID = localStorage.getItem("UserID");
    console.log(UserID);
    var url = "/Blackjack/add/";

    fetch(url + UserID, {
        method: "POST",
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

/*
var ws = new WebSocket("ws://localhost:8081/client/blackjack.html");
ws.onopen = function() {
    console.log("Socket opened");
    ws.send("Hello Server");
};
*/
/*
var canvas = document.querySelector("canvas");
canvas.width  = window.innerWidth*0.8;
canvas.height = window.innerHeight*0.8;

window.addEventListener("resize", function(){
    canvas.width = window.innerWidth*0.8;
    canvas.height = window.innerHeight*0.8;
})
 */

var canvas = document.querySelector("canvas");
var ctx =  canvas.getContext("2d");
var img = new Image();
img.onload = function() {
    ctx.drawImage(img, 10, 10, 100, 100);
}
img.src = 'img/blue.jpg';

