function pageLoad() {
    //document.getElementById("game").style.display ="none";
    document.getElementById("hitBtn").style.display="none";
    document.getElementById("getBlackjackSession").addEventListener("click", getBlackjackSessionCode);
    document.getElementById("createBlackjackSession").addEventListener("click", createBlackjackSessionCode);
    document.getElementById("startBtn").addEventListener("click", initializeGame);
    document.getElementById("hitBtn").addEventListener("click", hitBtn);
    console.log("Creating cookie/UserID");
    var UserID = localStorage.getItem("UserID");
    var cookie = false;
    setInterval(function(){
        if (localStorage.getItem("UserID") === null || localStorage.getItem("UserID") != UserID) {
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
                    UserID = response.UserID;
                    console.log(UserID);
                    cookie = false;
                }
            });
        }
        console.log(playerInp);
        var url1 = "/BlackjackUpdate/add/";
        fetch(url1 + UserID +  "/" + playerInp, {
            method: "POST",
        }).then(response => {
            return response.json();                 //return response to JSON
        }).then(response => {
            if (response.hasOwnProperty("Error")) {
                console.log(JSON.stringify(response));
            } else {
                console.log(response);
                if (response.turn == true)  {
                    document.getElementById("hitBtn").style.display = "block";
                    document.getElementById("hitBtn").style.border = "5px solid black";
                    console.log("initializing Game");
                } else if (response.turn == false) {
                    document.getElementById("messageBox").style.display = "none";
                    document.getElementById("messageBox").style.border = "0px solid black";
                    document.getElementById("hitBtn").style.display = "none";
                    document.getElementById("hitBtn").style.border = "0px solid black";
                }
                playerInp = "1";
            }
        });

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
                document.getElementById("inputs").style.display="none";
                document.getElementById("game").style.display = "block";
                document.getElementById("messageBox").style.display = "block";
                document.getElementById("messageBox").style.border = "5px solid black";
                document.getElementById("messageBox").innerHTML  = "Wait for Owner to start game...";
                owner = false;
                return owner;
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
        document.getElementById("inputs").style.display="none";
        document.getElementById("game").style.display = "block";
        document.getElementById("startBtn").style.display = "block";
        document.getElementById("startBtn").style.border = "5px solid black";
        document.getElementById("startBtn").innerHTML  = "Press to Start";
        owner = true;
        return owner;
        //window.open("/client/home.html", "_self");   //if not open this page (for example!)
    }
    });

}

function initializeGame() {

    var url = "/BlackjackStart/add/";
    if (owner == true) {
        fetch(url + UserID, {
            method: "POST",
        }).then(response => {
            return response.json()
        }).then(response => {
            if (response.hasOwnProperty("Error2")) {   //checks if response from server has a key "Error"
                alert(JSON.stringify(response));        // if it does, convert JSON object to string and alert
            } else if (response.hasOwnProperty("Error")) {
                console.log(JSON.stringify(response));
            }else {
                console.log(response);
                document.getElementById("startBtn").style.display = "none";
                document.getElementById("startBtn").style.border = "0px solid black";
                document.getElementById("hitBtn").style.display = "block";
                document.getElementById("hitBtn").style.border = "5px solid black";
                console.log("initializing Game");
            }
        });
    } else {
        console.log("You have to be the owner of this session!");
    }
}

function hitBtn() {
    playerInp = "hit";
    console.log(playerInp);
    return playerInp;
}
var owner = false;
var playerInp = "1";
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

/*
var canvas = document.querySelector("canvas");
var ctx =  canvas.getContext("2d");
var img = new Image();
window.addEventListener("mousedown",
    function(event) {
    console.log(window.devicePixelRatio);
    var multiplierNum = window.devicePixelRatio;
    if (event.clientX >= 424 && event.clientY >= 60 && event.clientX <= 1623 && event.clientY <= 808)  {
        ctx.font=  "150px Arial";
        ctx.fillText("Start:",1400, 910);

        //img.src = './img/Cards/2D.png';
        //ctx.drawImage(img, 0, 0);
    }
    //console.log(event);
})
 */
/*
img.onload = function() {
    ctx.drawImage(img, 0, 0);

}
*/
//console.log(Math.round(window.devicePixelRatio*100));
//img.src = './img/Cards/2D.png';

