function pageLoad() {
    //document.getElementById("game").style.display ="none";
    document.getElementById("hitBtn").style.display="none";
    document.getElementById("standBtn").style.display="none";
    document.getElementById("score").style.display="none";
    document.getElementById("score").style.top = "650px";
    document.getElementById("score").style.width = "600px";
    document.getElementById("hitBtn").style.top = "175px";
    document.getElementById("standBtn").style.top = "250px";
    document.getElementById("result").style.display = "none";
    document.getElementById("result").style.border = "0px solid black";
    document.getElementById("result").style.fontSize = "75px";
    document.getElementById("result").style.width = "800px";
    document.getElementById("result").style.top = "550px";
    document.getElementById("player").style.display = "none";
    document.getElementById("player").style.top = "200px";
    document.getElementById("nextRoundBtn").style.display="none";
    document.getElementById("nextRoundBtn").style.top = "200px";
    document.getElementById("getBridgeSession").addEventListener("click", getBridgeSessionID);
    document.getElementById("createBridgeSession").addEventListener("click", createBridgeSessionCode);
    document.getElementById("startBtn").addEventListener("click", initializeGame);
    document.getElementById("hitBtn").addEventListener("click", hitBtn);
    document.getElementById("nextRoundBtn").addEventListener("click", nextRoundBtn);
    document.getElementById("standBtn").addEventListener("click", standBtn);
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
        var url1 = "/BridgeUpdate/add/";
        fetch(url1 + UserID +  "/" + playerInp, {
            method: "POST",
        }).then(response => {
            return response.json();                 //return response to JSON
        }).then(response => {
            if (response.hasOwnProperty("Error")) {
                console.log(JSON.stringify(response));
            } else if (response.hasOwnProperty("Error3")){
                console.log(JSON.stringify(response));
            } else if (response.hasOwnProperty("Error4")){
                console.log(JSON.stringify(response));
            } else {
                console.log(response);

                //document.getElementById("inputs").style.display="none";
                //document.getElementById("game").style.display = "block";
                if (response.turn == 4) {

                }
                if (response.cards !== undefined) {

                    document.getElementById("cardIMG").innerHTML = "";
                    for (var i = 2; i < ((response.cards).length) + 2; i = i + 2) {
                        let img = document.createElement("img");
                        console.log(i);
                        img.src = './img/Cards/' + (response.cards).substring(i - 2, i) + '.png';
                        console.log(img.src);
                        img.classList.add("bridgePlayerView");
                        document.getElementById("cardIMG").appendChild(img);
                        console.log((response.cards).substring(i - 2, i))
                    }
                    if (response.teamCards !== undefined) {
                        var myElements = document.querySelectorAll(".bridgePlayerView");
                        for (var i=0; i < myElements.length; i++) {
                            myElements[i].style.top = "605px";
                        }
                        document.getElementById("cardIMG2").innerHTML = "";
                        for (var i = 2; i < ((response.teamCards).length) + 2; i = i + 2) {
                            let img = document.createElement("img");
                            console.log(i);
                            img.src = './img/Cards/' + (response.teamCards).substring(i - 2, i) + '.png';
                            console.log(img.src);
                            img.classList.add("bridgeTeamView");
                            document.getElementById("cardIMG2").appendChild(img);
                            console.log((response.teamCards).substring(i - 2, i))
                        }
                    }
                    document.getElementById("cardIMG3").innerHTML = "";
                    document.getElementById("cardIMG4").innerHTML = "";
                    for (var t =0; t<response.enemyCards; t++) {
                        let img = document.createElement("img");
                        img.src = './img/Cards/blank.jpg';
                        img.classList.add("bridgeEnemyView");

                        if (t<(response.enemyCards)/2) {
                            document.getElementById("cardIMG3").appendChild(img);
                        } else {
                            document.getElementById("cardIMG4").appendChild(img);
                        }


                    }
                }

                playerInp = "1";
            }
        });

    }, 1000);

}


function getBridgeSessionID(){
    const SessionID = document.getElementById("getBridgeSessionID").value;
    let isNum = /^\d+$/.test(SessionID);
    // checks to see if they're any numbers in the String, using Boolean
    if (isNum === true && SessionID.indexOf(' ') == -1) {
        console.log("Fetching session code");
        UserID = localStorage.getItem("UserID");
        var url = "/Bridge/get/";

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
                document.getElementById("sessionCode").innerHTML  = "Session Code: " + response.BridgeSessionID;
                owner = false;
                return owner;
                //this function will create an HTML table of the data (as we
                // did in lesson 2
            }
        });
    }
}

function createBridgeSessionCode(){
    console.log("Invoked postBridgeAdd()");

    //const formData = new FormData(document.getElementById("formCreateBridgeSession"));
    UserID = localStorage.getItem("UserID");
    console.log(UserID);
    var url = "/Bridge/add/";

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
            document.getElementById("sessionCode").innerHTML  = "Session Code: " + response.BridgeSessionID;
            owner = true;
            return owner;
            //window.open("/client/home.html", "_self");   //if not open this page (for example!)
        }
    });

}

function initializeGame() {

    var url = "/BridgeStart/add/";
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
function nextRoundBtn() {
    playerInp = "next";
    console.log(playerInp);
    return playerInp;
}
function standBtn() {
    playerInp = "stand";
    console.log(playerInp);
    return playerInp;
}
var owner = false;
var playerInp = "1";
var numOfIMG = 0;
var newRound = false;
var score = 0
var reset = false;
var Ace = 0; //number of aces
var clientScore = 0;
var card = "";
var oldLength = 0;
var lowAce = 0;
var oldRound = 1;
/*
var ws = new WebSocket("ws://localhost:8081/client/Bridge.html");
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

        //img.src = './img/Cards/D2.png';
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
//img.src = './img/Cards/D2.png';

