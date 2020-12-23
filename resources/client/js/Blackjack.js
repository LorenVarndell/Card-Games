function pageLoad() {
    //document.getElementById("game").style.display ="none";
    document.getElementById("hitBtn").style.display="none";
    document.getElementById("standBtn").style.display="none";
    document.getElementById("hitBtn").style.top = "175px";
    document.getElementById("standBtn").style.top = "250px";
    document.getElementById("score").style.display="none";
    document.getElementById("score").style.top = "650px";
    document.getElementById("score").style.width = "600px";
    document.getElementById("result").style.display = "none";
    document.getElementById("result").style.border = "0px solid black";
    document.getElementById("result").style.fontSize = "75px";
    document.getElementById("result").style.width = "800px";
    document.getElementById("result").style.top = "550px";
    document.getElementById("player").style.display = "none";
    document.getElementById("player").style.top = "200px";
    document.getElementById("nextRoundBtn").style.display="none";
    document.getElementById("nextRoundBtn").style.top = "200px";
    document.getElementById("getBlackjackSession").addEventListener("click", getBlackjackSessionCode);
    document.getElementById("createBlackjackSession").addEventListener("click", createBlackjackSessionCode);
    document.getElementById("startBtn").addEventListener("click", initializeGame);
    document.getElementById("hitBtn").addEventListener("click", hitBtn);
    document.getElementById("standBtn").addEventListener("click", standBtn);
    document.getElementById("nextRoundBtn").addEventListener("click", nextRoundBtn);
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
            } else if (response.hasOwnProperty("Error2")){
                console.log(JSON.stringify(response));
            } else if (response.hasOwnProperty("Error3")){
                console.log(JSON.stringify(response));
            } else {
                console.log(response);
                document.getElementById("inputs").style.display="none";
                document.getElementById("game").style.display = "block";
                if (response.finalScore !== undefined) {
                    document.getElementById("cardIMG").innerHTML = "";
                    document.getElementById("result").style.display = "none";
                    document.getElementById("sessionCode").style.display = "none";
                    document.getElementById("hitBtn").style.display="none";
                    document.getElementById("standBtn").style.display="none";
                    document.getElementById("score").style.display="block";
                    document.getElementById("nextRoundBtn").style.display="none";
                    document.getElementById("score").style.top = "200px";
                    document.getElementById("score").style.fontSize = "100px";
                    document.getElementById("score").innerHTML = "Winners scores: " + response.finalScore;
                    document.getElementById("player").style.display = "block";
                    document.getElementById("player").style.top = "500px";
                    document.getElementById("player").style.fontSize = "35px";
                    document.getElementById("player").style.border = "0px solid black";
                    document.getElementById("player").innerHTML = "Winners: ";
                    for (var i = 0; i < (response.winners).length; i++) {
                        if (i == (response.winners).length-1) {
                            document.getElementById("player").innerHTML += "Player" + (response.winners).charAt(i)
                        } else {
                            document.getElementById("player").innerHTML += "Player" + (response.winners).charAt(i) + ", ";
                        }
                    }

                }
                if (response.cards !== undefined) {
                    if (response.round != oldRound) {
                        reset = true;
                    }

                    if ((response.cards).length != 0 && (response.cards).length != oldLength) {
                        for (var i = 2 + oldLength; i < ((response.cards).length) + 2; i = i + 2) {
                            card = (response.cards).substring(i - 2, i);
                            if (card.charAt(1) === "T" || card.charAt(1) === "J" || card.charAt(1) === "Q" || card.charAt(1) === "K") {
                                clientScore = clientScore + 10;
                            } else if (card.charAt(1) === "A") {
                                clientScore = clientScore + 11;
                                Ace++;
                            } else {
                                clientScore = clientScore + parseInt(card.charAt(1));
                            }
                        }

                        if (Ace > lowAce) {
                            if (clientScore > 21) {
                                for (var i = 0; i <= Ace; i++) {
                                    console.log("test");
                                    if ((clientScore - (i * 10)) <= 21) {
                                        clientScore = clientScore - (i * 10);
                                        lowAce++;
                                        i = 10;
                                    }
                                }
                            }
                        }
                    }
                    if (response.score < 0 || newRound == true) {
                        newRound = true;
                    }
                    if (newRound == true) {
                        if (clientScore > 21) {
                            document.getElementById("result").innerHTML = "Score over 21, lost";
                        } else if (clientScore == 21) {
                            document.getElementById("result").innerHTML = "Blackjack!";
                        } else {
                            document.getElementById("result").innerHTML = "Player has stood";
                        }
                        document.getElementById("result").style.display = "block";
                    }

                    if (response.turn == true) {
                        document.getElementById("player").style.display = "none";
                        document.getElementById("player").style.border = "0px solid black";
                        if (newRound == true) {
                            document.getElementById("hitBtn").style.display = "none";
                            document.getElementById("hitBtn").style.border = "0px solid black";
                            document.getElementById("standBtn").style.display = "none";
                            document.getElementById("standBtn").style.border = "0px solid black";
                        } else {
                            document.getElementById("score").style.display = "block";
                            document.getElementById("hitBtn").style.display = "block";
                            document.getElementById("hitBtn").style.border = "5px solid black";
                            document.getElementById("standBtn").style.display = "block";
                            document.getElementById("standBtn").style.border = "5px solid black";
                        }


                    } else if (response.turn == false) {
                        if ((response.standbyCurrent == true && newRound == true) || response.standbyCurrentEnd == true) {
                            if (response.standbyCurrentEnd == true) {
                                document.getElementById("nextRoundBtn").innerHTML = "View final standings";
                            }
                            document.getElementById("player").style.display = "none";
                            document.getElementById("player").style.border = "0px solid black";
                            document.getElementById("nextRoundBtn").style.display = "block";
                            document.getElementById("nextRoundBtn").style.border = "5px solid black";
                        } else {
                            document.getElementById("nextRoundBtn").style.display = "none";
                            document.getElementById("nextRoundBtn").style.border = "0px solid black";
                            document.getElementById("player").innerHTML = "Player: " + response.round;
                            document.getElementById("player").style.display = "block";
                            document.getElementById("player").style.border = "5px solid black";
                        }

                        document.getElementById("messageBox").style.display = "none";
                        document.getElementById("messageBox").style.border = "0px solid black";
                        document.getElementById("hitBtn").style.display = "none";
                        document.getElementById("hitBtn").style.border = "0px solid black";
                        document.getElementById("score").style.display = "block";
                    }

                    if ((response.cards).length != 0) {
                        if (((response.cards).length) / 2 > numOfIMG) {
                            numOfIMG++;
                            let img = document.createElement("img");
                            for (let i = 2; i < ((response.cards).length) + 2; i = i + 2) {
                                img.src = './img/Cards/' + (response.cards).substring(i - 2, i) + '.png';
                                img.classList.add("cards");
                                document.getElementById("cardIMG").appendChild(img);
                                console.log((response.cards).substring(i - 2, i))
                            }
                        }
                    }

                    document.getElementById("score").innerHTML = "Current score: " + clientScore;
                    console.log(reset);
                    if (reset == true) {
                        oldLength = 0;
                        clientScore = 0;
                        reset = false;
                        newRound = false;
                        numOfIMG = 0;
                        Ace = 0;
                        lowAce = 0;
                        document.getElementById("cardIMG").innerHTML = "";
                        document.getElementById("score").innerHTML = "";
                        document.getElementById("result").style.display = "none";
                    }
                    if ((response.cards).length != 0) {
                        oldLength = (response.cards).length;
                    }
                    oldRound = response.round;
                }

                playerInp = "1";
            }
        });

    }, 1000)

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
                document.getElementById("sessionCode").innerHTML  = "Session Code: " + response.BlackjackSessionID;
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
        console.log(response.BlackjackSessionID);
        document.getElementById("inputs").style.display="none";
        document.getElementById("game").style.display = "block";
        document.getElementById("startBtn").style.display = "block";
        document.getElementById("startBtn").style.border = "5px solid black";
        document.getElementById("startBtn").innerHTML  = "Press to Start";
        document.getElementById("sessionCode").innerHTML  = "Session Code: " + response.BlackjackSessionID;
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
function standBtn() {
    playerInp = "stand";
    console.log(playerInp);
    return playerInp;
}
function nextRoundBtn() {
    playerInp = "next";
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

