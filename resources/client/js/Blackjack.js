function pageLoad() {

    document.getElementById("getBlackjackSession").addEventListener("click", getBlackjackSessionCode);
    document.getElementById("createBlackjackSession").addEventListener("click", createBlackjackSessionCode);
    console.log("Creating cookie/UserID");


    var url = "/Users/add";
    fetch(url, {
        method: "POST",
    }).then(response => {
        return response.json()
    }).then(response => {
        if (response.hasOwnProperty("Error")) {   //checks if response from server has a key "Error"
            alert(JSON.stringify(response));        // if it does, convert JSON object to string and alert
        } else {
            console.log(localStorage.getItem("UserID"));
            //Cookies.set("UserID",response.UserID);
            if (localStorage.getItem("UserID") === null) {
                localStorage.setItem("UserID", response.UserID);
            }
            console.log("Cookie set")
        }
    });



}


function getBlackjackSessionCode(){
    console.log("Fetching session code");

    const SessionID = document.getElementById("getBlackjackSessionID").value;
    const url = "/Blackjack/get/";

    fetch(url + SessionID, {
        method: "GET",
    }).then(response => {
        return response.json();                 //return response to JSON
    }).then(response => {
        if (response.hasOwnProperty("Error")) { //checks if response from server has a key "Error"
            alert(JSON.stringify(response));    // if it does, convert JSON object to string and alert
        } else {
            console.log(response);
            //this function will create an HTML table of the data (as we
            // did in lesson 2
        }
    });
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
        //window.open("/client/home.html", "_self");   //if not open this page (for example!)
    }
    });

}

